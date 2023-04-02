package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.AlertLog;

/**
 * 设备告警Service接口
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
public interface AlertLogService extends IService<AlertLog> {

    /**
     * 分页查询设备告警日志列表
     *
     */
    IPage<AlertLog> find(AlertLog alertLog, Integer pageNum, Integer pageSize);

    /**
     * 获取设备告警详细信息
     *
     */
    AlertLog getDetail(Long alertLogId);

    /**
     * 新增设备告警
     *
     */
    void add(AlertLog alertLog);

    /**
     * 修改设备告警
     *
     */
    void update(AlertLog alertLog);

    /**
     * 批量删除设备告警
     *
     */
    void delete(List<Long> alertLogIds);

    /**
     * 根据设备ID删除告警日志
     */
    void deleteByDeviceId(Long deviceId);

    List<AlertLog> getNewAlertLogList();

    /**
     * 获取今天的日志数量
     * 
     * @return
     */
    int getTodayLogCount();

    /**
     * 获取当月日志数量
     * 
     * @return
     */
    int getMonthLogCount();

}
