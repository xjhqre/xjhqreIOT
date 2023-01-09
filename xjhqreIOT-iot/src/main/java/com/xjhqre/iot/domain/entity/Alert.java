package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * 设备告警设置
 * 
 * @author xjhqre
 * @date 2022-12-19
 */
@Data
@TableName("iot_alert")
public class Alert extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 告警ID */
    private Long alertId;

    /** 告警名称 */
    private String alertName;

    /** 告警级别（1=提醒通知，2=轻微问题，3=严重警告） */
    private Long alertLevel;

    /** 产品ID */
    private Long productId;

    /** 产品名称 */
    private String productName;

    /** 触发器 */
    private String triggers;

    /** 执行动作 */
    private String actions;

    /** 告警状态 （1-启动，2-停止） **/
    private Integer status;
}
