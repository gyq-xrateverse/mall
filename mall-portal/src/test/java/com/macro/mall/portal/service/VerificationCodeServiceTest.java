package com.macro.mall.portal.service;

import com.macro.mall.portal.dao.UmsVerificationCodeMapper;
import com.macro.mall.portal.domain.UmsVerificationCode;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.enums.CodeUsedStatus;
import com.macro.mall.portal.service.impl.VerificationCodeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 验证码服务单元测试
 */
@DisplayName("验证码服务测试")
public class VerificationCodeServiceTest {

    @Mock
    private UmsVerificationCodeMapper verificationCodeMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private MailService mailService;

    @InjectMocks
    private VerificationCodeServiceImpl verificationCodeService;

    private final String testEmail = "test@example.com";
    private final String testIpAddress = "192.168.1.1";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // 设置验证码配置
        ReflectionTestUtils.setField(verificationCodeService, "codeLength", 6);
        ReflectionTestUtils.setField(verificationCodeService, "expireMinutes", 5);
        ReflectionTestUtils.setField(verificationCodeService, "sendIntervalSeconds", 60);
        ReflectionTestUtils.setField(verificationCodeService, "maxSendPerDay", 10);
        ReflectionTestUtils.setField(verificationCodeService, "codeType", "NUMERIC");
        
        // Mock RedisTemplate
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("生成验证码成功")
    void testGenerateCodeSuccess() {
        // When
        String code = verificationCodeService.generateCode(testEmail, CodeType.REGISTER);

        // Then
        assertNotNull(code, "验证码不应为空");
        assertEquals(6, code.length(), "验证码长度应为6位");
        assertTrue(code.matches("\\d{6}"), "验证码应为6位数字");
    }

