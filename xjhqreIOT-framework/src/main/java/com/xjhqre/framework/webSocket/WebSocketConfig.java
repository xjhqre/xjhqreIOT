// package com.xjhqre.framework.webSocket;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.socket.server.standard.ServerEndpointExporter;
// import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
//
// @Configuration
// public class WebSocketConfig {
// /**
// * 注入ServerEndpointExporter， 这个bean会自动注册使用了@ServerEndpoint注解声明的Websocket endpoint
// */
// @Bean
// public ServerEndpointExporter serverEndpointExporter() {
// return new ServerEndpointExporter();
// }
//
// @Bean
// public ServletServerContainerFactoryBean createWebSocketContainer() {
// ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
// // 在此处设置bufferSize
// container.setMaxTextMessageBufferSize(512000);
// container.setMaxBinaryMessageBufferSize(512000);
// container.setMaxSessionIdleTimeout(15 * 60000L);
// return container;
// }
//
// }
