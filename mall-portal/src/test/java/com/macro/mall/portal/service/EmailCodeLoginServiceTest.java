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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 邮箱验证码登录服务测试
 * 测试邮箱验证码登录的完整功能：
 * 1. 邮箱不存在时自动创建账号并登录
 * 2. 邮箱存在时直接登录
 * @author Claude
 * @since 2025-09-11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("邮箱验证码登录服务测试")
public class EmailCodeLoginServiceTest {

    @Mock
    private UmsMemberMapper memberMapper;

    @Mock
    private UmsMemberExtMapper memberExtMapper;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private TokenService tokenService;

    @Mock
    private MailService mailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private EmailCodeLoginParam loginParam;
    private final String testEmail = "newuser@example.com";
    private final String existingEmail = "existing@example.com";
    private final String testCode = "123456";

    @BeforeEach
    void setUp() {
        loginParam = new EmailCodeLoginParam();
        loginParam.setEmail(testEmail);
        loginParam.setVerificationCode(testCode);
    }

    @Test
    @DisplayName("测试邮箱不存在时自动创建账号并登录成功")
    void testEmailCodeLogin_NewUser_CreateAndLoginSuccess() {
        // Given
        // 模拟验证码验证成功
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN))
                .thenReturn(true);
        
        // 模拟用户不存在
        when(memberExtMapper.selectByEmail(testEmail)).thenReturn(null);
        
        // 模拟用户名检查（确保生成的用户名唯一）
        when(memberExtMapper.selectByUsername(anyString())).thenReturn(null);
        
        // 模拟创建用户成功
        UmsMember newMember = createTestMember(1L, "newuser", testEmail);
        when(memberMapper.insert(any(UmsMember.class))).thenAnswer(invocation -> {
            UmsMember member = invocation.getArgument(0);
            member.setId(1L); // 模拟数据库生成ID
            return 1;
        });
        
        // 模拟Token生成
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "access_token_123");
        tokenPair.put("refresh_token", "refresh_token_123");
        when(tokenService.generateTokenPair(anyString(), eq(1L))).thenReturn(tokenPair);

        // When
        AuthTokenResult result = authService.loginWithEmailCode(loginParam);

        // Then
        assertNotNull(result, "登录结果不应该为空");
        assertEquals("access_token_123", result.getAccessToken(), "访问Token应该正确");
        assertEquals("refresh_token_123", result.getRefreshToken(), "刷新Token应该正确");
        assertEquals("Bearer", result.getTokenType(), "Token类型应该为Bearer");
        assertEquals(86400L, result.getExpiresIn(), "Token过期时间应该为86400秒");
        
        assertNotNull(result.getUserInfo(), "用户信息不应该为空");
        assertEquals(1L, result.getUserInfo().getId(), "用户ID应该正确");
        
        // 验证调用
        verify(verificationCodeService).verifyCode(testEmail, testCode, CodeType.LOGIN);
        verify(memberExtMapper).selectByEmail(testEmail);
        verify(memberMapper).insert(any(UmsMember.class));
        verify(tokenService).generateTokenPair(anyString(), eq(1L));
        
        // 验证发送了验证码邮件（新用户创建后应该发送邮件）
        verify(verificationCodeService).sendCode(testEmail, CodeType.LOGIN);
        
        // 验证创建的用户信息
        verify(memberMapper).insert(argThat(member -> {
            return member.getUsername().startsWith("newuser") && 
                   member.getNickname().startsWith("newuser") &&
                   member.getStatus().equals(AccountStatus.NORMAL.getCode());
        }));
    }

    @Test
    @DisplayName("测试邮箱存在时直接登录成功")
    void testEmailCodeLogin_ExistingUser_LoginSuccess() {
        // Given
        loginParam.setEmail(existingEmail);
        
        // 模拟验证码验证成功
        when(verificationCodeService.verifyCode(existingEmail, testCode, CodeType.LOGIN))
                .thenReturn(true);
        
        // 模拟用户已存在
        UmsMember existingMember = createTestMember(2L, "existing_user", existingEmail);
        when(memberExtMapper.selectByEmail(existingEmail)).thenReturn(existingMember);
        
        // 模拟Token生成
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "existing_access_token");
        tokenPair.put("refresh_token", "existing_refresh_token");
        when(tokenService.generateTokenPair("existing_user", 2L)).thenReturn(tokenPair);

        // When
        AuthTokenResult result = authService.loginWithEmailCode(loginParam);

        // Then
        assertNotNull(result, "登录结果不应该为空");
        assertEquals("existing_access_token", result.getAccessToken(), "访问Token应该正确");
        assertEquals("existing_refresh_token", result.getRefreshToken(), "刷新Token应该正确");
        
        assertNotNull(result.getUserInfo(), "用户信息不应该为空");
        assertEquals(2L, result.getUserInfo().getId(), "用户ID应该正确");
        assertEquals("existing_user", result.getUserInfo().getUsername(), "用户名应该正确");
        assertEquals(existingEmail, result.getUserInfo().getEmail(), "邮箱应该正确");
        
        // 验证调用
        verify(verificationCodeService).verifyCode(existingEmail, testCode, CodeType.LOGIN);
        verify(memberExtMapper).selectByEmail(existingEmail);
        verify(tokenService).generateTokenPair("existing_user", 2L);
        
        // 验证没有创建新用户
        verify(memberMapper, never()).insert(any(UmsMember.class));
        
        // 验证更新了最后登录时间
        verify(memberExtMapper).updateLastLoginTime(eq(2L), any(Date.class));
        
        // 验证发送了验证码邮件（已存在用户登录时也应该发送邮件）
        verify(verificationCodeService).sendCode(existingEmail, CodeType.LOGIN);
    }

    @Test
    @DisplayName("测试验证码无效时登录失败")
    void testEmailCodeLogin_InvalidCode_LoginFailed() {
        // Given
        // 模拟验证码验证失败
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN))
                .thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.loginWithEmailCode(loginParam);
        }, "应该抛出运行时异常");

        assertEquals("验证码无效或已过期", exception.getMessage(), "异常消息应该正确");
        
        // 验证只调用了验证码验证，没有其他操作
        verify(verificationCodeService).verifyCode(testEmail, testCode, CodeType.LOGIN);
        verify(memberExtMapper, never()).selectByEmail(anyString());
        verify(memberMapper, never()).insert(any(UmsMember.class));
        verify(tokenService, never()).generateTokenPair(anyString(), anyLong());
    }

    @Test
    @DisplayName("测试创建用户时用户名生成逻辑")
    void testEmailCodeLogin_UsernameGeneration() {
        // Given
        String complexEmail = "test.user+tag@example.com";
        loginParam.setEmail(complexEmail);
        
        // 模拟验证码验证成功
        when(verificationCodeService.verifyCode(complexEmail, testCode, CodeType.LOGIN))
                .thenReturn(true);
        
        // 模拟用户不存在
        when(memberExtMapper.selectByEmail(complexEmail)).thenReturn(null);
        
        // 模拟第一个用户名已存在，第二个可用
        when(memberExtMapper.selectByUsername("test_user_tag"))
                .thenReturn(new UmsMember()); // 已存在
        when(memberExtMapper.selectByUsername("test_user_tag_1"))
                .thenReturn(null); // 可用
        
        // 模拟创建用户成功
        when(memberMapper.insert(any(UmsMember.class))).thenAnswer(invocation -> {
            UmsMember member = invocation.getArgument(0);
            member.setId(4L);
            return 1;
        });
        
        // 模拟Token生成
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", "test_token");
        tokenPair.put("refresh_token", "test_refresh");
        when(tokenService.generateTokenPair(anyString(), eq(4L))).thenReturn(tokenPair);

        // When
        AuthTokenResult result = authService.loginWithEmailCode(loginParam);

        // Then
        assertNotNull(result, "登录结果不应该为空");
        
        // 验证用户名生成逻辑
        verify(memberExtMapper).selectByUsername("test_user_tag");
        verify(memberExtMapper).selectByUsername("test_user_tag_1");
        
        // 验证创建的用户使用了正确的用户名
        verify(memberMapper).insert(argThat(member -> 
            "test_user_tag_1".equals(member.getUsername())
        ));
        
        // 验证发送了验证码邮件
        verify(verificationCodeService).sendCode(complexEmail, CodeType.LOGIN);
    }

    @Test
    @DisplayName("测试数据库插入失败时抛出异常")
    void testEmailCodeLogin_DatabaseInsertFailed() {
        // Given
        // 模拟验证码验证成功
        when(verificationCodeService.verifyCode(testEmail, testCode, CodeType.LOGIN))
                .thenReturn(true);
        
        // 模拟用户不存在
        when(memberExtMapper.selectByEmail(testEmail)).thenReturn(null);
        
        // 模拟用户名唯一
        when(memberExtMapper.selectByUsername(anyString())).thenReturn(null);
        
        // 模拟数据库插入失败
        when(memberMapper.insert(any(UmsMember.class))).thenReturn(0);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.loginWithEmailCode(loginParam);
        }, "应该抛出运行时异常");

        assertEquals("创建用户失败", exception.getMessage(), "异常消息应该正确");
        
        // 验证调用了插入操作
        verify(memberMapper).insert(any(UmsMember.class));
        
        // 验证没有生成Token
        verify(tokenService, never()).generateTokenPair(anyString(), anyLong());
    }

    /**
     * 创建测试用户
     */
    private UmsMember createTestMember(Long id, String username, String email) {
        UmsMember member = new UmsMember();
        member.setId(id);
        member.setUsername(username);
        member.setNickname(username);
        member.setPassword(new BCryptPasswordEncoder().encode("test_password"));
        member.setStatus(AccountStatus.NORMAL.getCode());
        member.setCreateTime(new Date());
        
        // 这里无法直接设置email等扩展字段，在实际测试中需要mock或使用真实的扩展类
        // 在buildUserInfoResult方法中会尝试通过反射获取这些字段
        return member;
    }
}