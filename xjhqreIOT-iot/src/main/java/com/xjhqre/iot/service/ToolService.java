package com.xjhqre.iot.service;

/**
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
public interface ToolService {

    /**
     * 设备加密认证
     */
    void verifyPassword(String productId, String deviceNumber, String username, String password);
}
