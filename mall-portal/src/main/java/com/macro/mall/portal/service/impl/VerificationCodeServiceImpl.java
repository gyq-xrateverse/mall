package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.config.VerificationCodeConfig;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.service.MailService;
import com.macro.mall.portal.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 * @author Claude
 * @since 2025-09-10
 */
@Slf4j
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService {
    
    @Autowired
    private VerificationCodeConfig config;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private MailService mailService;
    
    private final SecureRandom random = new SecureRandom();

    // 测试时可通过反射设置的字段
    private int codeLength = 6;
    private int expireMinutes = 5;
    private int sendIntervalSeconds = 60;
    private int maxSendPerDay = 20;
    private String codeType = "NUMERIC";

    private static final String CODE_KEY_PREFIX = "verification_code:";
    private static final String SEND_COUNT_KEY_PREFIX = "send_count:";
    private static final String SEND_TIME_KEY_PREFIX = "send_time:";
    
    @Override
    public String generateCode(String email, CodeType codeType) {
        String code;
        switch (this.codeType != null ? this.codeType : config.getType()) {
            case "LETTER":
                code = generateLetterCode();
                break;
            case "MIXED":
                code = generateMixedCode();
                break;
            case "NUMERIC":
            default:
                code = generateNumericCode();
                break;
        }
        
        // 存储验证码到Redis
        String key = getCodeKey(email, codeType);
        redisTemplate.opsForValue().set(key, code, expireMinutes > 0 ? expireMinutes : config.getExpireMinutes(), TimeUnit.MINUTES);
        
        log.info("Generated verification code for email: {}, type: {}", email, codeType);
        return code;
    }
    
    @Override
    public boolean sendCode(String email, CodeType codeType) {
        // 检查发送频率限制
        if (!checkSendFrequency(email)) {
            log.warn("Send frequency limit exceeded for email: {}", email);
            return false;
        }
        
        // 检查每日发送次数限制
        if (!checkDailySendLimit(email)) {
            log.warn("Daily send limit exceeded for email: {}", email);
            return false;
        }
        
        try {
            // 生成验证码
            String code = generateCode(email, codeType);
            
            // 发送邮件
            boolean sent = mailService.sendVerificationCode(email, code, codeType.getDescription(), expireMinutes > 0 ? expireMinutes : config.getExpireMinutes());
            
            if (sent) {
                // 记录发送时间
                recordSendTime(email);
                // 增加发送次数计数
                incrementSendCount(email);
                log.info("Verification code sent successfully to email: {}", email);
            }
            
            return sent;
        } catch (Exception e) {
            log.error("Failed to send verification code to email: {}", email, e);
            return false;
        }
    }
    
    @Override
    public boolean verifyCode(String email, String code, CodeType codeType) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        String key = getCodeKey(email, codeType);
        String storedCode = redisTemplate.opsForValue().get(key);
        
        if (storedCode != null && storedCode.equals(code.trim())) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            log.info("Verification code verified successfully for email: {}, type: {}", email, codeType);
            return true;
        }
        
        log.warn("Verification code verification failed for email: {}, type: {}", email, codeType);
        return false;
    }
    
    @Override
    public int getRemainingTimes(String email) {
        String countKey = getSendCountKey(email);
        String countStr = redisTemplate.opsForValue().get(countKey);
        int sentToday = countStr != null ? Integer.parseInt(countStr) : 0;
        return Math.max(0, (maxSendPerDay > 0 ? maxSendPerDay : config.getMaxSendPerDay()) - sentToday);
    }
    
    @Override
    public long getNextSendTime(String email) {
        String timeKey = getSendTimeKey(email);
        String lastSendTimeStr = redisTemplate.opsForValue().get(timeKey);
        
        if (lastSendTimeStr == null) {
            return 0; // 可以立即发送
        }
        
        long lastSendTime = Long.parseLong(lastSendTimeStr);
        long currentTime = System.currentTimeMillis();
        long intervalMs = (sendIntervalSeconds > 0 ? sendIntervalSeconds : config.getSendIntervalSeconds()) * 1000L;
        long nextSendTime = lastSendTime + intervalMs;
        
        return Math.max(0, (nextSendTime - currentTime) / 1000);
    }
    
    private String generateNumericCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < (codeLength > 0 ? codeLength : config.getLength()); i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    private String generateLetterCode() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < (codeLength > 0 ? codeLength : config.getLength()); i++) {
            code.append(letters.charAt(random.nextInt(letters.length())));
        }
        return code.toString();
    }
    
    private String generateMixedCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < (codeLength > 0 ? codeLength : config.getLength()); i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
    
    private String getCodeKey(String email, CodeType codeType) {
        return CODE_KEY_PREFIX + email + ":" + codeType.getType();
    }
    
    private String getSendCountKey(String email) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return SEND_COUNT_KEY_PREFIX + email + ":" + today;
    }
    
    private String getSendTimeKey(String email) {
        return SEND_TIME_KEY_PREFIX + email;
    }
    
    private boolean checkSendFrequency(String email) {
        return getNextSendTime(email) == 0;
    }
    
    private boolean checkDailySendLimit(String email) {
        return getRemainingTimes(email) > 0;
    }
    
    private void recordSendTime(String email) {
        String timeKey = getSendTimeKey(email);
        String currentTime = String.valueOf(System.currentTimeMillis());
        redisTemplate.opsForValue().set(timeKey, currentTime, sendIntervalSeconds > 0 ? sendIntervalSeconds : config.getSendIntervalSeconds(), TimeUnit.SECONDS);
    }
    
    private void incrementSendCount(String email) {
        String countKey = getSendCountKey(email);
        String countStr = redisTemplate.opsForValue().get(countKey);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;
        
        redisTemplate.opsForValue().set(countKey, String.valueOf(count + 1), 24, TimeUnit.HOURS);
    }
    
    @Override
    public boolean resetDailySendCount(String email) {
        try {
            String countKey = getSendCountKey(email);
            redisTemplate.delete(countKey);
            log.info("Reset daily send count for email: {}", email);
            return true;
        } catch (Exception e) {
            log.error("Failed to reset daily send count for email: {}", email, e);
            return false;
        }
    }
}