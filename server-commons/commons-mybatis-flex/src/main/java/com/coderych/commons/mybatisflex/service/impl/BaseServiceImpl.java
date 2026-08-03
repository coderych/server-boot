package com.coderych.commons.mybatisflex.service.impl;

import com.coderych.commons.core.model.P;
import com.coderych.commons.core.model.PageQuery;
import com.coderych.commons.core.model.Query;
import com.coderych.commons.core.enums.ResultCode;
import com.coderych.commons.core.exception.BizException;
import com.coderych.commons.core.util.BEAN;
import com.coderych.commons.core.util.object.ClassUtils;
import com.coderych.commons.mybatisflex.service.BaseService;
import com.coderych.commons.mybatisflex.util.Of;
import com.coderych.commons.mybatisflex.util.QueryWrapperBuilder;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * 通用业务服务实现基类，基于 MyBatis-Flex 的 {@link ServiceImpl} 提供标准 CRUD 实现。
 * <p>通过反射在构造时解析泛型参数获取实体类和 DTO 类，支持自动类型转换。
 * 提供 {@link #onInsertOrUpdateBefore}、{@link #onInsertOrUpdateAfter}、
 * {@link #onDeleteBefore}、{@link #onDeleteAfter} 生命周期钩子供子类扩展。</p>
 *
 * @param <M> Mapper 类型
 * @param <E> 实体类型
 * @param <Q> 查询对象类型
 * @param <F> 表单对象类型
 * @param <D> DTO 类型
 * @author YCH
 */
public class BaseServiceImpl<M extends BaseMapper<E>, E, Q, F, D> extends ServiceImpl<M, E> implements BaseService<E, Q, F, D> {
    protected final Class<E> eClass;
    protected final Class<D> dClass;
    private final Class<?>[] parameterTypes = ClassUtils.getGenericParameterTypes(getClass());

    @SuppressWarnings("unchecked")
    public BaseServiceImpl() {
        this.eClass = (Class<E>) parameterTypes[1];
        this.dClass = (Class<D>) parameterTypes[4];
    }

    @Override
    public P<D> page(Q query, PageQuery pageQuery) {
        QueryWrapper queryWrapper = buildQueryWrapper(query, pageQuery);
        return Of.p(mapper.paginateAs(Of.page(pageQuery), queryWrapper, dClass));
    }

    @Override
    public List<D> list(Q query, Query baseQuery) {
        QueryWrapper queryWrapper = buildQueryWrapper(query, baseQuery);
        return mapper.selectListByQueryAs(queryWrapper, dClass);
    }

    @Override
    public D selectById(Serializable id) {
        return BEAN.convert(mapper.selectOneById(id), dClass);
    }

    @Override
    public void insert(F form) {
        onInsertOrUpdateBefore(form, true);
        E entity = BEAN.convert(form, eClass);
        mapper.insert(entity);
        onInsertOrUpdateAfter(entity, form, true);
    }

    @Override
    public void update(F form) {
        onInsertOrUpdateBefore(form, false);
        E entity = BEAN.convert(form, eClass);
        mapper.update(entity);
        onInsertOrUpdateAfter(entity, form, false);
    }

    @Override
    public void deleteById(Serializable id) {
        onDeleteBefore(id);
        mapper.deleteById(id);
        onDeleteAfter(id);
    }

    /**
     * 根据查询对象构建 QueryWrapper，并追加排序条件。
     */
    protected QueryWrapper buildQueryWrapper(Q q, Query query) {
        QueryWrapper queryWrapper = QueryWrapperBuilder.build(q);
        if (query != null) {
            query.parseOrderBy().forEach(pair ->
                    queryWrapper.orderBy(pair.getFirst(), pair.getSecond())
            );
        }
        return queryWrapper;
    }

    /**
     * 校验查询条件对应的数据是否唯一。
     */
    protected void validateUnique(QueryWrapper queryWrapper, String message) {
        if (mapper.selectCountByQuery(queryWrapper) > 0) {
            throw new BizException(ResultCode.DATA_ALREADY_EXISTS, message);
        }
    }

    /**
     * 校验查询条件对应的数据存在。
     */
    protected void validateExists(QueryWrapper queryWrapper, String message) {
        if (mapper.selectCountByQuery(queryWrapper) == 0) {
            throw new BizException(ResultCode.NOT_FOUND, message);
        }
    }

    /**
     * 校验查询条件对应的数据不存在。
     */
    protected void validateNotExists(QueryWrapper queryWrapper, String message) {
        if (mapper.selectCountByQuery(queryWrapper) > 0) {
            throw new BizException(ResultCode.CONFLICT, message);
        }
    }

    /**
     * 新增/更新前置钩子，子类可重写以实现自定义校验或数据预处理。
     */
    protected void onInsertOrUpdateBefore(F form, boolean isSave) {
    }

    /**
     * 新增/更新后置钩子，子类可重写以实现关联数据处理等逻辑。
     */
    protected void onInsertOrUpdateAfter(E entity, F form, boolean isSave) {
    }

    /**
     * 删除前置钩子，子类可重写以实现删除前的校验逻辑。
     */
    protected void onDeleteBefore(Serializable id) {
    }

    /**
     * 删除后置钩子，子类可重写以实现关联数据清理等逻辑。
     */
    protected void onDeleteAfter(Serializable id) {
    }
}
