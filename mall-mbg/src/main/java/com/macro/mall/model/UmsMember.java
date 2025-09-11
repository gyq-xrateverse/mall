package com.macro.mall.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;

public class UmsMember implements Serializable {
    private Long id;

    private Long memberLevelId;

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "帐号启用状态:0->禁用；1->启用")
    private Integer status;

    @Schema(title = "注册时间")
    private Date createTime;

    @Schema(title = "头像")
    private String icon;

    @Schema(title = "性别：0->未知；1->男；2->女")
    private Integer gender;

    @Schema(title = "生日")
    private Date birthday;

    @Schema(title = "所做城市")
    private String city;

    @Schema(title = "职业")
    private String job;

    @Schema(title = "个性签名")
    private String personalizedSignature;

    @Schema(title = "用户来源")
    private Integer sourceType;

    @Schema(title = "积分")
    private Integer integration;

    @Schema(title = "成长值")
    private Integer growth;

    @Schema(title = "剩余抽奖次数")
    private Integer luckeyCount;

    @Schema(title = "历史积分数量")
    private Integer historyIntegration;

    // ==================== 白鹿AI平台扩展字段 ====================
    
    // 用户认证相关字段
    @Schema(title = "邮箱地址")
    private String email;
    
    @Schema(title = "微信OpenID")
    private String wechatOpenid;
    
    @Schema(title = "Google用户ID")
    private String googleId;
    
    @Schema(title = "注册方式: 1-邮箱, 2-微信, 3-Google")
    private Integer registerType;
    
    @Schema(title = "邮箱验证状态: 0-未验证, 1-已验证")
    private Integer emailVerified;
    
    @Schema(title = "最后登录时间")
    private Date lastLoginTime;
    
    @Schema(title = "头像URL")
    private String avatarUrl;
    
    @Schema(title = "账户状态: 0-禁用, 1-正常, 2-锁定")
    private Integer accountStatus;
    
    // 积分系统相关字段
    @Schema(title = "每日免费积分")
    private Integer freeDailyCredits;
    
    @Schema(title = "今日已使用免费积分")
    private Integer usedTodayFree;
    
    @Schema(title = "最后重置日期")
    private Date lastResetDate;
    
    // 套餐相关字段
    @Schema(title = "当前有效套餐订单ID")
    private Long currentSubscriptionId;
    
    @Schema(title = "套餐等级: 0-免费, 1-基础, 2-标准, 3-专业, 4-企业")
    private Integer subscriptionLevel;
    
    @Schema(title = "每日积分限额")
    private Integer dailyCreditsLimit;
    
    @Schema(title = "当日已使用积分")
    private Integer dailyCreditsUsed;
    
    @Schema(title = "套餐到期日期")
    private Date subscriptionEndDate;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberLevelId() {
        return memberLevelId;
    }

    public void setMemberLevelId(Long memberLevelId) {
        this.memberLevelId = memberLevelId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPersonalizedSignature() {
        return personalizedSignature;
    }

    public void setPersonalizedSignature(String personalizedSignature) {
        this.personalizedSignature = personalizedSignature;
    }

    public Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public Integer getLuckeyCount() {
        return luckeyCount;
    }

    public void setLuckeyCount(Integer luckeyCount) {
        this.luckeyCount = luckeyCount;
    }

    public Integer getHistoryIntegration() {
        return historyIntegration;
    }

    public void setHistoryIntegration(Integer historyIntegration) {
        this.historyIntegration = historyIntegration;
    }

    // ==================== 白鹿AI平台扩展字段的Getter/Setter ====================
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWechatOpenid() {
        return wechatOpenid;
    }

    public void setWechatOpenid(String wechatOpenid) {
        this.wechatOpenid = wechatOpenid;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public Integer getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
    }

    public Integer getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Integer emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
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

    public Date getLastResetDate() {
        return lastResetDate;
    }

    public void setLastResetDate(Date lastResetDate) {
        this.lastResetDate = lastResetDate;
    }

    public Long getCurrentSubscriptionId() {
        return currentSubscriptionId;
    }

    public void setCurrentSubscriptionId(Long currentSubscriptionId) {
        this.currentSubscriptionId = currentSubscriptionId;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", memberLevelId=").append(memberLevelId);
        sb.append(", username=").append(username);
        sb.append(", password=").append(password);
        sb.append(", nickname=").append(nickname);
        sb.append(", phone=").append(phone);
        sb.append(", status=").append(status);
        sb.append(", createTime=").append(createTime);
        sb.append(", icon=").append(icon);
        sb.append(", gender=").append(gender);
        sb.append(", birthday=").append(birthday);
        sb.append(", city=").append(city);
        sb.append(", job=").append(job);
        sb.append(", personalizedSignature=").append(personalizedSignature);
        sb.append(", sourceType=").append(sourceType);
        sb.append(", integration=").append(integration);
        sb.append(", growth=").append(growth);
        sb.append(", luckeyCount=").append(luckeyCount);
        sb.append(", historyIntegration=").append(historyIntegration);
        sb.append(", email=").append(email);
        sb.append(", wechatOpenid=").append(wechatOpenid);
        sb.append(", googleId=").append(googleId);
        sb.append(", registerType=").append(registerType);
        sb.append(", emailVerified=").append(emailVerified);
        sb.append(", lastLoginTime=").append(lastLoginTime);
        sb.append(", avatarUrl=").append(avatarUrl);
        sb.append(", accountStatus=").append(accountStatus);
        sb.append(", freeDailyCredits=").append(freeDailyCredits);
        sb.append(", usedTodayFree=").append(usedTodayFree);
        sb.append(", lastResetDate=").append(lastResetDate);
        sb.append(", currentSubscriptionId=").append(currentSubscriptionId);
        sb.append(", subscriptionLevel=").append(subscriptionLevel);
        sb.append(", dailyCreditsLimit=").append(dailyCreditsLimit);
        sb.append(", dailyCreditsUsed=").append(dailyCreditsUsed);
        sb.append(", subscriptionEndDate=").append(subscriptionEndDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}