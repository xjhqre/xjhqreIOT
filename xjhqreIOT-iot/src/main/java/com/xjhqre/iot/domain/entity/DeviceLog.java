package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备日志对象 iot_device_log
 *
 * @author xjhqre
 * @date 2022-01-13
 */
@Data
@TableName("iot_device_log")
public class DeviceLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 设备日志ID
     */
    @ApiModelProperty(value = "设备日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线）
     */
    @ApiModelProperty(value = "类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线）")
    private Integer logType;

    /**
     * 日志值
     */
    @ApiModelProperty(value = "日志值")
    private String message;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID")
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 设备ID
     */
    @ApiModelProperty(value = "设备ID")
    private Long deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 路由
     */
    @ApiModelProperty(value = "路由")
    private String router;

    /**
     * 服务质量
     */
    @ApiModelProperty(value = "服务质量")
    private Integer qos;
}
