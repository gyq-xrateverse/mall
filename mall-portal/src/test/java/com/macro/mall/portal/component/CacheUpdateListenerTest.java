package com.macro.mall.portal.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.constant.CacheKeyConstants;
import com.macro.mall.portal.dto.CacheUpdateMessage;
import com.macro.mall.portal.service.PortalCaseCacheService;
import com.macro.mall.portal.test.util.PortalCacheTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.Message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CacheUpdateListener 单元测试
 */
@ExtendWith(MockitoExtension.class)
class CacheUpdateListenerTest {

    @Mock
    private PortalCaseCacheService portalCaseCacheService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Message message;

    @InjectMocks
    private CacheUpdateListener cacheUpdateListener;

    private String validChannel;
    private String invalidChannel;

    @BeforeEach
    void setUp() {
        validChannel = CacheKeyConstants.CACHE_UPDATE_CHANNEL;
        invalidChannel = "invalid:channel";
    }

    @Test
    void testOnMessage_ValidCreateMessage_ShouldProcessSuccessfully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, times(1)).delCaseCategoryCache();
        verify(portalCaseCacheService, times(1)).delLatestCaseCache();
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
        verify(portalCaseCacheService, never()).delHotCaseCache();
    }

    @Test
    void testOnMessage_ValidUpdateMessage_ShouldProcessSuccessfully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, times(1)).delCaseDetailCache(123L);
        verify(portalCaseCacheService, times(1)).delHotCaseCache();
        verify(portalCaseCacheService, times(1)).delLatestCaseCache();
        verify(portalCaseCacheService, never()).delCaseCategoryCache();
    }

    @Test
    void testOnMessage_ValidDeleteMessage_ShouldProcessSuccessfully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.DELETE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, times(1)).delCaseDetailCache(123L);
        verify(portalCaseCacheService, times(1)).delCaseCategoryCache();
        verify(portalCaseCacheService, times(1)).delHotCaseCache();
        verify(portalCaseCacheService, times(1)).delLatestCaseCache();
    }

    @Test
    void testOnMessage_ValidBatchDeleteMessage_ShouldProcessSuccessfully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.BATCH_DELETE,
            CacheUpdateMessage.ResourceType.CASE,
            "[1,2,3]"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, times(1)).delAllCache();
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
    }

    @Test
    void testOnMessage_ValidStatusUpdateMessage_ShouldProcessSuccessfully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.STATUS_UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, times(1)).delCaseDetailCache(123L);
        verify(portalCaseCacheService, times(1)).delHotCaseCache();
        verify(portalCaseCacheService, times(1)).delLatestCaseCache();
        verify(portalCaseCacheService, never()).delCaseCategoryCache();
    }

    @Test
    void testOnMessage_InvalidChannel_ShouldIgnore() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(invalidChannel.getBytes());

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(objectMapper, never()).readValue(anyString(), eq(CacheUpdateMessage.class));
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
        verify(portalCaseCacheService, never()).delCaseCategoryCache();
    }

    @Test
    void testOnMessage_EmptyMessage_ShouldIgnore() throws Exception {
        // Given
        String emptyMessage = PortalCacheTestData.createEmptyMessage();

        when(message.getBody()).thenReturn(emptyMessage.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(objectMapper, never()).readValue(anyString(), eq(CacheUpdateMessage.class));
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
    }

    @Test
    void testOnMessage_OversizedMessage_ShouldIgnore() throws Exception {
        // Given
        String oversizedMessage = PortalCacheTestData.createOversizedMessageJson();

        when(message.getBody()).thenReturn(oversizedMessage.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(objectMapper, never()).readValue(anyString(), eq(CacheUpdateMessage.class));
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
    }

    @Test
    void testOnMessage_ExpiredMessage_ShouldIgnore() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage expiredMessage = PortalCacheTestData.createExpiredCacheUpdateMessage();

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(expiredMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
        verify(portalCaseCacheService, never()).delCaseCategoryCache();
    }

    @Test
    void testOnMessage_JsonParseException_ShouldHandleGracefully() throws Exception {
        // Given
        String invalidJsonMessage = PortalCacheTestData.createInvalidMessageJson();

        when(message.getBody()).thenReturn(invalidJsonMessage.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(invalidJsonMessage, CacheUpdateMessage.class))
            .thenThrow(new RuntimeException("JSON解析失败"));

        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> {
            cacheUpdateListener.onMessage(message, null);
        });

        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
    }

    @Test
    void testOnMessage_NonCaseResourceType_ShouldIgnore() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CATEGORY, // 非CASE类型
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
        verify(portalCaseCacheService, never()).delCaseCategoryCache();
    }

    @Test
    void testOnMessage_InvalidResourceId_ShouldHandleGracefully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "invalid_id" // 无效的ID
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> {
            cacheUpdateListener.onMessage(message, null);
        });

        // 由于ID无效，不应该调用缓存删除方法
        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
    }

    @Test
    void testOnMessage_UnknownActionType_ShouldHandleGracefully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            null, // 未知操作类型
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> {
            cacheUpdateListener.onMessage(message, null);
        });

        verify(portalCaseCacheService, never()).delCaseDetailCache(anyLong());
    }

    @Test
    void testOnMessage_CacheServiceException_ShouldHandleGracefully() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);
        doThrow(new RuntimeException("缓存服务异常")).when(portalCaseCacheService).delCaseCategoryCache();

        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> {
            cacheUpdateListener.onMessage(message, null);
        });

        verify(portalCaseCacheService, times(1)).delCaseCategoryCache();
    }

    @Test
    void testOnMessage_NullTimestamp_ShouldProcessNormally() throws Exception {
        // Given
        String messageBody = PortalCacheTestData.createTestMessageJson();
        CacheUpdateMessage cacheUpdateMessage = PortalCacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );
        cacheUpdateMessage.setTimestamp(null); // 空时间戳

        when(message.getBody()).thenReturn(messageBody.getBytes());
        when(message.getChannel()).thenReturn(validChannel.getBytes());
        when(objectMapper.readValue(messageBody, CacheUpdateMessage.class)).thenReturn(cacheUpdateMessage);

        // When
        cacheUpdateListener.onMessage(message, null);

        // Then - 应该正常处理，因为没有时间戳检查
        verify(portalCaseCacheService, times(1)).delCaseCategoryCache();
        verify(portalCaseCacheService, times(1)).delLatestCaseCache();
    }
}