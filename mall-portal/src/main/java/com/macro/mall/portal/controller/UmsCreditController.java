package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsIntegrationFreeze;
import com.macro.mall.portal.domain.CreditBalanceResult;
import com.macro.mall.portal.domain.CreditDeductRequest;
import com.macro.mall.portal.domain.CreditFreezeRequest;
import com.macro.mall.portal.domain.CreditUnfreezeRequest;
import com.macro.mall.portal.service.UmsCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 积分管理Controller
 * 提供积分冻结、扣减、释放和余额查询接口
 * Created by code-executor on 2026-01-05.
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/credits")
@Tag(name = "UmsCreditController", description = "积分管理接口")
public class UmsCreditController {

    @Autowired
    private UmsCreditService creditService;

    @Operation(summary = "冻结用户积分",
        description = "冻结指定数量的用户积分，用于AI任务等场景。支持幂等性控制，相同businessId多次调用只会冻结一次")
    @PostMapping("/freeze")
    public CommonResult<UmsIntegrationFreeze> freezeCredit(@Valid @RequestBody CreditFreezeRequest request) {
        try {
            log.info("接收冻结积分请求: memberId={}, amount={}, businessId={}",
                request.getMemberId(), request.getFreezeAmount(), request.getBusinessId());

            UmsIntegrationFreeze freeze = creditService.freezeCredit(request);

            log.info("冻结积分成功: freezeId={}, memberId={}, amount={}",
                freeze.getId(), request.getMemberId(), request.getFreezeAmount());

            return CommonResult.success(freeze, "积分冻结成功");
        } catch (Exception e) {
            log.error("冻结积分失败: memberId={}, amount={}, businessId={}",
                request.getMemberId(), request.getFreezeAmount(), request.getBusinessId(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "扣减已冻结的积分",
        description = "确认扣减已冻结的积分，用于AI任务成功完成等场景。支持幂等性控制")
    @PostMapping("/deduct")
    public CommonResult<Boolean> deductCredit(@Valid @RequestBody CreditDeductRequest request) {
        try {
            log.info("接收扣减积分请求: businessId={}", request.getBusinessId());

            Boolean result = creditService.deductCredit(request);

            if (Boolean.TRUE.equals(result)) {
                log.info("扣减积分成功: businessId={}", request.getBusinessId());
                return CommonResult.success(null, "积分扣减成功");
            } else {
                log.warn("扣减积分失败: businessId={}", request.getBusinessId());
                return CommonResult.failed("积分扣减失败");
            }
        } catch (Exception e) {
            log.error("扣减积分失败: businessId={}", request.getBusinessId(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "释放已冻结的积分",
        description = "释放已冻结的积分并返还给用户，用于AI任务失败等场景。支持幂等性控制")
    @PostMapping("/unfreeze")
    public CommonResult<Boolean> unfreezeCredit(@Valid @RequestBody CreditUnfreezeRequest request) {
        try {
            log.info("接收释放积分请求: businessId={}, reason={}",
                request.getBusinessId(), request.getReason());

            Boolean result = creditService.unfreezeCredit(request);

            if (Boolean.TRUE.equals(result)) {
                log.info("释放积分成功: businessId={}", request.getBusinessId());
                return CommonResult.success(null, "积分释放成功");
            } else {
                log.warn("释放积分失败: businessId={}", request.getBusinessId());
                return CommonResult.failed("积分释放失败");
            }
        } catch (Exception e) {
            log.error("释放积分失败: businessId={}", request.getBusinessId(), e);
            return CommonResult.failed(e.getMessage());
        }
    }

    @Operation(summary = "查询用户积分余额",
        description = "查询用户的总积分、冻结积分和可用积分")
    @GetMapping("/balance/{memberId}")
    public CommonResult<CreditBalanceResult> getBalance(
        @Parameter(description = "用户ID", required = true)
        @NotNull(message = "用户ID不能为空")
        @PathVariable Long memberId) {
        try {
            log.info("接收查询积分余额请求: memberId={}", memberId);

            CreditBalanceResult result = creditService.getBalance(memberId);

            log.info("查询积分余额成功: memberId={}, total={}, frozen={}, available={}",
                memberId, result.getTotalIntegration(), result.getFrozenIntegration(),
                result.getAvailableIntegration());

            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("查询积分余额失败: memberId={}", memberId, e);
            return CommonResult.failed(e.getMessage());
        }
    }
}
