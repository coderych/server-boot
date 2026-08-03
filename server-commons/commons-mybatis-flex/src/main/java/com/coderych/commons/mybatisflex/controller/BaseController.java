package com.coderych.commons.mybatisflex.controller;

import com.coderych.commons.core.model.P;
import com.coderych.commons.core.model.PageQuery;
import com.coderych.commons.core.model.Query;
import com.coderych.commons.core.model.R;
import com.coderych.commons.mybatisflex.enums.Api;
import com.coderych.commons.mybatisflex.service.BaseService;
import com.coderych.commons.satoken.core.LoginUser;
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
    /**
     * 业务服务。
     */
    @Autowired
    protected S service;

    /**
     * 分页查询数据。
     *
     * @param q 查询条件
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @GetMapping("/page")
    public R<P<D>> page(Q q, PageQuery pageQuery) {
        checkPermission(Api.PAGE);
        return R.ok(service.page(q, pageQuery));
    }

    /**
     * 查询数据列表。
     *
     * @param query 查询条件
     * @param baseQuery 查询参数
     * @return 数据列表
     */
    @GetMapping
    public R<List<D>> list(Q query, Query baseQuery) {
        checkPermission(Api.LIST);
        return R.ok(service.list(query, baseQuery));
    }

    /**
     * 根据主键查询数据详情。
     *
     * @param id 主键
     * @return 数据详情
     */
    @GetMapping("/{id}")
    public R<D> getById(@PathVariable Serializable id) {
        checkPermission(Api.GET);
        return R.ok(service.selectById(id));
    }

    /**
     * 新增数据。
     *
     * @param form 新增表单
     * @return 空成功响应
     */
    @PostMapping
    public R<?> save(@Valid @RequestBody F form) {
        checkPermission(Api.SAVE);
        service.insert(form);
        return R.ok();
    }

    /**
     * 根据主键修改数据。
     *
     * @param form 修改表单
     * @return 空成功响应
     */
    @PutMapping
    public R<?> updateById(@Valid @RequestBody F form) {
        checkPermission(Api.UPDATE);
        service.update(form);
        return R.ok();
    }

    /**
     * 根据主键删除数据。
     *
     * @param id 主键
     * @return 空成功响应
     */
    @DeleteMapping("/{id}")
    public R<?> removeById(@PathVariable Serializable id) {
        checkPermission(Api.REMOVE);
        service.deleteById(id);
        return R.ok();
    }

    /**
     * 校验当前操作权限，由子类根据业务资源重写。
     *
     * @param api 操作类型
     */
    protected void checkPermission(Api api) {
    }

    /**
     * 根据操作类型和资源标识校验权限。
     *
     * @param api 操作类型
     * @param resource 资源标识
     */
    protected void checkPermission(Api api, String resource) {
        switch (api) {
            case PAGE, LIST -> LoginUser.checkPermission(resource + ":list");
            case GET -> LoginUser.checkPermission(resource + ":query");
            case SAVE -> LoginUser.checkPermission(resource + ":add");
            case UPDATE -> LoginUser.checkPermission(resource + ":edit");
            case REMOVE -> LoginUser.checkPermission(resource + ":remove");
        }
    }
}
