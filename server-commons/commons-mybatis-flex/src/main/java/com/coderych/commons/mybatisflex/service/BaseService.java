package com.coderych.commons.mybatisflex.service;

import com.coderych.commons.core.model.P;
import com.coderych.commons.core.model.PageQuery;
import com.coderych.commons.core.model.Query;
import com.mybatisflex.core.service.IService;

import java.io.Serializable;
import java.util.List;

/**
 * 通用业务服务接口，定义 CRUD 标准操作并支持 Entity/Query/Form/DTO 四层模型转换。
 *
 * @param <E> 实体类型
 * @param <Q> 查询对象类型
 * @param <F> 表单对象类型
 * @param <D> DTO 类型
 * @author YCH
 */
public interface BaseService<E, Q, F, D> extends IService<E> {
    P<D> page(Q query, PageQuery pageQuery);

    List<D> list(Q query, Query baseQuery);

    D selectById(Serializable id);

    void insert(F form);

    void update(F form);

    void deleteById(Serializable id);
}
