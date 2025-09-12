package com.macro.mall.portal.service;

import com.macro.mall.portal.enums.CodeType;

/**
 * 验证码服务接口
 * @author Claude
 * @since 2025-09-10
 */
public interface VerificationCodeService {
    
    /**
     * 生成验证码
     * @param email 邮箱地址
     * @param codeType 验证码类型
     * @return 验证码
     */
    String generateCode(String email, CodeType codeType);
    
    /**
     * 发送验证码邮件
     * @param email 邮箱地址
     * @param codeType 验证码类型
     * @return 是否发送成功
     */
    boolean sendCode(String email, CodeType codeType);
    
    /**
     * 验证验证码
     * @param email 邮箱地址
     * @param code 验证码
     * @param codeType 验证码类型
     * @return 是否验证成功
     */
    boolean verifyCode(String email, String code, CodeType codeType);
    
    /**
     * 获取剩余发送次数
     * @param email 邮箱地址
     * @return 剩余发送次数
     */
    int getRemainingTimes(String email);
    
    /**
     * 获取下次可发送时间（秒）
     * @param email 邮箱地址
     * @return 下次可发送的剩余时间（秒），0表示可以立即发送
     */
    long getNextSendTime(String email);
    
    /**
     * 重置指定邮箱的每日发送计数
     * @param email 邮箱地址
     * @return 是否重置成功
     */
    boolean resetDailySendCount(String email);
}