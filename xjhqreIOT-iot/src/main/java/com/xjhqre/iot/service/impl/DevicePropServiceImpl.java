package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.DeviceProp;
import com.xjhqre.iot.domain.entity.ThingsModel;
import com.xjhqre.iot.mapper.DevicePropMapper;
import com.xjhqre.iot.service.DevicePropService;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.ProductService;
import com.xjhqre.iot.service.ThingsModelService;

/**
 * <p>
 * DevicePropServiceImpl
 * </p>
 *
 * @author xjhqre
 * @since 2月 08, 2023
 */
@Service
public class DevicePropServiceImpl extends ServiceImpl<DevicePropMapper, DeviceProp> implements DevicePropService {

    @Resource
    ProductService productService;
    @Resource
    DeviceService deviceService;
    @Resource
    ThingsModelService thingsModelService;
    @Resource
    DevicePropMapper devicePropMapper;

    @Override
    public List<DeviceProp> list(DeviceProp deviceProp) {
        LambdaQueryWrapper<DeviceProp> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(deviceProp.getDeviceId() != null, DeviceProp::getDeviceId, deviceProp.getDeviceId())
            .eq(deviceProp.getModelId() != null, DeviceProp::getModelId, deviceProp.getModelId());
        return this.devicePropMapper.selectList(wrapper);
    }

    @Override
    public void add(Long productId, Long deviceId, Long modelId, String value) {
        ThingsModel thingsModel = this.thingsModelService.getById(modelId);
        Device device = this.deviceService.getById(deviceId);
        DeviceProp deviceProp = new DeviceProp();
        deviceProp.setDeviceId(deviceId);
        deviceProp.setDeviceName(device.getDeviceName());
        deviceProp.setModelId(modelId);
        deviceProp.setModelName(thingsModel.getModelName());
        deviceProp.setIdentifier(thingsModel.getIdentifier());
        deviceProp.setType("integer");
        deviceProp.setValue(value);
        deviceProp.setIs_readonly(thingsModel.getIsReadonly());
        deviceProp.setReportTime(DateUtils.getNowDate());
        deviceProp.setCreateBy("admin");
        deviceProp.setCreateTime(DateUtils.getNowDate());
        this.devicePropMapper.insert(deviceProp);
    }
}
