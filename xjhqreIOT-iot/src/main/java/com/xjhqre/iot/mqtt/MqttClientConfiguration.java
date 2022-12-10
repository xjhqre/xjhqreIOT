package com.xjhqre.iot.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttClientConfiguration {

    @Autowired
    private MqttConfig mqttConfig;

    @Bean
    EmqxClient mqttClient() {
        EmqxClient client = new EmqxClient(this.mqttConfig.getclientId(), this.mqttConfig.getusername(), this.mqttConfig.getpassword(),
                this.mqttConfig.gethostUrl(), this.mqttConfig.gettimeout(), this.mqttConfig.getkeepalive(), this.mqttConfig.isClearSession());
        return client;
    }
}
