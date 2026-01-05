package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 积分冻结请求
 * Created by code-executor on 2026-01-05.
 */
@Data
@Schema(description = "积分冻结请求参数")
public class CreditFreezeRequest {

    @Schema(description = "用户ID", required = true, example = "1")
    @NotNull(message = "用户ID不能为空")
    private Long memberId;

    @Schema(description = "冻结积分数量", required = true, example = "100")
    @NotNull(message = "冻结积分数量不能为空")
    @Min(value = 1, message = "冻结积分数量必须大于0")
    private Integer freezeAmount;

    @Schema(description = "业务ID（唯一，用于幂等性控制）", required = true, example = "AI_TASK_12345")
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    @Schema(description = "业务类型", required = true, example = "AI_TASK")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "备注说明", example = "AI任务积分冻结")
    private String note;
}
