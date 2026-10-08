package org.example.common;
/**
 * 统一响应状态码枚举
 * <p>
 * 定义业务中常见的状态码与提示信息
 * </p>
 *
 * @author example
 */
public enum ResultCode {

    /** 成功 */
    SUCCESS(200, "操作成功"),

    /** 参数错误 */
    PARAM_ERROR(400, "参数错误"),

    /** 用户名或密码错误 */
    LOGIN_FAILED(401, "用户名或密码错误"),

    /** 用户名已存在 */
    USERNAME_EXISTS(409, "用户名已存在"),

    /** 未登录 / token 无效 */
    UNAUTHORIZED(403, "未登录或登录已过期"),

    /** 服务器内部错误 */
    SYSTEM_ERROR(500, "系统内部错误");

    /** 状态码 */
    private final Integer code;

    /** 提示信息 */
    private final String message;

    /**
     * 构造方法
     *
     * @param code    状态码
     * @param message 提示信息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /** 获取状态码 */
    public Integer getCode() {
        return code;
    }

    /** 获取提示信息 */
    public String getMessage() {
        return message;
    }
}
