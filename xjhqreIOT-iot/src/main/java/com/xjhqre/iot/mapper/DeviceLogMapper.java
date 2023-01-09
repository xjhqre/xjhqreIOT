package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.DeviceLog;

/**
 * 设备日志Mapper接口
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Mapper
public interface DeviceLogMapper extends BaseMapper<DeviceLog> {

}
