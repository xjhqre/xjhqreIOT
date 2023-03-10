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
    @ApiModelProperty(value = "告警ID")
    @TableId(value = "alert_log_id", type = IdType.AUTO)
    private Long alertLogId;

    /** 告警名称 */
    @ApiModelProperty(value = "告警名称")
    private String alertName;

    /** 产品ID */
    @ApiModelProperty(value = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /** 设备ID */
    @ApiModelProperty(value = "设备ID")
    private Long deviceId;

    /** 设备名称 */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /** 用户ID */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /** 用户昵称 */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /**
     * 告警数据
     */
    @ApiModelProperty(value = "告警数据")
    private String data;
}
