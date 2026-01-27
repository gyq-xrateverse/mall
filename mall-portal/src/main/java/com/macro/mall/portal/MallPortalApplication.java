package com.macro.mall.portal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.macro.mall")
@MapperScan({"com.macro.mall.mapper", "com.macro.mall.dao", "com.macro.mall.integration.dao"})
public class MallPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallPortalApplication.class, args);
    }

}
