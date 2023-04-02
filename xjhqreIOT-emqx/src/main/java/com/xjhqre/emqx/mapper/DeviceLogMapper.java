package com.xjhqre.emqx.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.emqx.domain.entity.DeviceLog;

/**
 * 设备日志Mapper接口
 *
 * @author xjhqre
 * @date 2022-01-13
 */
@Mapper
public interface DeviceLogMapper extends BaseMapper<DeviceLog> {

}
