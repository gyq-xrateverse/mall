package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CaseCategoryParam;
import com.macro.mall.dto.CaseCategoryQueryParam;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.service.CaseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 案例分类管理Controller
 */
@Controller
@Tag(name = "CaseCategoryController", description = "案例分类管理")
@RequestMapping("/caseCategory")
public class CaseCategoryController {
    
    @Autowired
    private CaseCategoryService caseCategoryService;

    @Operation(summary = "添加案例分类")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody CaseCategoryParam caseCategoryParam) {
        int count = caseCategoryService.create(caseCategoryParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "修改案例分类")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,
                         @Validated
                         @RequestBody CaseCategoryParam caseCategoryParam) {
        int count = caseCategoryService.update(id, caseCategoryParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "分页查询案例分类")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<CaseCategory>> getList(CaseCategoryQueryParam queryParam) {
        List<CaseCategory> caseCategoryList = caseCategoryService.list(queryParam);
        return CommonResult.success(CommonPage.restPage(caseCategoryList));
    }

    @Operation(summary = "根据id获取案例分类")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CaseCategory> getItem(@PathVariable Long id) {
        CaseCategory caseCategory = caseCategoryService.getItem(id);
        return CommonResult.success(caseCategory);
    }

    @Operation(summary = "删除案例分类")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = caseCategoryService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量删除案例分类")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = caseCategoryService.deleteBatch(ids);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "修改显示状态")
    @RequestMapping(value = "/update/showStatus", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateShowStatus(@RequestParam("ids") List<Long> ids, 
                                       @RequestParam("showStatus") Integer showStatus) {
        int count = caseCategoryService.updateShowStatus(ids, showStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "获取所有启用的案例分类")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<CaseCategory>> listAll() {
        List<CaseCategory> caseCategoryList = caseCategoryService.listAll();
        return CommonResult.success(caseCategoryList);
    }
}