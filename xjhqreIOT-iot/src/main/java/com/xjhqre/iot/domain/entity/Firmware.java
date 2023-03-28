package com.xjhqre.iot.domain.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.common.group.Insert;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 产品固件对象 iot_firmware
 * 
 * @author xjhqre
 * @date 2021-12-16
 */
@Data
@TableName("iot_firmware")
public class Firmware extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 固件ID */
    @ApiModelProperty(value = "固件ID")
    @TableId(value = "firmware_id", type = IdType.AUTO)
    private Long firmwareId;

    /** 固件名称 */
    @ApiModelProperty(value = "固件名称")
    @NotBlank(groups = Insert.class)
    private String firmwareName;

    /**
     * oss唯一文件名
     */
    @ApiModelProperty(value = "oss唯一文件名")
    private String ossName;

    /** 产品ID */
    @ApiModelProperty(value = "产品ID")
    @NotNull(groups = Insert.class)
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /** 固件版本 */
    @ApiModelProperty(value = "固件版本")
    @NotBlank(groups = Insert.class)
    private String version;

    /** 文件路径 */
    @ApiModelProperty(value = "文件路径")
    private String filePath;
}
