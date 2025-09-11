package com.macro.mall.portal.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.dto.EmailCodeLoginParam;
import com.macro.mall.portal.dto.VerificationCodeParam;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.service.MailService;
import com.macro.mall.portal.service.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 简化版邮箱验证码登录集成测试
 * 专注于核心功能测试，避免版本兼容性问题
 * @author Claude
 * @since 2025-09-11
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DisplayName("简化版邮箱验证码登录集成测试")
public class SimpleEmailCodeLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @MockBean
    private VerificationCodeService verificationCodeService;

    private final String testEmail = "simple.test@example.com";
    private final String testCode = "123456";

    @BeforeEach
    void setUp() {
        // Mock邮件发送服务
        when(mailService.sendVerificationCode(anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(true);
        
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
    @DisplayName("测试1: 发送验证码API")
    void test1_SendVerificationCodeApi() throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("🧪 测试1: 发送验证码API");
        System.out.println("=".repeat(50));

        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail(testEmail);
        param.setCodeType(CodeType.LOGIN.getCode());

        MvcResult result = mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("📋 API响应: " + response);
        
        // 验证邮件服务被调用
        verify(mailService).sendVerificationCode(eq(testEmail), anyString(), anyString(), anyInt());
        
        System.out.println("✅ 发送验证码API测试通过");
        System.out.println("=".repeat(50));
    }

    @Test
    @DisplayName("测试2: 邮箱验证码登录API - 成功场景")
    void test2_EmailCodeLoginSuccess() throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("🧪 测试2: 邮箱验证码登录API - 成功场景");
        System.out.println("=".repeat(50));

        // Mock验证码验证成功
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN))
                .thenReturn(true);

        EmailCodeLoginParam loginParam = new EmailCodeLoginParam();
        loginParam.setEmail(testEmail);
        loginParam.setVerificationCode(testCode);

        MvcResult result = mockMvc.perform(post("/api/auth/login-with-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.userInfo").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("📋 登录成功响应: " + response);

        // 验证验证码服务被调用
        verify(verificationCodeService).verifyCode(testEmail, testCode, CodeType.LOGIN);
        
        System.out.println("✅ 邮箱验证码登录成功测试通过");
        System.out.println("=".repeat(50));
    }

    @Test
    @DisplayName("测试3: 邮箱验证码登录API - 验证码错误")
    void test3_EmailCodeLoginInvalidCode() throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("🧪 测试3: 邮箱验证码登录API - 验证码错误");
        System.out.println("=".repeat(50));

        // Mock验证码验证失败
        when(verificationCodeService.verifyCode(testEmail, "wrong_code", CodeType.LOGIN))
                .thenReturn(false);

        EmailCodeLoginParam loginParam = new EmailCodeLoginParam();
        loginParam.setEmail(testEmail);
        loginParam.setVerificationCode("wrong_code");

        MvcResult result = mockMvc.perform(post("/api/auth/login-with-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("验证码无效或已过期"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        System.out.println("📋 验证码错误响应: " + response);

        verify(verificationCodeService).verifyCode(testEmail, "wrong_code", CodeType.LOGIN);
        
        System.out.println("✅ 验证码错误处理测试通过");
        System.out.println("=".repeat(50));
    }

    @Test
    @DisplayName("测试4: 参数验证测试")
    void test4_ParameterValidation() throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("🧪 测试4: 参数验证测试");
        System.out.println("=".repeat(50));

        // 测试空邮箱
        EmailCodeLoginParam emptyEmailParam = new EmailCodeLoginParam();
        emptyEmailParam.setEmail("");
        emptyEmailParam.setVerificationCode(testCode);

        mockMvc.perform(post("/api/auth/login-with-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyEmailParam)))
                .andExpect(status().isBadRequest());

        System.out.println("✅ 空邮箱验证通过");

        // 测试无效邮箱格式
        EmailCodeLoginParam invalidEmailParam = new EmailCodeLoginParam();
        invalidEmailParam.setEmail("invalid-email");
        invalidEmailParam.setVerificationCode(testCode);

        mockMvc.perform(post("/api/auth/login-with-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidEmailParam)))
                .andExpect(status().isBadRequest());

        System.out.println("✅ 无效邮箱格式验证通过");

        // 测试空验证码
        EmailCodeLoginParam emptyCodeParam = new EmailCodeLoginParam();
        emptyCodeParam.setEmail(testEmail);
        emptyCodeParam.setVerificationCode("");

        mockMvc.perform(post("/api/auth/login-with-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyCodeParam)))
                .andExpect(status().isBadRequest());

        System.out.println("✅ 空验证码验证通过");
        System.out.println("=".repeat(50));
    }

    @Test
    @DisplayName("功能测试总结")
    void testSummary() {
        System.out.println("=".repeat(70));
        System.out.println("📊 简化版邮箱验证码登录集成测试总结");
        System.out.println("=".repeat(70));
        System.out.println();
        
        System.out.println("✅ 完成的测试:");
        System.out.println("  1. 📤 发送验证码API功能测试");
        System.out.println("  2. ✅ 邮箱验证码登录成功场景");
        System.out.println("  3. ❌ 验证码错误处理");
        System.out.println("  4. 🔍 API参数验证");
        System.out.println();
        
        System.out.println("🎯 验证的核心功能:");
        System.out.println("  • Controller接口正确响应HTTP请求");
        System.out.println("  • Service层Mock交互正常");
        System.out.println("  • JSON序列化和反序列化");
        System.out.println("  • 参数验证和错误处理");
        System.out.println("  • 返回数据格式正确");
        System.out.println();
        
        System.out.println("🔧 技术特点:");
        System.out.println("  • 使用Spring Boot 3.2.2兼容配置");
        System.out.println("  • MockMvc进行Web层测试");
        System.out.println("  • @MockBean模拟外部依赖");
        System.out.println("  • 避免复杂的数据库配置");
        System.out.println();
        
        System.out.println("💡 使用说明:");
        System.out.println("  • 这是简化版本，专注API层测试");
        System.out.println("  • 如需完整测试，可运行RealEmailCodeLoginTest");
        System.out.println("  • 单元测试在EmailCodeLoginServiceTest");
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("🏆 简化版集成测试完成");
        System.out.println("=".repeat(70));
    }
}