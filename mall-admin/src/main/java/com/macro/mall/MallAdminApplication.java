package com.macro.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@SpringBootApplication(scanBasePackages = "com.macro.mall")
@EnableRetry
public class MallAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAdminApplication.class, args);
    }
}
