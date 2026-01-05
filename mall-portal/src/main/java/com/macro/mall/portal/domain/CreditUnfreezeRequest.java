package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 积分释放请求
 * Created by code-executor on 2026-01-05.
 */
@Data
@Schema(description = "积分释放请求参数")
public class CreditUnfreezeRequest {

    @Schema(description = "业务ID（对应冻结时的业务ID）", required = true, example = "AI_TASK_12345")
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    @Schema(description = "释放原因", required = true, example = "AI任务失败，释放积分")
    @NotBlank(message = "释放原因不能为空")
    private String reason;
}
