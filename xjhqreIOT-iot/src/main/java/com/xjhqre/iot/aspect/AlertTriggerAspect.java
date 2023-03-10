package com.xjhqre.iot.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 告警触发器AOP
 *
 * @author xjhqre
 */
@Aspect
@Component
@Slf4j
public class AlertTriggerAspect {

    /**
     * 处理完请求后执行 argNames属性使用了命名绑定模式，定义参数类型、个数和顺序，和args(下文有介绍)效果一样，只是argNames优先级高于args
     *
     */
    @AfterReturning(
        pointcut = "execution(* com.xjhqre.iot.mqtt.EmqxService.subscribeCallback(..))&&args(topic, mqttMessage)",
        returning = "resultValue", argNames = "topic,mqttMessage,resultValue")
    public void doAfterReturning(String topic, MqttMessage mqttMessage, Object resultValue) {
        log.info("aop收到topic：{}", topic);
        log.info("aop收到mqttMessage：{}", mqttMessage);
    }
}
