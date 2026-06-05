package com.tools.config;

import com.tools.common.ApiResponse;
import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，拦截 Controller 层抛出的所有异常，转换为统一的 {@link ApiResponse} JSON 响应。
 * <p>
 * 异常处理优先级（Spring 会选择最匹配的 handler）：
 * <ol>
 *   <li>先匹配 {@link BusinessException} — 业务异常，返回对应的错误码和消息</li>
 *   <li>再匹配 {@link MethodArgumentNotValidException} — 参数校验失败，拼合所有字段错误</li>
 *   <li>最后兜底 {@link Exception} — 未知异常，返回 500 并附带异常信息</li>
 * </ol>
 * <p>
 * 所有响应都是 JSON 格式，不会出现 Spring 默认的 HTML 错误页面。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常。
     * <p>
     * 直接使用异常中携带的 code 和 message，HTTP 状态码通过
     * {@link ApiResponse#code} 字段体现（Spring HTTP response 仍为 200）。
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        log.warn("业务异常: code={}, message={}", ex.getCode(), ex.getMessage());
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理 Jakarta Validation 校验失败异常。
     * <p>
     * 将多个字段的校验错误拼接为一个字符串，格式：{@code "username: 用户名不能为空; password: 密码不能为空"}
     * 同时设置 HTTP 状态码为 400。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        // 收集所有字段的校验错误信息，用分号分隔
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());
        return ApiResponse.error(ErrorCode.VALIDATION_ERROR.getCode(), message);
    }

    /**
     * 兜底处理所有未被上面两个 handler 捕获的异常。
     * <p>
     * 返回 500 错误，消息中包含具体异常信息便于调试。
     * HTTP 状态码设为 500。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneral(Exception ex) {
        return ApiResponse.error(ErrorCode.INTERNAL_ERROR.getCode(),
                ErrorCode.INTERNAL_ERROR.getMessage() + ": " + ex.getMessage());
    }
}
