package com.xjhqre.iot.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

/**
 * <p>
 * UpdateDeviceGroupsDTO
 * </p>
 *
 * @author xjhqre
 * @since 2月 27, 2023
 */
@Data
public class UpdateDeviceGroupsDTO {

    /**
     * 分组id
     */
    private Long groupId;

    /**
     * 设备id集合
     */
    @JsonAlias("deviceIds")
    private List<Long> deviceIdList;

}
