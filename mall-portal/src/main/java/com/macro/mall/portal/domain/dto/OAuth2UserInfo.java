package com.macro.mall.portal.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2UserInfo {
    
    private String openId;
    
    private String unionId;
    
    private String nickname;
    
    private String avatar;
    
    private String email;
    
    private String provider;
    
    private String providerUserId;
    
    private String accessToken;
    
    private String refreshToken;
    
    private Long expiresIn;
}