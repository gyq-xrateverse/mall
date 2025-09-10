package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.UmsThirdPartyAuth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 第三方认证Mapper接口
 * @author Claude
 * @since 2025-09-10
 */
public interface UmsThirdPartyAuthMapper {
    
    /**
     * 插入第三方认证记录
     * @param record 第三方认证记录
     * @return 影响行数
     */
    int insert(UmsThirdPartyAuth record);
    
    /**
     * 根据主键删除
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);
    
    /**
     * 根据主键更新
     * @param record 第三方认证记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UmsThirdPartyAuth record);
    
    /**
     * 选择性更新
     * @param record 第三方认证记录
     * @return 影响行数
     */
    int updateByPrimaryKeySelective(UmsThirdPartyAuth record);
    
    /**
     * 根据主键查询
     * @param id 主键ID
     * @return 第三方认证记录
     */
    UmsThirdPartyAuth selectByPrimaryKey(Long id);
    
    /**
     * 根据会员ID查询第三方认证列表
     * @param memberId 会员ID
     * @return 第三方认证列表
     */
    List<UmsThirdPartyAuth> selectByMemberId(@Param("memberId") Long memberId);
    
    /**
     * 根据第三方提供商和第三方ID查询
     * @param provider 第三方提供商
     * @param thirdPartyId 第三方用户ID
     * @return 第三方认证记录
     */
    UmsThirdPartyAuth selectByProviderAndThirdPartyId(@Param("provider") Integer provider,
                                                      @Param("thirdPartyId") String thirdPartyId);
    
    /**
     * 根据会员ID和提供商查询
     * @param memberId 会员ID
     * @param provider 第三方提供商
     * @return 第三方认证记录
     */
    UmsThirdPartyAuth selectByMemberIdAndProvider(@Param("memberId") Long memberId,
                                                  @Param("provider") Integer provider);
    
    /**
     * 根据会员ID删除第三方认证
     * @param memberId 会员ID
     * @return 影响行数
     */
    int deleteByMemberId(@Param("memberId") Long memberId);
    
    /**
     * 根据会员ID和提供商删除
     * @param memberId 会员ID
     * @param provider 第三方提供商
     * @return 影响行数
     */
    int deleteByMemberIdAndProvider(@Param("memberId") Long memberId,
                                    @Param("provider") Integer provider);
    
    /**
     * 更新令牌信息
     * @param id 记录ID
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param tokenExpireTime 令牌过期时间
     * @return 影响行数
     */
    int updateTokenInfo(@Param("id") Long id,
                        @Param("accessToken") String accessToken,
                        @Param("refreshToken") String refreshToken,
                        @Param("tokenExpireTime") java.util.Date tokenExpireTime);
}