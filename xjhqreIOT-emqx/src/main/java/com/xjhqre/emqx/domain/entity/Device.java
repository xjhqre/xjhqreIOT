package com.xjhqre.emqx.domain.entity;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.xjhqre.common.base.BaseEntity;
import com.xjhqre.common.group.Insert;
import com.xjhqre.common.group.Update;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备对象 iot_device
 * 
 * @author xjhqre
 * @since 2022-12-19
 */
@Data
@TableName("iot_device")
public class Device extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 设备ID */
    @ApiModelProperty(value = "设备ID", hidden = true)
    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty(value = "设备编号", hidden = true)
    private String deviceNumber;

    /** 设备名称 */
    @ApiModelProperty(value = "设备名称")
    @NotBlank
    private String deviceName;

    /**
     * 产品ID
     */
    @ApiModelProperty(value = "产品ID")
    @NotNull(groups = {Insert.class, Update.class})
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

    /** 用户昵称 */
    @ApiModelProperty(value = "用户昵称", hidden = true)
    private String userName;

    /**
     * 设备密钥
     */
    @ApiModelProperty(value = "设备密码", hidden = true)
    private String devicePassword;

    /** 固件版本 */
    @ApiModelProperty(value = "固件版本")
    private Double firmwareVersion;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty(value = "设备状态（1-未激活，2-禁用，3-在线，4-离线）", hidden = true)
    private Integer status;

    /**
     * wifi信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]）
     */
    @ApiModelProperty(value = "wifi信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]）",
        hidden = true)
    private Integer rssi;

    /**
     * 设备影子
     */
    @ApiModelProperty(value = "设备影子")
    // @NotNull(groups = {Insert.class, Update.class})
    private Integer isShadow;

    /**
     * 定位方式
     */
    @ApiModelProperty(value = "定位方式, 1=ip自动定位，2=设备上报定位，3=自定义")
    @NotNull(message = "定位方式不能为空")
    private Integer locationWay;

    /** 设备所在地址 */
    @ApiModelProperty(value = "设备所在地址")
    private String address;

    /** 设备入网IP */
    @ApiModelProperty(value = "设备入网IP", hidden = true)
    private String networkIp;

    /** 设备经度 */
    @ApiModelProperty(value = "设备经度")
    private Double longitude;

    /** 设备纬度 */
    @ApiModelProperty(value = "设备纬度")
    private Double latitude;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "激活时间", hidden = true)
    private Date activeTime;

    /** 最后上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "最后上线时间", hidden = true)
    private Date lastOnlineTime;

    /** 图片地址 */
    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    @ApiModelProperty(value = "升级状态(1：未升级，2：升级中，3：升级失败，4：升级成功)")
    private Integer upgradeStatus;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）", hidden = true, example = "0")
    private String delFlag;

    /**
     * 产品密钥
     */
    @TableField(exist = false)
    private String productKey;
}
