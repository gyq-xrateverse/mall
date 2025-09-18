package com.macro.mall.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * 缓存测试配置类
 * 提供测试环境下的Mock Bean配置
 */
@TestConfiguration
public class CacheTestConfig {

    /**
     * Mock RedisService
     */
    @Bean
    @Primary
    public RedisService mockRedisService() {
        return Mockito.mock(RedisService.class);
    }

    /**
     * Mock RedisTemplate
     */
    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> mockRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = Mockito.mock(RedisTemplate.class);
        ValueOperations<String, Object> valueOperations = Mockito.mock(ValueOperations.class);

        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        return redisTemplate;
    }

    /**
     * ObjectMapper for JSON serialization
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}