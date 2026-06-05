package com.tools.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 通用
    USERNAME_EXISTS(400, "用户名已存在"),
    EMAIL_EXISTS(400, "邮箱已被注册"),
    INVALID_JSON(400, "JSON 格式无效"),
    VALIDATION_ERROR(400, "参数校验失败"),

    // 认证
    BAD_CREDENTIALS(401, "用户名或密码错误"),
    UNAUTHORIZED(401, "未认证，请登录"),

    // 权限
    FORBIDDEN(403, "无权访问此文档"),
    FORBIDDEN_MODIFY(403, "无权修改此文档"),
    FORBIDDEN_DELETE(403, "无权删除此文档"),
    FORBIDDEN_JSON(403, "无权访问"),
    FORBIDDEN_MODIFY_JSON(403, "无权修改"),
    FORBIDDEN_DELETE_JSON(403, "无权删除"),

    // 资源
    DOC_NOT_FOUND(404, "文档不存在"),
    RECORD_NOT_FOUND(404, "记录不存在"),
    USER_NOT_FOUND(404, "用户不存在"),

    // 服务器
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
