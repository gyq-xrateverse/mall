package com.macro.mall.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;

/**
 * 积分操作参数
 * Created by macro on 2026/01/13.
 */
@Getter
@Setter
public class UmsMemberIntegrationParam {
    @Schema(title = "会员ID")
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @Schema(title = "积分数量")
    @NotNull(message = "积分数量不能为空")
    private Integer integration;

    @Schema(title = "来源类型：0->购物；1->管理员修改；5->赠送")
    @NotNull(message = "来源类型不能为空")
    private Integer sourceType;

    @Schema(title = "操作备注")
    private String operateNote;
}
