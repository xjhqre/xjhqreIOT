package com.xjhqre.common.exception;

import com.xjhqre.common.constant.HttpStatus;

import lombok.Data;

/**
 * <p>
 * MqttException
 * </p>
 *
 * @author xjhqre
 * @since 1月 04, 2023
 */
@Data
public class EmqxException extends RuntimeException {
    // 错误码
    private Integer code;

    // 错误提示
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public EmqxException() {}

    public EmqxException(String message) {
        this.code = HttpStatus.ERROR;
        this.message = message;
    }

    public EmqxException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
