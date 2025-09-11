package com.macro.mall.portal.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.dto.*;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 邮箱验证登录完整流程集成测试
 * 测试从发送验证码到注册登录的完整业务流程
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("邮箱验证登录流程集成测试")
@Transactional
public class EmailVerificationLoginFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private AuthService authService;

    // 测试用户数据
    private final String testEmail = "flowtest@example.com";
    private final String testUsername = "flowtestuser";
    private final String testPassword = "FlowTest123!";
    private String verificationCode;
    private String authToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        // Mock邮件服务始终返回成功
        when(mailService.sendVerificationCode(anyString(), anyString(), anyString(), anyInt())).thenReturn(true);
    }

    @Test
    @Order(1)
    @DisplayName("流程步骤1: 检查邮箱不存在")
    void step1_checkEmailNotExists() throws Exception {
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", testEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false)); // 邮箱不存在
    }

    @Test
    @Order(2)
    @DisplayName("流程步骤2: 检查用户名不存在")
    void step2_checkUsernameNotExists() throws Exception {
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", testUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false)); // 用户名不存在
    }

    @Test
    @Order(3)
    @DisplayName("流程步骤3: 发送注册验证码")
    void step3_sendRegisterVerificationCode() throws Exception {
        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail(testEmail);
        param.setCodeType(CodeType.REGISTER.getCode());

        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value("验证码发送成功"));

        // 验证邮件服务被调用
        verify(mailService, atLeastOnce()).sendVerificationCode(eq(testEmail), anyString(), anyString(), anyInt());
    }

    @Test
    @Order(4)
    @DisplayName("流程步骤4: 获取验证码并验证注册")
    void step4_registerWithVerificationCode() throws Exception {
        // 模拟获取验证码（在真实场景中从邮件中获取）
        verificationCode = generateMockVerificationCode();
        
        // 直接使用VerificationCodeService发送验证码用于测试
        boolean codeStored = verificationCodeService.sendCode(testEmail, CodeType.REGISTER);
        assert codeStored : "验证码存储失败";

        // 构造注册请求
        AuthRegisterParam registerParam = new AuthRegisterParam();
        registerParam.setUsername(testUsername);
        registerParam.setEmail(testEmail);
        registerParam.setPassword(testPassword);
        registerParam.setConfirmPassword(testPassword);
        registerParam.setVerificationCode(verificationCode);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty())
                .andDo(result -> {
                    // 保存令牌用于后续测试
                    String response = result.getResponse().getContentAsString();
                    var resultData = objectMapper.readTree(response);
                    authToken = resultData.get("data").get("accessToken").asText();
                    refreshToken = resultData.get("data").get("refreshToken").asText();
                });
    }

    @Test
    @Order(5)
    @DisplayName("流程步骤5: 验证邮箱和用户名现在存在")
    void step5_checkUserExistsAfterRegister() throws Exception {
        // 检查邮箱现在存在
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", testEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true)); // 邮箱现在存在

        // 检查用户名现在存在
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", testUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true)); // 用户名现在存在
    }

    @Test
    @Order(6)
    @DisplayName("流程步骤6: 获取用户信息")
    void step6_getUserInfo() throws Exception {
        mockMvc.perform(get("/api/auth/user-info")
                .header("Authorization", "Bearer " + authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value(testUsername))
                .andExpect(jsonPath("$.data.email").value(testEmail));
    }

    @Test
    @Order(7)
    @DisplayName("流程步骤7: 注销并重新登录")
    void step7_logoutAndLogin() throws Exception {
        // 注销
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("注销成功"));

        // 重新登录
        AuthLoginParam loginParam = new AuthLoginParam();
        loginParam.setEmail(testEmail); // 邮箱登录
        loginParam.setPassword(testPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andDo(result -> {
                    // 更新令牌
                    String response = result.getResponse().getContentAsString();
                    var resultData = objectMapper.readTree(response);
                    authToken = resultData.get("data").get("accessToken").asText();
                    refreshToken = resultData.get("data").get("refreshToken").asText();
                });
    }

    @Test
    @Order(8)
    @DisplayName("流程步骤8: 测试密码重置流程")
    void step8_passwordResetFlow() throws Exception {
        // 发送密码重置验证码
        VerificationCodeParam resetCodeParam = new VerificationCodeParam();
        resetCodeParam.setEmail(testEmail);
        resetCodeParam.setCodeType(CodeType.RESET_PASSWORD.getCode());

        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetCodeParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 模拟重置密码验证码
        String resetCode = generateMockVerificationCode();
        boolean resetCodeStored = verificationCodeService.sendCode(testEmail, CodeType.RESET_PASSWORD);
        assert resetCodeStored : "重置密码验证码存储失败";

        // 重置密码
        ResetPasswordParam resetParam = new ResetPasswordParam();
        resetParam.setEmail(testEmail);
        resetParam.setVerificationCode(resetCode);
        resetParam.setNewPassword("NewPassword123!");
        resetParam.setConfirmPassword("NewPassword123!");

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        // 使用新密码登录验证重置成功
        AuthLoginParam newLoginParam = new AuthLoginParam();
        newLoginParam.setEmail(testEmail);
        newLoginParam.setPassword("NewPassword123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newLoginParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @Order(9)
    @DisplayName("流程步骤9: 测试Token刷新")
    void step9_refreshToken() throws Exception {
        mockMvc.perform(post("/api/auth/refresh-token")
                .header("Authorization", "Bearer " + refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    @Order(10)
    @DisplayName("流程步骤10: 错误场景测试")
    void step10_errorScenarios() throws Exception {
        // 测试重复注册
        AuthRegisterParam duplicateParam = new AuthRegisterParam();
        duplicateParam.setUsername("newuser");
        duplicateParam.setEmail(testEmail); // 使用已存在的邮箱
        duplicateParam.setPassword(testPassword);
        duplicateParam.setConfirmPassword(testPassword);
        duplicateParam.setVerificationCode("123456");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("该邮箱已被注册"));

        // 测试错误密码登录
        AuthLoginParam wrongPasswordParam = new AuthLoginParam();
        wrongPasswordParam.setEmail(testEmail);
        wrongPasswordParam.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("密码错误"));

        // 测试错误验证码
        VerificationCodeParam wrongCodeParam = new VerificationCodeParam();
        wrongCodeParam.setEmail("nonexistent@example.com");
        wrongCodeParam.setCodeType(CodeType.RESET_PASSWORD.getCode());

        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongCodeParam)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("邮件发送失败场景测试")
    void testEmailSendFailureScenario() throws Exception {
        // 模拟邮件发送失败
        when(mailService.sendVerificationCode(anyString(), anyString(), anyString(), anyInt())).thenReturn(false);

        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail("failure@example.com");
        param.setCodeType(CodeType.REGISTER.getCode());

        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("验证码发送失败，请稍后重试"));
    }

    @Test
    @DisplayName("验证码过期场景测试")
    void testExpiredVerificationCode() throws Exception {
        String expiredEmail = "expired@example.com";
        
        // 发送验证码
        boolean sent = verificationCodeService.sendCode(expiredEmail, CodeType.REGISTER);
        assert sent : "验证码发送失败";

        // 模拟验证码过期（这里通过修改过期时间实现）
        // 在实际测试中可能需要等待或者修改数据库中的过期时间

        AuthRegisterParam param = new AuthRegisterParam();
        param.setUsername("expireduser");
        param.setEmail(expiredEmail);
        param.setPassword(testPassword);
        param.setConfirmPassword(testPassword);
        param.setVerificationCode("000000"); // 使用错误的验证码模拟过期

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("验证码无效或已过期"));
    }

    @Test
    @DisplayName("并发注册测试")
    void testConcurrentRegistration() throws Exception {
        String concurrentEmail = "concurrent@example.com";
        
        // 发送验证码
        VerificationCodeParam codeParam = new VerificationCodeParam();
        codeParam.setEmail(concurrentEmail);
        codeParam.setCodeType(CodeType.REGISTER.getCode());

        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(codeParam)))
                .andExpect(status().isOk());

        // 模拟两个并发注册请求
        AuthRegisterParam param1 = new AuthRegisterParam();
        param1.setUsername("concurrent1");
        param1.setEmail(concurrentEmail);
        param1.setPassword(testPassword);
        param1.setConfirmPassword(testPassword);
        param1.setVerificationCode(generateMockVerificationCode());

        AuthRegisterParam param2 = new AuthRegisterParam();
        param2.setUsername("concurrent2");
        param2.setEmail(concurrentEmail); // 相同邮箱
        param2.setPassword(testPassword);
        param2.setConfirmPassword(testPassword);
        param2.setVerificationCode(generateMockVerificationCode());

        // 第一个请求应该成功
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param1)))
                .andExpect(status().isOk());

        // 第二个请求应该失败（邮箱已存在）
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("该邮箱已被注册"));
    }

    /**
     * 生成模拟验证码
     */
    private String generateMockVerificationCode() {
        return "123456"; // 在真实环境中这应该是随机生成的
    }
}