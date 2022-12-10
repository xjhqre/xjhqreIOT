package com.xjhqre.framework.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author : xjhqre
 * @CreateTime : 2022/11/4
 **/
@Configuration
public class RabbitMQConfig {

    // 队列
    public static final String PICTURE = "picture"; // 批量图片审核
    public static final String THUMB_COLLECT = "thumb_collect"; // 点赞收藏队列
    public static final String COMMENT = "comment"; // 评论和@队列
    public static final String FOLLOW = "follow"; // 粉丝队列
    public static final String ES_ARTICLE_SAVE = "es_article_update"; // 添加或更新文档
    public static final String ES_ARTICLE_DELETE = "es_article_delete"; // 删除es文档

    // 交换机
    public static final String DIRECT_EXCHANGE = "directExchange"; // 图片处理交换机
    public static final String MESSAGE_EXCHANGE = "messageExchange"; // 消息交换机
    public static final String ES_ARTICLE_EXCHANGE = "es_article_exchange"; // 消息交换机

    // 路由密钥
    public static final String ROUTING_KEY_PICTURE = "routing_key_picture"; // 处理图片
    public static final String ROUTING_KEY_THUMB_COLLECT = "thumb_collect_key"; // 点赞收藏密钥
    public static final String ROUTING_KEY_COMMENT = "comment_key"; // 评论密钥
    public static final String ROUTING_KEY_FOLLOW = "follow_key"; // 关注密钥
    public static final String ROUTING_KEY_ES_ARTICLE_SAVE = "routing_key_es_article_update"; // 添加或更新文档密钥
    public static final String ROUTING_KEY_ES_ARTICLE_DELETE = "routing_key_es_article_delete"; // 关注密钥

    ///////////////////////////////////////// 交换机声明 ///////////////////////////////////////////

    // 图片处理Direct交换机
    @Bean
    DirectExchange directExchange() {
        // 声明路由交换机，durable:在rabbitmq重启后，交换机还在
        return ExchangeBuilder.directExchange(DIRECT_EXCHANGE).durable(true).build();
    }

    // 用户消息Direct交换机
    @Bean
    DirectExchange messageExchange() {
        return ExchangeBuilder.directExchange(MESSAGE_EXCHANGE).durable(true).build();
    }

    // es全文检索交换机
    @Bean
    DirectExchange esArticleExchange() {
        return ExchangeBuilder.directExchange(ES_ARTICLE_EXCHANGE).durable(true).build();
    }

    ///////////////////////////////////////// 队列声明 ///////////////////////////////////////////

    @Bean
    public Queue pictureQueue() {
        // durable:是否持久化,默认是false,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        // return new Queue("TestDirectQueue",true,true,false);
        // 一般设置一下队列的持久化就好,其余两个就是默认false
        return new Queue(PICTURE, true);
    }

    @Bean
    public Queue thumbCollectQueue() {
        return new Queue(THUMB_COLLECT, true);
    }

    @Bean
    public Queue commentQueue() {
        return new Queue(COMMENT, true);
    }

    @Bean
    public Queue followQueue() {
        return new Queue(FOLLOW, true);
    }

    @Bean
    public Queue esArticleUpdateQueue() {
        return new Queue(ES_ARTICLE_SAVE, true);
    }

    @Bean
    public Queue esArticleDeleteQueue() {
        return new Queue(ES_ARTICLE_DELETE, true);
    }

    ///////////////////////////////////////// 绑定队列和交换机 ///////////////////////////////////////////

    // 绑定 将队列和交换机绑定, 并设置用于匹配键
    @Bean
    Binding bindingPicture() {
        return BindingBuilder.bind(this.pictureQueue()).to(this.directExchange()).with(ROUTING_KEY_PICTURE);
    }

    @Bean
    Binding bindingThumbCollect() {
        return BindingBuilder.bind(this.thumbCollectQueue()).to(this.messageExchange()).with(ROUTING_KEY_THUMB_COLLECT);
    }

    @Bean
    Binding bindingComment() {
        return BindingBuilder.bind(this.commentQueue()).to(this.messageExchange()).with(ROUTING_KEY_COMMENT);
    }

    @Bean
    Binding bindingFollow() {
        return BindingBuilder.bind(this.followQueue()).to(this.messageExchange()).with(ROUTING_KEY_FOLLOW);
    }

    @Bean
    Binding bindingEsArticleUpdate() {
        return BindingBuilder.bind(this.esArticleUpdateQueue()).to(this.esArticleExchange())
            .with(ROUTING_KEY_ES_ARTICLE_SAVE);
    }

    @Bean
    Binding bindingEsArticleDelete() {
        return BindingBuilder.bind(this.esArticleDeleteQueue()).to(this.esArticleExchange())
            .with(ROUTING_KEY_ES_ARTICLE_DELETE);
    }

    ///////////////////////////////////////// json ///////////////////////////////////////////

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}