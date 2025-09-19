# 08-Redis缓存集成

## 任务概述
完善Redis缓存集成，实现分布式锁、缓存一致性控制、缓存预热等功能，确保高并发场景下的数据一致性。

## 前置条件
- Redis服务已配置并运行
- 热度计算定时任务已完成
- Spring Data Redis依赖已添加

## 实施步骤

### 1. Redis配置类
**文件路径：** `mall-common/src/main/java/com/macro/mall/common/config/RedisConfig.java`

```java
package com.macro.mall.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis配置类
 */
@EnableCaching
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        
        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(600)) // 设置缓存有效期10分钟
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)))
                .disableCachingNullValues(); // 不缓存null值
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
```

### 2. 分布式锁工具类
**文件路径：** `mall-common/src/main/java/com/macro/mall/common/util/RedisLockUtil.java`

```java
package com.macro.mall.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 */
@Component
public class RedisLockUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockUtil.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String LOCK_PREFIX = "mall:lock:";
    private static final String UNLOCK_LUA_SCRIPT = 
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";
    
    /**
     * 尝试获取分布式锁
     * 
     * @param lockKey 锁的key
     * @param lockValue 锁的value（通常使用UUID）
     * @param expireTime 锁的过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime) {
        try {
            String key = LOCK_PREFIX + lockKey;
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, lockValue, expireTime, TimeUnit.SECONDS);
            boolean lockSuccess = Boolean.TRUE.equals(result);
            
            if (lockSuccess) {
                LOGGER.debug("获取分布式锁成功，key: {}, value: {}", key, lockValue);
            } else {
                LOGGER.debug("获取分布式锁失败，key: {}, value: {}", key, lockValue);
            }
            
            return lockSuccess;
        } catch (Exception e) {
            LOGGER.error("获取分布式锁异常，key: {}, value: {}", lockKey, lockValue, e);
            return false;
        }
    }
    
    /**
     * 释放分布式锁
     * 
     * @param lockKey 锁的key
     * @param lockValue 锁的value
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        try {
            String key = LOCK_PREFIX + lockKey;
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(UNLOCK_LUA_SCRIPT);
            redisScript.setResultType(Long.class);
            
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), lockValue);
            boolean releaseSuccess = Long.valueOf(1).equals(result);
            
            if (releaseSuccess) {
                LOGGER.debug("释放分布式锁成功，key: {}, value: {}", key, lockValue);
            } else {
                LOGGER.debug("释放分布式锁失败，key: {}, value: {}", key, lockValue);
            }
            
            return releaseSuccess;
        } catch (Exception e) {
            LOGGER.error("释放分布式锁异常，key: {}, value: {}", lockKey, lockValue, e);
            return false;
        }
    }
    
    /**
     * 尝试获取锁并执行业务逻辑
     * 
     * @param lockKey 锁的key
     * @param expireTime 锁的过期时间（秒）
     * @param task 业务逻辑
     * @return 是否执行成功
     */
    public boolean executeWithLock(String lockKey, long expireTime, Runnable task) {
        String lockValue = java.util.UUID.randomUUID().toString();
        
        if (tryLock(lockKey, lockValue, expireTime)) {
            try {
                task.run();
                return true;
            } finally {
                releaseLock(lockKey, lockValue);
            }
        }
        
        return false;
    }
}
```

### 3. 缓存管理服务
**文件路径：** `mall-admin/src/main/java/com/macro/mall/service/CaseCacheService.java`

```java
package com.macro.mall.service;

import java.util.List;

/**
 * 案例缓存管理服务
 */
public interface CaseCacheService {
    
    /**
     * 预热所有缓存
     */
    void warmUpAllCache();
    
    /**
     * 清除所有案例相关缓存
     */
    void clearAllCaseCache();
    
    /**
     * 清除指定案例的相关缓存
     */
    void clearCaseRelatedCache(Long caseId);
    
    /**
     * 清除分类相关缓存
     */
    void clearCategoryCache();
    
    /**
     * 获取缓存统计信息
     */
    CacheStatistics getCacheStatistics();
}
```

### 4. 实现缓存管理服务
**文件路径：** `mall-admin/src/main/java/com/macro/mall/service/impl/CaseCacheServiceImpl.java`

