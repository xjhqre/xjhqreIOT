package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.iot.domain.model.DataType;
import com.xjhqre.iot.mybatisConfig.DataTypeTypeHandler;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * ModelParam
 * </p>
 *
 * @author xjhqre
 * @since 2月 03, 2023
 */
@Data
@TableName(value = "iot_model_param", autoResultMap = true)
public class ModelParam extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 参数id
     */
    @ApiModelProperty(value = "参数id")
    @TableId(value = "param_id", type = IdType.AUTO)
    private Long paramId;

    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称")
    @JsonAlias("name")
    private String paramName;

    /**
     * 对应物模型id
     */
    @ApiModelProperty(value = "对应物模型id")
    private Long modelId;

    /**
     * 物模型名称
     */
    @ApiModelProperty(value = "物模型名称")
    private String modelName;

    /**
     * 标识符（物模型下唯一）
     */
    @ApiModelProperty(value = "标识符（物模型下唯一）")
    private String identifier;

    /**
     * 产品id
     */
    @ApiModelProperty(value = "产品id")
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 参数类型（0：入参 1：出参）
     */
    @ApiModelProperty(value = "参数类型（0：入参 1：出参）")
    private Integer type;

    /**
     * 数据定义
     */
    @ApiModelProperty(value = "数据定义")
    @TableField(typeHandler = DataTypeTypeHandler.class)
    private DataType dataType;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

}
