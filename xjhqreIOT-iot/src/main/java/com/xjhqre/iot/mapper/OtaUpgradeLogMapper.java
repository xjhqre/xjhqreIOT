package com.xjhqre.iot.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjhqre.iot.domain.entity.OtaUpgradeLog;

/**
 * 设备告警Mapper接口
 * 
 * @author xjhqre
 * @since 2023-01-6
 */
@Mapper
public interface OtaUpgradeLogMapper extends BaseMapper<OtaUpgradeLog> {}
