package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备告警日志
 * 
 * @author xjhqre
 * @date 2022-12-1349
 */
@Data
@TableName("iot_alert_log")
public class AlertLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 告警ID */
    @ApiModelProperty(name = "告警ID")
    @TableId(value = "alert_log_id", type = IdType.AUTO)
    private Long alertLogId;

    /** 告警名称 */
    @ApiModelProperty(name = "告警名称")
    private String alertName;

    /** 告警级别（1=提醒通知，2=轻微问题，3=严重警告，4=场景联动） */
    @ApiModelProperty(name = "告警级别（1=提醒通知，2=轻微问题，3=严重警告，4=场景联动）")
    private Long alertLevel;

    /** 处理状态(0=不需要处理,1=未处理,2=已处理) */
    @ApiModelProperty(name = "处理状态(0=不需要处理,1=未处理,2=已处理)")
    private Long status;

    /** 产品ID */
    @ApiModelProperty(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(name = "产品名称")
    private String productName;

    /** 设备ID */
    @ApiModelProperty(name = "设备ID")
    private Long deviceId;

    /** 设备名称 */
    @ApiModelProperty(name = "设备名称")
    private String deviceName;

    /** 用户ID */
    @ApiModelProperty(name = "用户ID")
    private Long userId;

    /** 用户昵称 */
    @ApiModelProperty(name = "用户昵称")
    private String userName;
}
