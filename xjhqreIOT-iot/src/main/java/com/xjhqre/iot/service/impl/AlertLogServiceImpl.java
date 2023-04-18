package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.AlertLog;
import com.xjhqre.iot.mapper.AlertLogMapper;
import com.xjhqre.iot.service.AlertLogService;

/**
 * AlertLogServiceImpl
 * 
 * @author xjhqre
 * @date 2023-01-2
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AlertLogServiceImpl extends ServiceImpl<AlertLogMapper, AlertLog> implements AlertLogService {

    @Resource
    private AlertLogMapper alertLogMapper;

    /**
     * 分页查询设备告警日志列表
     */
    @Override
    public IPage<AlertLog> find(AlertLog alertLog, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AlertLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(alertLog.getAlertLogId() != null, AlertLog::getAlertLogId, alertLog.getAlertLogId())
            .like(alertLog.getAlertName() != null && !"".equals(alertLog.getAlertName()), AlertLog::getAlertName,
                alertLog.getAlertName())
            .like(alertLog.getProductName() != null && !"".equals(alertLog.getProductName()), AlertLog::getProductName,
                alertLog.getProductName())
            .like(alertLog.getDeviceName() != null && !"".equals(alertLog.getDeviceName()), AlertLog::getDeviceName,
                alertLog.getDeviceName())
            .eq(alertLog.getProductId() != null, AlertLog::getProductId, alertLog.getProductId());
        return this.alertLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取设备告警详细信息
     *
     */
    @Override
    public AlertLog getDetail(Long alertLogId) {
        return this.alertLogMapper.selectById(alertLogId);
    }

    /**
     * 新增设备告警
     *
     */
    @Override
    public void add(AlertLog alertLog) {
        alertLog.setCreateTime(DateUtils.getNowDate());
        alertLog.setCreateBy(SecurityUtils.getUsername());
        this.alertLogMapper.insert(alertLog);
    }

    /**
     * 修改设备告警
     *
     */
    @Override
    public void update(AlertLog alertLog) {
        alertLog.setUpdateTime(DateUtils.getNowDate());
        alertLog.setUpdateBy(SecurityUtils.getUsername());
        this.alertLogMapper.updateById(alertLog);
    }

    /**
     * 批量删除设备告警
     *
     */
    @Override
    public void delete(List<Long> alertLogIds) {
        this.alertLogMapper.deleteBatchIds(alertLogIds);
    }

    /**
     * 根据设备ID删除告警日志
     */
    @Override
    public void deleteByDeviceId(Long deviceId) {
        LambdaQueryWrapper<AlertLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertLog::getDeviceId, deviceId);
        this.alertLogMapper.delete(wrapper);
    }

    /**
     * 获取最新的三个告警日志
     * 
     * @return
     */
    @Override
    public List<AlertLog> getNewAlertLogList() {
        return this.alertLogMapper.getNewAlertLogList();
    }

    /**
     * 获取今天的日志数量
     * 
     * @return
     */
    @Override
    public int getTodayLogCount() {
        return this.alertLogMapper.getTodayLogCount();
    }

    /**
     * 获取当月日志数量
     * 
     * @return
     */
    @Override
    public int getMonthLogCount() {
        return this.alertLogMapper.getMonthLogCount();
    }
}