    @Test
    @DisplayName("发送验证码成功")
    void testSendVerificationCodeSuccess() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null); // 没有发送间隔限制
        // Mock查询验证码发送次数（假设有合适的方法）
        when(verificationCodeMapper.countTodaySent(eq(testEmail), any(Date.class), any(Date.class))).thenReturn(5); // 当日发送次数未超限
        when(mailService.sendVerificationCode(eq(testEmail), anyString(), anyString(), anyInt())).thenReturn(true);
        when(verificationCodeMapper.insert(any(UmsVerificationCode.class))).thenReturn(1);

        // When
        boolean result = verificationCodeService.sendCode(testEmail, CodeType.REGISTER);

        // Then
        assertTrue(result, "验证码发送应该成功");
        verify(mailService).sendVerificationCode(eq(testEmail), anyString(), anyString(), anyInt());
        verify(verificationCodeMapper).insert(any(UmsVerificationCode.class));
        verify(valueOperations).set(anyString(), eq(true), eq(60L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("发送验证码失败 - 发送间隔限制")
    void testSendVerificationCodeFailedInterval() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(true); // 存在发送间隔限制

        // When
        boolean result = verificationCodeService.sendCode(testEmail, CodeType.REGISTER);

        // Then
        assertFalse(result, "验证码发送应该失败（间隔限制）");
        verify(mailService, never()).sendVerificationCode(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("发送验证码失败 - 每日发送次数超限")
    void testSendVerificationCodeFailedDailyLimit() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);
        // Mock超过每日限制 - 返回10次发送记录
        when(verificationCodeMapper.countTodaySent(eq(testEmail), any(Date.class), any(Date.class))).thenReturn(10);

        // When
        boolean result = verificationCodeService.sendCode(testEmail, CodeType.REGISTER);

        // Then
        assertFalse(result, "验证码发送应该失败（每日次数超限）");
        verify(mailService, never()).sendVerificationCode(anyString(), anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("发送验证码失败 - 邮件发送失败")
    void testSendVerificationCodeFailedEmailError() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);
        when(verificationCodeMapper.countTodaySent(eq(testEmail), any(Date.class), any(Date.class))).thenReturn(5);
        when(mailService.sendVerificationCode(eq(testEmail), anyString(), anyString(), anyInt())).thenReturn(false); // 邮件发送失败

        // When
        boolean result = verificationCodeService.sendCode(testEmail, CodeType.REGISTER);

        // Then
        assertFalse(result, "验证码发送应该失败（邮件发送失败）");
        verify(verificationCodeMapper, never()).insert(any(UmsVerificationCode.class));
    }

    @Test
    @DisplayName("验证验证码成功")
    void testVerifyCodeSuccess() {
        // Given
        String testCode = "123456";
        UmsVerificationCode verificationCode = createTestVerificationCode(testCode);
        when(verificationCodeMapper.selectByEmailCodeAndType(testEmail, testCode, CodeType.REGISTER.getCode()))
            .thenReturn(verificationCode);

        // When
        boolean result = verificationCodeService.verifyCode(testEmail, testCode, CodeType.REGISTER);

        // Then
        assertTrue(result, "验证码验证应该成功");
        verify(verificationCodeMapper).updateByPrimaryKey(argThat(code -> 
            CodeUsedStatus.USED.getCode() == code.getUsedStatus()
        ));
    }

    @Test
    @DisplayName("验证验证码失败 - 验证码不存在")
    void testVerifyCodeFailedNotExist() {
        // Given
        String testCode = "123456";
        when(verificationCodeMapper.selectByEmailCodeAndType(testEmail, testCode, CodeType.REGISTER.getCode()))
            .thenReturn(null);

        // When
        boolean result = verificationCodeService.verifyCode(testEmail, testCode, CodeType.REGISTER);

        // Then
        assertFalse(result, "验证码验证应该失败（不存在）");
    }

    @Test
    @DisplayName("验证验证码失败 - 验证码已过期")
    void testVerifyCodeFailedExpired() {
        // Given
        String testCode = "123456";
        UmsVerificationCode expiredCode = createExpiredVerificationCode(testCode);
        when(verificationCodeMapper.selectByEmailCodeAndType(testEmail, testCode, CodeType.REGISTER.getCode()))
            .thenReturn(expiredCode);

        // When
        boolean result = verificationCodeService.verifyCode(testEmail, testCode, CodeType.REGISTER);

        // Then
        assertFalse(result, "验证码验证应该失败（已过期）");
    }

    @Test
    @DisplayName("验证验证码失败 - 验证码已使用")
    void testVerifyCodeFailedAlreadyUsed() {
        // Given
        String testCode = "123456";
        UmsVerificationCode usedCode = createUsedVerificationCode(testCode);
        when(verificationCodeMapper.selectByEmailCodeAndType(testEmail, testCode, CodeType.REGISTER.getCode()))
            .thenReturn(usedCode);

        // When
        boolean result = verificationCodeService.verifyCode(testEmail, testCode, CodeType.REGISTER);

        // Then
        assertFalse(result, "验证码验证应该失败（已使用）");
    }

    @Test
    @DisplayName("不同类型验证码测试")
    void testDifferentCodeTypes() {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);
        when(verificationCodeMapper.countTodaySent(eq(testEmail), any(Date.class), any(Date.class))).thenReturn(5);
        when(mailService.sendVerificationCode(eq(testEmail), anyString(), anyString(), anyInt())).thenReturn(true);
        when(verificationCodeMapper.insert(any(UmsVerificationCode.class))).thenReturn(1);

        // When & Then
        CodeType[] codeTypes = {CodeType.REGISTER, CodeType.LOGIN, CodeType.RESET_PASSWORD};
        for (CodeType codeType : codeTypes) {
            boolean result = verificationCodeService.sendCode(testEmail, codeType);
            assertTrue(result, "验证码类型 " + codeType + " 发送应该成功");
        }

        verify(verificationCodeMapper, times(codeTypes.length)).insert(any(UmsVerificationCode.class));
    }

    @Test
    @DisplayName("验证码生成类型测试")
    void testCodeGenerationTypes() {
        // 测试数字类型
        ReflectionTestUtils.setField(verificationCodeService, "codeType", "NUMERIC");
        String numericCode = verificationCodeService.generateCode(testEmail, CodeType.REGISTER);
        assertTrue(numericCode.matches("\\d{6}"), "数字类型验证码应为6位数字");

        // 测试字母类型
        ReflectionTestUtils.setField(verificationCodeService, "codeType", "LETTER");
        String letterCode = verificationCodeService.generateCode(testEmail, CodeType.REGISTER);
        assertTrue(letterCode.matches("[A-Z]{6}"), "字母类型验证码应为6位大写字母");

        // 测试混合类型
        ReflectionTestUtils.setField(verificationCodeService, "codeType", "MIXED");
        String mixedCode = verificationCodeService.generateCode(testEmail, CodeType.REGISTER);
        assertTrue(mixedCode.matches("[A-Z0-9]{6}"), "混合类型验证码应为6位数字和大写字母");
    }

    @Test
    @DisplayName("并发发送验证码测试")
    void testConcurrentSendVerificationCode() throws InterruptedException {
        // Given
        when(valueOperations.get(anyString())).thenReturn(null);
        when(verificationCodeMapper.countTodaySent(anyString(), any(Date.class), any(Date.class))).thenReturn(5);
        when(mailService.sendVerificationCode(anyString(), anyString(), anyString(), anyInt())).thenReturn(true);
        when(verificationCodeMapper.insert(any(UmsVerificationCode.class))).thenReturn(1);

        // When - 模拟并发请求
        Thread[] threads = new Thread[5];
        boolean[] results = new boolean[5];
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = verificationCodeService.sendCode(
                    testEmail + index, CodeType.REGISTER);
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Then
        for (int i = 0; i < 5; i++) {
            assertTrue(results[i], "并发请求 " + i + " 应该成功");
        }
    }

    /**
     * 创建测试用的验证码对象
     */
    private UmsVerificationCode createTestVerificationCode(String code) {
        UmsVerificationCode verificationCode = new UmsVerificationCode();
        verificationCode.setEmail(testEmail);
        verificationCode.setCode(code);
        verificationCode.setCodeType(CodeType.REGISTER.getCode());
        verificationCode.setUsedStatus(CodeUsedStatus.UNUSED.getCode());
        verificationCode.setCreateTime(new Date());
        verificationCode.setExpireTime(Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant()));
        verificationCode.setIpAddress(testIpAddress);
        return verificationCode;
    }

    /**
     * 创建已过期的验证码对象
     */
    private UmsVerificationCode createExpiredVerificationCode(String code) {
        UmsVerificationCode verificationCode = createTestVerificationCode(code);
        verificationCode.setExpireTime(Date.from(LocalDateTime.now().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant())); // 已过期
        return verificationCode;
    }

    /**
     * 创建已使用的验证码对象
     */
    private UmsVerificationCode createUsedVerificationCode(String code) {
        UmsVerificationCode verificationCode = createTestVerificationCode(code);
        verificationCode.setUsedStatus(CodeUsedStatus.USED.getCode());
        verificationCode.setUsedTime(new Date());
        return verificationCode;
    }
}