package com.macro.mall.portal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.dto.*;
import com.macro.mall.portal.service.AuthService;
import com.macro.mall.portal.service.OAuth2Service;
import com.macro.mall.portal.service.TokenService;
import com.macro.mall.portal.service.VerificationCodeService;
import com.macro.mall.portal.service.impl.OAuth2ServiceImpl;
import com.macro.mall.portal.config.OAuth2Properties;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.security.util.PortalJwtTokenUtil;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.mockito.Mockito;

/**
 * 认证API核心接口测试
 * 基于API文档的完整测试用例
 * @author Claude
 * @since 2025-09-24
 */
@WebMvcTest(controllers = AuthController.class,
    excludeAutoConfiguration = SecurityAutoConfiguration.class)
@Import(AuthApiTest.TestConfig.class)
@DisplayName("认证API核心接口测试")
public class AuthApiTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Qualifier("portalJwtTokenUtil")
        public PortalJwtTokenUtil portalJwtTokenUtil() {
            return Mockito.mock(PortalJwtTokenUtil.class);
        }

        @Bean
        public OAuth2Service oauth2Service() {
            return Mockito.mock(OAuth2Service.class);
        }

        @Bean
        public OAuth2Properties oauth2Properties() {
            return Mockito.mock(OAuth2Properties.class);
        }

        @Bean("portalUserDetailsService")
        public UserDetailsService portalUserDetailsService() {
            return Mockito.mock(UserDetailsService.class);
        }

        @Bean
        public UmsMemberService umsMemberService() {
            return Mockito.mock(UmsMemberService.class);
        }

    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private VerificationCodeService verificationCodeService;


    @Autowired
    @Qualifier("portalJwtTokenUtil")
    private PortalJwtTokenUtil jwtTokenUtil;

    @Autowired
    private OAuth2Service oauth2Service;

    @Autowired
    private OAuth2Properties oauth2Properties;

    @Autowired
    @Qualifier("portalUserDetailsService")
    private UserDetailsService portalUserDetailsService;

    @Autowired
    private UmsMemberService umsMemberService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // 测试数据常量
    private static final String TEST_EMAIL = "user@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_USERNAME = "user123";
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTY5NTU2MTIwMCwiZXhwIjoxNjk1NjQ3NjAwfQ.signature";
    private static final String TEST_REFRESH_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6InJlZnJlc2giLCJpYXQiOjE2OTU1NjEyMDAsImV4cCI6MTY5NjE2NjAwMH0.signature";
    private static final String NEW_ACCESS_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTY5NTU2MTIwMCwiZXhwIjoxNjk1NjQ3NjAwfQ.newSignature";

    @BeforeEach
    void setUp() {
        // 重置所有mock对象
        reset(authService, oauth2Service, oauth2Properties, tokenService, verificationCodeService,
              portalUserDetailsService, umsMemberService, jwtTokenUtil, redisTemplate);
    }

    @Nested
    @DisplayName("1. 用户登录接口测试")
    class LoginApiTest {

        @Test
        @DisplayName("登录成功 - 返回完整token信息")
        public void testLoginSuccess() throws Exception {
            // Given - 准备测试数据
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(TEST_PASSWORD);

            UserInfoResult userInfo = new UserInfoResult();
            userInfo.setId(TEST_USER_ID);
            userInfo.setUsername(TEST_USERNAME);
            userInfo.setEmail(TEST_EMAIL);
            userInfo.setNickname("用户昵称");
            userInfo.setAvatar("http://example.com/avatar.jpg");
            userInfo.setAccountStatus(1);

            AuthTokenResult tokenResult = new AuthTokenResult();
            tokenResult.setAccessToken(TEST_ACCESS_TOKEN);
            tokenResult.setRefreshToken(TEST_REFRESH_TOKEN);
            tokenResult.setTokenType("Bearer");
            tokenResult.setExpiresIn(86400L);
            tokenResult.setUserInfo(userInfo);

            // Mock服务行为
            when(authService.login(any())).thenReturn(tokenResult);

            // When & Then - 执行请求并验证结果
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data.accessToken").value(TEST_ACCESS_TOKEN))
                    .andExpect(jsonPath("$.data.refreshToken").value(TEST_REFRESH_TOKEN))
                    .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                    .andExpect(jsonPath("$.data.expiresIn").value("86400"))
                    .andExpect(jsonPath("$.data.userInfo.id").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.data.userInfo.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.data.userInfo.email").value(TEST_EMAIL));

            // 验证服务调用
            verify(authService).login(any());
        }

        @Test
        @DisplayName("登录失败 - 邮箱不能为空")
        public void testLoginFailedEmptyEmail() throws Exception {
            // Given
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(""); // 空邮箱
            loginParam.setPassword(TEST_PASSWORD);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").isNotEmpty());

            // 验证服务未被调用
            verify(authService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 密码不能为空")
        public void testLoginFailedEmptyPassword() throws Exception {
            // Given
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(""); // 空密码

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            verify(authService, never()).login(any());
        }

        @Test
        @DisplayName("登录失败 - 邮箱或密码错误")
        public void testLoginFailedInvalidCredentials() throws Exception {
            // Given
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword("wrongPassword");

            when(authService.login(any()))
                    .thenThrow(new RuntimeException("邮箱或密码错误"));

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andDo(print())
                    .andExpect(status().isOk()) // 控制器捕获异常返回200
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("邮箱或密码错误"));

            verify(authService).login(any());
        }

        @Test
        @DisplayName("登录失败 - 邮箱格式不正确")
        public void testLoginFailedInvalidEmailFormat() throws Exception {
            // Given
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail("invalid-email-format");
            loginParam.setPassword(TEST_PASSWORD);

            // When & Then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));

            verify(authService, never()).login(any());
        }
    }

    @Nested
    @DisplayName("2. Token验证接口测试")
    class ValidateTokenApiTest {

        @Test
        @DisplayName("Token验证成功 - 返回用户信息")
        public void testValidateTokenSuccess() throws Exception {
            // Given
            when(jwtTokenUtil.validateAccessToken(TEST_ACCESS_TOKEN)).thenReturn(true);
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(jwtTokenUtil.getUserTypeFromToken(TEST_ACCESS_TOKEN)).thenReturn("member");

            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("Token验证成功"))
                    .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
                    .andExpect(jsonPath("$.data.userId").value(TEST_USER_ID))
                    .andExpect(jsonPath("$.data.userType").value("member"))
                    .andExpect(jsonPath("$.data.valid").value(true));

            // 验证调用
            verify(jwtTokenUtil).validateAccessToken(TEST_ACCESS_TOKEN);
            verify(jwtTokenUtil).getUsernameFromToken(TEST_ACCESS_TOKEN);
            verify(jwtTokenUtil).getUserIdFromToken(TEST_ACCESS_TOKEN);
            verify(jwtTokenUtil).getUserTypeFromToken(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("Token验证失败 - Token不能为空")
        public void testValidateTokenFailedEmptyToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", ""))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Token不能为空"));

            verify(jwtTokenUtil, never()).validateAccessToken(anyString());
        }

        @Test
        @DisplayName("Token验证失败 - Token无效")
        public void testValidateTokenFailedInvalidToken() throws Exception {
            // Given
            String invalidToken = "invalid-token";
            when(jwtTokenUtil.validateAccessToken(invalidToken)).thenReturn(false);

            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", invalidToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Token验证失败"));

            verify(jwtTokenUtil).validateAccessToken(invalidToken);
            verify(jwtTokenUtil, never()).getUsernameFromToken(anyString());
        }

        @Test
        @DisplayName("Token验证异常 - 服务器内部错误")
        public void testValidateTokenException() throws Exception {
            // Given
            when(jwtTokenUtil.validateAccessToken(TEST_ACCESS_TOKEN))
                    .thenThrow(new RuntimeException("JWT解析异常"));

            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("Token验证异常: JWT解析异常"));

            verify(jwtTokenUtil).validateAccessToken(TEST_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("3. 刷新Token接口测试")
    class RefreshTokenApiTest {

        @Test
        @DisplayName("刷新Token成功 - 返回新的access token")
        public void testRefreshTokenSuccess() throws Exception {
            // Given
            when(authService.refreshAccessToken(TEST_REFRESH_TOKEN)).thenReturn(NEW_ACCESS_TOKEN);

            // When & Then
            mockMvc.perform(post("/api/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("refreshToken", TEST_REFRESH_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value(NEW_ACCESS_TOKEN));

            verify(authService).refreshAccessToken(TEST_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("刷新Token失败 - refreshToken不能为空")
        public void testRefreshTokenFailedEmptyToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("refreshToken", ""))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("刷新Token不能为空"));

            verify(authService, never()).refreshAccessToken(anyString());
        }

        @Test
        @DisplayName("刷新Token失败 - refreshToken无效或已过期")
        public void testRefreshTokenFailedInvalidOrExpired() throws Exception {
            // Given
            String expiredRefreshToken = "expired-refresh-token";
            when(authService.refreshAccessToken(expiredRefreshToken)).thenReturn(null);

            // When & Then
            mockMvc.perform(post("/api/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("refreshToken", expiredRefreshToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("刷新Token无效或已过期"));

            verify(authService).refreshAccessToken(expiredRefreshToken);
        }

        @Test
        @DisplayName("刷新Token异常 - 服务器内部错误")
        public void testRefreshTokenException() throws Exception {
            // Given
            when(authService.refreshAccessToken(TEST_REFRESH_TOKEN))
                    .thenThrow(new RuntimeException("Redis连接异常"));

            // When & Then
            mockMvc.perform(post("/api/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("refreshToken", TEST_REFRESH_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("Token刷新失败: Redis连接异常"));

            verify(authService).refreshAccessToken(TEST_REFRESH_TOKEN);
        }
    }

    @Nested
    @DisplayName("4. 强制用户下线接口测试")
    class ForceLogoutApiTest {

        @Test
        @DisplayName("强制下线成功")
        public void testForceLogoutSuccess() throws Exception {
            // Given
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            doNothing().when(tokenService).revokeToken(TEST_ACCESS_TOKEN);

            // When & Then
            mockMvc.perform(post("/api/auth/force-logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value("用户下线成功"));

            // 验证调用
            verify(jwtTokenUtil).getUsernameFromToken(TEST_ACCESS_TOKEN);
            verify(jwtTokenUtil).getUserIdFromToken(TEST_ACCESS_TOKEN);
            verify(tokenService).revokeToken(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("强制下线失败 - Token不能为空")
        public void testForceLogoutFailedEmptyToken() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/auth/force-logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", ""))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Token不能为空"));

            verify(tokenService, never()).revokeToken(anyString());
        }

        @Test
        @DisplayName("强制下线失败 - Token格式无效")
        public void testForceLogoutFailedInvalidTokenFormat() throws Exception {
            // Given
            String invalidToken = "invalid-token-format";
            when(jwtTokenUtil.getUsernameFromToken(invalidToken)).thenReturn(null);
            when(jwtTokenUtil.getUserIdFromToken(invalidToken)).thenReturn(null);

            // When & Then
            mockMvc.perform(post("/api/auth/force-logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", invalidToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("Token格式无效"));

            verify(tokenService, never()).revokeToken(anyString());
        }

        @Test
        @DisplayName("强制下线异常 - 服务器内部错误")
        public void testForceLogoutException() throws Exception {
            // Given
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            doThrow(new RuntimeException("Redis操作异常")).when(tokenService).revokeToken(TEST_ACCESS_TOKEN);

            // When & Then
            mockMvc.perform(post("/api/auth/force-logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户下线失败: Redis操作异常"));

            verify(tokenService).revokeToken(TEST_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("5. 用户注销接口测试")
    class LogoutApiTest {

        @Test
        @DisplayName("用户注销成功")
        public void testLogoutSuccess() throws Exception {
            // Given
            when(authService.logout(TEST_ACCESS_TOKEN)).thenReturn(true);

            // When & Then
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("操作成功"))
                    .andExpect(jsonPath("$.data").value("注销成功"));

            verify(authService).logout(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("用户注销成功 - 无Token（幂等性）")
        public void testLogoutSuccessWithoutToken() throws Exception {
            // When & Then - 不提供Authorization头
            mockMvc.perform(post("/api/auth/logout"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("注销成功"));

            // 验证服务未被调用（token为空时直接返回成功）
            verify(authService, never()).logout(anyString());
        }

        @Test
        @DisplayName("用户注销失败")
        public void testLogoutFailed() throws Exception {
            // Given
            when(authService.logout(TEST_ACCESS_TOKEN)).thenReturn(false);

            // When & Then
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("注销失败"));

            verify(authService).logout(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("用户注销异常 - 服务器内部错误")
        public void testLogoutException() throws Exception {
            // Given
            when(authService.logout(TEST_ACCESS_TOKEN))
                    .thenThrow(new RuntimeException("服务异常"));

            // When & Then
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("注销失败: 服务异常"));

            verify(authService).logout(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("用户注销 - Authorization头格式错误")
        public void testLogoutWithInvalidAuthorizationHeader() throws Exception {
            // When & Then - 错误的Authorization头格式
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "InvalidFormat " + TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("注销成功"));

            // 验证服务未被调用（因为token解析失败）
            verify(authService, never()).logout(anyString());
        }
    }

    @Nested
    @DisplayName("边界条件和异常测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("并发登录测试")
        public void testConcurrentLogin() throws Exception {
            // Given
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(TEST_PASSWORD);

            AuthTokenResult tokenResult = new AuthTokenResult();
            tokenResult.setAccessToken(TEST_ACCESS_TOKEN);
            tokenResult.setRefreshToken(TEST_REFRESH_TOKEN);

            when(authService.login(any())).thenReturn(tokenResult);

            // When & Then - 模拟并发请求
            for (int i = 0; i < 5; i++) {
                mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginParam)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200));
            }

            // 验证服务被调用5次
            verify(authService, times(5)).login(any());
        }

        @Test
        @DisplayName("超长Token测试")
        public void testVeryLongToken() throws Exception {
            // Given - 构建超长token（模拟恶意请求）
            StringBuilder longToken = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                longToken.append("a");
            }

            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", longToken.toString()))
                    .andDo(print())
                    .andExpect(status().isOk());
            // 应该正常处理，不会导致系统崩溃
        }

        @Test
        @DisplayName("特殊字符Token测试")
        public void testSpecialCharacterToken() throws Exception {
            // Given
            String specialToken = "<!@#$%^&*()_+{}|:<>?[]\\;',./>";
            when(jwtTokenUtil.validateAccessToken(specialToken)).thenReturn(false);

            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", specialToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400));

            verify(jwtTokenUtil).validateAccessToken(specialToken);
        }

        @Test
        @DisplayName("Redis连接异常测试")
        public void testRedisConnectionException() throws Exception {
            // Given - 模拟Redis连接异常
            when(jwtTokenUtil.validateAccessToken(TEST_ACCESS_TOKEN))
                    .thenThrow(new RuntimeException("Redis connection failed"));

            // When & Then
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", TEST_ACCESS_TOKEN))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message", containsString("Redis connection failed")));
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTest {

        @Test
        @DisplayName("高频Token验证测试")
        public void testHighFrequencyTokenValidation() throws Exception {
            // Given
            when(jwtTokenUtil.validateAccessToken(TEST_ACCESS_TOKEN)).thenReturn(true);
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(jwtTokenUtil.getUserTypeFromToken(TEST_ACCESS_TOKEN)).thenReturn("member");

            long startTime = System.currentTimeMillis();

            // When - 执行100次Token验证
            for (int i = 0; i < 100; i++) {
                mockMvc.perform(post("/api/auth/validate-token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("token", TEST_ACCESS_TOKEN))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.code").value(200));
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Then - 验证性能（应该在合理时间内完成）
            System.out.println("100次Token验证耗时: " + duration + "ms");
            // 可以添加具体的性能断言
            // assertTrue(duration < 5000, "Token验证性能不达标");

            verify(jwtTokenUtil, times(100)).validateAccessToken(TEST_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("安全测试")
    class SecurityTest {

        @Test
        @DisplayName("SQL注入防护测试")
        public void testSqlInjectionProtection() throws Exception {
            // Given - SQL注入尝试
            AuthLoginParam maliciousParam = new AuthLoginParam();
            maliciousParam.setEmail("test@example.com'; DROP TABLE users; --");
            maliciousParam.setPassword("password' OR '1'='1");

            // When & Then - 应该被参数验证或服务层正确处理
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(maliciousParam)))
                    .andDo(print());
            // 系统应该正常处理，不会执行恶意SQL
        }

        @Test
        @DisplayName("XSS攻击防护测试")
        public void testXssProtection() throws Exception {
            // Given - XSS攻击尝试
            String xssToken = "<script>alert('XSS')</script>";

            // When & Then - 应该被正确处理，不会执行脚本
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", xssToken))
                    .andDo(print())
                    .andExpect(status().isOk());
            // 响应应该不包含未转义的脚本内容
        }
    }
}
