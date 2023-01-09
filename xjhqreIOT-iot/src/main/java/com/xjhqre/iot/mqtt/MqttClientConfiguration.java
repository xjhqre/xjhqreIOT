package com.xjhqre.iot.mqtt;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientConfiguration {

    @Resource
    private MqttConfig mqttConfig;

    @Bean
    EmqxClient mqttClient() {
        return new EmqxClient(this.mqttConfig.getClientId(), this.mqttConfig.getUsername(),
            this.mqttConfig.getPassword(), this.mqttConfig.getHostUrl(), this.mqttConfig.getTimeout(),
            this.mqttConfig.getKeepalive(), this.mqttConfig.isClearSession());
    }
}
