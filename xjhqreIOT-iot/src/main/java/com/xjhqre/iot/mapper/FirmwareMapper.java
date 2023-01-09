package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.Firmware;

/**
 * 产品固件Mapper接口
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Mapper
public interface FirmwareMapper extends BaseMapper<Firmware> {

    /**
     * 查询设备最新固件
     *
     * @param deviceId
     *            产品固件主键
     * @return 产品固件
     */
    Firmware selectLatestFirmware(Long deviceId);
}
