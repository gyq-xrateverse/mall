package com.macro.mall.portal.test.util;

import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.dto.CacheUpdateMessage;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListResult;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 门户端缓存测试数据工厂
 */
public class PortalCacheTestData {

    /**
     * 创建测试案例详情数据
     */
    public static CaseDetailResult createTestCaseDetail(Long id) {
        CaseDetailResult detail = new CaseDetailResult();
        detail.setId(id);
        detail.setTitle("测试案例详情" + id);
        detail.setContent("这是测试案例" + id + "的详细内容");
        detail.setCategoryId(1L);
        detail.setImage("test_image_" + id + ".jpg");
        detail.setVideo("test_video_" + id + ".mp4");
        detail.setStatus(1);
        detail.setShowStatus(1);
        detail.setCreateTime(new Date());
        return detail;
    }

    /**
     * 创建测试案例列表数据
     */
    public static CaseListResult createTestCaseListItem(Long id) {
        CaseListResult item = new CaseListResult();
        item.setId(id);
        item.setTitle("测试案例列表项" + id);
        item.setCategoryId(1L);
        item.setImage("test_image_" + id + ".jpg");
        item.setViewCount(100L + id);
        item.setLikeCount(10L + id);
        item.setCreateTime(new Date());
        return item;
    }

    /**
     * 创建测试案例分类数据
     */
    public static CaseCategory createTestCaseCategory(Long id) {
        CaseCategory category = new CaseCategory();
        category.setId(id);
        category.setName("测试分类" + id);
        category.setCaseCount(10);
        category.setStatus(1);
        category.setShowStatus(1);
        category.setSort(id.intValue());
        return category;
    }

    /**
     * 创建测试案例分类列表
     */
    public static List<CaseCategory> createTestCaseCategoryList() {
        return Arrays.asList(
            createTestCaseCategory(1L),
            createTestCaseCategory(2L),
            createTestCaseCategory(3L)
        );
    }

    /**
     * 创建测试案例列表
     */
    public static List<CaseListResult> createTestCaseList() {
        return Arrays.asList(
            createTestCaseListItem(1L),
            createTestCaseListItem(2L),
            createTestCaseListItem(3L)
        );
    }

    /**
     * 创建缓存更新消息
     */
    public static CacheUpdateMessage createCacheUpdateMessage(
            CacheUpdateMessage.ActionType action,
            CacheUpdateMessage.ResourceType resourceType,
            String resourceId) {

        CacheUpdateMessage message = new CacheUpdateMessage();
        message.setAction(action);
        message.setResourceType(resourceType);
        message.setResourceId(resourceId);
        message.setCacheKeys(Arrays.asList("test:key:1", "test:key:2"));
        message.setTimestamp(System.currentTimeMillis());
        message.setOperator("test_user");
        return message;
    }

    /**
     * 创建过期的缓存更新消息
     */
    public static CacheUpdateMessage createExpiredCacheUpdateMessage() {
        CacheUpdateMessage message = createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123"
        );
        // 设置为10分钟前
        message.setTimestamp(System.currentTimeMillis() - (10 * 60 * 1000));
        return message;
    }

    /**
     * 创建测试消息JSON
     */
    public static String createTestMessageJson() {
        return "{\n" +
                "  \"action\": \"CREATE\",\n" +
                "  \"resourceType\": \"CASE\",\n" +
                "  \"resourceId\": \"123\",\n" +
                "  \"cacheKeys\": [\"test:key:1\", \"test:key:2\"],\n" +
                "  \"timestamp\": " + System.currentTimeMillis() + ",\n" +
                "  \"operator\": \"test_user\"\n" +
                "}";
    }

    /**
     * 创建过大的消息JSON
     */
    public static String createOversizedMessageJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"action\": \"CREATE\", \"data\": \"");
        // 创建超过10000字符的消息
        for (int i = 0; i < 10001; i++) {
            sb.append("x");
        }
        sb.append("\"}");
        return sb.toString();
    }

    /**
     * 创建无效的消息JSON
     */
    public static String createInvalidMessageJson() {
        return "{invalid json format";
    }

    /**
     * 创建空消息
     */
    public static String createEmptyMessage() {
        return "";
    }

    /**
     * 创建测试频道名称
     */
    public static String createTestChannel() {
        return "mall:cache:update";
    }

    /**
     * 创建错误的频道名称
     */
    public static String createWrongChannel() {
        return "wrong:channel:name";
    }
}