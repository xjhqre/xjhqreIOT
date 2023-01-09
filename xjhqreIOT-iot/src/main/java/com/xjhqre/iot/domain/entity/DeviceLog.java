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
 * @author kerwincui
 * @date 2022-01-13
 */
@Data
@TableName("iot_device_log")
public class DeviceLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 设备日志ID
     */
    @ApiModelProperty(name = "设备日志ID")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 物模型Id
     */
    @ApiModelProperty(name = "物模型Id")
    private Long modelId;

    /**
     * 类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线）
     */
    @ApiModelProperty(name = "类型（1=属性上报，2=事件上报，3=调用功能，4=设备升级，5=设备上线，6=设备离线）")
    private Integer logType;

    /**
     * 日志值
     */
    @ApiModelProperty(name = "日志值")
    private String logValue;

    /**
     * 设备ID
     */
    @ApiModelProperty(name = "设备ID")
    private Long deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(name = "设备名称")
    private String deviceName;

    /**
     * 设备编号
     */
    @ApiModelProperty(name = "设备编号")
    private String deviceNumber;

    /**
     * 是否监测数据（1=是，0=否）
     */
    @ApiModelProperty(name = "是否监测数据（1=是，0=否）")
    private Integer isMonitor;

    /**
     * 模式(1=影子模式，2=在线模式,3=其他)
     */
    @ApiModelProperty(name = "模式(1=影子模式，2=在线模式,3=其他)")
    private Integer mode;

    /**
     * 用户ID
     */
    @ApiModelProperty(name = "用户ID")
    private Long userId;

    /**
     * 用户昵称
     */
    @ApiModelProperty(name = "用户昵称")
    private String userName;
}
