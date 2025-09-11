package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

/**
 * 邮件服务实现类
 * @author Claude
 * @since 2025-09-10
 */
@Slf4j
@Service
public class MailServiceImpl implements MailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    @Value("${mail.from}")
    private String from;
    
    @Value("${spring.mail.username}")
    private String mailUsername;
    
    @Value("${mail.from-name:BEILV AI}")
    private String fromName;
    
    @Value("${spring.profiles.active:dev}")
    private String activeProfile;
    
    @Value("${mail.debug:false}")
    private boolean debugMode;
    
    @Override
    public boolean sendVerificationCode(String to, String code, String type, int expireMinutes) {
        return sendEmailWithRetry(to, code, type, expireMinutes, 3);
    }
    
    /**
     * 带重试机制的邮件发送
     */
    private boolean sendEmailWithRetry(String to, String code, String type, int expireMinutes, int maxRetries) {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("尝试发送邮件 (第{}次): 收件人={}", attempt, to);
                
                // 创建邮件消息
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                
                // 设置邮件信息 - 发件人地址必须与认证用户一致
                String actualFrom = String.format("%s <%s>", fromName, mailUsername);
                helper.setFrom(actualFrom);
                helper.setTo(to);
                helper.setSubject("BEILV AI - 验证码邮件");
                
                // 创建HTML内容 - 如果没有模板引擎，使用简单HTML
                String htmlContent = createVerificationEmailContent(code, type, expireMinutes);
                helper.setText(htmlContent, true);
                
                // 发送邮件
                mailSender.send(message);
                log.info("✅ 邮件发送成功: 收件人={}, 验证码={}", to, debugMode ? code : "****");
                return true;
                
            } catch (Exception e) {
                lastException = e;
                log.warn("❌ 邮件发送失败 (第{}次): 收件人={}, 错误={}", attempt, to, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        // 指数退避重试策略
                        long waitTime = (long) Math.pow(2, attempt) * 1000;
                        log.info("等待{}ms后重试...", waitTime);
                        Thread.sleep(waitTime);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("重试等待被中断", ie);
                        break;
                    }
                }
            }
        }
        
        log.error("🚫 邮件发送最终失败: 收件人={}, 尝试次数={}", to, maxRetries);
        if (lastException != null) {
            log.error("最后一次失败的详细错误:", lastException);
        }
        return false;
    }
    
    /**
     * 创建验证码邮件HTML内容
     */
    private String createVerificationEmailContent(String code, String type, int expireMinutes) {
        // 如果有模板引擎，使用模板
        if (templateEngine != null) {
            try {
                Context context = new Context();
                context.setVariable("code", code);
                context.setVariable("type", type);
                context.setVariable("expireMinutes", expireMinutes);
                return templateEngine.process("email/verification-code", context);
            } catch (Exception e) {
                log.warn("模板引擎处理失败，使用简单HTML模板: {}", e.getMessage());
            }
        }
        
        // 简单HTML模板（兼容所有邮件客户端）
        return createSimpleEmailTemplate(code, type, expireMinutes);
    }
    
    /**
     * 创建简单的邮件HTML模板
     */
    private String createSimpleEmailTemplate(String code, String type, int expireMinutes) {
        String typeText = "登录".equals(type) ? "登录" : "注册";
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>BEILV AI - 验证码</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 28px;">🔐 BEILV AI</h1>
                </div>
                
                <div style="background: #ffffff; padding: 40px; border: 1px solid #e0e0e0; border-top: none; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #333; margin-top: 0; font-size: 24px;">%s验证码</h2>
                    
                    <p style="font-size: 16px; margin: 20px 0;">
                        您好！您正在进行%s验证，您的验证码是：
                    </p>
                    
                    <div style="background: #f8f9fa; border: 2px dashed #667eea; border-radius: 8px; padding: 20px; text-align: center; margin: 30px 0;">
                        <span style="font-family: 'Courier New', monospace; font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px;">%s</span>
                    </div>
                    
                    <p style="color: #666; font-size: 14px; margin: 20px 0;">
                        ⏰ 此验证码将在 <strong>%d分钟</strong> 后失效，请尽快使用。
                    </p>
                    
                    <p style="color: #666; font-size: 14px;">
                        🛡️ 如果这不是您本人的操作，请忽略此邮件。
                    </p>
                    
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    
                    <p style="color: #999; font-size: 12px; text-align: center; margin: 0;">
                        此邮件由 BEILV AI 系统自动发送，请勿回复。<br>
                        © 2025 BEILV AI. All rights reserved.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(typeText, typeText, code, expireMinutes);
    }
    
    @Override
    public boolean sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailUsername);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
            return false;
        }
    }
    
    @Override
    public boolean sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            String actualFrom = String.format("%s <%s>", fromName, mailUsername);
            helper.setFrom(actualFrom);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            return false;
        }
    }
}