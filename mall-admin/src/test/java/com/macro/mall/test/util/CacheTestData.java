package com.macro.mall.test.util;

import com.macro.mall.dto.CacheUpdateMessage;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 缓存测试数据工厂
 * 提供测试所需的各种数据对象
 */
public class CacheTestData {

    /**
     * 创建测试案例数据
     */
    public static CaseData createTestCaseData(Long id) {
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseData.setCategoryId(1L);
        caseData.setTitle("测试案例" + id);
        caseData.setContent("这是测试案例" + id + "的内容");
        caseData.setImage("test_image_" + id + ".jpg");
        caseData.setVideo("test_video_" + id + ".mp4");
        caseData.setTags("测试,案例" + id);
        caseData.setViewCount(100L + id);
        caseData.setLikeCount(10L + id);
        caseData.setHotScore(new BigDecimal("85.5"));
        caseData.setStatus(1);
        caseData.setShowStatus(1);
        caseData.setCreateTime(new Date());
        caseData.setUpdateTime(new Date());
        return caseData;
    }

    /**
     * 创建测试案例分类数据
     */
    public static CaseCategory createTestCaseCategory(Long id) {
        CaseCategory category = new CaseCategory();
        category.setId(id);
        category.setName("测试分类" + id);
        category.setDescription("测试分类" + id + "的描述");
        category.setIcon("test_icon_" + id + ".png");
        category.setSort(id.intValue());
        category.setStatus(1);
        category.setShowStatus(1);
        category.setCaseCount(10);
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        return category;
    }

    /**
     * 创建缓存更新消息
     */
    public static CacheUpdateMessage createCacheUpdateMessage(
            CacheUpdateMessage.ActionType action,
            CacheUpdateMessage.ResourceType resourceType,
            String resourceId,
            String operator) {

        List<String> cacheKeys = Arrays.asList(
            "mall:case:admin:data:" + resourceId,
            "mall:case:portal:detail:" + resourceId
        );

        return new CacheUpdateMessage(action, resourceType, resourceId, cacheKeys, operator);
    }

    /**
     * 创建测试用的缓存键列表
     */
    public static List<String> createTestCacheKeys(Long caseId) {
        return Arrays.asList(
            "mall:case:admin:data:" + caseId,
            "mall:case:admin:category:list",
            "mall:case:admin:data:hot",
            "mall:case:portal:detail:" + caseId,
            "mall:case:portal:category:list"
        );
    }

    /**
     * 创建批量测试案例ID列表
     */
    public static List<Long> createTestCaseIds() {
        return Arrays.asList(1L, 2L, 3L, 4L, 5L);
    }

    /**
     * 创建测试操作人员ID
     */
    public static String createTestOperator() {
        return "test_admin_user";
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
     * 创建无效的测试消息
     */
    public static String createInvalidMessageJson() {
        return "{\n" +
                "  \"invalid\": \"message\",\n" +
                "  \"missing\": \"required_fields\"\n" +
                "}";
    }

    /**
     * 创建过期的测试消息
     */
    public static CacheUpdateMessage createExpiredMessage() {
        CacheUpdateMessage message = createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.UPDATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            "test_user"
        );
        // 设置为10分钟前的时间戳
        message.setTimestamp(System.currentTimeMillis() - (10 * 60 * 1000));
        return message;
    }
}
