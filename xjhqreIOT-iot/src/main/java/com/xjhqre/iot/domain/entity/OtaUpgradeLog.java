package com.xjhqre.iot.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xjhqre.common.base.BaseEntity;

import lombok.Data;

/**
 * 固件升级日志表
 *
 * @author xjhqre
 * @date 2022-01-13
 */
@Data
@TableName("iot_ota_upgrade_log")
public class OtaUpgradeLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 唯一识别id
     */
    private String uuid;

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 升级结果（1：成功，2：失败）
     */
    private Integer status;

    /**
     * 升级时间
     */
    private Date upgradeTime;
}
