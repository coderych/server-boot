package com.coderych.commons.core.enums;

import java.time.format.DateTimeFormatter;

/**
 * 全局常量定义。
 *
 * @author YCH
 */
public class Constants {

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String SUCCESS = "success";

    public static final String FAIL = "fail";

    public static final String YES = "1";

    public static final String NO = "0";

    public static final Integer STATUS_ENABLE = 1;

    public static final Integer STATUS_DISABLE = 0;

    public static final Integer DELETED_NO = 0;

    public static final Integer DELETED_YES = 1;

    public static final Integer DEFAULT_PAGE_NUM = 1;

    public static final Integer DEFAULT_PAGE_SIZE = 10;

    public static final Integer MAX_PAGE_SIZE = 1000;

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final String HEADER_AUTHORIZATION = "Authorization";

    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";

    public static final String ROLE_ADMIN = "admin";

    public static final Long SUPER_ADMIN_ID = 1L;

    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
}
