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
    @ApiModelProperty(name = "设备ID")
    @TableId(value = "device_id", type = IdType.AUTO)
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty(name = "设备编号")
    private String deviceNumber;

    /** 设备名称 */
    @ApiModelProperty(name = "设备名称")
    private String deviceName;

    /** 产品ID */
    @ApiModelProperty(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty(name = "产品名称")
    private String productName;

    /** 用户ID */
    @ApiModelProperty(name = "用户ID")
    private Long userId;

    /** 用户昵称 */
    @ApiModelProperty(name = "用户昵称")
    private String userName;

    /**
     * 设备密钥
     */
    @ApiModelProperty(name = "设备密码")
    private String devicePassword;

    /** 固件版本 */
    @ApiModelProperty(name = "固件版本")
    private Double firmwareVersion;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty(name = "设备状态（1-未激活，2-禁用，3-在线，4-离线）")
    private Integer status;

    /** wifi信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]） */
    @ApiModelProperty(name = "wifi信号强度（信号极好4格[-55— 0]，信号好3格[-70— -55]，信号一般2格[-85— -70]，信号差1格[-100— -85]）")
    private Integer rssi;

    /** 设备影子 */
    @ApiModelProperty(name = "设备影子")
    private Integer isShadow;

    /**
     * 定位方式
     */
    @ApiModelProperty(name = "定位方式, 1=ip自动定位，2=设备上报定位，3=自定义")
    private Integer locationWay;

    @ApiModelProperty(name = "物模型")
    private String thingsModelValue;

    /** 设备所在地址 */
    @ApiModelProperty(name = "设备所在地址")
    private String address;

    /** 设备入网IP */
    @ApiModelProperty(name = "设备入网IP")
    private String networkIp;

    /** 设备经度 */
    @ApiModelProperty(name = "设备经度")
    private Double longitude;

    /** 设备纬度 */
    @ApiModelProperty(name = "设备纬度")
    private Double latitude;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(name = "激活时间")
    private Date activeTime;

    /** 设备摘要 **/
    @ApiModelProperty(name = "设备摘要")
    private String summary;

    /** 图片地址 */
    @ApiModelProperty(name = "图片地址")
    private String imgUrl;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;
}
