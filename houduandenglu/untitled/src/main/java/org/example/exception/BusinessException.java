package org.example.exception;

import org.example.common.ResultCode;

/**
 * 自定义业务异常
 * <p>
 * 业务处理过程中出现可预期的错误时抛出，由全局异常处理器捕获
 * </p>
 *
 * @author example
 */
public class BusinessException extends RuntimeException {

    /** 状态码 */
    private final Integer code;

    /**
     * 使用状态码枚举构造异常
     *
     * @param resultCode 状态码枚举
     */
    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    /**
     * 使用自定义消息构造异常
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
    }

    /**
     * 使用自定义状态码和消息构造异常
     *
     * @param code    状态码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /** 获取状态码 */
    public Integer getCode() {
        return code;
    }
}