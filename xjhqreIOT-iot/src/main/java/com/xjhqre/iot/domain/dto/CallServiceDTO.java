package com.xjhqre.iot.domain.dto;

import lombok.Data;

/**
 * <p>
 * CallServiceDTO
 * <p>
 *
 * @author xjhqre
 * @since 5月 07, 2023
 */
@Data
public class CallServiceDTO {
    private String productKey;

    private String deviceNumber;

    private String message;
}
