package com.xjhqre.iot.mqtt;

import javax.annotation.Resource;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Classname MqttCallback
 * @Description 消费监听类
 */
@Slf4j
@Component
public class EmqxCallback implements MqttCallbackExtended {
    @Resource
    private EmqxService emqxService;

    @Override
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
        log.info("mqtt断开连接--");

    }

    /**
     * 收到消息调用的方法
     * 
     * @param topic
     *            主题
     * @param mqttMessage
     *            消息体
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        this.emqxService.subscribeCallback(topic, mqttMessage);
    }

    /**
     * 发布消息后，到达MQTT服务器，服务器回调消息接收
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // 消息到达 MQTT 代理时触发的事件
        log.info("消息到达EMQX");
    }

    /**
     * 监听mqtt连接消息
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        log.info("mqtt已经连接！！");
        // 连接后，可以在此做初始化事件，或订阅
        try {
            this.emqxService.subscribe(EmqxClient.asyncClient);
        } catch (MqttException e) {
            log.error("======>>>>>订阅主题失败 error={}", e.getMessage());
        }
    }
}
