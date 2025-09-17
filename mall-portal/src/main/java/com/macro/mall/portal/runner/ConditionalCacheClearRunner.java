package com.macro.mall.portal.runner;

import com.macro.mall.portal.service.PortalCaseCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 有条件的应用启动缓存清理Runner
 * 可通过配置 app.cache.clear-on-startup=true/false 控制是否启动时清理缓存
 * @author Claude
 * @since 2025-09-16
 */
@Component
@Order(100)
@Slf4j
@ConditionalOnProperty(name = "app.cache.clear-on-startup", havingValue = "true", matchIfMissing = false)
public class ConditionalCacheClearRunner implements ApplicationRunner {

    @Autowired
    private PortalCaseCacheService portalCaseCacheService;

    @Value("${app.cache.clear-types:all}")
    private String clearTypes;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("================ 条件缓存清理开始 ================");
        log.info("清理类型配置: {}", clearTypes);

        try {
            if ("all".equalsIgnoreCase(clearTypes) || clearTypes.contains("case")) {
                portalCaseCacheService.delAllCache();
                log.info("✅ 案例缓存清理成功");
            }

            if (clearTypes.contains("category")) {
                portalCaseCacheService.delCaseCategoryCache();
                log.info("✅ 分类缓存清理成功");
            }

            if (clearTypes.contains("hot")) {
                portalCaseCacheService.delHotCaseCache();
                log.info("✅ 热门案例缓存清理成功");
            }

            if (clearTypes.contains("latest")) {
                portalCaseCacheService.delLatestCaseCache();
                log.info("✅ 最新案例缓存清理成功");
            }

            log.info("🎉 启动时缓存清理全部完成");

        } catch (Exception e) {
            log.error("❌ 启动时缓存清理失败", e);
        }

        log.info("================ 条件缓存清理结束 ================");
    }
}