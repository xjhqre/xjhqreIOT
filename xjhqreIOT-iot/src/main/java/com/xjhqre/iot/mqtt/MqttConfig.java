package com.xjhqre.iot.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * @Classname MqttConfig
 * @Description mqtt配置信息
 * @author kerwincui
 */
@Component
@ConfigurationProperties("spring.mqtt")
@Data
public class MqttConfig {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 连接地址
     */
    private String hostUrl;
    /**
     * 客户端Id，不能相同，采用随机数 ${random.value}
     */
    private String clientId;
    /**
     * 默认连接话题
     */
    private String defaultTopic;
    /**
     * 超时时间
     */
    private int timeout;
    /**
     * 保持连接数
     */
    private int keepalive;

    /** 是否清除session */
    private boolean clearSession;

    /** 是否共享订阅 */
    private boolean isShared;

    /** 分组共享订阅 */
    private boolean isSharedGroup;

    public String getClientId() {
        return "server-" + this.clientId;
    }
}
