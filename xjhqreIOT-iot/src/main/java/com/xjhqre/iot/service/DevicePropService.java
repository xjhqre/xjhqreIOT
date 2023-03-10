package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.DeviceProp;

/**
 * <p>
 * DevicePropService
 * </p>
 *
 * @author xjhqre
 * @since 2月 08, 2023
 */
public interface DevicePropService extends IService<DeviceProp> {
    void add(Long productId, Long deviceId, Long modelId, String value);

    List<DeviceProp> list(DeviceProp deviceProp);
}
