package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

/**
 * 积分信息视图对象
 * Created by macro on 2026/01/13.
 */
public class UmsMemberIntegrationVO {

    @Schema(title = "会员ID")
    private Long id;

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "邮箱地址")
    private String email;

    @Schema(title = "当前积分")
    private Integer integration;

    @Schema(title = "冻结积分")
    private Integer freezeIntegration;

    @Schema(title = "历史积分数量")
    private Integer historyIntegration;

    @Schema(title = "成长值")
    private Integer growth;

    @Schema(title = "套餐等级: 0-免费, 1-基础, 2-标准, 3-专业, 4-企业")
    private Integer subscriptionLevel;

    @Schema(title = "每日积分限额")
    private Integer dailyCreditsLimit;

    @Schema(title = "当日已使用积分")
    private Integer dailyCreditsUsed;

    @Schema(title = "套餐到期日期")
    private Date subscriptionEndDate;

    @Schema(title = "每日免费积分")
    private Integer freeDailyCredits;

    @Schema(title = "今日已使用免费积分")
    private Integer usedTodayFree;

    @Schema(title = "注册时间")
    private Date createTime;

    @Schema(title = "账户状态: 0-禁用, 1-正常, 2-锁定")
    private Integer accountStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getFreezeIntegration() {
        return freezeIntegration;
    }

    public void setFreezeIntegration(Integer freezeIntegration) {
        this.freezeIntegration = freezeIntegration;
    }

    public Integer getHistoryIntegration() {
        return historyIntegration;
    }

    public void setHistoryIntegration(Integer historyIntegration) {
        this.historyIntegration = historyIntegration;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public Integer getSubscriptionLevel() {
        return subscriptionLevel;
    }

    public void setSubscriptionLevel(Integer subscriptionLevel) {
        this.subscriptionLevel = subscriptionLevel;
    }

    public Integer getDailyCreditsLimit() {
        return dailyCreditsLimit;
    }

    public void setDailyCreditsLimit(Integer dailyCreditsLimit) {
        this.dailyCreditsLimit = dailyCreditsLimit;
    }

    public Integer getDailyCreditsUsed() {
        return dailyCreditsUsed;
    }

    public void setDailyCreditsUsed(Integer dailyCreditsUsed) {
        this.dailyCreditsUsed = dailyCreditsUsed;
    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public Integer getFreeDailyCredits() {
        return freeDailyCredits;
    }

    public void setFreeDailyCredits(Integer freeDailyCredits) {
        this.freeDailyCredits = freeDailyCredits;
    }

    public Integer getUsedTodayFree() {
        return usedTodayFree;
    }

    public void setUsedTodayFree(Integer usedTodayFree) {
        this.usedTodayFree = usedTodayFree;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
    }
}
