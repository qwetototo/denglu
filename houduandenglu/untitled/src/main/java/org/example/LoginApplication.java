package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动类
 * <p>
 * 使用 @SpringBootApplication 开启自动配置、组件扫描等功能
 * </p>
 *
 * @author example
 */
@SpringBootApplication
public class LoginApplication {

    /**
     * 程序主入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 启动 Spring Boot 应用
        SpringApplication.run(LoginApplication.class, args);
    }
}