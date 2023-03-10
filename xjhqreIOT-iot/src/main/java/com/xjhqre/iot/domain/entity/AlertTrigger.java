package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

// 触发器类
@Data
@TableName("iot_alert_trigger")
public class AlertTrigger extends BaseEntity {

    @ApiModelProperty(value = "告警触发器id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "告警id")
    private Long alertId;

    @ApiModelProperty(value = "物模型id")
    private Long modelId;

    @ApiModelProperty(value = "符号")
    private String operator;

    @ApiModelProperty(value = "值")
    private String value;
}