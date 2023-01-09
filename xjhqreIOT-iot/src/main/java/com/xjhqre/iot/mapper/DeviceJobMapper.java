package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.DeviceJob;

/**
 * 调度任务信息 数据层
 * 
 * @author kerwincui
 */
@Mapper
public interface DeviceJobMapper extends BaseMapper<DeviceJob> {

}
