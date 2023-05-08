package com.xjhqre.iot.domain.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * <p>
 * UpgradeDeviceDTO
 * <p>
 *
 * @author xjhqre
 * @since 5月 06, 2023
 */
@Data
public class UpgradeDeviceDTO implements Serializable {
    private static final long serialVersionUID = 361233031912472227L;

    private Long deviceId;

    private Long firmwareId;

    /** 固件名称 */
    private String firmwareName;

    /**
     * oss唯一文件名
     */
    private String ossName;

    /** 产品ID */
    private Long productId;

    /** 产品名称 */
    private String productName;

    /** 固件版本 */
    private String version;

    /** 文件路径 */
    private String filePath;
}
