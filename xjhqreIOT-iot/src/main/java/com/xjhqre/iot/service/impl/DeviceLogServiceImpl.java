package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.DeviceLog;
import com.xjhqre.iot.mapper.DeviceLogMapper;
import com.xjhqre.iot.service.DeviceLogService;

/**
 * 设备日志Service业务层处理
 * 
 * @author xjhqre
 * @since 2023-01-6
 */
@Service
public class DeviceLogServiceImpl extends ServiceImpl<DeviceLogMapper, DeviceLog> implements DeviceLogService {
    @Resource
    private DeviceLogMapper deviceLogMapper;

    /**
     * 分页查询设备日志列表
     */
    @Override
    public IPage<DeviceLog> find(DeviceLog deviceLog, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<DeviceLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(deviceLog.getDeviceNumber() != null, DeviceLog::getDeviceNumber, deviceLog.getDeviceNumber())
            .eq(deviceLog.getDeviceId() != null, DeviceLog::getDeviceId, deviceLog.getDeviceId())
            .like(deviceLog.getDeviceName() != null, DeviceLog::getDeviceName, deviceLog.getDeviceName())
            .eq(deviceLog.getUserId() != null, DeviceLog::getUserId, deviceLog.getUserId())
            .like(deviceLog.getUserName() != null, DeviceLog::getUserName, deviceLog.getUserName())
            .eq(deviceLog.getModelId() != null, DeviceLog::getModelId, deviceLog.getModelId())
            .eq(deviceLog.getMode() != null, DeviceLog::getMode, deviceLog.getMode())
            .eq(deviceLog.getLogType() != null, DeviceLog::getLogType, deviceLog.getLogType());
        return this.deviceLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取设备日志详情
     *
     */
    @Override
    public DeviceLog getDetail(Long logId) {
        return this.deviceLogMapper.selectById(logId);
    }

    /**
     * 新增设备日志
     *
     */
    @Override
    public void add(DeviceLog deviceLog) {
        deviceLog.setCreateBy(SecurityUtils.getUsername());
        deviceLog.setCreateTime(DateUtils.getNowDate());
        this.deviceLogMapper.insert(deviceLog);
    }

    /**
     * 修改设备日志
     *
     */
    @Override
    public void update(DeviceLog deviceLog) {
        deviceLog.setUpdateBy(SecurityUtils.getUsername());
        deviceLog.setUpdateTime(DateUtils.getNowDate());
        this.deviceLogMapper.updateById(deviceLog);
    }

    /**
     * 批量删除设备日志
     *
     */
    @Override
    public void delete(List<Long> logIds) {
        this.deviceLogMapper.deleteBatchIds(logIds);
    }

    /**
     * 根据设备Ids批量删除设备日志
     *
     */
    @Override
    public void deleteDeviceLogByDeviceNumber(String deviceNumber) {
        LambdaQueryWrapper<DeviceLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DeviceLog::getDeviceNumber, deviceNumber);
        this.deviceLogMapper.delete(wrapper);
    }
}
