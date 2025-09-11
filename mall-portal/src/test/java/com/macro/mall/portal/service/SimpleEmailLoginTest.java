package com.macro.mall.portal.service;

import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.dao.UmsMemberExtMapper;
import com.macro.mall.portal.dto.AuthTokenResult;
import com.macro.mall.portal.dto.EmailCodeLoginParam;
import com.macro.mall.portal.enums.AccountStatus;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 简洁的邮箱验证码登录测试
 * 专门测试两个核心功能：
 * 1. 邮箱不存在时创建账号并发送验证码
 * 2. 邮箱存在时直接发送验证码
 * @author Claude
 * @since 2025-09-11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("简洁邮箱验证码登录测试")
public class SimpleEmailLoginTest {

    @Mock
    private UmsMemberMapper memberMapper;

    @Mock
    private UmsMemberExtMapper memberExtMapper;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    private EmailCodeLoginParam loginParam;
    private final String testEmail = "test@example.com";
    private final String testCode = "123456";

    @BeforeEach
    void setUp() {
        loginParam = new EmailCodeLoginParam();
        loginParam.setEmail(testEmail);
        loginParam.setVerificationCode(testCode);
    }

    @Test
    @DisplayName("邮箱不存在时创建账号并发送验证码")
    void testCreateAccountAndSendCode() {
        // Given - 邮箱不存在
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN)).thenReturn(true);
        when(memberExtMapper.selectByEmail(testEmail)).thenReturn(null);
        when(memberExtMapper.selectByUsername(anyString())).thenReturn(null);
        
        // 模拟创建用户成功
        when(memberMapper.insert(any(UmsMember.class))).thenAnswer(invocation -> {
            UmsMember member = invocation.getArgument(0);
            member.setId(1L);
            return 1;
        });
        
        // 模拟Token生成
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "test_token");
        tokenPair.put("refresh_token", "refresh_token");
        when(tokenService.generateTokenPair(anyString(), eq(1L))).thenReturn(tokenPair);

        // When
        AuthTokenResult result = authService.loginWithEmailCode(loginParam);

        // Then
        assertNotNull(result);
        assertEquals("test_token", result.getAccessToken());
        
        // 验证创建了新用户
        verify(memberMapper).insert(any(UmsMember.class));
        
        // 验证发送了验证码（这是关键验证）
        verify(verificationCodeService).sendCode(testEmail, CodeType.LOGIN);
    }

    @Test
    @DisplayName("邮箱存在时直接发送验证码")
    void testExistingAccountSendCode() {
        // Given - 邮箱存在
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN)).thenReturn(true);
        
        UmsMember existingUser = createTestUser(2L, "existing_user");
        when(memberExtMapper.selectByEmail(testEmail)).thenReturn(existingUser);
        
        // 模拟Token生成
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "existing_token");
        tokenPair.put("refresh_token", "existing_refresh");
        when(tokenService.generateTokenPair("existing_user", 2L)).thenReturn(tokenPair);

        // When
        AuthTokenResult result = authService.loginWithEmailCode(loginParam);

        // Then
        assertNotNull(result);
        assertEquals("existing_token", result.getAccessToken());
        
        // 验证没有创建新用户
        verify(memberMapper, never()).insert(any(UmsMember.class));
        
        // 验证发送了验证码（这是关键验证）
        verify(verificationCodeService).sendCode(testEmail, CodeType.LOGIN);
    }

    @Test
    @DisplayName("验证码错误时登录失败")
    void testInvalidCodeFails() {
        // Given
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.loginWithEmailCode(loginParam);
        });

        assertEquals("验证码无效或已过期", exception.getMessage());
        
        // 验证没有进行其他操作
        verify(memberExtMapper, never()).selectByEmail(anyString());
        verify(memberMapper, never()).insert(any(UmsMember.class));
        verify(verificationCodeService, never()).sendCode(anyString(), any(CodeType.class));
    }

    private UmsMember createTestUser(Long id, String username) {
        UmsMember member = new UmsMember();
        member.setId(id);
        member.setUsername(username);
        member.setNickname(username);
        member.setStatus(AccountStatus.NORMAL.getCode());
        member.setCreateTime(new Date());
        return member;
    }
}