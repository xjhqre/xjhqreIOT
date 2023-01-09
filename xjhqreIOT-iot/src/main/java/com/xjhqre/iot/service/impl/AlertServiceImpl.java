package com.xjhqre.iot.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Alert;
import com.xjhqre.iot.mapper.AlertMapper;
import com.xjhqre.iot.service.AlertService;

/**
 * 设备告警Service业务层处理
 * 
 * @author kerwincui
 * @date 2022-01-13
 */
@Service
public class AlertServiceImpl implements AlertService {
    @Resource
    private AlertMapper alertMapper;

    @Override
    public IPage<Alert> find(Alert alert, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(alert.getAlertLevel() != null, Alert::getAlertLevel, alert.getAlertLevel())
            .eq(alert.getAlertId() != null, Alert::getAlertId, alert.getAlertId())
            .eq(alert.getAlertName() != null, Alert::getAlertName, alert.getAlertName())
            .eq(alert.getStatus() != null, Alert::getStatus, alert.getStatus());
        return this.alertMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 获取产品告警设置详情
     *
     */
    @Override
    public Alert getDetail(Long alertId) {
        return this.alertMapper.selectById(alertId);
    }

    /**
     * 添加产品告警设置
     */
    @Override
    public void add(Alert alert) {
        alert.setCreateTime(DateUtils.getNowDate());
        alert.setCreateBy(SecurityUtils.getUsername());
        this.alertMapper.insert(alert);
    }

    /**
     * 修改产品告警设置
     *
     */
    @Override
    public void update(Alert alert) {
        alert.setUpdateTime(DateUtils.getNowDate());
        alert.setUpdateBy(SecurityUtils.getUsername());
        this.alertMapper.updateById(alert);
    }

    /**
     * 删除产品告警设置
     *
     */
    @Override
    public void delete(List<Long> alertIds) {
        this.alertMapper.deleteBatchIds(alertIds);
    }
}
