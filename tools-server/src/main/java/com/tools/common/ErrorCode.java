package com.tools.common;

import lombok.Getter;

/**
 * 统一业务错误码枚举。
 * <p>
 * 按 HTTP 状态码语义分为五类：
 * <ul>
 *   <li><b>400</b> — 客户端输入错误（参数校验、格式、重复等）</li>
 *   <li><b>401</b> — 认证失败（未登录或凭据错误）</li>
 *   <li><b>403</b> — 权限不足（已认证但无权操作该资源）</li>
 *   <li><b>404</b> — 资源不存在</li>
 *   <li><b>500</b> — 服务端内部错误</li>
 * </ul>
 * <p>
 * 使用方式：在 Service 中通过 {@code throw new BusinessException(ErrorCode.XXX)} 抛出，
 * 由 {@link com.tools.config.GlobalExceptionHandler} 统一处理。
 */
@Getter
public enum ErrorCode {

    // ========== 400 客户端输入错误 ==========
    USERNAME_EXISTS(400, "用户名已存在"),
    EMAIL_EXISTS(400, "邮箱已被注册"),
    INVALID_JSON(400, "JSON 格式无效"),
    VALIDATION_ERROR(400, "参数校验失败"),

    // ========== 401 认证失败 ==========
    BAD_CREDENTIALS(401, "用户名或密码错误"),
    UNAUTHORIZED(401, "未认证，请登录"),

    // ========== 403 权限不足 ==========
    FORBIDDEN(403, "无权访问此文档"),
    FORBIDDEN_MODIFY(403, "无权修改此文档"),
    FORBIDDEN_DELETE(403, "无权删除此文档"),
    FORBIDDEN_JSON(403, "无权访问"),
    FORBIDDEN_MODIFY_JSON(403, "无权修改"),
    FORBIDDEN_DELETE_JSON(403, "无权删除"),

    // ========== 404 资源不存在 ==========
    DOC_NOT_FOUND(404, "文档不存在"),
    RECORD_NOT_FOUND(404, "记录不存在"),
    USER_NOT_FOUND(404, "用户不存在"),

    // ========== 500 服务端错误 ==========
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
