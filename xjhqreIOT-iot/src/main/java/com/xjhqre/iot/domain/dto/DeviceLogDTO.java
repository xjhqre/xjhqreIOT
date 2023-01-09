package com.xjhqre.iot.domain.dto;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * DeviceLog
 * </p>
 *
 * @author xjhqre
 * @since 1月 02, 2023
 */
@Data
public class DeviceLogDTO {

    @ApiModelProperty(name = "设备日志ID")
    private Date ts;

    /** 查询用的开始时间 */
    @ApiModelProperty(name = "查询用的开始时间")
    private String beginTime;

    /** 查询用的结束时间 */
    @ApiModelProperty(name = "查询用的结束时间")
    private String endTime;

    /** 查询的总数 */
    @ApiModelProperty(name = "查询的总数")
    private int total;
}
