package com.macro.mall.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 积分余额查询结果
 * Created by code-executor on 2026-01-05.
 */
@Data
@Schema(description = "积分余额查询结果")
public class CreditBalanceResult {

    @Schema(description = "总积分（包含冻结和可用）", example = "1000")
    private Integer totalIntegration;

    @Schema(description = "冻结积分", example = "100")
    private Integer frozenIntegration;

    @Schema(description = "可用积分（总积分-冻结积分）", example = "900")
    private Integer availableIntegration;

    @Schema(description = "用户ID", example = "1")
    private Long memberId;
}
