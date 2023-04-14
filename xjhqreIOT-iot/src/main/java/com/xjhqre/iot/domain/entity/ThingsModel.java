package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.iot.domain.model.DataType;
import com.xjhqre.iot.mybatisConfig.DataTypeTypeHandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 物模型对象 iot_things_model
 * 
 * @author xjhqre
 * @since 2022-12-20
 */
@Data
@TableName(value = "iot_things_model", autoResultMap = true)
public class ThingsModel extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 物模型ID */
    @ApiModelProperty(value = "物模型ID")
    @TableId(value = "model_id", type = IdType.AUTO)
    private Long modelId;

    /** 物模型名称 */
    @ApiModelProperty(value = "物模型名称")
    private String modelName;

    @ApiModelProperty(value = "标识符（产品下唯一）")
    private String identifier;

    /** 产品ID */
    @ApiModelProperty(value = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty(value = "模型类别（1-属性，2-服务，3-事件）")
    private Integer type;

    /**
     * 事件类型（1-信息，2-告警，3-故障）
     */
    @ApiModelProperty(value = "事件类型（1-信息，2-告警，3-故障）")
    private Integer eventType;

    /** 数据定义 */
    /*
    {
        "type":"",
        "specs":{
            "min":"",
            "max":"",
            "unit":"",
            "step":"",
            "maxLength":"",
            "falseText":"",
            "trueText":"",
            "arrayType":"",
            "enumList":""
        }
    }
     */
    @ApiModelProperty(value = "数据定义")
    @TableField(typeHandler = DataTypeTypeHandler.class)
    private DataType dataType;

    @ApiModelProperty(value = "入参定义")
    @TableField(typeHandler = DataTypeTypeHandler.class)
    private DataType inputParam;

    @ApiModelProperty(value = "出参定义")
    @TableField(typeHandler = DataTypeTypeHandler.class)
    private DataType outputParam;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    // 最后一次值
    @TableField(exist = false)
    private String lastValue;
}
