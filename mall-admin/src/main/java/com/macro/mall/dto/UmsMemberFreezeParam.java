package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 冻结积分参数
 * Created by macro on 2026/01/13.
 */
@Schema(description = "冻结积分参数")
public class UmsMemberFreezeParam {

    @Schema(description = "会员ID", required = true)
    @NotNull(message = "会员ID不能为空")
    private Long memberId;

    @Schema(description = "冻结积分数量", required = true)
    @NotNull(message = "冻结积分数量不能为空")
    @Min(value = 1, message = "冻结积分数量必须大于0")
    private Integer amount;

    @Schema(description = "业务ID", required = true)
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    @Schema(description = "业务类型", required = true)
    @NotNull(message = "业务类型不能为空")
    private Integer businessType;

    @Schema(description = "操作备注")
    private String operateNote;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getOperateNote() {
        return operateNote;
    }

    public void setOperateNote(String operateNote) {
        this.operateNote = operateNote;
    }
}
