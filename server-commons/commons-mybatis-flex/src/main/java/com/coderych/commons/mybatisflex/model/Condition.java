package com.coderych.commons.mybatisflex.model;

import com.coderych.commons.mybatisflex.enums.Operator;
import com.coderych.commons.mybatisflex.enums.Relation;
import lombok.Data;

import java.util.List;

/**
 * 动态查询条件模型，支持叶子节点（单个条件）和分支节点（嵌套条件组）两种形态。
 * <p>叶子节点通过 key/operator/value 定义单个查询条件；
 * 分支节点通过 children 列表组合多个子条件，并通过 relation 指定 AND/OR 关系。</p>
 *
 * @author YCH
 */
@Data
public class Condition {
    private String key;

    /**
     * 查询值。
     */
    private Object value;

    /**
     * 查询操作符。
     */
    private Operator operator = Operator.EQ;

    /**
     * 子条件连接关系。
     */
    private Relation relation = Relation.AND;

    /**
     * 子条件列表。
     */
    private List<Condition> children;
}
