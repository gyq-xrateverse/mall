package com.macro.mall.portal.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.portal.dao.UmsMemberExtMapper;
import com.macro.mall.portal.enums.AccountStatus;
import com.macro.mall.portal.enums.CodeType;
import com.macro.mall.portal.enums.EmailVerifyStatus;
import com.macro.mall.portal.enums.RegisterType;
import com.macro.mall.portal.domain.dto.OAuth2UserInfo;
import com.macro.mall.portal.dto.*;
import com.macro.mall.portal.service.AuthService;
import com.macro.mall.portal.service.TokenService;
import com.macro.mall.portal.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.util.CollectionUtils;

/**
 * 认证服务实现类
 * @author Claude
 * @since 2025-09-10
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberExtMapper memberExtMapper;

    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AuthTokenResult register(AuthRegisterParam param) {
        // 验证确认密码
        if (!param.getPassword().equals(param.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查邮箱是否已存在
        if (isEmailExists(param.getEmail())) {
            throw new RuntimeException("该邮箱已被注册");
        }

        // 检查用户名是否已存在
        if (isUsernameExists(param.getUsername())) {
            throw new RuntimeException("该用户名已被使用");
        }

        // 验证验证码
        CodeType codeType = CodeType.fromCode(1); // 注册验证码
        boolean codeValid = verificationCodeService.verifyCode(param.getEmail(), param.getVerificationCode(), codeType);
        if (!codeValid) {
            throw new RuntimeException("验证码无效或已过期");
        }

        // 创建用户
        UmsMember member = new UmsMember();
        member.setUsername(param.getUsername());
        member.setPassword(passwordEncoder.encode(param.getPassword()));
        member.setNickname(param.getUsername()); // 默认昵称为用户名
        member.setCreateTime(new Date());
        member.setStatus(1); // 正常状态

        // 设置认证相关字段（需要先扩展UmsMember模型或使用反射/动态SQL）
        // 这里假设已经扩展了UmsMember模型
        try {
            // 使用反射设置扩展字段
            member.getClass().getMethod("setEmail", String.class).invoke(member, param.getEmail());
            member.getClass().getMethod("setRegisterType", Integer.class).invoke(member, param.getRegisterType());
            member.getClass().getMethod("setEmailVerified", Integer.class).invoke(member, EmailVerifyStatus.VERIFIED.getCode());
            member.getClass().getMethod("setAccountStatus", Integer.class).invoke(member, AccountStatus.NORMAL.getCode());
            member.getClass().getMethod("setLastLoginTime", Date.class).invoke(member, new Date());
        } catch (Exception e) {
            log.warn("设置用户扩展字段失败，可能需要先运行数据库扩展脚本", e);
            // 继续执行，不影响基本注册功能
        }

        // 插入用户
        int result = memberMapper.insert(member);
        if (result <= 0) {
            throw new RuntimeException("用户注册失败");
        }

        // 生成Token
        Map<String, String> tokenPair = tokenService.generateTokenPair(member.getUsername(), member.getId());

        // 构建用户信息
        UserInfoResult userInfo = buildUserInfoResult(member);

        return AuthTokenResult.builder()
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .tokenType("Bearer")
                .expiresIn(86400L)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public AuthTokenResult login(AuthLoginParam param) {
        // 根据邮箱查找用户
        UmsMember member = findMemberByEmail(param.getEmail());
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(param.getPassword(), member.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 检查账户状态
        Integer accountStatus = getAccountStatus(member);
        if (!AccountStatus.NORMAL.getCode().equals(accountStatus)) {
            throw new RuntimeException("账户已被冻结或禁用");
        }

        // 更新最后登录时间
        updateLastLoginTime(member.getId());

        // 生成Token
        Map<String, String> tokenPair = tokenService.generateTokenPair(member.getUsername(), member.getId());

        // 构建用户信息
        UserInfoResult userInfo = buildUserInfoResult(member);

        return AuthTokenResult.builder()
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .tokenType("Bearer")
                .expiresIn(86400L)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public AuthTokenResult loginWithEmailCode(EmailCodeLoginParam param) {
        log.info("开始邮箱验证码登录: {}", param.getEmail());

        // 1. 验证邮箱验证码
        CodeType codeType = CodeType.LOGIN; // 登录验证码类型
        boolean codeValid = verificationCodeService.verifyCode(param.getEmail(), param.getVerificationCode(), codeType);
        if (!codeValid) {
            throw new RuntimeException("验证码无效或已过期");
        }

        // 2. 查找用户
        UmsMember member = findMemberByEmail(param.getEmail());

        if (member == null) {
            // 3. 用户不存在，创建新用户
            log.info("用户不存在，创建新账户: {}", param.getEmail());
            member = createEmailCodeUser(param.getEmail());
        } else {
            // 4. 用户存在，检查账户状态
            log.info("用户已存在，直接登录: {}", param.getEmail());
            Integer accountStatus = getAccountStatus(member);
            if (!AccountStatus.NORMAL.getCode().equals(accountStatus)) {
                throw new RuntimeException("账户已被冻结或禁用");
            }
            // 更新最后登录时间
            updateLastLoginTime(member.getId());
        }

        // 5. 生成Token
        Map<String, String> tokenPair = tokenService.generateTokenPair(member.getUsername(), member.getId());

        // 6. 构建用户信息
        UserInfoResult userInfo = buildUserInfoResult(member);

        log.info("邮箱验证码登录成功: userId={}, email={}", member.getId(), param.getEmail());

        return AuthTokenResult.builder()
                .accessToken(tokenPair.get("access_token"))
                .refreshToken(tokenPair.get("refresh_token"))
                .tokenType("Bearer")
                .expiresIn(86400L)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public boolean sendVerificationCode(VerificationCodeParam param) {
        CodeType codeType = CodeType.fromCode(param.getCodeType());
        return verificationCodeService.sendCode(param.getEmail(), codeType);
    }

    @Override
    public boolean resetPassword(ResetPasswordParam param) {
        // 验证确认密码
        if (!param.getNewPassword().equals(param.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 验证验证码
        CodeType codeType = CodeType.fromCode(3); // 重置密码验证码
        boolean codeValid = verificationCodeService.verifyCode(param.getEmail(), param.getVerificationCode(), codeType);
        if (!codeValid) {
            throw new RuntimeException("验证码无效或已过期");
        }

        // 查找用户
        UmsMember member = findMemberByEmail(param.getEmail());
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新密码
        member.setPassword(passwordEncoder.encode(param.getNewPassword()));
        int result = memberMapper.updateByPrimaryKeySelective(member);

        return result > 0;
    }

    @Override
    public String refreshAccessToken(String refreshToken) {
        return tokenService.refreshAccessToken(refreshToken);
    }

    @Override
    public UserInfoResult getUserInfo(Long userId) {
        UmsMember member = memberMapper.selectByPrimaryKey(userId);
        if (member == null) {
            throw new RuntimeException("用户不存在");
        }

        return buildUserInfoResult(member);
    }

    @Override
    public boolean logout(String token) {
        tokenService.revokeToken(token);
        return true;
    }

    @Override
    public boolean isEmailExists(String email) {
        try {
            // 使用动态查询检查邮箱是否存在
            // 这里需要自定义查询方法或使用现有的查询方法
            return findMemberByEmail(email) != null;
        } catch (Exception e) {
            log.warn("检查邮箱存在性失败", e);
            return false;
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        try {
            UmsMember member = memberExtMapper.selectByUsername(username);
            return member != null;
        } catch (Exception e) {
            log.warn("检查用户名存在性失败", e);
            return false;
        }
    }

    /**
     * 根据邮箱查找用户
     */
    private UmsMember findMemberByEmail(String email) {
        try {
            return memberExtMapper.selectByEmail(email);
        } catch (Exception e) {
            log.warn("根据邮箱查询用户失败", e);
            return null;
        }
    }

    /**
     * 获取账户状态
     */
    private Integer getAccountStatus(UmsMember member) {
        try {
            return (Integer) member.getClass().getMethod("getAccountStatus").invoke(member);
        } catch (Exception e) {
            // 如果扩展字段不存在，默认返回正常状态
            return AccountStatus.NORMAL.getCode();
        }
    }

    /**
     * 更新最后登录时间
     */
    private void updateLastLoginTime(Long userId) {
        try {
            memberExtMapper.updateLastLoginTime(userId, new Date());
        } catch (Exception e) {
            log.warn("更新最后登录时间失败", e);
        }
    }

    /**
     * 创建邮箱验证码登录用户
     */
    private UmsMember createEmailCodeUser(String email) {
        UmsMember member = new UmsMember();

        // 生成唯一用户名（使用邮箱前缀）
        String username = generateUniqueUsernameFromEmail(email);
        member.setUsername(username);
        member.setNickname(username); // 默认昵称为用户名

        // 邮箱验证码登录不需要密码，设置一个随机的
        member.setPassword(passwordEncoder.encode("email_code_user_" + System.currentTimeMillis()));

        // 设置状态
        member.setStatus(AccountStatus.NORMAL.getCode());
        member.setCreateTime(new Date());

        // 获取默认会员等级并设置
        UmsMemberLevelExample levelExample = new UmsMemberLevelExample();
        levelExample.createCriteria().andDefaultStatusEqualTo(1);
        List<UmsMemberLevel> memberLevelList = memberLevelMapper.selectByExample(levelExample);
        if (!CollectionUtils.isEmpty(memberLevelList)) {
            member.setMemberLevelId(memberLevelList.get(0).getId());
        }

        // 设置扩展字段
        member.setEmail(email);
        member.setRegisterType(RegisterType.EMAIL.getCode());
        member.setEmailVerified(EmailVerifyStatus.VERIFIED.getCode());
        member.setAccountStatus(AccountStatus.NORMAL.getCode());
        member.setLastLoginTime(new Date());

        // 插入用户
        int result = memberMapper.insert(member);
        if (result <= 0) {
            throw new RuntimeException("创建用户失败");
        }

        log.info("邮箱验证码登录用户创建成功: userId={}, email={}, username={}", member.getId(), email, username);
        return member;
    }

    /**
     * 根据邮箱生成唯一用户名
     */
    private String generateUniqueUsernameFromEmail(String email) {
        // 获取邮箱前缀作为基础用户名
        String baseUsername = email.split("@")[0];

        // 清理用户名，只保留字母数字和下划线
        baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9_]", "_");

        // 如果用户名太短，添加后缀
        if (baseUsername.length() < 3) {
            baseUsername = "user_" + baseUsername;
        }

        // 确保用户名唯一
        String username = baseUsername;
        int counter = 1;
        while (isUsernameExists(username)) {
            username = baseUsername + "_" + counter;
            counter++;
        }

        return username;
    }

    /**
     * 构建用户信息结果
     */
    private UserInfoResult buildUserInfoResult(UmsMember member) {
        UserInfoResult.UserInfoResultBuilder builder = UserInfoResult.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .createTime(member.getCreateTime());

        // 尝试获取扩展字段
        try {
            String email = (String) member.getClass().getMethod("getEmail").invoke(member);
            Integer registerType = (Integer) member.getClass().getMethod("getRegisterType").invoke(member);
            Integer emailVerified = (Integer) member.getClass().getMethod("getEmailVerified").invoke(member);
            Integer accountStatus = (Integer) member.getClass().getMethod("getAccountStatus").invoke(member);
            String avatarUrl = (String) member.getClass().getMethod("getAvatarUrl").invoke(member);
            Date lastLoginTime = (Date) member.getClass().getMethod("getLastLoginTime").invoke(member);

            builder.email(email)
                   .registerType(registerType)
                   .emailVerified(emailVerified)
                   .accountStatus(accountStatus)
                   .avatar(avatarUrl)
                   .lastLoginTime(lastLoginTime);
        } catch (Exception e) {
            log.warn("获取用户扩展字段失败，返回基本信息", e);
        }

        return builder.build();
    }

    @Override
    public AuthTokenResult processThirdPartyLogin(OAuth2UserInfo userInfo, RegisterType registerType) {
        log.info("处理第三方登录: provider={}, openId={}", userInfo.getProvider(), userInfo.getOpenId());

        try {
            // 1. 根据第三方信息查找现有用户
            UmsMember existingMember = findMemberByThirdPartyInfo(userInfo, registerType);

            if (existingMember != null) {
                // 2. 现有用户，直接登录
                log.info("找到现有用户，用户ID: {}", existingMember.getId());
                updateLastLoginTime(existingMember.getId());

                // 生成Token
                Map<String, String> tokens = tokenService.generateTokenPair(existingMember.getUsername(), existingMember.getId());

                return AuthTokenResult.builder()
                        .accessToken(tokens.get("access_token"))
                        .refreshToken(tokens.get("refresh_token"))
                        .tokenType("Bearer")
                        .expiresIn(86400L)
                        .userInfo(buildUserInfoResult(existingMember))
                        .build();
            } else {
                // 3. 新用户，创建账户
                UmsMember newMember = createThirdPartyMember(userInfo, registerType);
                log.info("创建新用户成功，用户ID: {}", newMember.getId());

                // 生成Token
                Map<String, String> tokens = tokenService.generateTokenPair(newMember.getUsername(), newMember.getId());

                return AuthTokenResult.builder()
                        .accessToken(tokens.get("access_token"))
                        .refreshToken(tokens.get("refresh_token"))
                        .tokenType("Bearer")
                        .expiresIn(86400L)
                        .userInfo(buildUserInfoResult(newMember))
                        .build();
            }

        } catch (Exception e) {
            log.error("处理第三方登录失败", e);
            throw new RuntimeException("第三方登录失败: " + e.getMessage());
        }
    }

    /**
     * 根据第三方信息查找用户
     */
    private UmsMember findMemberByThirdPartyInfo(OAuth2UserInfo userInfo, RegisterType registerType) {
        try {
            // 优先通过第三方openId查找
            String providerField = getProviderField(registerType);
            UmsMember member = memberExtMapper.findByThirdPartyId(providerField, userInfo.getOpenId());

            if (member == null && userInfo.getEmail() != null) {
                // 如果没找到且有邮箱，通过邮箱查找
                member = memberExtMapper.findByEmail(userInfo.getEmail());
                if (member != null) {
                    // 更新第三方信息
                    updateThirdPartyInfo(member.getId(), userInfo, registerType);
                }
            }

            return member;
        } catch (Exception e) {
            log.error("查找第三方用户失败", e);
            return null;
        }
    }

    /**
     * 创建第三方注册用户
     */
    private UmsMember createThirdPartyMember(OAuth2UserInfo userInfo, RegisterType registerType) {
        UmsMember member = new UmsMember();

        // 生成唯一用户名
        String username = generateUniqueUsername(userInfo.getNickname(), userInfo.getProvider());
        member.setUsername(username);
        member.setNickname(userInfo.getNickname() != null ? userInfo.getNickname() : username);

        // 第三方登录不需要密码，设置一个随机的
        member.setPassword(passwordEncoder.encode("oauth2_user_" + System.currentTimeMillis()));

        // 设置状态
        member.setStatus(AccountStatus.NORMAL.getCode());
        member.setCreateTime(new Date());

        // 保存基本信息
        memberMapper.insert(member);

        // 保存扩展信息
        updateThirdPartyInfo(member.getId(), userInfo, registerType);

        return member;
    }

    /**
     * 更新第三方信息
     */
    private void updateThirdPartyInfo(Long userId, OAuth2UserInfo userInfo, RegisterType registerType) {
        try {
            String providerField = getProviderField(registerType);
            memberExtMapper.updateThirdPartyInfo(userId, providerField, userInfo.getOpenId(),
                    userInfo.getEmail(), userInfo.getAvatar(), registerType.getCode());
        } catch (Exception e) {
            log.error("更新第三方信息失败", e);
        }
    }

    /**
     * 获取第三方字段名
     */
    private String getProviderField(RegisterType registerType) {
        switch (registerType) {
            case WECHAT:
                return "wechat_openid";
            case GOOGLE:
                return "google_id";
            default:
                throw new IllegalArgumentException("不支持的第三方类型: " + registerType);
        }
    }

    /**
     * 生成唯一用户名
     */
    private String generateUniqueUsername(String nickname, String provider) {
        String baseUsername = provider + "_" + System.currentTimeMillis();
        if (nickname != null && !nickname.trim().isEmpty()) {
            // 清理昵称，只保留字母数字
            String cleanNickname = nickname.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fa5]", "");
            if (!cleanNickname.isEmpty()) {
                baseUsername = cleanNickname + "_" + provider;
            }
        }

        // 确保用户名唯一
        String username = baseUsername;
        int counter = 1;
        while (isUsernameExists(username)) {
            username = baseUsername + "_" + counter;
            counter++;
        }

        return username;
    }
}
