package com.coderych.commons.core.enums;

import java.time.format.DateTimeFormatter;

/**
 * 全局常量定义。
 *
 * @author YCH
 */
public class Constants {

    /**
     * 默认字符集。
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 成功标识。
     */
    public static final String SUCCESS = "success";

    /**
     * 失败标识。
     */
    public static final String FAIL = "fail";

    /**
     * 是标识。
     */
    public static final String YES = "1";

    /**
     * 否标识。
     */
    public static final String NO = "0";

    /**
     * 启用状态值。
     */
    public static final Integer STATUS_ENABLE = 1;

    /**
     * 禁用状态值。
     */
    public static final Integer STATUS_DISABLE = 0;

    /**
     * 未删除标识。
     */
    public static final Integer DELETED_NO = 0;

    /**
     * 已删除标识。
     */
    public static final Integer DELETED_YES = 1;

    /**
     * 默认页码。
     */
    public static final Integer DEFAULT_PAGE_NUM = 1;

    /**
     * 默认分页大小。
     */
    public static final Integer DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小。
     */
    public static final Integer MAX_PAGE_SIZE = 1000;

    /**
     * Bearer 令牌前缀。
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Authorization 请求头名称。
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Content-Type 请求头名称。
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * JSON 内容类型。
     */
    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * 表单内容类型。
     */
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    /**
     * 管理员角色标识。
     */
    public static final String ROLE_ADMIN = "admin";

    /**
     * 超级管理员 ID。
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 日期时间格式。
     */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 日期时间格式化器。
     */
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /**
     * 日期格式。
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /**
     * 日期格式化器。
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * 时间格式。
     */
    public static final String TIME_PATTERN = "HH:mm:ss";
    /**
     * 时间格式化器。
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
}
