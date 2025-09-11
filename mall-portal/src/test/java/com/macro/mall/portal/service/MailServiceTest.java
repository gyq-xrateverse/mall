package com.macro.mall.portal.service;

import com.macro.mall.portal.service.impl.MailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;
import jakarta.mail.Message;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 邮件服务单元测试
 */
@DisplayName("邮件服务测试")
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private MailServiceImpl mailService;

    private final String testEmail = "test@example.com";
    private final String testCode = "123456";
    private final String fromAddress = "noreply@beilv-ai.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 设置邮件服务配置
        ReflectionTestUtils.setField(mailService, "from", "BEILV AI <" + fromAddress + ">");

        // Mock MimeMessage
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("发送验证码邮件成功")
    void testSendVerificationCodeSuccess() throws Exception {
        // Given
        String expectedHtml = "<html><body>验证码: " + testCode + "</body></html>";
        when(templateEngine.process(eq("verification-code"), any(Context.class)))
            .thenReturn(expectedHtml);

        // When
        boolean result = mailService.sendVerificationCode(testEmail, testCode, "注册", 5);

        // Then
        assertTrue(result, "邮件发送应该成功");
        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(eq("verification-code"), any(Context.class));
    }

    @Test
    @DisplayName("发送验证码邮件失败 - 无效邮箱地址")
    void testSendVerificationCodeInvalidEmail() {
        // Given
        String invalidEmail = "invalid-email";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            mailService.sendVerificationCode(invalidEmail, testCode, "注册", 5);
        }, "应该抛出非法参数异常");

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("发送验证码邮件失败 - 空验证码")
    void testSendVerificationCodeEmptyCode() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            mailService.sendVerificationCode(testEmail, "", "注册", 5);
        }, "应该抛出非法参数异常");

        assertThrows(IllegalArgumentException.class, () -> {
            mailService.sendVerificationCode(testEmail, null, "注册", 5);
        }, "应该抛出非法参数异常");

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("发送验证码邮件失败 - 邮件服务器异常")
    void testSendVerificationCodeMailServerException() throws Exception {
        // Given
        String expectedHtml = "<html><body>验证码: " + testCode + "</body></html>";
        when(templateEngine.process(eq("verification-code"), any(Context.class)))
            .thenReturn(expectedHtml);

        doThrow(new RuntimeException("邮件服务器连接失败"))
            .when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = mailService.sendVerificationCode(testEmail, testCode, "注册", 5);

        // Then
        assertFalse(result, "邮件发送应该失败");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("发送HTML邮件成功")
    void testSendHtmlMailSuccess() throws Exception {
        // Given
        String subject = "测试邮件";
        String content = "<html><body><h1>测试内容</h1></body></html>";

        // When
        boolean result = mailService.sendHtmlMail(testEmail, subject, content);

        // Then
        assertTrue(result, "HTML邮件发送应该成功");
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("邮件模板渲染测试")
    void testEmailTemplateRendering() throws Exception {
        // Given
        String expectedHtml = "<html><body>" +
            "<h2>BEILV AI 邮箱验证</h2>" +
            "<p>您的验证码是: <strong>" + testCode + "</strong></p>" +
            "<p>验证码5分钟内有效，请及时使用。</p>" +
            "</body></html>";

        when(templateEngine.process(eq("verification-code"), any(Context.class)))
            .thenReturn(expectedHtml);

        // When
        boolean result = mailService.sendVerificationCode(testEmail, testCode, "注册", 5);

        // Then
        assertTrue(result, "邮件发送应该成功");

        // 验证模板引擎被调用时传入了正确的上下文
        verify(templateEngine).process(eq("verification-code"), argThat(context -> {
            Context ctx = (Context) context;
            return testCode.equals(ctx.getVariable("code")) &&
                   testEmail.equals(ctx.getVariable("email"));
        }));
    }

    @Test
    @DisplayName("批量发送邮件测试")
    void testSendBatchMail() throws Exception {
        // Given
        String[] emails = {"test1@example.com", "test2@example.com", "test3@example.com"};
        String[] codes = {"123456", "789012", "345678"};

        String expectedHtml = "<html><body>验证码邮件</body></html>";
        when(templateEngine.process(eq("verification-code"), any(Context.class)))
            .thenReturn(expectedHtml);

        // When
        for (int i = 0; i < emails.length; i++) {
            boolean result = mailService.sendVerificationCode(emails[i], codes[i], "批量测试", 5);
            assertTrue(result, "第" + (i+1) + "封邮件发送应该成功");
        }

        // Then
        verify(mailSender, times(emails.length)).send(mimeMessage);
        verify(templateEngine, times(emails.length)).process(eq("verification-code"), any(Context.class));
    }

    @Test
    @DisplayName("邮件发送性能测试")
    void testMailSendingPerformance() throws Exception {
        // Given
        String expectedHtml = "<html><body>性能测试邮件</body></html>";
        when(templateEngine.process(eq("verification-code"), any(Context.class)))
            .thenReturn(expectedHtml);

        // When
        long startTime = System.currentTimeMillis();
        boolean result = mailService.sendVerificationCode(testEmail, testCode, "注册", 5);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Then
        assertTrue(result, "邮件发送应该成功");
        assertTrue(duration < 5000, "邮件发送应该在5秒内完成，实际耗时: " + duration + "ms");

        verify(mailSender).send(mimeMessage);
    }
}
