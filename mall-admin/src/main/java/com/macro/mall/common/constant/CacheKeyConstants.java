package com.macro.mall.common.constant;

/**
 * 缓存键常量管理类
 * 用于统一管理案例相关的缓存键和消息频道
 */
public class CacheKeyConstants {

    // 管理端缓存键前缀
    public static final String ADMIN_CASE_PREFIX = "mall:case:admin:";

    // 门户端缓存键前缀
    public static final String PORTAL_CASE_PREFIX = "mall:case:portal:";

    // 消息频道
    public static final String CACHE_UPDATE_CHANNEL = "mall:cache:update";

    // 管理端具体缓存键
    public static final String ADMIN_CASE_CATEGORY = "category";
    public static final String ADMIN_CASE_CATEGORY_LIST = "category:list";
    public static final String ADMIN_CASE_DATA = "data";
    public static final String ADMIN_CASE_DATA_HOT = "data:hot";
    public static final String ADMIN_CASE_DATA_LATEST = "data:latest";

    // 门户端具体缓存键
    public static final String PORTAL_CASE_CATEGORY_LIST = "category:list";
    public static final String PORTAL_CASE_DETAIL = "detail";
    public static final String PORTAL_CASE_HOT = "hot";
    public static final String PORTAL_CASE_LATEST = "latest";

    // 缓存键构建方法
    public static String buildAdminCaseKey(String key, Object... params) {
        StringBuilder builder = new StringBuilder(ADMIN_CASE_PREFIX);
        builder.append(key);
        for (Object param : params) {
            builder.append(":").append(param);
        }
        return builder.toString();
    }

    public static String buildPortalCaseKey(String key, Object... params) {
        StringBuilder builder = new StringBuilder(PORTAL_CASE_PREFIX);
        builder.append(key);
        for (Object param : params) {
            builder.append(":").append(param);
        }
        return builder.toString();
    }

    // 构建具体的缓存键
    public static String getAdminCaseCategoryKey(Long categoryId) {
        return buildAdminCaseKey(ADMIN_CASE_CATEGORY, categoryId);
    }

    public static String getAdminCaseCategoryListKey() {
        return buildAdminCaseKey(ADMIN_CASE_CATEGORY_LIST);
    }

    public static String getAdminCaseDataKey(Long caseId) {
        return buildAdminCaseKey(ADMIN_CASE_DATA, caseId);
    }

    public static String getAdminCaseDataHotKey() {
        return buildAdminCaseKey(ADMIN_CASE_DATA_HOT);
    }

    public static String getAdminCaseDataLatestKey() {
        return buildAdminCaseKey(ADMIN_CASE_DATA_LATEST);
    }

    public static String getPortalCaseCategoryListKey() {
        return buildPortalCaseKey(PORTAL_CASE_CATEGORY_LIST);
    }

    public static String getPortalCaseDetailKey(Long caseId) {
        return buildPortalCaseKey(PORTAL_CASE_DETAIL, caseId);
    }

    public static String getPortalCaseHotKey(Integer size) {
        return buildPortalCaseKey(PORTAL_CASE_HOT, size);
    }

    public static String getPortalCaseLatestKey(Integer size) {
        return buildPortalCaseKey(PORTAL_CASE_LATEST, size);
    }

    // 用于模式匹配的通配符方法
    public static String getPortalCaseHotPattern() {
        return buildPortalCaseKey(PORTAL_CASE_HOT, "*");
    }

    public static String getPortalCaseLatestPattern() {
        return buildPortalCaseKey(PORTAL_CASE_LATEST, "*");
    }
}