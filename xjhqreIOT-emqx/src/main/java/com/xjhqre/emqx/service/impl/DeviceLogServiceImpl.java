package com.xjhqre.emqx.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.emqx.domain.entity.DeviceLog;
import com.xjhqre.emqx.mapper.DeviceLogMapper;
import com.xjhqre.emqx.service.DeviceLogService;

/**
 * 设备日志Service业务层处理
 * 
 * @author xjhqre
 * @since 2023-01-6
 */
@Service
public class DeviceLogServiceImpl extends ServiceImpl<DeviceLogMapper, DeviceLog> implements DeviceLogService {}
