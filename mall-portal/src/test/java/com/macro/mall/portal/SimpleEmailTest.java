package com.macro.mall.portal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

/**
 * 简单邮件发送测试
 * 不依赖Spring容器，直接使用JavaMail API测试
 */
@DisplayName("简单邮件发送测试")
public class SimpleEmailTest {

    // 邮件配置（基于mailx项目）
    private static final String SMTP_HOST = "smtp.exmail.qq.com";
    private static final String SMTP_PORT = "465";
    private static final String USERNAME = "vcode@xrateverse.com";
    private static final String PASSWORD = "NNyqSi67bLuGLzpv";
    private static final String FROM_EMAIL = "vcode@xrateverse.com";
    private static final String FROM_NAME = "BEILV AI";

    // 目标邮箱
    private static final String TARGET_EMAIL = "gaoyongqiang@xrateverse.com";

    @Test
    @DisplayName("直接使用JavaMail发送验证码邮件")
    void testDirectJavaMailSend() {
        System.out.println("========================================");
        System.out.println("开始直接JavaMail测试");
        System.out.println("目标邮箱: " + TARGET_EMAIL);
        System.out.println("========================================");

        try {
            // 配置邮件属性
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.trust", "*");
            props.put("mail.smtp.connectiontimeout", "30000");
            props.put("mail.smtp.timeout", "30000");
            props.put("mail.smtp.writetimeout", "30000");

            System.out.println("邮件服务器配置:");
            System.out.println("  主机: " + SMTP_HOST);
            System.out.println("  端口: " + SMTP_PORT);
            System.out.println("  用户: " + USERNAME);
            System.out.println("  SSL: 启用");
            System.out.println();

            // 创建认证器
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            };

            // 创建会话
            Session session = Session.getInstance(props, authenticator);
            session.setDebug(true); // 启用调试输出

            // 创建邮件消息
            MimeMessage message = new MimeMessage(session);

            // 设置发件人
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, "UTF-8"));

            // 设置收件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(TARGET_EMAIL));

            // 设置邮件主题
            message.setSubject("BEILV AI - 邮箱验证码", "UTF-8");

            // 生成验证码
            String verificationCode = "666888";

            // 创建HTML邮件内容
            String htmlContent = createVerificationEmailHtml(verificationCode);

            // 设置邮件内容
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            // 设置发送时间
            message.setSentDate(new java.util.Date());

            System.out.println("📨 正在发送邮件...");

            // 发送邮件
            Transport.send(message);

            System.out.println("✅ 邮件发送成功！");
            System.out.println("验证码: " + verificationCode);
            System.out.println("📮 发送到: " + TARGET_EMAIL);
            System.out.println("🕐 请在5分钟内使用验证码");

        } catch (Exception e) {
            System.err.println("❌ 邮件发送失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("邮件发送失败", e);
        }

        System.out.println("========================================");
        System.out.println("JavaMail直接发送测试完成");
        System.out.println("========================================");
    }

    @Test
    @DisplayName("测试SMTP连接")
    void testSMTPConnection() {
        System.out.println("========================================");
        System.out.println("🔌 测试SMTP服务器连接");
        System.out.println("========================================");

        try {
            // 配置连接属性
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.connectiontimeout", "10000");

            // 创建认证器
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, PASSWORD);
                }
            };

            // 创建会话
            Session session = Session.getInstance(props, authenticator);

            // 获取传输对象
            Transport transport = session.getTransport("smtps");

            System.out.println("🔗 正在连接SMTP服务器...");

            // 连接到服务器
            transport.connect(SMTP_HOST, Integer.parseInt(SMTP_PORT), USERNAME, PASSWORD);

            System.out.println("✅ SMTP服务器连接成功！");
            System.out.println("🔐 身份验证通过");
            System.out.println("📡 服务器响应正常");

            // 关闭连接
            transport.close();

        } catch (Exception e) {
            System.err.println("❌ SMTP连接失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("SMTP连接失败", e);
        }

        System.out.println("========================================");
        System.out.println("SMTP连接测试完成");
        System.out.println("========================================");
    }

    /**
     * 创建验证码邮件HTML内容
     */
    private String createVerificationEmailHtml(String code) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>邮箱验证码</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 0; padding: 0; background-color: #f4f7fa; }
                    .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); overflow: hidden; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-align: center; padding: 40px 30px; }
                    .header h1 { margin: 0; font-size: 28px; font-weight: 600; }
                    .header p { margin: 10px 0 0 0; opacity: 0.9; font-size: 16px; }
                    .content { padding: 40px 30px; }
                    .greeting { color: #2c3e50; font-size: 18px; margin-bottom: 20px; }
                    .message { color: #5a6c7d; line-height: 1.6; margin-bottom: 30px; font-size: 16px; }
                    .code-container { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); border-radius: 8px; padding: 25px; text-align: center; margin: 30px 0; }
                    .code { font-size: 32px; font-weight: bold; color: white; letter-spacing: 8px; text-shadow: 0 2px 4px rgba(0,0,0,0.3); }
                    .code-label { color: white; font-size: 14px; margin-bottom: 10px; opacity: 0.9; }
                    .warning { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 6px; padding: 15px; margin: 20px 0; color: #856404; }
                    .footer { background: #f8f9fa; padding: 30px; text-align: center; border-top: 1px solid #e9ecef; }
                    .footer p { margin: 5px 0; color: #6c757d; font-size: 14px; }
                    .brand { color: #667eea; font-weight: 600; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚀 BEILV AI</h1>
                        <p>人工智能商城门户</p>
                    </div>
                    <div class="content">
                        <div class="greeting">尊敬的用户，您好！</div>
                        <div class="message">
                            您正在进行邮箱验证操作，请使用以下验证码完成验证：
                        </div>
                        <div class="code-container">
                            <div class="code-label">您的验证码</div>
                            <div class="code">""" + code + """
</div>
                        </div>
                        <div class="warning">
                            <strong>⏰ 重要提醒：</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>验证码5分钟内有效，请及时使用</li>
                                <li>请勿将验证码告知他人</li>
                                <li>如非本人操作，请忽略此邮件</li>
                            </ul>
                        </div>
                        <div class="message">
                            感谢您使用 <span class="brand">BEILV AI</span> 服务！
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>BEILV AI Mall Portal</strong></p>
                        <p>© 2024 BEILV AI. All rights reserved.</p>
                        <p>这是一封自动发送的邮件，请勿回复。</p>
                        <p>发送时间: """ + new java.util.Date() + """
</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
