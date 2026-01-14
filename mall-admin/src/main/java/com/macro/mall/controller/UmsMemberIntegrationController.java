package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.*;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.service.UmsMemberIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 用户积分管理Controller
 * Created by macro on 2026/01/13.
 */
@Controller
@Tag(name = "UmsMemberIntegrationController", description = "用户积分管理")
@RequestMapping("/admin/integration")
public class UmsMemberIntegrationController {

    @Autowired
    private UmsMemberIntegrationService integrationService;

    @Operation(summary = "查询用户积分列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsMemberIntegrationVO>> list(UmsMemberIntegrationQuery query) {
        List<UmsMemberIntegrationVO> list = integrationService.listIntegration(query);
        return CommonResult.success(CommonPage.restPage(list));
    }

    @Operation(summary = "查看冻结详情")
    @RequestMapping(value = "/freeze/{memberId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsIntegrationFreeze>> getFreezeList(@PathVariable Long memberId) {
        List<UmsIntegrationFreeze> list = integrationService.getFreezeList(memberId);
        return CommonResult.success(list);
    }

    @Operation(summary = "查看积分历史")
    @RequestMapping(value = "/history/{memberId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<UmsIntegrationChangeHistory>> getHistory(
            @PathVariable Long memberId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        List<UmsIntegrationChangeHistory> list = integrationService.getHistory(memberId, pageNum, pageSize);
        return CommonResult.success(CommonPage.restPage(list));
    }

    @Operation(summary = "增加积分")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> addIntegration(@Validated @RequestBody UmsMemberIntegrationParam param) {
        int count = integrationService.addIntegration(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "减少积分")
    @RequestMapping(value = "/reduce", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> reduceIntegration(@Validated @RequestBody UmsMemberIntegrationParam param) {
        int count = integrationService.reduceIntegration(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "赠送积分")
    @RequestMapping(value = "/gift", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> giftIntegration(@Validated @RequestBody UmsMemberIntegrationParam param) {
        int count = integrationService.giftIntegration(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "冻结积分")
    @RequestMapping(value = "/freeze", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> freezeIntegration(@Valid @RequestBody UmsMemberFreezeParam param) {
        int count = integrationService.freezeIntegration(
                param.getMemberId(),
                param.getAmount(),
                param.getBusinessId(),
                param.getBusinessType(),
                param.getOperateNote()
        );
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "解冻并扣减积分")
    @RequestMapping(value = "/deduct", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> deductIntegration(@Valid @RequestBody UmsMemberDeductParam param) {
        int count = integrationService.deductIntegration(param.getBusinessId(), param.getSourceType());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @Operation(summary = "释放冻结积分")
    @RequestMapping(value = "/release", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> releaseIntegration(@Valid @RequestBody UmsMemberReleaseParam param) {
        int count = integrationService.releaseIntegration(param.getBusinessId(), param.getOperateNote());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
