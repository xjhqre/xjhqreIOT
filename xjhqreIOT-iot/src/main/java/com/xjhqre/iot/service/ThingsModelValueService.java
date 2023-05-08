package com.xjhqre.iot.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xjhqre.iot.domain.entity.ThingsModelValue;

/**
 * <p>
 * ThingsModelValueLogService
 * </p>
 *
 * @author xjhqre
 * @since 3月 13, 2023
 */
public interface ThingsModelValueService extends IService<ThingsModelValue> {

    /**
     * 查询设备物模型值列表
     * 
     * @param thingsModelValue
     * @return
     */
    List<ThingsModelValue> list(ThingsModelValue thingsModelValue, Integer dateRange);

    /**
     * 添加物模型值
     * 
     * @param productKey
     * @param deviceNum
     * @param message
     */
    void add(String productKey, String deviceNum, String message);

    /**
     * 获取物模型最新的值
     * 
     * @param modelId
     * @return
     */
    ThingsModelValue getNewValue(Long modelId);

    void deleteByDeviceId(Long deviceId);
}
