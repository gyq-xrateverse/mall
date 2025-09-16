package com.macro.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@SpringBootApplication(scanBasePackages = "com.macro.mall")
public class MallAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallAdminApplication.class, args);
    }
}
