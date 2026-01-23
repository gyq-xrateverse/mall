package com.macro.mall.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 解冻并扣减积分参数
 * Created by macro on 2026/01/13.
 */
@Schema(description = "解冻并扣减积分参数")
public class UmsMemberDeductParam {

    @Schema(description = "业务ID", required = true)
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    @Schema(description = "来源类型", required = true)
    @NotNull(message = "来源类型不能为空")
    private Integer sourceType;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
}
