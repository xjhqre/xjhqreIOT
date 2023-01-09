package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xjhqre.iot.domain.entity.Firmware;

/**
 * FirmwareService
 * 
 * @author xjhqre
 * @date 2022-12-16
 */
public interface FirmwareService {

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
     * 获取设备最新固件
     */
    Firmware getLatest(Long deviceId);

    /**
     * 新增产品固件
     */
    void add(Firmware firmware);

    /**
     * 更新产品固件
     */
    void update(Firmware firmware);

    void delete(Long[] firmwareIds);
}
