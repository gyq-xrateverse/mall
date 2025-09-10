package com.macro.mall.portal.dao;

import com.macro.mall.portal.domain.UmsVerificationCode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 验证码Mapper接口
 * @author Claude
 * @since 2025-09-10
 */
public interface UmsVerificationCodeMapper {
    
    /**
     * 插入验证码记录
     * @param record 验证码记录
     * @return 影响行数
     */
    int insert(UmsVerificationCode record);
    
    /**
     * 根据主键删除
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Long id);
    
    /**
     * 根据主键更新
     * @param record 验证码记录
     * @return 影响行数
     */
    int updateByPrimaryKey(UmsVerificationCode record);
    
    /**
     * 根据主键查询
     * @param id 主键ID
     * @return 验证码记录
     */
    UmsVerificationCode selectByPrimaryKey(Long id);
    
    /**
     * 根据邮箱和验证码类型查询最新的验证码
     * @param email 邮箱地址
     * @param codeType 验证码类型
     * @return 验证码记录
     */
    UmsVerificationCode selectLatestByEmailAndType(@Param("email") String email, 
                                                   @Param("codeType") Integer codeType);
    
    /**
     * 根据邮箱、验证码和类型查询
     * @param email 邮箱地址
     * @param code 验证码
     * @param codeType 验证码类型
     * @return 验证码记录
     */
    UmsVerificationCode selectByEmailCodeAndType(@Param("email") String email,
                                                 @Param("code") String code,
                                                 @Param("codeType") Integer codeType);
    
    /**
     * 标记验证码为已使用
     * @param id 验证码ID
     * @param usedTime 使用时间
     * @return 影响行数
     */
    int markAsUsed(@Param("id") Long id, @Param("usedTime") Date usedTime);
    
    /**
     * 根据邮箱查询今日发送次数
     * @param email 邮箱地址
     * @param startTime 开始时间（今日00:00:00）
     * @param endTime 结束时间（今日23:59:59）
     * @return 发送次数
     */
    int countTodaySent(@Param("email") String email,
                       @Param("startTime") Date startTime,
                       @Param("endTime") Date endTime);
    
    /**
     * 删除过期的验证码
     * @param expireTime 过期时间点
     * @return 删除数量
     */
    int deleteExpired(@Param("expireTime") Date expireTime);
    
    /**
     * 根据邮箱查询验证码列表
     * @param email 邮箱地址
     * @return 验证码列表
     */
    List<UmsVerificationCode> selectByEmail(@Param("email") String email);
}