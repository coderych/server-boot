package com.coderych.commons.mybatisflex.controller;

import com.coderych.commons.core.model.P;
import com.coderych.commons.core.model.PageQuery;
import com.coderych.commons.core.model.Query;
import com.coderych.commons.core.model.R;
import com.coderych.commons.mybatisflex.service.BaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 通用 CRUD 控制器基类，提供分页、列表、详情、新增、修改、删除标准接口。
 * <p>子类通过泛型参数指定服务层、实体、查询对象、表单对象和 DTO 类型。</p>
 *
 * @param <S> 服务层类型
 * @param <E> 实体类型
 * @param <Q> 查询对象类型
 * @param <F> 表单对象类型
 * @param <D> DTO 类型
 * @author YCH
 */
public abstract class BaseController<S extends BaseService<E, Q, F, D>, E, Q, F, D> {
    @Autowired
    protected S service;

    @GetMapping("/page")
    public R<P<D>> page(Q q, PageQuery pageQuery) {
        return R.ok(service.page(q, pageQuery));
    }

    @GetMapping
    public R<List<D>> list(Q query, Query baseQuery) {
        return R.ok(service.list(query, baseQuery));
    }

    @GetMapping("/{id}")
    public R<D> getById(@PathVariable Serializable id) {
        return R.ok(service.selectById(id));
    }

    @PostMapping
    public R<?> save(@Valid @RequestBody F form) {
        service.insert(form);
        return R.ok();
    }

    @PutMapping
    public R<?> updateById(@Valid @RequestBody F form) {
        service.update(form);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<?> removeById(@PathVariable Serializable id) {
        service.deleteById(id);
        return R.ok();
    }

}
