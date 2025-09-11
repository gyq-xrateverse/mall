package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsSubscriptionUsage implements Serializable {
    private Long id;

    @Schema(title = "用户ID")
    private Long memberId;

    @Schema(title = "套餐订单ID")
    private Long subscriptionOrderId;

    @Schema(title = "使用日期")
    private Date usageDate;

    @Schema(title = "当日使用积分")
    private Integer creditsUsed;

    @Schema(title = "当日创建任务数")
    private Integer tasksCreated;

    @Schema(title = "创建时间")
    private Date createdTime;

    private static final long serialVersionUID = 1L;

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

    public Long getSubscriptionOrderId() {
        return subscriptionOrderId;
    }

    public void setSubscriptionOrderId(Long subscriptionOrderId) {
        this.subscriptionOrderId = subscriptionOrderId;
    }

    public Date getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(Date usageDate) {
        this.usageDate = usageDate;
    }

    public Integer getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(Integer creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public Integer getTasksCreated() {
        return tasksCreated;
    }

    public void setTasksCreated(Integer tasksCreated) {
        this.tasksCreated = tasksCreated;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberId=").append(memberId);
        sb.append(", subscriptionOrderId=").append(subscriptionOrderId);
        sb.append(", usageDate=").append(usageDate);
        sb.append(", creditsUsed=").append(creditsUsed);
        sb.append(", tasksCreated=").append(tasksCreated);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}