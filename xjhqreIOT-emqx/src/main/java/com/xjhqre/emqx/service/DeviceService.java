package com.xjhqre.emqx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.emqx.domain.entity.Device;

/**
 * DeviceService
 *
 * @author xjhqre
 * @date 2022-12-19
 */
public interface DeviceService extends IService<Device> {

    /**
     * 根据设备编号查询设备
     *
     * @param deviceNumber
     *            设备主键
     * @return 设备
     */
    Device getByDeviceNumber(String deviceNumber);

    /**
     * 更新设备状态和定位
     *
     * @param device
     *            设备
     * @return 结果
     */
    void updateDeviceStatusAndLocation(Device device, String ipAddress);

}
