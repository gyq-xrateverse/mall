package com.macro.mall.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存更新消息测试
 * 测试消息对象创建、JSON序列化、反序列化、字段验证和消息格式
 */
class CacheUpdateMessageTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testDefaultConstructor_ShouldCreateEmptyMessage() {
        // When
        CacheUpdateMessage message = new CacheUpdateMessage();

        // Then
        assertNull(message.getAction());
        assertNull(message.getResourceType());
        assertNull(message.getResourceId());
        assertNull(message.getCacheKeys());
        assertNull(message.getTimestamp());
        assertNull(message.getOperator());
    }

    @Test
    public void testParameterizedConstructor_ShouldCreateCompleteMessage() {
        // Given
        CacheUpdateMessage.ActionType action = CacheUpdateMessage.ActionType.CREATE;
        CacheUpdateMessage.ResourceType resourceType = CacheUpdateMessage.ResourceType.CASE;
        String resourceId = "123";
        List<String> cacheKeys = Arrays.asList("key1", "key2");
        String operator = "admin";
        long beforeTime = System.currentTimeMillis();

        // When
        CacheUpdateMessage message = new CacheUpdateMessage(action, resourceType, resourceId, cacheKeys, operator);

        // Then
        assertEquals(action, message.getAction());
        assertEquals(resourceType, message.getResourceType());
        assertEquals(resourceId, message.getResourceId());
        assertEquals(cacheKeys, message.getCacheKeys());
        assertEquals(operator, message.getOperator());
        assertNotNull(message.getTimestamp());
        assertTrue(message.getTimestamp() >= beforeTime);
        assertTrue(message.getTimestamp() <= System.currentTimeMillis());
    }

    @Test
    public void testSettersAndGetters_ShouldWorkCorrectly() {
        // Given
        CacheUpdateMessage message = new CacheUpdateMessage();
        CacheUpdateMessage.ActionType action = CacheUpdateMessage.ActionType.UPDATE;
        CacheUpdateMessage.ResourceType resourceType = CacheUpdateMessage.ResourceType.CATEGORY;
        String resourceId = "456";
        List<String> cacheKeys = Arrays.asList("category:1", "category:list");
        Long timestamp = System.currentTimeMillis();
        String operator = "user123";

        // When
        message.setAction(action);
        message.setResourceType(resourceType);
        message.setResourceId(resourceId);
        message.setCacheKeys(cacheKeys);
        message.setTimestamp(timestamp);
        message.setOperator(operator);

        // Then
        assertEquals(action, message.getAction());
        assertEquals(resourceType, message.getResourceType());
        assertEquals(resourceId, message.getResourceId());
        assertEquals(cacheKeys, message.getCacheKeys());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(operator, message.getOperator());
    }

    @Test
    public void testActionTypeEnum_ShouldHaveCorrectValues() {
        // When & Then
        assertEquals("CREATE", CacheUpdateMessage.ActionType.CREATE.getValue());
        assertEquals("UPDATE", CacheUpdateMessage.ActionType.UPDATE.getValue());
        assertEquals("DELETE", CacheUpdateMessage.ActionType.DELETE.getValue());
        assertEquals("BATCH_DELETE", CacheUpdateMessage.ActionType.BATCH_DELETE.getValue());
        assertEquals("STATUS_UPDATE", CacheUpdateMessage.ActionType.STATUS_UPDATE.getValue());
    }

    @Test
    public void testResourceTypeEnum_ShouldHaveCorrectValues() {
        // When & Then
        assertEquals("CASE", CacheUpdateMessage.ResourceType.CASE.getValue());
        assertEquals("CATEGORY", CacheUpdateMessage.ResourceType.CATEGORY.getValue());
    }

    @Test
    public void testActionTypeEnum_ShouldContainAllExpectedValues() {
        // Given
        CacheUpdateMessage.ActionType[] expectedActions = {
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ActionType.UPDATE,
            CacheUpdateMessage.ActionType.DELETE,
            CacheUpdateMessage.ActionType.BATCH_DELETE,
            CacheUpdateMessage.ActionType.STATUS_UPDATE
        };

        // When
        CacheUpdateMessage.ActionType[] actualActions = CacheUpdateMessage.ActionType.values();

        // Then
        assertEquals(expectedActions.length, actualActions.length);
        for (CacheUpdateMessage.ActionType expected : expectedActions) {
            boolean found = false;
            for (CacheUpdateMessage.ActionType actual : actualActions) {
                if (expected.equals(actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Missing action type: " + expected);
        }
    }

    @Test
    public void testResourceTypeEnum_ShouldContainAllExpectedValues() {
        // Given
        CacheUpdateMessage.ResourceType[] expectedTypes = {
            CacheUpdateMessage.ResourceType.CASE,
            CacheUpdateMessage.ResourceType.CATEGORY
        };

        // When
        CacheUpdateMessage.ResourceType[] actualTypes = CacheUpdateMessage.ResourceType.values();

        // Then
        assertEquals(expectedTypes.length, actualTypes.length);
        for (CacheUpdateMessage.ResourceType expected : expectedTypes) {
            boolean found = false;
            for (CacheUpdateMessage.ResourceType actual : actualTypes) {
                if (expected.equals(actual)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Missing resource type: " + expected);
        }
    }

    @Test
    public void testJsonSerialization_ShouldSerializeCorrectly() throws Exception {
        // Given
        CacheUpdateMessage message = new CacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            Arrays.asList("key1", "key2"),
            "admin"
        );

        // When
        String json = objectMapper.writeValueAsString(message);

        // Then
        assertNotNull(json);
        assertFalse(json.isEmpty());
        assertTrue(json.contains("\"action\":\"CREATE\""));
        assertTrue(json.contains("\"resourceType\":\"CASE\""));
        assertTrue(json.contains("\"resourceId\":\"123\""));
        assertTrue(json.contains("\"cacheKeys\":[\"key1\",\"key2\"]"));
        assertTrue(json.contains("\"operator\":\"admin\""));
        assertTrue(json.contains("\"timestamp\""));
    }

    @Test
    public void testJsonDeserialization_ShouldDeserializeCorrectly() throws Exception {
        // Given
        String json = "{\n" +
            "  \"action\": \"UPDATE\",\n" +
            "  \"resourceType\": \"CATEGORY\",\n" +
            "  \"resourceId\": \"456\",\n" +
            "  \"cacheKeys\": [\"category:1\", \"category:list\"],\n" +
            "  \"timestamp\": 1234567890,\n" +
            "  \"operator\": \"user123\"\n" +
            "}";

        // When
        CacheUpdateMessage message = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertNotNull(message);
        assertEquals(CacheUpdateMessage.ActionType.UPDATE, message.getAction());
        assertEquals(CacheUpdateMessage.ResourceType.CATEGORY, message.getResourceType());
        assertEquals("456", message.getResourceId());
        assertEquals(Arrays.asList("category:1", "category:list"), message.getCacheKeys());
        assertEquals(1234567890L, message.getTimestamp());
        assertEquals("user123", message.getOperator());
    }

    @Test
    public void testJsonSerializationDeserialization_ShouldBeSymmetric() throws Exception {
        // Given
        CacheUpdateMessage originalMessage = new CacheUpdateMessage(
            CacheUpdateMessage.ActionType.DELETE,
            CacheUpdateMessage.ResourceType.CASE,
            "789",
            Arrays.asList("case:detail:789", "case:list", "case:hot"),
            "admin123"
        );

        // When
        String json = objectMapper.writeValueAsString(originalMessage);
        CacheUpdateMessage deserializedMessage = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertEquals(originalMessage.getAction(), deserializedMessage.getAction());
        assertEquals(originalMessage.getResourceType(), deserializedMessage.getResourceType());
        assertEquals(originalMessage.getResourceId(), deserializedMessage.getResourceId());
        assertEquals(originalMessage.getCacheKeys(), deserializedMessage.getCacheKeys());
        assertEquals(originalMessage.getTimestamp(), deserializedMessage.getTimestamp());
        assertEquals(originalMessage.getOperator(), deserializedMessage.getOperator());
    }

    @Test
    public void testJsonDeserialization_WithNullValues_ShouldHandleGracefully() throws Exception {
        // Given
        String json = "{\n" +
            "  \"action\": \"CREATE\",\n" +
            "  \"resourceType\": \"CASE\",\n" +
            "  \"resourceId\": null,\n" +
            "  \"cacheKeys\": null,\n" +
            "  \"timestamp\": null,\n" +
            "  \"operator\": null\n" +
            "}";

        // When
        CacheUpdateMessage message = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertNotNull(message);
        assertEquals(CacheUpdateMessage.ActionType.CREATE, message.getAction());
        assertEquals(CacheUpdateMessage.ResourceType.CASE, message.getResourceType());
        assertNull(message.getResourceId());
        assertNull(message.getCacheKeys());
        assertNull(message.getTimestamp());
        assertNull(message.getOperator());
    }

    @Test
    public void testJsonDeserialization_WithEmptyCollections_ShouldHandleCorrectly() throws Exception {
        // Given
        String json = "{\n" +
            "  \"action\": \"BATCH_DELETE\",\n" +
            "  \"resourceType\": \"CASE\",\n" +
            "  \"resourceId\": \"[]\",\n" +
            "  \"cacheKeys\": [],\n" +
            "  \"timestamp\": 1234567890,\n" +
            "  \"operator\": \"admin\"\n" +
            "}";

        // When
        CacheUpdateMessage message = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertNotNull(message);
        assertEquals(CacheUpdateMessage.ActionType.BATCH_DELETE, message.getAction());
        assertEquals(Collections.emptyList(), message.getCacheKeys());
    }

    @Test
    public void testToString_ShouldContainAllFields() {
        // Given
        CacheUpdateMessage message = new CacheUpdateMessage(
            CacheUpdateMessage.ActionType.STATUS_UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "999",
            Arrays.asList("test:key1", "test:key2"),
            "testUser"
        );

        // When
        String toString = message.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("action=STATUS_UPDATE"));
        assertTrue(toString.contains("resourceType=CASE"));
        assertTrue(toString.contains("resourceId='999'"));
        assertTrue(toString.contains("cacheKeys=[test:key1, test:key2]"));
        assertTrue(toString.contains("operator='testUser'"));
        assertTrue(toString.contains("timestamp="));
    }

    @Test
    public void testMessageWithLargeCacheKeysList_ShouldHandleCorrectly() throws Exception {
        // Given
        List<String> largeCacheKeysList = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeCacheKeysList.add("cache:key:" + i);
        }

        CacheUpdateMessage message = new CacheUpdateMessage(
            CacheUpdateMessage.ActionType.BATCH_DELETE,
            CacheUpdateMessage.ResourceType.CASE,
            "batch_operation",
            largeCacheKeysList,
            "admin"
        );

        // When
        String json = objectMapper.writeValueAsString(message);
        CacheUpdateMessage deserializedMessage = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertEquals(1000, deserializedMessage.getCacheKeys().size());
        assertEquals(largeCacheKeysList, deserializedMessage.getCacheKeys());
    }

    @Test
    public void testMessageWithSpecialCharacters_ShouldHandleCorrectly() throws Exception {
        // Given
        CacheUpdateMessage message = new CacheUpdateMessage(
            CacheUpdateMessage.ActionType.UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "special:id/with\\chars",
            Arrays.asList("key:with:colons", "key/with/slashes", "key with spaces"),
            "user@domain.com"
        );

        // When
        String json = objectMapper.writeValueAsString(message);
        CacheUpdateMessage deserializedMessage = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertEquals("special:id/with\\chars", deserializedMessage.getResourceId());
        assertEquals("user@domain.com", deserializedMessage.getOperator());
        assertTrue(deserializedMessage.getCacheKeys().contains("key:with:colons"));
        assertTrue(deserializedMessage.getCacheKeys().contains("key/with/slashes"));
        assertTrue(deserializedMessage.getCacheKeys().contains("key with spaces"));
    }

    @Test
    public void testJsonSerialization_WithNullFields_ShouldHandleGracefully() throws Exception {
        // Given
        CacheUpdateMessage message = new CacheUpdateMessage();
        message.setAction(CacheUpdateMessage.ActionType.CREATE);
        message.setResourceType(CacheUpdateMessage.ResourceType.CASE);
        // 其他字段保持null

        // When
        String json = objectMapper.writeValueAsString(message);
        CacheUpdateMessage deserializedMessage = objectMapper.readValue(json, CacheUpdateMessage.class);

        // Then
        assertEquals(CacheUpdateMessage.ActionType.CREATE, deserializedMessage.getAction());
        assertEquals(CacheUpdateMessage.ResourceType.CASE, deserializedMessage.getResourceType());
        assertNull(deserializedMessage.getResourceId());
        assertNull(deserializedMessage.getCacheKeys());
        assertNull(deserializedMessage.getTimestamp());
        assertNull(deserializedMessage.getOperator());
    }

    @Test
    public void testMessageEqualsAndHashCode_ShouldWorkForComparison() {
        // Given
        CacheUpdateMessage message1 = new CacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            Arrays.asList("key1", "key2"),
            "admin"
        );
        Long timestamp = message1.getTimestamp();

        CacheUpdateMessage message2 = new CacheUpdateMessage();
        message2.setAction(CacheUpdateMessage.ActionType.CREATE);
        message2.setResourceType(CacheUpdateMessage.ResourceType.CASE);
        message2.setResourceId("123");
        message2.setCacheKeys(Arrays.asList("key1", "key2"));
        message2.setOperator("admin");
        message2.setTimestamp(timestamp);

        // Then - 虽然没有实现equals/hashCode，但可以比较字段
        assertEquals(message1.getAction(), message2.getAction());
        assertEquals(message1.getResourceType(), message2.getResourceType());
        assertEquals(message1.getResourceId(), message2.getResourceId());
        assertEquals(message1.getCacheKeys(), message2.getCacheKeys());
        assertEquals(message1.getOperator(), message2.getOperator());
        assertEquals(message1.getTimestamp(), message2.getTimestamp());
    }
}