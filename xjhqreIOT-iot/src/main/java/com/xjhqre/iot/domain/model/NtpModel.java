package com.xjhqre.iot.domain.model;

import lombok.Data;

/**
 * ntp时间模型，设备端收到服务端的时间记为${deviceRecvTime}，则设备上的精确时间为：(${serverRecvTime}+${serverSendTime}+${deviceRecvTime}-${deviceSendTime})/2。
 * 
 * @author xjhqre
 * @date 2023-1-3
 */
@Data
public class NtpModel {
    /**
     * 设备发送时间
     */
    private Long deviceSendTime;

    /**
     * 服务器接收时间
     */
    private Long serverRecvTime;

    /**
     * 服务器发送时间
     */
    private Long serverSendTime;
}
