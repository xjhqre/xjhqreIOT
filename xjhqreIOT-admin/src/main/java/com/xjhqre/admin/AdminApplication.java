package com.xjhqre.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * <p>
 * AdminApplication
 * </p>
 *
 * @author xjhqre
 * @since 11月 10, 2022
 */
@SpringBootApplication(
    scanBasePackages = {"com.xjhqre.common.*", "com.xjhqre.system.*", "com.xjhqre.framework.*", "com.xjhqre.admin.*"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
