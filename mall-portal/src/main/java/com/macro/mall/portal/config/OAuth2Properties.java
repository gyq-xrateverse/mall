package com.macro.mall.portal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {
    
    private Client client = new Client();
    private Frontend frontend = new Frontend();
    
    @Data
    public static class Client {
        private WeChat wechat = new WeChat();
        private Google google = new Google();
    }
    
    @Data
    public static class WeChat {
        private String appId;
        private String appSecret;
        private String redirectUri;
    }
    
    @Data
    public static class Google {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }
    
    @Data
    public static class Frontend {
        private String baseUrl;
        private String authSuccessRedirect;
        private String authErrorRedirect;
    }
}