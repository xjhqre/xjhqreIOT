package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 物模型对象 iot_things_model
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Data
@TableName("iot_things_model")
public class ThingsModel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 物模型ID */
    @ApiModelProperty(name = "物模型ID")
    @TableId(value = "model_id", type = IdType.AUTO)
    private Long modelId;

    /** 物模型名称 */
    @ApiModelProperty(name = "物模型名称")
    private String modelName;

    /** 产品ID */
    @ApiModelProperty(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(name = "产品名称")
    private String productName;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty(name = "模型类别（1-属性，2-功能，3-事件）")
    private Integer type;

    /** 数据类型（integer、decimal、string、bool、array、enum） */
    @ApiModelProperty(name = "数据类型（integer、decimal、string、bool、array、enum）")
    private String datatype;

    /** 数据定义 */
    /*
        {
            "type": "",
            "min": "",
            "max": "",
            "unit": "",
            "step": "",
            "maxLength": "",
            "falseText": "",
            "trueText": "",
            "arrayType": "",
            "enumList": ""
        }
     */
    @ApiModelProperty(name = "数据定义")
    private String specs;

    /** 是否首页显示（0-否，1-是） */
    @ApiModelProperty(name = "是否首页显示（0-否，1-是）")
    private Integer isTop;

    /** 是否实时监测（0-否，1-是） */
    @ApiModelProperty(name = "是否实时监测（0-否，1-是）")
    private Integer isMonitor;
}
