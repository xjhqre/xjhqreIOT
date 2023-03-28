package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * 物模型对象 iot_things_model
 * 
 * @author xjhqre
 * @since 2022-12-20
 */
@Data
@TableName(value = "iot_things_model_value", autoResultMap = true)
public class ThingsModelValue extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 物模型ID */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 物模型id
     */
    private Long modelId;

    /**
     * 物模型名称
     */
    private String modelName;

    /**
     * 物模型标识符
     */
    private String identifier;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 模型类别（1-属性，2-功能，3-事件）
     */
    private Integer type;

    /**
     * 物模型值
     */
    private String value;

    /**
     * 消息
     */
    private String message;

    // 单位
    @TableField(exist = false)
    private String unit;
}
