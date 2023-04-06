package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品对象 iot_product
 * 
 * @author xjhqre
 * @since 2023-1-16
 */
@Data
@TableName("iot_product")
public class Product extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID", hidden = true)
    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID", hidden = true)
    private Long userId;

    /**
     * 产品键
     */
    @ApiModelProperty(value = "产品键", hidden = true)
    private String productKey;

    /**
     * 状态（1-未发布，2-已发布，不能修改）
     */
    @ApiModelProperty(value = "状态 1==未发布，2=已发布，不能修改", hidden = true)
    private Integer status;

    /**
     * 物模型Json，用户添加物模型后设置
     */
    @ApiModelProperty(value = "物模型Json", hidden = true)
    private String thingsModelsJson;

    /**
     * 联网方式（1=-wifi、2-蜂窝(2G/3G/4G/5G)、3-以太网、4-其他），用户输入
     */
    @ApiModelProperty(value = "联网方式（1=-wifi、2-蜂窝(2G/3G/4G/5G)、3-以太网、4-其他）")
    private Integer networkMethod;

    /**
     * 产品图片，用户上传添加
     */
    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}
