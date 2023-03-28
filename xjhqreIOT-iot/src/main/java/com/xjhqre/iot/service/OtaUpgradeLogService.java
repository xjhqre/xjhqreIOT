package com.xjhqre.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.OtaUpgradeLog;

/**
 * 设备告警Service接口
 * 
 * @author xjhqre
 * @date 2022-01-13
 */
public interface OtaUpgradeLogService extends IService<OtaUpgradeLog> {

    IPage<OtaUpgradeLog> find(OtaUpgradeLog otaUpgradeLog, Integer pageNum, Integer pageSize);

    void recordOtaLog(String deviceNum, String message);
}
