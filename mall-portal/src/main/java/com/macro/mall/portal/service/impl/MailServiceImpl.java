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
    
    @Override
    public boolean sendVerificationCode(String to, String code, String type, int expireMinutes) {
        try {
            // 创建邮件消息
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 设置邮件信息
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("BEILV AI - 验证码邮件");
            
            // 使用Thymeleaf模板生成HTML内容
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("type", type);
            context.setVariable("expireMinutes", expireMinutes);
            
            String htmlContent = templateEngine.process("email/verification-code", context);
            helper.setText(htmlContent, true);
            
            // 发送邮件
            mailSender.send(message);
            log.info("Verification code email sent successfully to: {}", to);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to send verification code email to: {}", to, e);
            return false;
        }
    }
    
    @Override
    public boolean sendSimpleMail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
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
            
            helper.setFrom(from);
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