package com.xjhqre.framework.config;

import java.util.TimeZone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 程序注解配置
 *
 * @author xjhqre
 */
@Configuration
// 表示通过aop框架暴露该代理对象，通过 (T)AopContext.currentProxy() 获取代理对象。用于解决在代理对象方法内继续调用内部方法
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.xjhqre.**.mapper")
public class ApplicationConfig {
    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.timeZone(TimeZone.getDefault());
    }
}
