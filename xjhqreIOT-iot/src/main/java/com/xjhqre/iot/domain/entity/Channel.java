package com.xjhqre.iot.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 视频通道
 * </p>
 *
 * @author xjhqre
 * @since 4月 11, 2023
 */
@Data
@TableName("iot_channel")
public class Channel extends BaseEntity {
    private static final long serialVersionUID = 3361549227370243305L;

    /**
     * 通道id
     */
    @ApiModelProperty(value = "通道id", hidden = true)
    @TableId(value = "channel_id", type = IdType.AUTO)
    private Long channelId;

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号", hidden = true)
    private Long deviceId;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称", hidden = true)
    private String deviceName;

    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号", hidden = true)
    private Long productId;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称", hidden = true)
    private String productName;

    /**
     * 推流码
     */
    @ApiModelProperty(value = "推流码", hidden = true)
    private String streamCode;

    /**
     * 设备状态
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "设备状态")
    private Integer status;
}
