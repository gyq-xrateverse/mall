package com.macro.mall.portal.config;

import com.macro.mall.portal.component.CacheUpdateListener;
import com.macro.mall.portal.constant.CacheKeyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * Redis 消息监听配置
 * 配置门户端监听管理端的缓存更新消息
 */
@Configuration
public class RedisMessageConfig {

    @Autowired
    private CacheUpdateListener cacheUpdateListener;

    /**
     * Redis消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 添加消息监听器
        container.addMessageListener(messageListenerAdapter(), channelTopic());

        return container;
    }

    /**
     * 消息监听器适配器
     */
    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(cacheUpdateListener);
    }

    /**
     * 消息频道主题
     */
    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL);
    }
}