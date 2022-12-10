package com.xjhqre.admin.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2的接口配置
 * 
 * @author xjhqre
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(this.apiInfo())
            // 是否开启 (true 开启 false隐藏。生产环境建议隐藏)
            // .enable(false)
            .select()
            // 扫描的路径包,设置basePackage会将包下的所有被@Api标记类的所有方法作为api
            // .apis(RequestHandlerSelectors.basePackage("com.mcy.springbootswagger.controller"))
            // 扫描所有接口方法
            .apis(RequestHandlerSelectors.any())
            // 指定路径处理PathSelectors.any()代表所有的路径
            .paths(PathSelectors.any()).build()
            // 添加权限认证
            .securitySchemes(this.securitySchemes()).securityContexts(this.securityContexts());
    }

    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList();
        apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(SecurityContext.builder().securityReferences(this.defaultAuth())
            .forPaths(PathSelectors.regex("^(?!auth).*$")).build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            // 设置文档标题(API名称)
            .title("xjhqreBBS接口文档")
            // 文档描述
            .description("xjhqreBBS后台管理系统")
            // 作者信息
            .contact(new Contact("xjhqre", null, null))
            // 版本号
            .version("1.0.0").build();
    }
}
