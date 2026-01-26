package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 积分扣减请求
 * Created by code-executor on 2026-01-05.
 * Updated by code-executor on 2026-01-15: 添加 businessType 字段
 * Updated by code-executor on 2026-01-26: 添加 operateMan 字段
 */
@Data
@Schema(description = "积分扣减请求参数")
public class CreditDeductRequest {

    @Schema(description = "业务ID（对应冻结时的业务ID）", required = true, example = "AI_TASK_12345")
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    @Schema(description = "业务类型（对应冻结时的业务类型）", required = true, example = "video_generation")
    @NotBlank(message = "业务类型不能为空")
    private String businessType;

    @Schema(description = "扣减说明", example = "AI任务完成，确认扣减")
    private String note;

    @Schema(description = "操作人", example = "系统")
    private String operateMan;
}
