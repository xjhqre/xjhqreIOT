package com.xjhqre.iot.mqtt;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动执行
 */
@Component
@Order(value = 1) // 执行顺序控制
public class EmqxStart implements ApplicationRunner {

    @Resource
    private EmqxClient emqxClient;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        this.emqxClient.connect();
    }
}
