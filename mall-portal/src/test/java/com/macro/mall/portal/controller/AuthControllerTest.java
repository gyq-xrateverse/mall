package com.macro.mall.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dto.*;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.service.AuthService;
import com.macro.mall.portal.service.VerificationCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证控制器集成测试
 */
@WebMvcTest(AuthController.class)
@DisplayName("认证控制器测试")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";
    private final String testCode = "123456";
    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        // Mock服务默认行为
        when(authService.isEmailExists(testEmail)).thenReturn(false);
        when(authService.isUsernameExists(testUsername)).thenReturn(false);
    }

    @Test
    @DisplayName("发送注册验证码成功")
    void testSendRegisterCodeSuccess() throws Exception {
        // Given
        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail(testEmail);
        param.setCodeType(CodeType.REGISTER.getCode());

        when(authService.sendVerificationCode(any())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("验证码发送成功"))
                .andExpect(jsonPath("$.data").value(true));

        verify(authService).sendVerificationCode(any());
    }

    @Test
    @DisplayName("发送验证码失败 - 无效邮箱格式")
    void testSendCodeInvalidEmail() throws Exception {
        // Given
        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail("invalid-email");
        param.setCodeType(CodeType.REGISTER.getCode());

        // When & Then
        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));

        verify(authService, never()).sendVerificationCode(any());
    }

    @Test
    @DisplayName("发送验证码失败 - 邮箱已存在（注册类型）")
    void testSendRegisterCodeEmailExists() throws Exception {
        // Given
        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail(testEmail);
        param.setCodeType(CodeType.REGISTER.getCode());

        when(authService.isEmailExists(testEmail)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("邮箱已存在"));

        verify(authService, never()).sendVerificationCode(any());
    }

    @Test
    @DisplayName("用户注册成功")
    void testRegisterSuccess() throws Exception {
        // Given
        AuthRegisterParam param = new AuthRegisterParam();
        param.setUsername(testUsername);
        param.setEmail(testEmail);
        param.setPassword(testPassword);
        param.setConfirmPassword(testPassword);
        param.setVerificationCode(testCode);

        AuthTokenResult tokenResult = new AuthTokenResult();
        tokenResult.setAccessToken("mock-jwt-token");
        tokenResult.setRefreshToken("mock-refresh-token");

        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.REGISTER)).thenReturn(true);
        when(authService.register(any(AuthRegisterParam.class))).thenReturn(tokenResult);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"));

        verify(verificationCodeService).verifyCode(testEmail, testCode, CodeType.REGISTER);
        verify(authService).register(any(AuthRegisterParam.class));
    }

    @Test
    @DisplayName("用户注册失败 - 验证码错误")
    void testRegisterFailedInvalidCode() throws Exception {
        // Given
        AuthRegisterParam param = new AuthRegisterParam();
        param.setUsername(testUsername);
        param.setEmail(testEmail);
        param.setPassword(testPassword);
        param.setConfirmPassword(testPassword);
        param.setVerificationCode("wrong-code");

        when(verificationCodeService.verifyCode(testEmail, "wrong-code", CodeType.REGISTER)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("验证码错误或已过期"));

        verify(authService, never()).register(any(AuthRegisterParam.class));
    }

    @Test
    @DisplayName("用户登录成功")
    void testLoginSuccess() throws Exception {
        // Given
        AuthLoginParam param = new AuthLoginParam();
        param.setEmail(testEmail);
        param.setPassword(testPassword);

        AuthTokenResult tokenResult = new AuthTokenResult();
        tokenResult.setAccessToken("mock-jwt-token");
        tokenResult.setRefreshToken("mock-refresh-token");

        when(authService.login(param)).thenReturn(tokenResult);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"));

        verify(authService).login(param);
    }

    @Test
    @DisplayName("用户登录失败 - 用户名或密码错误")
    void testLoginFailedInvalidCredentials() throws Exception {
        // Given
        AuthLoginParam param = new AuthLoginParam();
        param.setEmail(testEmail);
        param.setPassword("wrong-password");

        when(authService.login(param)).thenReturn(null);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    @DisplayName("重置密码成功")
    void testResetPasswordSuccess() throws Exception {
        // Given
        ResetPasswordParam param = new ResetPasswordParam();
        param.setEmail(testEmail);
        param.setVerificationCode(testCode);
        param.setNewPassword("newPassword123");
        param.setConfirmPassword("newPassword123");

        when(authService.isEmailExists(testEmail)).thenReturn(true);
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.RESET_PASSWORD)).thenReturn(true);
        when(authService.resetPassword(param)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码重置成功"));

        verify(verificationCodeService).verifyCode(testEmail, testCode, CodeType.RESET_PASSWORD);
        verify(authService).resetPassword(any(ResetPasswordParam.class));
    }

    @Test
    @DisplayName("重置密码失败 - 邮箱不存在")
    void testResetPasswordFailedEmailNotExists() throws Exception {
        // Given
        ResetPasswordParam param = new ResetPasswordParam();
        param.setEmail(testEmail);
        param.setVerificationCode(testCode);
        param.setNewPassword("newPassword123");
        param.setConfirmPassword("newPassword123");

        when(authService.isEmailExists(testEmail)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("邮箱不存在"));

        verify(verificationCodeService, never()).verifyCode(anyString(), anyString(), any(CodeType.class));
        verify(authService, never()).resetPassword(any(ResetPasswordParam.class));
    }

    @Test
    @DisplayName("检查邮箱存在性")
    void testCheckEmailExists() throws Exception {
        // Given
        when(authService.isEmailExists(testEmail)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/auth/check-email")
                .param("email", testEmail))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        verify(authService).isEmailExists(testEmail);
    }

    @Test
    @DisplayName("检查用户名存在性")
    void testCheckUsernameExists() throws Exception {
        // Given
        when(authService.isUsernameExists(testUsername)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/auth/check-username")
                .param("username", testUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));

        verify(authService).isUsernameExists(testUsername);
    }

    @Test
    @DisplayName("刷新Token成功")
    void testRefreshTokenSuccess() throws Exception {
        // Given
        String refreshToken = "valid-refresh-token";
        String newAccessToken = "new-jwt-token";

        when(authService.refreshAccessToken(refreshToken)).thenReturn(newAccessToken);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh-token")
                .param("refreshToken", refreshToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("new-jwt-token"));

        verify(authService).refreshAccessToken(refreshToken);
    }

    @Test
    @DisplayName("注销成功")
    void testLogoutSuccess() throws Exception {
        // Given
        String token = "valid-token";
        when(authService.logout(token)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注销成功"));

        verify(authService).logout(token);
    }

    @Test
    @DisplayName("参数验证测试 - 缺少必填字段")
    void testValidationRequiredFields() throws Exception {
        // 测试注册时缺少必填字段
        AuthRegisterParam incompleteParam = new AuthRegisterParam();
        incompleteParam.setEmail(testEmail); // 缺少用户名和密码

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteParam)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("参数验证测试 - 密码强度")
    void testPasswordValidation() throws Exception {
        // 测试弱密码
        AuthRegisterParam weakPasswordParam = new AuthRegisterParam();
        weakPasswordParam.setUsername(testUsername);
        weakPasswordParam.setEmail(testEmail);
        weakPasswordParam.setPassword("123"); // 弱密码
        weakPasswordParam.setConfirmPassword("123");
        weakPasswordParam.setVerificationCode(testCode);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPasswordParam)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("速率限制测试")
    void testRateLimiting() throws Exception {
        // Given
        VerificationCodeParam param = new VerificationCodeParam();
        param.setEmail(testEmail);
        param.setCodeType(CodeType.REGISTER.getCode());

        // 第一次请求成功
        when(authService.sendVerificationCode(any()))
            .thenReturn(true)
            .thenReturn(false); // 第二次请求因为间隔限制失败

        // When & Then - 第一次请求
        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andExpect(status().isOk());

        // When & Then - 第二次请求（应该被限制）
        mockMvc.perform(post("/api/auth/send-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(param)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("发送过于频繁，请稍后再试"));
    }
}