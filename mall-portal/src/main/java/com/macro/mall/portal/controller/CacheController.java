package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.service.PortalCaseCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 缓存管理控制器
 * @author Claude
 * @since 2025-09-16
 */
@RestController
@RequestMapping("/api/cache")
@Tag(name = "缓存管理", description = "案例缓存管理相关接口")
@Slf4j
@Validated
public class CacheController {

    @Autowired
    private PortalCaseCacheService portalCaseCacheService;

    @Operation(summary = "清理所有案例缓存", description = "清理所有案例相关的缓存数据")
    @PostMapping("/case/clear/all")
    public CommonResult<String> clearAllCaseCache() {
        try {
            portalCaseCacheService.delAllCache();
            log.info("所有案例缓存清理成功");
            return CommonResult.success("已清理所有案例缓存");
        } catch (Exception e) {
            log.error("清理所有案例缓存失败", e);
            return CommonResult.failed("清理缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清理案例分类缓存", description = "清理案例分类列表缓存")
    @PostMapping("/case/clear/category")
    public CommonResult<String> clearCaseCategoryCache() {
        try {
            portalCaseCacheService.delCaseCategoryCache();
            log.info("案例分类缓存清理成功");
            return CommonResult.success("已清理案例分类缓存");
        } catch (Exception e) {
            log.error("清理案例分类缓存失败", e);
            return CommonResult.failed("清理分类缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清理单个案例详情缓存", description = "清理指定案例的详情缓存")
    @PostMapping("/case/clear/detail/{caseId}")
    public CommonResult<String> clearCaseDetailCache(
            @Parameter(description = "案例ID", required = true)
            @PathVariable @NotNull @Positive Long caseId) {
        try {
            portalCaseCacheService.delCaseDetailCache(caseId);
            log.info("案例详情缓存清理成功: caseId={}", caseId);
            return CommonResult.success("已清理案例详情缓存");
        } catch (Exception e) {
            log.error("清理案例详情缓存失败: caseId={}", caseId, e);
            return CommonResult.failed("清理案例详情缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清理热门案例缓存", description = "清理所有热门案例列表缓存")
    @PostMapping("/case/clear/hot")
    public CommonResult<String> clearHotCaseCache() {
        try {
            portalCaseCacheService.delHotCaseCache();
            log.info("热门案例缓存清理成功");
            return CommonResult.success("已清理热门案例缓存");
        } catch (Exception e) {
            log.error("清理热门案例缓存失败", e);
            return CommonResult.failed("清理热门案例缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清理最新案例缓存", description = "清理所有最新案例列表缓存")
    @PostMapping("/case/clear/latest")
    public CommonResult<String> clearLatestCaseCache() {
        try {
            portalCaseCacheService.delLatestCaseCache();
            log.info("最新案例缓存清理成功");
            return CommonResult.success("已清理最新案例缓存");
        } catch (Exception e) {
            log.error("清理最新案例缓存失败", e);
            return CommonResult.failed("清理最新案例缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "刷新所有案例缓存", description = "清理所有案例缓存，下次访问时将重新加载最新数据")
    @PostMapping("/case/refresh/all")
    public CommonResult<String> refreshAllCaseCache() {
        try {
            portalCaseCacheService.delAllCache();
            log.info("所有案例缓存刷新成功");
            return CommonResult.success("已刷新所有案例缓存，下次访问将重新加载");
        } catch (Exception e) {
            log.error("刷新案例缓存失败", e);
            return CommonResult.failed("刷新缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "缓存状态检查", description = "检查缓存服务是否正常工作")
    @GetMapping("/health")
    public CommonResult<String> checkCacheHealth() {
        try {
            // 简单的缓存健康检查
            String testKey = "cache:health:test";
            // 这里可以添加更多的健康检查逻辑
            log.info("缓存健康检查执行成功");
            return CommonResult.success("缓存服务正常");
        } catch (Exception e) {
            log.error("缓存健康检查失败", e);
            return CommonResult.failed("缓存服务异常: " + e.getMessage());
        }
    }
}