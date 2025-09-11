package com.macro.mall.portal.service;

import com.macro.mall.portal.domain.dto.OAuth2UserInfo;
import com.macro.mall.portal.dto.AuthTokenResult;

public interface OAuth2Service {
    
    String generateWeChatAuthUrl(String state);
    
    String generateGoogleAuthUrl(String state);
    
    OAuth2UserInfo getWeChatUserInfo(String code, String state);
    
    OAuth2UserInfo getGoogleUserInfo(String code, String state);
    
    AuthTokenResult processOAuth2Login(OAuth2UserInfo userInfo, String provider);
}