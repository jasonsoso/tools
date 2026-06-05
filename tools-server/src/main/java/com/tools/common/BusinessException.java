package com.tools.common;

import lombok.Getter;

/**
 * 业务异常类，用于在 Service 层抛出可预期的业务错误。
 * <p>
 * 继承 {@link RuntimeException}，无需在方法签名中声明，会被
 * {@link com.tools.config.GlobalExceptionHandler} 统一捕获并转换为
 * {@link ApiResponse} 格式的 JSON 响应。
 * <p>
 * 使用示例：
 * <pre>{@code throw new BusinessException(ErrorCode.USERNAME_EXISTS);}</pre>
 *
 * @see ErrorCode 统一错误码定义
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 错误码（对应 HTTP 状态码） */
    private final int code;

    /**
     * 使用预定义的错误码创建异常。
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用预定义的错误码 + 详情信息创建异常。
     * <p>
     * 最终消息格式为：{@code "错误消息: 详情"}，如 {@code "JSON 格式无效: Unexpected character..."}
     */
    public BusinessException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + ": " + detail);
        this.code = errorCode.getCode();
    }
}
