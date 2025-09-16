package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.service.PortalCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台案例相关Controller
 */
@Controller
@Tag(name = "PortalCaseController", description = "前台案例管理")
@RequestMapping("/api/case")
public class PortalCaseController {

    @Autowired
    private PortalCaseService portalCaseService;

    @Operation(summary = "获取案例分类列表")
    @RequestMapping(value = "/categoryList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CaseCategory>> getCategoryList() {
        List<CaseCategory> categoryList = portalCaseService.getCategoryList();
        return CommonResult.success(categoryList);
    }

    @Operation(summary = "分页获取案例列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<CaseListResult>> getCaseList(CaseListQueryParam queryParam) {
        List<CaseListResult> caseList = portalCaseService.getCaseList(queryParam);
        return CommonResult.success(CommonPage.restPage(caseList));
    }

    @Operation(summary = "获取案例详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CaseDetailResult> getCaseDetail(@PathVariable Long id) {
        CaseDetailResult caseDetail = portalCaseService.getCaseDetail(id);
        return CommonResult.success(caseDetail);
    }

    @Operation(summary = "搜索案例")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<CaseListResult>> searchCase(@RequestParam String keyword,
                                                             @RequestParam(value = "pageSize", defaultValue = "12") Integer pageSize,
                                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        CaseListQueryParam queryParam = new CaseListQueryParam();
        queryParam.setKeyword(keyword);
        queryParam.setPageSize(pageSize);
        queryParam.setPageNum(pageNum);
        List<CaseListResult> caseList = portalCaseService.getCaseList(queryParam);
        return CommonResult.success(CommonPage.restPage(caseList));
    }

    @Operation(summary = "获取热门案例")
    @RequestMapping(value = "/hot", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CaseListResult>> getHotCaseList(@RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<CaseListResult> hotCaseList = portalCaseService.getHotCaseList(size);
        return CommonResult.success(hotCaseList);
    }

    @Operation(summary = "获取最新案例")
    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CaseListResult>> getLatestCaseList(@RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<CaseListResult> latestCaseList = portalCaseService.getLatestCaseList(size);
        return CommonResult.success(latestCaseList);
    }

    @Operation(summary = "根据分类获取案例")
    @RequestMapping(value = "/category/{categoryId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<CaseListResult>> getCaseByCategoryId(@PathVariable Long categoryId,
                                                                      @RequestParam(value = "pageSize", defaultValue = "12") Integer pageSize,
                                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        CaseListQueryParam queryParam = new CaseListQueryParam();
        queryParam.setCategoryId(categoryId);
        queryParam.setPageSize(pageSize);
        queryParam.setPageNum(pageNum);
        List<CaseListResult> caseList = portalCaseService.getCaseList(queryParam);
        return CommonResult.success(CommonPage.restPage(caseList));
    }

    @Operation(summary = "案例点赞")
    @RequestMapping(value = "/like/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Object> likeCase(@PathVariable Long id) {
        int count = portalCaseService.likeCase(id);
        if (count > 0) {
            return CommonResult.success("点赞成功");
        } else {
            return CommonResult.failed("点赞失败");
        }
    }

    @Operation(summary = "增加案例浏览量")
    @RequestMapping(value = "/view/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Object> viewCase(@PathVariable Long id) {
        int count = portalCaseService.viewCase(id);
        if (count > 0) {
            return CommonResult.success("浏览量增加成功");
        } else {
            return CommonResult.failed("浏览量增加失败");
        }
    }

    @Operation(summary = "懒加载获取案例列表")
    @RequestMapping(value = "/lazy-list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CaseListResult>> getLazyCaseList(@RequestParam(value = "lastId", defaultValue = "0") Long lastId,
                                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                            @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                            @RequestParam(value = "queryType", defaultValue = "all") String queryType) {
        List<CaseListResult> caseList = portalCaseService.getLazyCaseList(lastId, pageSize, categoryId, queryType);
        return CommonResult.success(caseList);
    }

    @Operation(summary = "无限滚动获取更多案例")
    @RequestMapping(value = "/more", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CaseListResult>> getMoreCases(@RequestParam("lastId") Long lastId,
                                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                         @RequestParam(value = "categoryId", required = false) Long categoryId) {
        List<CaseListResult> caseList = portalCaseService.getMoreCases(lastId, pageSize, categoryId);
        return CommonResult.success(caseList);
    }
}
