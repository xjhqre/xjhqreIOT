package com.xjhqre.iot.domain.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 设备分组关联对象
 * 
 * @author xjhqre
 * @since 2023-1-7
 */
@Data
public class DeviceGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 分组ID */
    private Long groupId;

    /** 设备ID */
    private Long deviceId;
}