```java
package com.macro.mall.service.impl;

import com.macro.mall.common.util.RedisLockUtil;
import com.macro.mall.service.CaseCacheService;
import com.macro.mall.service.CaseHotScoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CaseCacheServiceImpl implements CaseCacheService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CaseCacheServiceImpl.class);
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RedisLockUtil redisLockUtil;
    
    @Autowired
    private CaseHotScoreService caseHotScoreService;
    
    private static final String CACHE_PREFIX = "mall:case:";
    private static final String WARM_UP_LOCK = "cache:warmup";
    
    @Override
    public void warmUpAllCache() {
        LOGGER.info("开始预热所有案例缓存");
        
        // 使用分布式锁防止并发预热
        boolean lockSuccess = redisLockUtil.executeWithLock(WARM_UP_LOCK, 300, () -> {
            try {
                // 预热热门案例缓存
                caseHotScoreService.refreshHotCasesCache();
                
                LOGGER.info("案例缓存预热完成");
            } catch (Exception e) {
                LOGGER.error("案例缓存预热失败", e);
                throw new RuntimeException("缓存预热失败", e);
            }
        });
        
        if (!lockSuccess) {
            LOGGER.warn("案例缓存预热正在进行中，跳过本次预热");
        }
    }
    
    @Override
    public void clearAllCaseCache() {
        try {
            LOGGER.info("开始清除所有案例缓存");
            
            Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                LOGGER.info("清除案例缓存完成，共清除 {} 个缓存项", keys.size());
            } else {
                LOGGER.info("没有找到需要清除的案例缓存");
            }
            
        } catch (Exception e) {
            LOGGER.error("清除所有案例缓存失败", e);
        }
    }
    
    @Override
    public void clearCaseRelatedCache(Long caseId) {
        try {
            LOGGER.info("开始清除案例 {} 的相关缓存", caseId);
            
            // 清除案例详情缓存
            String detailKey = CACHE_PREFIX + "detail:" + caseId;
            redisTemplate.delete(detailKey);
            
            // 清除列表页第一页缓存（最常访问的页面）
            Set<String> listKeys = redisTemplate.keys(CACHE_PREFIX + "*:page:1");
            if (listKeys != null && !listKeys.isEmpty()) {
                redisTemplate.delete(listKeys);
            }
            
            // 清除热门案例缓存
            redisTemplate.delete(CACHE_PREFIX + "hot:list");
            redisTemplate.delete(CACHE_PREFIX + "latest:list");
            redisTemplate.delete(CACHE_PREFIX + "all:list");
            
            LOGGER.info("清除案例 {} 的相关缓存完成", caseId);
            
        } catch (Exception e) {
            LOGGER.error("清除案例 {} 的相关缓存失败", caseId, e);
        }
    }
    
    @Override
    public void clearCategoryCache() {
        try {
            LOGGER.info("开始清除分类缓存");
            
            // 清除分类列表缓存
            redisTemplate.delete(CACHE_PREFIX + "categories:all");
            
            // 清除各分类的案例列表缓存
            Set<String> categoryKeys = redisTemplate.keys(CACHE_PREFIX + "category:*:list");
            if (categoryKeys != null && !categoryKeys.isEmpty()) {
                redisTemplate.delete(categoryKeys);
            }
            
            LOGGER.info("清除分类缓存完成");
            
        } catch (Exception e) {
            LOGGER.error("清除分类缓存失败", e);
        }
    }
    
    @Override
    public CacheStatistics getCacheStatistics() {
        CacheStatistics statistics = new CacheStatistics();
        
        try {
            // 统计各类缓存的数量
            Set<String> allKeys = redisTemplate.keys(CACHE_PREFIX + "*");
            statistics.setTotalKeys(allKeys != null ? allKeys.size() : 0);
            
            Set<String> detailKeys = redisTemplate.keys(CACHE_PREFIX + "detail:*");
            statistics.setDetailCacheCount(detailKeys != null ? detailKeys.size() : 0);
            
            Set<String> listKeys = redisTemplate.keys(CACHE_PREFIX + "*:page:*");
            statistics.setListCacheCount(listKeys != null ? listKeys.size() : 0);
            
            Set<String> categoryKeys = redisTemplate.keys(CACHE_PREFIX + "category:*");
            statistics.setCategoryCacheCount(categoryKeys != null ? categoryKeys.size() : 0);
            
        } catch (Exception e) {
            LOGGER.error("获取缓存统计信息失败", e);
        }
        
        return statistics;
    }
    
    /**
     * 缓存统计信息类
     */
    public static class CacheStatistics {
        private int totalKeys;
        private int detailCacheCount;
        private int listCacheCount;
        private int categoryCacheCount;
        
        // getter和setter方法...
        public int getTotalKeys() { return totalKeys; }
        public void setTotalKeys(int totalKeys) { this.totalKeys = totalKeys; }
        
        public int getDetailCacheCount() { return detailCacheCount; }
        public void setDetailCacheCount(int detailCacheCount) { this.detailCacheCount = detailCacheCount; }
        
        public int getListCacheCount() { return listCacheCount; }
        public void setListCacheCount(int listCacheCount) { this.listCacheCount = listCacheCount; }
        
        public int getCategoryCacheCount() { return categoryCacheCount; }
        public void setCategoryCacheCount(int categoryCacheCount) { this.categoryCacheCount = categoryCacheCount; }
    }
}
```

