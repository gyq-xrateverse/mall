package com.macro.mall.portal;

import com.macro.mall.portal.dto.VerificationCodeParam;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.service.AuthService;
import com.macro.mall.portal.service.MailService;
import com.macro.mall.portal.service.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实邮件发送测试
 * 用于测试向真实邮箱发送验证码邮件
 */
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(locations = {"classpath:application-dev.yml"})
@DisplayName("真实邮件发送测试")
public class RealEmailSendTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private AuthService authService;

    // 目标测试邮箱
    private final String targetEmail = "gaoyongqiang@xrateverse.com";
    private final String testIpAddress = "127.0.0.1";

    @BeforeEach
    void setUp() {
        // 设置环境变量（如果.env文件加载失败）
        System.setProperty("MAIL_HOST", "smtp.exmail.qq.com");
        System.setProperty("MAIL_PORT", "465");
        System.setProperty("MAIL_USERNAME", "vcode@xrateverse.com");
        System.setProperty("MAIL_PASSWORD", "NNyqSi67bLuGLzpv");
        System.setProperty("MAIL_FROM_NAME", "BEILV AI");
        System.setProperty("MAIL_FROM_ADDRESS", "vcode@xrateverse.com");
        System.setProperty("MAIL_SSL_ENABLE", "true");
        System.setProperty("MAIL_PROTOCOL", "smtps");
    }

    @Test
    @DisplayName("发送注册验证码到目标邮箱")
    void testSendRegisterVerificationCode() {
        System.out.println("========================================");
        System.out.println("开始测试发送注册验证码邮件");
        System.out.println("目标邮箱: " + targetEmail);
        System.out.println("========================================");

        try {
            // 使用AuthService发送验证码（完整流程）
            VerificationCodeParam param = new VerificationCodeParam();
            param.setEmail(targetEmail);
            param.setCodeType(CodeType.REGISTER.getCode()); // 1-注册
            
            boolean result = authService.sendVerificationCode(param);

            System.out.println("验证码发送结果: " + (result ? "成功" : "失败"));
            
            if (result) {
                System.out.println("验证码邮件已成功发送到: " + targetEmail);
                System.out.println("请检查邮箱（包括垃圾邮件文件夹）");
                System.out.println("🔢 验证码有效期: 5分钟");
            } else {
                System.out.println("验证码邮件发送失败");
                System.out.println("可能原因: 邮件服务器配置问题、网络问题或邮箱地址无效");
            }

            assertTrue(result, "验证码邮件应该发送成功");

        } catch (Exception e) {
            System.err.println("邮件发送过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            fail("邮件发送不应该抛出异常: " + e.getMessage());
        }

        System.out.println("========================================");
        System.out.println("邮件发送测试完成");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("直接使用邮件服务发送测试邮件")
    void testDirectMailServiceSend() {
        System.out.println("========================================");
        System.out.println("开始测试直接邮件服务发送");
        System.out.println("目标邮箱: " + targetEmail);
        System.out.println("========================================");

        try {
            // 生成测试验证码
            String testCode = "888888";
            
            // 直接使用邮件服务发送（注意正确的参数）
            boolean result = mailService.sendVerificationCode(targetEmail, testCode, "测试", 5);

            System.out.println("邮件服务发送结果: " + (result ? "成功 ✅" : "失败 ❌"));
            
            if (result) {
                System.out.println("测试邮件已成功发送到: " + targetEmail);
                System.out.println("邮件内容包含验证码: " + testCode);
                System.out.println("发件人: BEILV AI <vcode@xrateverse.com>");
            } else {
                System.out.println("测试邮件发送失败");
            }

            assertTrue(result, "测试邮件应该发送成功");

        } catch (Exception e) {
            System.err.println("邮件发送过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            fail("邮件发送不应该抛出异常: " + e.getMessage());
        }

        System.out.println("========================================");
        System.out.println("直接邮件服务测试完成");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("发送密码重置验证码到目标邮箱")
    void testSendPasswordResetCode() {
        System.out.println("========================================");
        System.out.println("开始测试发送密码重置验证码邮件");
        System.out.println("目标邮箱: " + targetEmail);
        System.out.println("========================================");

        try {
            // 发送密码重置验证码
            VerificationCodeParam param = new VerificationCodeParam();
            param.setEmail(targetEmail);
            param.setCodeType(CodeType.RESET_PASSWORD.getCode()); // 3-重置密码
            
            boolean result = authService.sendVerificationCode(param);

            System.out.println("密码重置验证码发送结果: " + (result ? "成功" : "失败"));
            
            if (result) {
                System.out.println("密码重置验证码邮件已成功发送到: " + targetEmail);
                System.out.println("🔐 这是密码重置类型的验证码");
            } else {
                System.out.println("密码重置验证码邮件发送失败");
            }

            assertTrue(result, "密码重置验证码邮件应该发送成功");

        } catch (Exception e) {
            System.err.println("邮件发送过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            fail("邮件发送不应该抛出异常: " + e.getMessage());
        }

        System.out.println("========================================");
        System.out.println("密码重置验证码测试完成");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("测试HTML邮件发送")
    void testSendHtmlEmail() {
        System.out.println("========================================");
        System.out.println("开始测试HTML邮件发送");
        System.out.println("目标邮箱: " + targetEmail);
        System.out.println("========================================");

        try {
            String subject = "BEILV AI - 邮件服务测试";
            String htmlContent = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f5f5f5; }
                        .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                        .header { text-align: center; color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 20px; margin-bottom: 30px; }
                        .content { color: #34495e; line-height: 1.6; }
                        .code { background: #ecf0f1; border: 2px dashed #3498db; padding: 15px; text-align: center; font-size: 24px; font-weight: bold; color: #2c3e50; margin: 20px 0; border-radius: 5px; }
                        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #bdc3c7; text-align: center; color: #7f8c8d; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🚀 BEILV AI</h1>
                            <p>邮件服务测试</p>
                        </div>
                        <div class="content">
                            <p>尊敬的用户，</p>
                            <p>这是一封来自 <strong>BEILV AI Mall Portal</strong> 的测试邮件。</p>
                            <p>如果您收到这封邮件，说明我们的邮件服务配置正确并且正常工作。</p>
                            
                            <div class="code">
                                测试验证码: 123456
                            </div>
                            
                            <p><strong>测试信息:</strong></p>
                            <ul>
                                <li>目标邮箱: gaoyongqiang@xrateverse.com</li>
                                <li>发送时间: """ + java.time.LocalDateTime.now() + """
</li>
                                <li>邮件类型: 功能测试</li>
                                <li>发件服务器: smtp.exmail.qq.com</li>
                            </ul>
                            
                            <p>感谢您的配合测试！</p>
                        </div>
                        <div class="footer">
                            <p>© 2024 BEILV AI. All rights reserved.</p>
                            <p>这是一封自动生成的测试邮件，请勿回复。</p>
                        </div>
                    </div>
                </body>
                </html>
                """;

            boolean result = mailService.sendHtmlMail(targetEmail, subject, htmlContent);

            System.out.println("HTML邮件发送结果: " + (result ? "成功 ✅" : "失败 ❌"));
            
            if (result) {
                System.out.println("HTML测试邮件已成功发送到: " + targetEmail);
                System.out.println("邮件主题: " + subject);
                System.out.println("邮件包含精美的HTML格式");
            } else {
                System.out.println("HTML测试邮件发送失败");
            }

            assertTrue(result, "HTML邮件应该发送成功");

        } catch (Exception e) {
            System.err.println("HTML邮件发送过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            fail("HTML邮件发送不应该抛出异常: " + e.getMessage());
        }

        System.out.println("========================================");
        System.out.println("HTML邮件发送测试完成");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("邮件配置连接测试")
    void testMailConnectionConfiguration() {
        System.out.println("========================================");
        System.out.println("开始测试邮件服务器连接配置");
        System.out.println("========================================");

        try {
            // 打印当前邮件配置信息
            System.out.println("📋 当前邮件配置:");
            System.out.println("  SMTP服务器: " + System.getProperty("MAIL_HOST", "未设置"));
            System.out.println("  SMTP端口: " + System.getProperty("MAIL_PORT", "未设置"));
            System.out.println("  用户名: " + System.getProperty("MAIL_USERNAME", "未设置"));
            System.out.println("  SSL启用: " + System.getProperty("MAIL_SSL_ENABLE", "未设置"));
            System.out.println("  协议: " + System.getProperty("MAIL_PROTOCOL", "未设置"));
            System.out.println();

            // 测试发送简单邮件验证连接
            String simpleContent = "<html><body><h2>邮件服务器连接测试</h2><p>如果您收到这封邮件，说明邮件服务器连接正常。</p></body></html>";
            boolean result = mailService.sendHtmlMail(targetEmail, "邮件服务器连接测试", simpleContent);

            if (result) {
                System.out.println("邮件服务器连接测试成功");
                System.out.println("📡 SMTP连接正常");
                System.out.println("🔐 身份验证通过");
                System.out.println("📨 邮件发送成功");
            } else {
                System.out.println("邮件服务器连接测试失败");
                System.out.println("可能的问题:");
                System.out.println("  - SMTP服务器地址或端口错误");
                System.out.println("  - 用户名或密码错误");
                System.out.println("  - SSL/TLS配置问题");
                System.out.println("  - 网络连接问题");
            }

            assertTrue(result, "邮件服务器连接应该正常");

        } catch (Exception e) {
            System.err.println("邮件服务器连接测试异常: " + e.getMessage());
            e.printStackTrace();
            fail("邮件服务器连接测试失败: " + e.getMessage());
        }

        System.out.println("========================================");
        System.out.println("邮件服务器连接测试完成");
        System.out.println("========================================");
    }
}