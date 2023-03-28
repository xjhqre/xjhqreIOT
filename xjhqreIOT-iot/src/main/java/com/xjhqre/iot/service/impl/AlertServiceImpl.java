package com.xjhqre.iot.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjhqre.common.utils.DateUtils;
import com.xjhqre.common.utils.SecurityUtils;
import com.xjhqre.iot.domain.entity.Alert;
import com.xjhqre.iot.domain.entity.AlertTrigger;
import com.xjhqre.iot.mapper.AlertMapper;
import com.xjhqre.iot.service.AlertService;
import com.xjhqre.iot.service.AlertTriggerService;

/**
 * 设备告警Service业务层处理
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AlertServiceImpl extends ServiceImpl<AlertMapper, Alert> implements AlertService {
    @Resource
    private AlertMapper alertMapper;
    @Resource
    private AlertTriggerService alertTriggerService;

    @Override
    public IPage<Alert> find(Alert alert, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(alert.getAlertId() != null, Alert::getAlertId, alert.getAlertId())
            .eq(alert.getAlertName() != null && !"".equals(alert.getAlertName()), Alert::getAlertName,
                alert.getAlertName())
            .eq(alert.getProductId() != null, Alert::getProductId, alert.getProductId())
            .eq(alert.getStatus() != null, Alert::getStatus, alert.getStatus());
        return this.alertMapper.selectPage(new Page<>(pageNum, pageSize), wrapper).convert(alert1 -> {
            Long alertId = alert1.getAlertId();
            LambdaQueryWrapper<AlertTrigger> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(AlertTrigger::getAlertId, alertId);
            List<AlertTrigger> alertTriggers = this.alertTriggerService.list(wrapper1);
            alert1.setTriggers(alertTriggers);
            return alert1;
        });
    }

    /**
     * 获取产品告警设置详情
     *
     */
    @Override
    public Alert getDetail(Long alertId) {
        Alert alert = this.alertMapper.selectById(alertId);
        LambdaQueryWrapper<AlertTrigger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertTrigger::getAlertId, alert.getAlertId());
        List<AlertTrigger> alertTriggers = this.alertTriggerService.list(wrapper);
        alert.setTriggers(alertTriggers);
        return alert;
    }

    /**
     * 根据 productId 查询告警
     * 
     * @param productId
     * @return
     */
    @Override
    public Alert getByProductId(Long productId) {
        LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Alert::getProductId, productId).eq(Alert::getStatus, 1);
        Alert alert = this.alertMapper.selectOne(wrapper);
        LambdaQueryWrapper<AlertTrigger> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(AlertTrigger::getAlertId, alert.getAlertId());
        List<AlertTrigger> alertTriggers = this.alertTriggerService.list(wrapper1);
        alert.setTriggers(alertTriggers);
        return alert;
    }

    /**
     * 添加产品告警设置
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(Alert alert) {
        alert.setCreateTime(DateUtils.getNowDate());
        alert.setCreateBy(SecurityUtils.getUsername());
        this.alertMapper.insert(alert);
        for (AlertTrigger trigger : alert.getTriggers()) {
            trigger.setAlertId(alert.getAlertId());
            trigger.setCreateBy(SecurityUtils.getUsername());
            trigger.setCreateTime(DateUtils.getNowDate());
        }
        this.alertTriggerService.saveBatch(alert.getTriggers());
    }

    /**
     * 修改产品告警设置
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Alert alert) {
        // 先更新告警
        alert.setUpdateTime(DateUtils.getNowDate());
        alert.setUpdateBy(SecurityUtils.getUsername());
        this.alertMapper.updateById(alert);

        // 更新触发器，先删后增
        LambdaQueryWrapper<AlertTrigger> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlertTrigger::getAlertId, alert.getAlertId());
        this.alertTriggerService.remove(wrapper);
        List<AlertTrigger> alertTriggers = alert.getTriggers().stream().peek(alertTrigger -> {
            alertTrigger.setAlertId(alert.getAlertId());
            alertTrigger.setCreateTime(DateUtils.getNowDate());
            alertTrigger.setCreateBy(SecurityUtils.getUsername());
        }).collect(Collectors.toList());
        this.alertTriggerService.saveBatch(alertTriggers);
    }

    /**
     * 删除产品告警设置
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> alertIds) {
        // 先删除触发器
        for (Long alertId : alertIds) {
            LambdaQueryWrapper<AlertTrigger> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(AlertTrigger::getAlertId, alertId);
            this.alertTriggerService.remove(wrapper);
        }
        // 删除告警
        this.alertMapper.deleteBatchIds(alertIds);
    }

    /**
     * 根据产品id删除告警规则
     * 
     * @param productIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByProductIds(Long[] productIds) {
        for (Long productId : productIds) {
            LambdaQueryWrapper<Alert> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Alert::getProductId, productId);
            List<Long> alertIds =
                this.alertMapper.selectList(wrapper).stream().map(Alert::getAlertId).collect(Collectors.toList());
            // 先删除触发器
            for (Long alertId : alertIds) {
                LambdaQueryWrapper<AlertTrigger> wrapper1 = new LambdaQueryWrapper<>();
                wrapper1.eq(AlertTrigger::getAlertId, alertId);
                this.alertTriggerService.remove(wrapper1);
            }
            // 删除告警
            this.alertMapper.deleteBatchIds(alertIds);
        }
    }
}
