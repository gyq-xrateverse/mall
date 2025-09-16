package com.macro.mall.common.service;

/**
 * Redis分布式锁Service
 */
public interface RedisLockService {
    
    /**
     * 获取分布式锁
     * @param lockKey 锁的key
     * @param lockValue 锁的值
     * @param expireTime 锁过期时间（秒）
     * @return 是否获取成功
     */
    boolean tryLock(String lockKey, String lockValue, long expireTime);
    
    /**
     * 释放分布式锁
     * @param lockKey 锁的key
     * @param lockValue 锁的值
     * @return 是否释放成功
     */
    boolean unlock(String lockKey, String lockValue);
    
    /**
     * 续期分布式锁
     * @param lockKey 锁的key
     * @param lockValue 锁的值
     * @param expireTime 锁过期时间（秒）
     * @return 是否续期成功
     */
    boolean renewLock(String lockKey, String lockValue, long expireTime);
}