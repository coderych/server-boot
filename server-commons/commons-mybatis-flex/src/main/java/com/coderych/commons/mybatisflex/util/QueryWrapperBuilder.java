package com.coderych.commons.mybatisflex.util;

import cn.hutool.core.util.ReflectUtil;
import com.coderych.commons.core.util.STR;
import com.coderych.commons.core.util.collection.CollectionUtils;
import com.coderych.commons.core.util.object.ObjectUtils;
import com.coderych.commons.mybatisflex.annotation.QueryField;
import com.coderych.commons.mybatisflex.enums.Operator;
import com.coderych.commons.mybatisflex.enums.Relation;
import com.coderych.commons.mybatisflex.model.Condition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.util.SqlUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 查询条件构建器，将查询对象（通过 {@link QueryField} 注解）或 {@link Condition} 树
 * 转换为 MyBatis-Flex 的 {@link QueryWrapper}。
 * <p>内部缓存字段列表以提升反射性能。</p>
 *
 * @author YCH
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryWrapperBuilder {
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();


    /**
     * 从查询对象构建 QueryWrapper，遍历所有带 {@link QueryField} 注解的字段并生成对应查询条件。
     */
    public static <Q> QueryWrapper build(Q query) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (ObjectUtils.isNull(query)) {
            return queryWrapper;
        }

        List<Field> fields = FIELD_CACHE.computeIfAbsent(query.getClass(), clazz -> Arrays.stream(ReflectUtil.getFields(clazz))
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toList());

        for (Field field : fields) {
            handleField(queryWrapper, field, extractFieldValue(query, field));
        }
        return queryWrapper;
    }


    /**
     * 从 {@link Condition} 条件树构建 QueryWrapper，支持嵌套 AND/OR 组合。
     */
    public static QueryWrapper build(Condition condition) {
        QueryWrapper queryWrapper = new QueryWrapper();
        if (ObjectUtils.isNull(condition)) {
            return queryWrapper;
        }
        appendCondition(queryWrapper, condition);
        return queryWrapper;
    }


    private static void appendCondition(QueryWrapper queryWrapper, Condition condition) {
        if (ObjectUtils.isNull(condition)) {
            return;
        }

        if (isLeafNode(condition)) {
            if (!isValidLeafNode(condition)) {
                log.warn("Invalid leaf condition: {}", condition);
                return;
            }
            addLeafCondition(queryWrapper, condition);
        } else {
            addBranchCondition(queryWrapper, condition);
        }
    }


    private static void addLeafCondition(QueryWrapper queryWrapper, Condition condition) {
        Consumer<QueryWrapper> consumer = parseCondition(condition.getKey(), condition.getOperator(), condition.getValue());
        queryWrapper.and(consumer);
    }


    private static void addBranchCondition(QueryWrapper queryWrapper, Condition condition) {
        Relation relation = condition.getRelation();
        List<Condition> children = condition.getChildren().stream().filter(child -> {
            if (CollectionUtils.isNotEmpty(child.getChildren())) {
                return true;
            }
            return isLeafNode(child);
        }).toList();
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        queryWrapper.and(wrapper -> {
            boolean isFirst = true;
            for (Condition child : children) {
                if (isFirst) {
                    appendCondition(wrapper, child);
                    isFirst = false;
                    continue;
                }
                if (relation == Relation.AND) {
                    wrapper.and((Consumer<QueryWrapper>) w -> appendCondition(w, child));
                } else {
                    wrapper.or((Consumer<QueryWrapper>) w -> appendCondition(w, child));
                }
            }
        });
    }


    private static boolean isLeafNode(Condition condition) {
        return condition.getChildren() == null || condition.getChildren().isEmpty();
    }


    private static boolean isValidLeafNode(Condition condition) {
        boolean hasKey = ObjectUtils.isNotNull(condition.getKey());
        boolean hasOperator = ObjectUtils.isNotNull(condition.getOperator());
        boolean nullOperator = ObjectUtils.equal(condition.getOperator(), Operator.IS_NULL)
                || ObjectUtils.equal(condition.getOperator(), Operator.IS_NOT_NULL);
        boolean hasValue = ObjectUtils.isNotNull(condition.getValue()) || nullOperator;
        return hasKey && hasOperator && hasValue;
    }


    private static void handleField(QueryWrapper queryWrapper, Field field, Object value) {
        if (ObjectUtils.isEmpty(value)) {
            return;
        }

        QueryField annotation = field.getAnnotation(QueryField.class);
        if (ObjectUtils.isNull(annotation)) {
            return;
        }

        Operator operator = annotation.operator();
        String[] aliases = resolveFieldAliases(annotation.value(), field);

        List<Consumer<QueryWrapper>> conditions = new ArrayList<>(aliases.length);
        for (String alias : aliases) {
            Consumer<QueryWrapper> condition = parseCondition(alias, operator, value);
            conditions.add(condition);
        }

        boolean isOr = annotation.isOr();
        Relation relation = annotation.relation();
        applyConditions(queryWrapper, conditions, relation, isOr);
    }


    private static <Q> Object extractFieldValue(Q query, Field field) {
        return ReflectUtil.getFieldValue(query, field);
    }


    private static String[] resolveFieldAliases(String[] annotationAliases, Field field) {
        if (ObjectUtils.isNotEmpty(annotationAliases)) {
            return annotationAliases;
        }
        return new String[]{STR.toUnderlineCase(field.getName())};
    }


    /**
     * 将单个条件解析为 QueryWrapper 消费函数，列名会经过 SQL 注入安全校验。
     */
    public static Consumer<QueryWrapper> parseCondition(String alias, Operator operator, Object value) {

        SqlUtil.keepColumnSafely(alias);
        return switch (operator) {
            case EQ -> wrapper -> wrapper.eq(alias, value);
            case NE -> wrapper -> wrapper.ne(alias, value);
            case GT -> wrapper -> wrapper.gt(alias, value);
            case GE -> wrapper -> wrapper.ge(alias, value);
            case LT -> wrapper -> wrapper.lt(alias, value);
            case LE -> wrapper -> wrapper.le(alias, value);
            case LIKE -> wrapper -> wrapper.like(alias, value);
            case NOT_LIKE -> wrapper -> wrapper.notLike(alias, value);
            case IN -> wrapper -> wrapper.in(alias, getMultipleValues(value));
            case NOT_IN -> wrapper -> wrapper.notIn(alias, getMultipleValues(value));
            case IS_NULL -> wrapper -> wrapper.isNull(alias);
            case IS_NOT_NULL -> wrapper -> wrapper.isNotNull(alias);
            case BETWEEN -> buildBetweenCondition(alias, value, false);
            case NOT_BETWEEN -> buildBetweenCondition(alias, value, true);
        };
    }

    private static Consumer<QueryWrapper> buildBetweenCondition(String alias, Object value, boolean negated) {
        List<Object> values = getMultipleValues(value);
        if (values.size() != 2) {
            throw new IllegalArgumentException((negated ? "NOT_BETWEEN" : "BETWEEN") + " 条件值必须为 List 或 String，且长度为 2");
        }
        return negated
                ? wrapper -> wrapper.notBetween(alias, values.get(0), values.get(1))
                : wrapper -> wrapper.between(alias, values.get(0), values.get(1));
    }


    private static void applyConditions(QueryWrapper queryWrapper, List<Consumer<QueryWrapper>> consumers, Relation relation, boolean isOr) {
        if (consumers.isEmpty()) {
            return;
        }
        Consumer<Consumer<QueryWrapper>> action = isOr ? queryWrapper::or : queryWrapper::and;
        action.accept(qw -> {
            if (relation == Relation.OR) consumers.forEach(qw::or);
            else consumers.forEach(qw::and);
        });
    }


    @SuppressWarnings("unchecked")
    public static List<Object> getMultipleValues(Object value) {
        if (value instanceof List) {
            return (List<Object>) value;
        }
        if (value instanceof String) {
            return Arrays.asList(((String) value).split(","));
        }
        throw new IllegalArgumentException("IN 条件值必须为 List 或 String");
    }
}
