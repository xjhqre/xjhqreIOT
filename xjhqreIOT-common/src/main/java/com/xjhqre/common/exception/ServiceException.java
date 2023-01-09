package com.xjhqre.common.exception;

import lombok.Data;

/**
 * 业务异常
 * 
 * @Author: xjhqre
 * @DateTime: 2022/6/18 14:52
 */
@Data
public class ServiceException extends RuntimeException {
    // 错误码
    private Integer code;

    // 错误提示
    private String message;

    // 错误明细，内部调试错误
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {}

    public ServiceException(String message) {
        this.code = 500;
        this.message = message;
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}