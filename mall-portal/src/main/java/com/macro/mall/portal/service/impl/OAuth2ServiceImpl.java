package com.macro.mall.portal.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.config.OAuth2Properties;
import com.macro.mall.portal.domain.dto.AuthTokenResult;
import com.macro.mall.portal.domain.dto.OAuth2UserInfo;
import com.macro.mall.portal.domain.enums.RegisterType;
import com.macro.mall.portal.service.AuthService;
import com.macro.mall.portal.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {

    private final OAuth2Properties oauth2Properties;
    private final AuthService authService;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private static final String WECHAT_AUTH_URL = "https://open.weixin.qq.com/connect/qrconnect";
    private static final String WECHAT_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String WECHAT_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    @Override
    public String generateWeChatAuthUrl(String state) {
        try {
            String redirectUri = URLEncoder.encode(oauth2Properties.getClient().getWechat().getRedirectUri(), StandardCharsets.UTF_8);
            return String.format("%s?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect",
                    WECHAT_AUTH_URL,
                    oauth2Properties.getClient().getWechat().getAppId(),
                    redirectUri,
                    state);
        } catch (Exception e) {
            log.error("生成微信授权URL失败", e);
            throw new RuntimeException("生成微信授权URL失败");
        }
    }

    @Override
    public String generateGoogleAuthUrl(String state) {
        try {
            String redirectUri = URLEncoder.encode(oauth2Properties.getClient().getGoogle().getRedirectUri(), StandardCharsets.UTF_8);
            return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&scope=openid email profile&state=%s",
                    GOOGLE_AUTH_URL,
                    oauth2Properties.getClient().getGoogle().getClientId(),
                    redirectUri,
                    state);
        } catch (Exception e) {
            log.error("生成Google授权URL失败", e);
            throw new RuntimeException("生成Google授权URL失败");
        }
    }

    @Override
    public OAuth2UserInfo getWeChatUserInfo(String code, String state) {
        try {
            // 1. 获取access_token
            String tokenUrl = String.format("%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    WECHAT_TOKEN_URL,
                    oauth2Properties.getClient().getWechat().getAppId(),
                    oauth2Properties.getClient().getWechat().getAppSecret(),
                    code);

            WebClient webClient = webClientBuilder.build();
            String tokenResponse = webClient.get()
                    .uri(tokenUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode tokenJson = objectMapper.readTree(tokenResponse);
            if (tokenJson.has("errcode")) {
                log.error("微信获取token失败: {}", tokenResponse);
                throw new RuntimeException("微信获取token失败: " + tokenJson.get("errmsg").asText());
            }

            String accessToken = tokenJson.get("access_token").asText();
            String openid = tokenJson.get("openid").asText();
            String unionid = tokenJson.has("unionid") ? tokenJson.get("unionid").asText() : null;

            // 2. 获取用户信息
            String userInfoUrl = String.format("%s?access_token=%s&openid=%s",
                    WECHAT_USER_INFO_URL, accessToken, openid);

            String userInfoResponse = webClient.get()
                    .uri(userInfoUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode userInfoJson = objectMapper.readTree(userInfoResponse);
            if (userInfoJson.has("errcode")) {
                log.error("微信获取用户信息失败: {}", userInfoResponse);
                throw new RuntimeException("微信获取用户信息失败: " + userInfoJson.get("errmsg").asText());
            }

            return OAuth2UserInfo.builder()
                    .openId(openid)
                    .unionId(unionid)
                    .nickname(userInfoJson.get("nickname").asText())
                    .avatar(userInfoJson.get("headimgurl").asText())
                    .provider("wechat")
                    .providerUserId(openid)
                    .accessToken(accessToken)
                    .build();

        } catch (Exception e) {
            log.error("获取微信用户信息失败", e);
            throw new RuntimeException("获取微信用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public OAuth2UserInfo getGoogleUserInfo(String code, String state) {
        try {
            // 1. 获取access_token
            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("client_id", oauth2Properties.getClient().getGoogle().getClientId());
            tokenParams.add("client_secret", oauth2Properties.getClient().getGoogle().getClientSecret());
            tokenParams.add("code", code);
            tokenParams.add("grant_type", "authorization_code");
            tokenParams.add("redirect_uri", oauth2Properties.getClient().getGoogle().getRedirectUri());

            WebClient webClient = webClientBuilder.build();
            String tokenResponse = webClient.post()
                    .uri(GOOGLE_TOKEN_URL)
                    .body(BodyInserters.fromFormData(tokenParams))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode tokenJson = objectMapper.readTree(tokenResponse);
            if (tokenJson.has("error")) {
                log.error("Google获取token失败: {}", tokenResponse);
                throw new RuntimeException("Google获取token失败: " + tokenJson.get("error_description").asText());
            }

            String accessToken = tokenJson.get("access_token").asText();

            // 2. 获取用户信息
            String userInfoResponse = webClient.get()
                    .uri(GOOGLE_USER_INFO_URL)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode userInfoJson = objectMapper.readTree(userInfoResponse);

            return OAuth2UserInfo.builder()
                    .openId(userInfoJson.get("id").asText())
                    .nickname(userInfoJson.get("name").asText())
                    .avatar(userInfoJson.get("picture").asText())
                    .email(userInfoJson.has("email") ? userInfoJson.get("email").asText() : null)
                    .provider("google")
                    .providerUserId(userInfoJson.get("id").asText())
                    .accessToken(accessToken)
                    .build();

        } catch (Exception e) {
            log.error("获取Google用户信息失败", e);
            throw new RuntimeException("获取Google用户信息失败: " + e.getMessage());
        }
    }

    @Override
    public AuthTokenResult processOAuth2Login(OAuth2UserInfo userInfo, String provider) {
        try {
            return authService.processThirdPartyLogin(userInfo, RegisterType.valueOf(provider.toUpperCase()));
        } catch (Exception e) {
            log.error("处理第三方登录失败: provider={}, userInfo={}", provider, userInfo, e);
            throw new RuntimeException("处理第三方登录失败: " + e.getMessage());
        }
    }
}