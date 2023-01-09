package com.xjhqre.iot.domain.model;

import lombok.Data;

/**
 * 设备统计属性
 * 
 * @author xjhqre
 * @date 2023-1-2
 */
@Data
public class DeviceStatistic {

    /**
     * 设备数量
     */
    private int deviceCount;

    /**
     * 产品数量
     */
    private int productCount;

    /**
     * 告警数量
     */
    private long alertCount;

    /**
     * 属性上报
     */
    private long propertyCount;

    /**
     * 功能上报
     */
    private long functionCount;

    /**
     * 事件上报
     */
    private long eventCount;

    /**
     * 监测数据上报
     */
    private long monitorCount;
}
