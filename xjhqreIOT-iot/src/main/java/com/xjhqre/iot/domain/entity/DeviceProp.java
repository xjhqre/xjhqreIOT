package com.xjhqre.iot.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 设备上报属性值记录表
 * </p>
 *
 * @author xjhqre
 * @since 2月 08, 2023
 */
@Data
@TableName("iot_device_prop")
public class DeviceProp extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private Long deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 物模型id
     */
    @ApiModelProperty(value = "物模型id")
    private Long modelId;

    /**
     * 物模型名称
     */
    @ApiModelProperty(value = "物模型名称")
    private String modelName;

    /**
     * 物模型标识符
     */
    @ApiModelProperty(value = "物模型标识符")
    private String identifier;

    /**
     * 数据类型（integer、decimal、string、bool、array、enum）
     */
    @ApiModelProperty(value = "数据类型（integer、decimal、string、bool、array、enum）")
    private String type;

    /**
     * 设备属性值
     */
    @ApiModelProperty(value = "设备属性值")
    private String value;

    /**
     * 是否只读
     */
    @ApiModelProperty(value = "是否只读")
    private Integer is_readonly;

    /**
     * 上报时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上报时间")
    private Date reportTime;
}
