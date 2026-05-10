package com.coderych.commons.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一结果状态码枚举。
 * <p>编码规则：2xxx 成功，4xxx 客户端错误，5xxx 服务端错误，1xxx 用户相关，2xxx 数据相关，3xxx 系统相关。</p>
 *
 * @author YCH
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS(2000, "操作成功"),
    BAD_REQUEST(4000, "请求参数错误"),
    UNAUTHORIZED(4001, "未授权，请先登录"),
    FORBIDDEN(4003, "禁止访问"),
    NOT_FOUND(4004, "资源不存在"),
    METHOD_NOT_ALLOWED(4005, "请求方法不允许"),
    CONFLICT(4009, "资源冲突"),
    VALIDATION_ERROR(4220, "数据验证失败"),
    INTERNAL_ERROR(5000, "操作失败"),
    ERROR(5001, "服务器内部错误"),
    SERVICE_UNAVAILABLE(5003, "服务不可用"),
    GATEWAY_TIMEOUT(5004, "网关超时"),
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "令牌已过期"),
    TOKEN_INVALID(1005, "令牌无效"),
    DATA_NOT_FOUND(2001, "数据不存在"),
    DATA_ALREADY_EXISTS(2002, "数据已存在"),
    DATA_VALIDATION_ERROR(2003, "数据验证失败"),
    SYSTEM_BUSY(3001, "系统繁忙，请稍后重试"),
    OPERATION_FAILED(3002, "操作失败"),
    PERMISSION_DENIED(3003, "权限不足");

    private final int code;
    private final String message;
}
