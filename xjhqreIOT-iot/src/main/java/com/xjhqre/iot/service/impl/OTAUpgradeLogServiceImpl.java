package com.xjhqre.iot.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.xjhqre.iot.domain.entity.Device;
import com.xjhqre.iot.domain.entity.OtaUpgradeLog;
import com.xjhqre.iot.mapper.OtaUpgradeLogMapper;
import com.xjhqre.iot.service.DeviceService;
import com.xjhqre.iot.service.OtaUpgradeLogService;

/**
 * AlertLogServiceImpl
 * 
 * @author xjhqre
 * @date 2023-01-2
 */
@Service
public class OTAUpgradeLogServiceImpl extends ServiceImpl<OtaUpgradeLogMapper, OtaUpgradeLog>
    implements OtaUpgradeLogService {

    @Resource
    private OtaUpgradeLogMapper otaUpgradeLogMapper;
    @Resource
    private DeviceService deviceService;

    @Override
    public IPage<OtaUpgradeLog> find(OtaUpgradeLog otaUpgradeLog, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<OtaUpgradeLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(otaUpgradeLog.getStatus() != null, OtaUpgradeLog::getStatus, otaUpgradeLog.getStatus()).eq(
            otaUpgradeLog.getDeviceName() != null && !"".equals(otaUpgradeLog.getDeviceName()),
            OtaUpgradeLog::getDeviceName, otaUpgradeLog.getDeviceName());
        return this.otaUpgradeLogMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
    }

    /**
     * 记录ota升级日志
     * 
     * @param deviceNum
     * @param message
     */
    @Override
    public void recordOtaLog(String deviceNum, String message) {
        Device device = this.deviceService.getByDeviceNumber(deviceNum);
        OtaUpgradeLog otaUpgradeLog = new OtaUpgradeLog();
        otaUpgradeLog.setDeviceId(device.getDeviceId());
        otaUpgradeLog.setDeviceName(device.getDeviceName());
        otaUpgradeLog.setProductId(device.getProductId());
        otaUpgradeLog.setProductName(device.getProductName());
        otaUpgradeLog.setStatus(0);
        otaUpgradeLog.setUpgradeTime(new Date());
        otaUpgradeLog.setCreateBy("");
        otaUpgradeLog.setCreateTime(new Date());
        otaUpgradeLog.setUpdateBy("");
        otaUpgradeLog.setUpdateTime(new Date());
        otaUpgradeLog.setRemark("");
        otaUpgradeLog.setParams(Maps.newHashMap());

    }
}
