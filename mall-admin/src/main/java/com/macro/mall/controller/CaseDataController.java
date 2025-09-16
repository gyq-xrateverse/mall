package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.macro.mall.service.CaseDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 案例数据管理Controller
 */
@Controller
@Tag(name = "CaseDataController", description = "案例数据管理")
@RequestMapping("/caseData")
public class CaseDataController {
    
    @Autowired
    private CaseDataService caseDataService;

    @Operation(summary = "添加案例数据")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@Validated @RequestBody CaseDataParam caseDataParam) {
        int count = caseDataService.create(caseDataParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "修改案例数据")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Long id,
                         @Validated
                         @RequestBody CaseDataParam caseDataParam) {
        int count = caseDataService.update(id, caseDataParam);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "分页查询案例数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<CaseDataResult>> getList(CaseDataQueryParam queryParam) {
        List<CaseDataResult> caseDataList = caseDataService.list(queryParam);
        return CommonResult.success(CommonPage.restPage(caseDataList));
    }

    @Operation(summary = "根据id获取案例数据")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CaseDataResult> getItem(@PathVariable Long id) {
        CaseDataResult caseData = caseDataService.getItem(id);
        return CommonResult.success(caseData);
    }

    @Operation(summary = "删除案例数据")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Long id) {
        int count = caseDataService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "批量删除案例数据")
    @RequestMapping(value = "/delete/batch", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = caseDataService.deleteBatch(ids);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "修改状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam("ids") List<Long> ids, 
                                   @RequestParam("status") Integer status) {
        int count = caseDataService.updateStatus(ids, status);
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
        int count = caseDataService.updateShowStatus(ids, showStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @Operation(summary = "审核案例")
    @RequestMapping(value = "/approve/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult approve(@PathVariable Long id, @RequestParam("status") Integer status) {
        int count = caseDataService.approve(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }
}