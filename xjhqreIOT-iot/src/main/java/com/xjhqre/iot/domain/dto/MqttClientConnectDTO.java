package com.xjhqre.iot.domain.dto;

import lombok.Data;

/**
 * 客户端连接模型
 * 
 * @author kerwincui
 * @date 2021-12-16
 */
@Data
public class MqttClientConnectDTO {
    /** 事件名称（固定为："client_connected" "client_disconnected"） **/
    private String action;

    /** 客户端 ClientId **/
    private String clientId;

    /** 客户端 Username，不存在时该值为 "undefined" **/
    private String username;

    /** 客户端源 IP 地址 **/
    private String ipaddress;

    /** 客户端申请的心跳保活时间 **/
    private Integer keepalive;

    /** 协议版本号 **/
    private Integer proto_ver;

    /** 时间戳(秒) **/
    private Long connected_at;

    /** 错误原因 **/
    private String reason;
}
