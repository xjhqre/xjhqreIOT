package com.xjhqre.iot.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xjhqre.common.exception.EmqxException;
import com.xjhqre.common.utils.AESUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Product;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.ToolService;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author xjhqre
 * @since 2023-1-5
 */
@Service
@Slf4j
public class ToolServiceImpl implements ToolService {

    @Resource
    private ProductService productService;
    @Resource
    private DeviceService deviceService;

    /**
     * 设备加密认证
     *
     */
    @Override
    public void verifyPassword(String productId, String deviceNumber, String username, String encryptPassword) {

        Product product = this.productService.getById(productId);
        String productSecret = product.getProductSecret();
        Device device = this.deviceService.getByDeviceNumber(deviceNumber);

        String decrypt = AESUtils.decrypt(encryptPassword, productSecret);
        if (decrypt == null || decrypt.equals("")) {
            throw new EmqxException("设备认证，设备密码解密失败");
        }
        String[] passwordArray = decrypt.split("&");
        if (passwordArray.length != 2) {
            // 密码加密格式 password & expireTime
            throw new EmqxException("设备认证，设备密码格式错误");
        }
        String decryptPassword = passwordArray[0];
        long expireTime = Long.parseLong(passwordArray[1]);
        // 验证密码
        if (!decryptPassword.equals(device.getDevicePassword())) {
            throw new EmqxException("设备认证，设备密码不匹配");
        }
        // 验证过期时间
        if (expireTime < System.currentTimeMillis()) {
            throw new EmqxException("设备认证，设备密码已过期");
        }
        // 设备状态验证 （1-未激活，2-禁用，3-在线，4-离线）
        if (device.getStatus() == 2) {
            throw new EmqxException("设备加密认证，设备处于禁用状态");
        }
        log.info("-----------设备加密认证成功,clientId: {}&{} ---------------", productId, deviceNumber);
    }
}
