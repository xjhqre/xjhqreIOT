package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.Firmware;

/**
 * FirmwareService
 * 
 * @author xjhqre
 * @date 2022-12-16
 */
public interface FirmwareService extends IService<Firmware> {

    /**
     * 产品固件分页列表
     */
    IPage<Firmware> find(Firmware firmware, Integer pageNum, Integer pageSize);

    /**
     * 产品固件列表
     */
    List<Firmware> list(Firmware firmware);

    Firmware getDetail(Long firmwareId);

    /**
     * 新增产品固件
     */
    void add(Firmware firmware);

    /**
     * 更新产品固件
     */
    void update(Firmware firmware);

    void delete(Long[] firmwareIds);

    /**
     * 查询固件涉及的设备
     * 
     * @param firmware
     * @param pageNum
     * @param pageSize
     * @return
     */
    List<Device> listDeviceByFirmwareId(Long firmwareId);
}
