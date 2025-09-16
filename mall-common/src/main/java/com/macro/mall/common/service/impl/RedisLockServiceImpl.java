package com.macro.mall.common.service.impl;

import com.macro.mall.common.service.RedisLockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁Service实现类
 */
@Service
public class RedisLockServiceImpl implements RedisLockService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "return redis.call('del', KEYS[1]) " +
        "else " +
        "return 0 " +
        "end";
    
    private static final String RENEW_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "return redis.call('expire', KEYS[1], ARGV[2]) " +
        "else " +
        "return 0 " +
        "end";

    @Override
    public boolean tryLock(String lockKey, String lockValue, long expireTime) {
        try {
            Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.SECONDS);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean unlock(String lockKey, String lockValue) {
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(UNLOCK_SCRIPT);
            script.setResultType(Long.class);
            
            Long result = redisTemplate.execute(script, 
                Collections.singletonList(lockKey), lockValue);
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean renewLock(String lockKey, String lockValue, long expireTime) {
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(RENEW_SCRIPT);
            script.setResultType(Long.class);
            
            Long result = redisTemplate.execute(script,
                Collections.singletonList(lockKey), lockValue, String.valueOf(expireTime));
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}