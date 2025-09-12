package com.macro.mall.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置
 * @author Claude
 * @since 2025-09-10
 */
@Data
@Component
@ConfigurationProperties(prefix = "verification-code")
public class VerificationCodeConfig {
    
    /**
     * 验证码长度，默认6位
     */
    private int length = 6;
    
    /**
     * 验证码有效期（分钟），默认5分钟
     */
    private int expireMinutes = 5;
    
    /**
     * 验证码限制发送间隔（秒），默认60秒
     */
    private int sendIntervalSeconds = 60;
    
    /**
     * 每日最大发送次数，默认20次
     */
    private int maxSendPerDay = 20;
    
    /**
     * 验证码类型：数字(NUMERIC)、字母(LETTER)、混合(MIXED)
     */
    private String type = "NUMERIC";
}