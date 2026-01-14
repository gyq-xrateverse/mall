package com.macro.mall.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分冻结表
 * ums_integration_freeze
 */
public class UmsIntegrationFreeze implements Serializable {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 冻结积分数量
     */
    private Integer freezeAmount;

    /**
     * 业务ID（AI任务ID）
     */
    private String businessId;

    /**
     * 业务类型（如：video_generation）
     */
    private String businessType;

    /**
     * 状态：0-冻结中，1-已扣减，2-已释放
     */
    private Integer status;

    /**
     * 创建时间（冻结时间）
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 扣减时间
     */
    private Date deductTime;

    /**
     * 释放时间
     */
    private Date releaseTime;

    /**
     * 操作备注
     */
    private String operateNote;

    private static final long serialVersionUID = 1L;

    /**
     * 状态常量
     */
    public static class Status {
        public static final int FROZEN = 0;      // 冻结中
        public static final int DEDUCTED = 1;    // 已扣减
        public static final int RELEASED = 2;    // 已释放
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Integer getFreezeAmount() {
        return freezeAmount;
    }

    public void setFreezeAmount(Integer freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getDeductTime() {
        return deductTime;
    }

    public void setDeductTime(Date deductTime) {
        this.deductTime = deductTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getOperateNote() {
        return operateNote;
    }

    public void setOperateNote(String operateNote) {
        this.operateNote = operateNote;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", freezeAmount=").append(freezeAmount);
        sb.append(", businessId=").append(businessId);
        sb.append(", businessType=").append(businessType);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", deductTime=").append(deductTime);
        sb.append(", releaseTime=").append(releaseTime);
        sb.append(", operateNote=").append(operateNote);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
