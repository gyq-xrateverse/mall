package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * @author Claude
 * @since 2025-09-10
 */
@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试系统状态和连接性的接口")
@Slf4j
public class TestController {
    
    @Operation(summary = "健康检查", description = "检查API服务是否正常运行")
    @GetMapping("/health")
    public CommonResult<Map<String, Object>> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        result.put("service", "mall-portal");
        result.put("version", "1.0.0");
        
        log.info("健康检查请求");
        return CommonResult.success(result);
    }
    
    @Operation(summary = "API状态", description = "获取认证API的基本信息")
    @GetMapping("/info")
    public CommonResult<Map<String, Object>> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "BEILV AI Mall Portal API");
        result.put("description", "BEILV AI商城门户认证服务");
        result.put("features", new String[]{"用户注册", "邮箱登录", "验证码", "密码重置", "JWT认证"});
        result.put("author", "Claude");
        result.put("timestamp", LocalDateTime.now());
        
        return CommonResult.success(result);
    }
}