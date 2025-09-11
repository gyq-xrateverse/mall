package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dto.*;
import com.macro.mall.portal.service.AuthService;
import com.macro.mall.portal.service.OAuth2Service;
import com.macro.mall.portal.service.TokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import com.macro.mall.portal.config.OAuth2Properties;
import com.macro.mall.portal.domain.dto.OAuth2UserInfo;
import com.macro.mall.security.util.PortalJwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.UUID;

/**
 * 用户认证控制器
 * @author Claude
 * @since 2025-09-10
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "用户认证", description = "用户注册、登录、密码重置等认证相关接口")
@Slf4j
@Validated
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private OAuth2Service oauth2Service;
    
    @Autowired
    private OAuth2Properties oauth2Properties;
    
    @Autowired
    @Qualifier("portalJwtTokenUtil")
    private PortalJwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private TokenService tokenService;
    
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    
    @Operation(summary = "用户注册", description = "邮箱注册新用户账号")
    @PostMapping("/register")
    public CommonResult<AuthTokenResult> register(@Valid @RequestBody AuthRegisterParam param) {
        try {
            AuthTokenResult result = authService.register(param);
            log.info("用户注册成功: {}", param.getEmail());
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("用户注册失败: {}", param.getEmail(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "用户登录", description = "邮箱密码登录")
    @PostMapping("/login")
    public CommonResult<AuthTokenResult> login(@Valid @RequestBody AuthLoginParam param) {
        try {
            AuthTokenResult result = authService.login(param);
            log.info("用户登录成功: {}", param.getEmail());
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("用户登录失败: {}", param.getEmail(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "发送验证码", description = "向指定邮箱发送验证码")
    @PostMapping("/send-code")
    public CommonResult<String> sendVerificationCode(@Valid @RequestBody VerificationCodeParam param) {
        try {
            boolean success = authService.sendVerificationCode(param);
            if (success) {
                log.info("验证码发送成功: {}", param.getEmail());
                return CommonResult.success("验证码发送成功");
            } else {
                log.warn("验证码发送失败: {}", param.getEmail());
                return CommonResult.failed("验证码发送失败，请稍后重试");
            }
        } catch (Exception e) {
            log.error("验证码发送异常: {}", param.getEmail(), e);
            return CommonResult.failed("验证码发送异常: " + e.getMessage());
        }
    }
    
    @Operation(summary = "重置密码", description = "通过验证码重置用户密码")
    @PostMapping("/reset-password")
    public CommonResult<String> resetPassword(@Valid @RequestBody ResetPasswordParam param) {
        try {
            boolean success = authService.resetPassword(param);
            if (success) {
                log.info("密码重置成功: {}", param.getEmail());
                return CommonResult.success("密码重置成功");
            } else {
                log.warn("密码重置失败: {}", param.getEmail());
                return CommonResult.failed("密码重置失败");
            }
        } catch (Exception e) {
            log.error("密码重置异常: {}", param.getEmail(), e);
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @Operation(summary = "刷新Token", description = "使用刷新Token获取新的访问Token")
    @PostMapping("/refresh-token")
    public CommonResult<String> refreshToken(
            @Parameter(description = "刷新Token", required = true)
            @RequestParam @NotBlank(message = "刷新Token不能为空") String refreshToken) {
        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            if (newAccessToken != null) {
                log.info("Token刷新成功");
                return CommonResult.success(newAccessToken);
            } else {
                log.warn("Token刷新失败");
                return CommonResult.failed("刷新Token无效或已过期");
            }
        } catch (Exception e) {
            log.error("Token刷新异常", e);
            return CommonResult.failed("Token刷新失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取用户信息", description = "根据Token获取当前用户信息")
    @GetMapping("/user-info")
    public CommonResult<UserInfoResult> getUserInfo(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return CommonResult.failed("请先登录");
            }
            
            Long userId = tokenService.getUserIdFromToken(token);
            if (userId == null) {
                return CommonResult.failed("Token无效");
            }
            
            UserInfoResult userInfo = authService.getUserInfo(userId);
            return CommonResult.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息异常", e);
            return CommonResult.failed("获取用户信息失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "用户注销", description = "注销当前用户登录状态")
    @PostMapping("/logout")
    public CommonResult<String> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return CommonResult.success("注销成功");
            }
            
            boolean success = authService.logout(token);
            if (success) {
                log.info("用户注销成功");
                return CommonResult.success("注销成功");
            } else {
                return CommonResult.failed("注销失败");
            }
        } catch (Exception e) {
            log.error("用户注销异常", e);
            return CommonResult.failed("注销失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "检查邮箱是否存在", description = "检查指定邮箱是否已被注册")
    @GetMapping("/check-email")
    public CommonResult<Boolean> checkEmailExists(
            @Parameter(description = "邮箱地址", required = true)
            @RequestParam @NotBlank(message = "邮箱不能为空") String email) {
        try {
            boolean exists = authService.isEmailExists(email);
            return CommonResult.success(exists);
        } catch (Exception e) {
            log.error("检查邮箱存在性异常: {}", email, e);
            return CommonResult.failed("检查失败");
        }
    }
    
    @Operation(summary = "检查用户名是否存在", description = "检查指定用户名是否已被使用")
    @GetMapping("/check-username")
    public CommonResult<Boolean> checkUsernameExists(
            @Parameter(description = "用户名", required = true)
            @RequestParam @NotBlank(message = "用户名不能为空") String username) {
        try {
            boolean exists = authService.isUsernameExists(username);
            return CommonResult.success(exists);
        } catch (Exception e) {
            log.error("检查用户名存在性异常: {}", username, e);
            return CommonResult.failed("检查失败");
        }
    }
    
    @Operation(summary = "微信OAuth2授权", description = "重定向到微信OAuth2授权页面")
    @GetMapping("/oauth2/wechat")
    public void wechatOAuth2(HttpServletResponse response) throws IOException {
        try {
            String state = UUID.randomUUID().toString();
            String authUrl = oauth2Service.generateWeChatAuthUrl(state);
            log.info("生成微信OAuth2授权URL: {}", authUrl);
            response.sendRedirect(authUrl);
        } catch (Exception e) {
            log.error("微信OAuth2授权失败", e);
            String errorUrl = oauth2Properties.getFrontend().getBaseUrl() + 
                    oauth2Properties.getFrontend().getAuthErrorRedirect() + "?error=wechat_auth_failed";
            response.sendRedirect(errorUrl);
        }
    }
    
    @Operation(summary = "Google OAuth2授权", description = "重定向到Google OAuth2授权页面")
    @GetMapping("/oauth2/google")
    public void googleOAuth2(HttpServletResponse response) throws IOException {
        try {
            String state = UUID.randomUUID().toString();
            String authUrl = oauth2Service.generateGoogleAuthUrl(state);
            log.info("生成Google OAuth2授权URL: {}", authUrl);
            response.sendRedirect(authUrl);
        } catch (Exception e) {
            log.error("Google OAuth2授权失败", e);
            String errorUrl = oauth2Properties.getFrontend().getBaseUrl() + 
                    oauth2Properties.getFrontend().getAuthErrorRedirect() + "?error=google_auth_failed";
            response.sendRedirect(errorUrl);
        }
    }
    
    @Operation(summary = "微信OAuth2回调", description = "处理微信OAuth2授权回调")
    @GetMapping("/oauth2/callback/wechat")
    public void wechatOAuth2Callback(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletResponse response) throws IOException {
        
        try {
            log.info("收到微信OAuth2回调: code={}, state={}", code, state);
            
            // 获取微信用户信息
            OAuth2UserInfo userInfo = oauth2Service.getWeChatUserInfo(code, state);
            
            // 处理登录
            AuthTokenResult tokenResult = oauth2Service.processOAuth2Login(userInfo, "wechat");
            
            // 重定向到前端成功页面，带上token
            String successUrl = String.format("%s%s?access_token=%s&refresh_token=%s&user_id=%s", 
                    oauth2Properties.getFrontend().getBaseUrl(),
                    oauth2Properties.getFrontend().getAuthSuccessRedirect(),
                    tokenResult.getAccessToken(),
                    tokenResult.getRefreshToken(),
                    tokenResult.getUserInfo().getId());
            
            log.info("微信OAuth2登录成功，用户ID: {}", tokenResult.getUserInfo().getId());
            response.sendRedirect(successUrl);
            
        } catch (Exception e) {
            log.error("微信OAuth2回调处理失败", e);
            String errorUrl = oauth2Properties.getFrontend().getBaseUrl() + 
                    oauth2Properties.getFrontend().getAuthErrorRedirect() + "?error=wechat_callback_failed";
            response.sendRedirect(errorUrl);
        }
    }
    
    @Operation(summary = "Google OAuth2回调", description = "处理Google OAuth2授权回调")
    @GetMapping("/oauth2/callback/google")
    public void googleOAuth2Callback(
            @RequestParam String code,
            @RequestParam String state,
            HttpServletResponse response) throws IOException {
        
        try {
            log.info("收到Google OAuth2回调: code={}, state={}", code, state);
            
            // 获取Google用户信息
            OAuth2UserInfo userInfo = oauth2Service.getGoogleUserInfo(code, state);
            
            // 处理登录
            AuthTokenResult tokenResult = oauth2Service.processOAuth2Login(userInfo, "google");
            
            // 重定向到前端成功页面，带上token
            String successUrl = String.format("%s%s?access_token=%s&refresh_token=%s&user_id=%s", 
                    oauth2Properties.getFrontend().getBaseUrl(),
                    oauth2Properties.getFrontend().getAuthSuccessRedirect(),
                    tokenResult.getAccessToken(),
                    tokenResult.getRefreshToken(),
                    tokenResult.getUserInfo().getId());
            
            log.info("Google OAuth2登录成功，用户ID: {}", tokenResult.getUserInfo().getId());
            response.sendRedirect(successUrl);
            
        } catch (Exception e) {
            log.error("Google OAuth2回调处理失败", e);
            String errorUrl = oauth2Properties.getFrontend().getBaseUrl() + 
                    oauth2Properties.getFrontend().getAuthErrorRedirect() + "?error=google_callback_failed";
            response.sendRedirect(errorUrl);
        }
    }
    
    @Operation(summary = "获取OAuth2配置信息", description = "获取前端所需的OAuth2配置信息")
    @GetMapping("/oauth2/config")
    public CommonResult<Object> getOAuth2Config() {
        try {
            return CommonResult.success(new Object() {
                public final String wechatAuthUrl = "/api/auth/oauth2/wechat";
                public final String googleAuthUrl = "/api/auth/oauth2/google";
                public final boolean wechatEnabled = oauth2Properties.getClient().getWechat().getAppId() != null && 
                        !oauth2Properties.getClient().getWechat().getAppId().equals("your-wechat-app-id");
                public final boolean googleEnabled = oauth2Properties.getClient().getGoogle().getClientId() != null && 
                        !oauth2Properties.getClient().getGoogle().getClientId().equals("your-google-client-id");
            });
        } catch (Exception e) {
            log.error("获取OAuth2配置失败", e);
            return CommonResult.failed("获取配置失败");
        }
    }
    
    /**
     * 从请求中提取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            return authHeader.substring(tokenHead.length());
        }
        return null;
    }
}