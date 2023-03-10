package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.DeviceLog;

/**
 * 设备日志Service接口
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
public interface DeviceLogService extends IService<DeviceLog> {

    /**
     * 分页查询设备日志列表
     */
    IPage<DeviceLog> find(DeviceLog deviceLog, Integer pageNum, Integer pageSize);

    /**
     * 获取设备日志详情
     */
    DeviceLog getDetail(Long logId);

    /**
     * 新增设备日志
     */
    void add(DeviceLog deviceLog);

    /**
     * 修改设备日志
     *
     */
    void update(DeviceLog deviceLog);

    /**
     * 批量删除设备日志
     *
     */
    void delete(List<Long> logIds);

    /**
     * 根据设备编号批量删除设备日志
     *
     * @param deviceNumber
     *            需要删除的设备日志ID
     * @return 结果
     */
    void deleteDeviceLogByDeviceId(Long deviceId);
}
