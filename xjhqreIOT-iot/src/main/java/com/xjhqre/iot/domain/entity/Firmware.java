package com.xjhqre.iot.domain.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品固件对象 iot_firmware
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Data
@TableName("iot_firmware")
public class Firmware extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 固件ID */
    @ApiModelProperty(name = "固件ID")
    @TableId(value = "firmware_id", type = IdType.AUTO)
    private Long firmwareId;

    /** 固件名称 */
    @ApiModelProperty(name = "固件名称")
    private String firmwareName;

    /** 产品ID */
    @ApiModelProperty(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(name = "产品名称")
    private String productName;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty(name = "是否系统通用（0-否，1-是）")
    private Integer isSys;

    /** 是否最新版（0-否，1-是） */
    @ApiModelProperty(name = "是否最新版（0-否，1-是）")
    private Integer isLatest;

    /** 固件版本 */
    @ApiModelProperty(name = "固件版本")
    private BigDecimal version;

    /** 文件路径 */
    @ApiModelProperty(name = "文件路径")
    private String filePath;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}
