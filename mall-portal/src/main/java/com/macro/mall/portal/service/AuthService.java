package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.*;
import com.macro.mall.portal.domain.dto.OAuth2UserInfo;
import com.macro.mall.portal.domain.enums.RegisterType;

/**
 * 认证服务接口
 * @author Claude
 * @since 2025-09-10
 */
public interface AuthService {
    
    /**
     * 用户注册
     * @param param 注册参数
     * @return 认证Token结果
     */
    AuthTokenResult register(AuthRegisterParam param);
    
    /**
     * 邮箱密码登录
     * @param param 登录参数
     * @return 认证Token结果
     */
    AuthTokenResult login(AuthLoginParam param);
    
    /**
     * 发送验证码
     * @param param 验证码请求参数
     * @return 是否发送成功
     */
    boolean sendVerificationCode(VerificationCodeParam param);
    
    /**
     * 重置密码
     * @param param 重置密码参数
     * @return 是否重置成功
     */
    boolean resetPassword(ResetPasswordParam param);
    
    /**
     * 刷新访问Token
     * @param refreshToken 刷新Token
     * @return 新的访问Token
     */
    String refreshAccessToken(String refreshToken);
    
    /**
     * 获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoResult getUserInfo(Long userId);
    
    /**
     * 注销登录
     * @param token 访问Token
     * @return 是否注销成功
     */
    boolean logout(String token);
    
    /**
     * 检查邮箱是否已存在
     * @param email 邮箱地址
     * @return 是否已存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 检查用户名是否已存在
     * @param username 用户名
     * @return 是否已存在
     */
    boolean isUsernameExists(String username);
    
    /**
     * 处理第三方登录
     * @param userInfo 第三方用户信息
     * @param registerType 注册类型
     * @return 认证Token结果
     */
    AuthTokenResult processThirdPartyLogin(OAuth2UserInfo userInfo, RegisterType registerType);
}