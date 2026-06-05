package com.tools.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一 API 响应包装类。
 * <p>
 * 所有 Controller 的返回值都通过此类包装，确保前端接收到的 JSON 结构一致：
 * <pre>{@code {"code": 200, "message": "success", "data": {...}}}</pre>
 * <p>
 * 设计原则：
 * <ul>
 *   <li>成功时 code=200，失败时 code 为对应的 HTTP 状态码（400/401/403/404/500）</li>
 *   <li>data 使用泛型，编译期保证类型安全</li>
 *   <li>通过静态工厂方法创建，语义清晰（success/error）</li>
 * </ul>
 *
 * @param <T> 响应数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /** HTTP 风格的状态码 */
    private int code;

    /** 人类可读的描述信息 */
    private String message;

    /** 响应数据，失败时为 null */
    private T data;

    /** 创建成功响应（默认消息 "success"） */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /** 创建成功响应（自定义消息） */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /** 创建失败响应 */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
