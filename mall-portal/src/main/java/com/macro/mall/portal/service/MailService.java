package com.macro.mall.portal.service;

/**
 * 邮件服务接口
 * @author Claude
 * @since 2025-09-10
 */
public interface MailService {
    
    /**
     * 发送验证码邮件
     * @param to 收件人邮箱
     * @param code 验证码
     * @param type 验证码类型描述
     * @param expireMinutes 过期时间（分钟）
     * @return 是否发送成功
     */
    boolean sendVerificationCode(String to, String code, String type, int expireMinutes);
    
    /**
     * 发送普通邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 是否发送成功
     */
    boolean sendSimpleMail(String to, String subject, String content);
    
    /**
     * 发送HTML邮件
     * @param to 收件人邮箱
     * @param subject 邮件主题
     * @param htmlContent HTML内容
     * @return 是否发送成功
     */
    boolean sendHtmlMail(String to, String subject, String htmlContent);
}