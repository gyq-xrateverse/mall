package com.macro.mall.portal.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 第三方认证实体类
 * @author Claude
 * @since 2025-09-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UmsThirdPartyAuth implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 会员ID
     */
    private Long memberId;
    
    /**
     * 第三方提供商：1-微信，2-谷歌，3-GitHub，4-QQ
     */
    private Integer provider;
    
    /**
     * 第三方用户唯一标识
     */
    private String thirdPartyId;
    
    /**
     * 第三方用户名/昵称
     */
    private String thirdPartyName;
    
    /**
     * 第三方用户头像
     */
    private String thirdPartyAvatar;
    
    /**
     * 第三方用户邮箱
     */
    private String thirdPartyEmail;
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 令牌过期时间
     */
    private Date tokenExpireTime;
    
    /**
     * 状态：1-有效，0-无效
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}