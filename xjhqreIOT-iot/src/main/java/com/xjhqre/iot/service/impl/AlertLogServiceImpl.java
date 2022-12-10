package com.xjhqre.iot.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.iot.domain.AlertLog;
import com.xjhqre.iot.mapper.AlertLogMapper;
import com.xjhqre.iot.service.IAlertLogService;

/**
 * 设备告警Service业务层处理
 * 
 * @author kerwincui
 * @date 2022-01-13
 */
@Service
public class AlertLogServiceImpl implements IAlertLogService {
    @Autowired
    private AlertLogMapper alertLogMapper;

    /**
     * 查询设备告警
     * 
     * @param alertLogId
     *            设备告警主键
     * @return 设备告警
     */
    @Override
    public AlertLog selectAlertLogByAlertLogId(Long alertLogId) {
        return this.alertLogMapper.selectAlertLogByAlertLogId(alertLogId);
    }

    /**
     * 查询设备告警列表
     * 
     * @param alertLog
     *            设备告警
     * @return 设备告警
     */
    @Override
    public List<AlertLog> selectAlertLogList(AlertLog alertLog) {
        return this.alertLogMapper.selectAlertLogList(alertLog);
    }

    /**
     * 新增设备告警
     * 
     * @param alertLog
     *            设备告警
     * @return 结果
     */
    @Override
    public int insertAlertLog(AlertLog alertLog) {
        alertLog.setCreateTime(DateUtils.getNowDate());
        return this.alertLogMapper.insertAlertLog(alertLog);
    }

    /**
     * 修改设备告警
     * 
     * @param alertLog
     *            设备告警
     * @return 结果
     */
    @Override
    public int updateAlertLog(AlertLog alertLog) {
        alertLog.setUpdateTime(DateUtils.getNowDate());
        return this.alertLogMapper.updateAlertLog(alertLog);
    }

    /**
     * 批量删除设备告警
     * 
     * @param alertLogIds
     *            需要删除的设备告警主键
     * @return 结果
     */
    @Override
    public int deleteAlertLogByAlertLogIds(Long[] alertLogIds) {
        return this.alertLogMapper.deleteAlertLogByAlertLogIds(alertLogIds);
    }

    /**
     * 删除设备告警信息
     * 
     * @param alertLogId
     *            设备告警主键
     * @return 结果
     */
    @Override
    public int deleteAlertLogByAlertLogId(Long alertLogId) {
        return this.alertLogMapper.deleteAlertLogByAlertLogId(alertLogId);
    }
}
