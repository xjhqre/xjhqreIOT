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
 * @author kerwincui
 * @date 2021-12-16
 */
@Data
@TableName("iot_product")
public class Product extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @ApiModelProperty(name = "产品ID")
    @TableId(value = "product_id", type = IdType.AUTO)
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(name = "产品名称")
    private String productName;

    /**
     * 用户ID
     */
    @ApiModelProperty(name = "用户ID")
    private Long userId;

    /**
     * 是否启用授权码（0-否，1-是），用户输入
     */
    @ApiModelProperty(name = "是否启用授权码")
    private Integer isAuthorize;

    /** 产品秘钥 */
    @ApiModelProperty(name = "产品秘钥")
    private String productSecret;

    /** 状态（1-未发布，2-已发布，不能修改） */
    @ApiModelProperty(name = "状态 1==未发布，2=已发布，不能修改")
    private Integer status;

    /**
     * 物模型Json，用户添加物模型后设置
     */
    @ApiModelProperty(name = "物模型Json")
    private String thingsModelsJson;

    /**
     * 设备类型（1-直连设备、2-网关子设备、3-网关设备），用户输入
     */
    @ApiModelProperty(name = "设备类型（1-直连设备、2-网关子设备、3-网关设备）")
    private Integer deviceType;

    /**
     * 联网方式（1=-wifi、2-蜂窝(2G/3G/4G/5G)、3-以太网、4-其他），用户输入
     */
    @ApiModelProperty(name = "联网方式（1=-wifi、2-蜂窝(2G/3G/4G/5G)、3-以太网、4-其他）")
    private Integer networkMethod;

    /**
     * 产品图片，用户上传添加
     */
    @ApiModelProperty(name = "图片地址")
    private String imgUrl;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}
