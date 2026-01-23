package com.macro.mall.integration.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

/**
 * 释放冻结积分参数
 * Created by macro on 2026/01/13.
 */
@Schema(description = "释放冻结积分参数")
public class UmsMemberReleaseParam {

    @Schema(description = "业务ID", required = true)
    @NotBlank(message = "业务ID不能为空")
    private String businessId;

    @Schema(description = "操作备注")
    private String operateNote;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getOperateNote() {
        return operateNote;
    }

    public void setOperateNote(String operateNote) {
        this.operateNote = operateNote;
    }
}
