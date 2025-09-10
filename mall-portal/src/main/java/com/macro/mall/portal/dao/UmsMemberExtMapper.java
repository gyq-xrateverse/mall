package com.macro.mall.portal.dao;

import com.macro.mall.model.UmsMember;
import org.apache.ibatis.annotations.Param;

/**
 * UmsMember扩展Mapper接口
 * @author Claude
 * @since 2025-09-10
 */
public interface UmsMemberExtMapper {
    
    /**
     * 根据邮箱查询用户
     * @param email 邮箱地址
     * @return 用户信息
     */
    UmsMember selectByEmail(@Param("email") String email);
    
    /**
     * 根据邮箱查找用户
     * @param email 邮箱地址
     * @return 用户信息
     */
    UmsMember findByEmail(@Param("email") String email);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    UmsMember selectByUsername(@Param("username") String username);
    
    /**
     * 根据微信OpenID查询用户
     * @param wechatOpenid 微信OpenID
     * @return 用户信息
     */
    UmsMember selectByWechatOpenid(@Param("wechatOpenid") String wechatOpenid);
    
    /**
     * 根据Google ID查询用户
     * @param googleId Google用户ID
     * @return 用户信息
     */
    UmsMember selectByGoogleId(@Param("googleId") String googleId);
    
    /**
     * 更新用户最后登录时间
     * @param id 用户ID
     * @param lastLoginTime 最后登录时间
     * @return 影响行数
     */
    int updateLastLoginTime(@Param("id") Long id, @Param("lastLoginTime") java.util.Date lastLoginTime);
    
    /**
     * 更新用户账户状态
     * @param id 用户ID
     * @param accountStatus 账户状态
     * @return 影响行数
     */
    int updateAccountStatus(@Param("id") Long id, @Param("accountStatus") Integer accountStatus);
    
    /**
     * 根据第三方ID查找用户
     * @param providerField 第三方字段名
     * @param providerId 第三方用户ID
     * @return 用户信息
     */
    UmsMember findByThirdPartyId(@Param("providerField") String providerField, @Param("providerId") String providerId);
    
    /**
     * 更新第三方信息
     * @param userId 用户ID
     * @param providerField 第三方字段名
     * @param providerId 第三方用户ID
     * @param email 邮箱
     * @param avatar 头像
     * @param registerType 注册类型
     * @return 影响行数
     */
    int updateThirdPartyInfo(@Param("userId") Long userId, 
                           @Param("providerField") String providerField, 
                           @Param("providerId") String providerId, 
                           @Param("email") String email, 
                           @Param("avatar") String avatar, 
                           @Param("registerType") Integer registerType);
}