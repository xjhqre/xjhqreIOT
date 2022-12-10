package com.xjhqre.iot.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xjhqre.framework.config.RabbitMQConfig;

/**
 * <p>
 * RabbitMQSender 用于发送消息
 * </p>
 *
 * @author xjhqre
 * @since 11月 15, 2022
 */
@Component
public class RabbitMQSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 发送图片批量处理消息
     *
     * @param pictureIds
     */
    public void sendPictureProcessMessage(String[] pictureIds) {
        this.rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY_PICTURE,
            pictureIds);
    }

}