### 5. 缓存管理Controller
**文件路径：** 在`CaseDataController`中添加缓存管理接口

```java
@Autowired
private CaseCacheService caseCacheService;

@Operation(summary = "预热所有缓存")
@PostMapping("/cache/warmup")
public CommonResult warmUpCache() {
    caseCacheService.warmUpAllCache();
    return CommonResult.success("缓存预热完成");
}

@Operation(summary = "清除所有案例缓存")
@PostMapping("/cache/clear/all")
public CommonResult clearAllCache() {
    caseCacheService.clearAllCaseCache();
    return CommonResult.success("所有缓存清除完成");
}

@Operation(summary = "清除指定案例相关缓存")
@PostMapping("/cache/clear/{id}")
public CommonResult clearCaseCache(@PathVariable Long id) {
    caseCacheService.clearCaseRelatedCache(id);
    return CommonResult.success("案例相关缓存清除完成");
}

@Operation(summary = "清除分类缓存")
@PostMapping("/cache/clear/category")
public CommonResult clearCategoryCache() {
    caseCacheService.clearCategoryCache();
    return CommonResult.success("分类缓存清除完成");
}

@Operation(summary = "获取缓存统计信息")
@GetMapping("/cache/statistics")
public CommonResult getCacheStatistics() {
    return CommonResult.success(caseCacheService.getCacheStatistics());
}
```

### 6. 优化Service层的缓存操作
**文件路径：** 更新`CaseDataServiceImpl`中的缓存处理

```java
@Autowired
private CaseCacheService caseCacheService;

@Override
public int create(CaseDataParam param) {
    int result = // ... 原有创建逻辑
    
    if (result > 0) {
        // 清除相关缓存
        caseCacheService.clearCategoryCache();
    }
    
    return result;
}

@Override
public int update(Long id, CaseDataParam param) {
    int result = // ... 原有更新逻辑
    
    if (result > 0) {
        // 清除相关缓存
        caseCacheService.clearCaseRelatedCache(id);
    }
    
    return result;
}

@Override
public int delete(Long id) {
    int result = // ... 原有删除逻辑
    
    if (result > 0) {
        // 清除相关缓存
        caseCacheService.clearCaseRelatedCache(id);
    }
    
    return result;
}
```

## 验证步骤
1. 启动Redis服务
2. 测试分布式锁功能
3. 验证缓存预热功能
4. 测试缓存清除功能
5. 检查缓存统计信息
6. 验证高并发场景下的缓存一致性

## 缓存策略说明
| 缓存类型 | 缓存时间 | 更新策略 |
|---------|---------|---------|
| 案例详情 | 1小时 | 案例更新时清除 |
| 案例列表第一页 | 15分钟 | 相关数据变更时清除 |
| 热门案例列表 | 30分钟 | 定时任务更新 |
| 分类列表 | 30分钟 | 分类变更时清除 |

## 分布式锁应用场景
1. **缓存预热**：防止并发预热导致重复操作
2. **热度计算**：防止并发计算导致数据不一致
3. **缓存更新**：防止并发更新导致缓存脏数据

## 输出物
- RedisConfig Redis配置类
- RedisLockUtil 分布式锁工具类
- CaseCacheService 缓存管理服务
- 缓存管理API接口
- 完整的缓存一致性控制机制

## 后续任务
- 下一步：09-添加案例管理菜单.md