package com.xjhqre.framework.config.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * druid 配置属性
 * 
 * @author xjhqre
 */
@Configuration
public class DruidProperties {
    /**
     * 初始连接数
     */
    @Value("${spring.datasource.druid.initialSize}")
    private int initialSize;

    /**
     * 最小连接池数量
     */
    @Value("${spring.datasource.druid.minIdle}")
    private int minIdle;

    /**
     * 最大连接池数量
     */
    @Value("${spring.datasource.druid.maxActive}")
    private int maxActive;

    /**
     * 获取连接等待超时时间
     */
    @Value("${spring.datasource.druid.maxWait}")
    private int maxWait;

    /**
     * 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
     */
    @Value("${spring.datasource.druid.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;

    /**
     * 配置一个连接在池中最小生存的时间，单位是毫秒
     */
    @Value("${spring.datasource.druid.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;

    /**
     * 配置一个连接在池中最大生存的时间，单位是毫秒
     */
    @Value("${spring.datasource.druid.maxEvictableIdleTimeMillis}")
    private int maxEvictableIdleTimeMillis;

    /**
     * 配置检测连接是否有效
     */
    @Value("${spring.datasource.druid.validationQuery}")
    private String validationQuery;

    /**
     * 是否空闲时测试
     */
    @Value("${spring.datasource.druid.testWhileIdle}")
    private boolean testWhileIdle;

    @Value("${spring.datasource.druid.testOnBorrow}")
    private boolean testOnBorrow;

    /**
     * 是否返回时测试
     */
    @Value("${spring.datasource.druid.testOnReturn}")
    private boolean testOnReturn;

    public DruidDataSource dataSource(DruidDataSource datasource) {
        /* 配置初始化大小、最小、最大 */
        datasource.setInitialSize(this.initialSize);
        datasource.setMaxActive(this.maxActive);
        datasource.setMinIdle(this.minIdle);

        /* 配置获取连接等待超时的时间 */
        datasource.setMaxWait(this.maxWait);

        /* 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        datasource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);

        /* 配置一个连接在池中最小、最大生存的时间，单位是毫秒 */
        datasource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
        datasource.setMaxEvictableIdleTimeMillis(this.maxEvictableIdleTimeMillis);

        /*
          用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
         */
        datasource.setValidationQuery(this.validationQuery);
        /* 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。 */
        datasource.setTestWhileIdle(this.testWhileIdle);
        /* 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnBorrow(this.testOnBorrow);
        /* 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。 */
        datasource.setTestOnReturn(this.testOnReturn);
        return datasource;
    }
}
