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
     * 在线设备数量
     */
    private int onlineDeviceCount;

    /**
     * 离线设备数量
     */
    private int OfflineDeviceCount;

    /**
     * 产品数量
     */
    private int productCount;

    /**
     * 已发布产品数量
     */
    private int publishedProductCount;

    /**
     * 未发布产品数量
     */
    private int unPublishedProductCount;

    /**
     * 今日告警日志数量
     */
    private long dayAlertLogCount;

    /**
     * 当月告警日志数量
     */
    private long monthAlertLogCount;

    /**
     * 告警配置数量
     */
    private long alertCount;

    /**
     * 正常告警配置数量
     */
    private int enableAlertCount;

    /**
     * 禁用告警配置数量
     */
    private int disableAlertCount;

    /**
     * 场景联动数量
     */
    private long sceneCount;

    /**
     * 正常场景联动
     */
    private int enableSceneCount;

    /**
     * 禁用场景联动
     */
    private int disableSceneCount;

}
