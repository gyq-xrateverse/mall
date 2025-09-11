package com.macro.mall.portal.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 第三方登录提供商枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum ThirdPartyProvider {
    WECHAT(1, "wechat", "微信"),
    GOOGLE(2, "google", "谷歌"),
    GITHUB(3, "github", "GitHub"),
    QQ(4, "qq", "QQ");
    
    private final Integer code;
    private final String provider;
    private final String description;
    
    public static ThirdPartyProvider fromCode(Integer code) {
        for (ThirdPartyProvider provider : values()) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Invalid ThirdPartyProvider code: " + code);
    }
    
    public static ThirdPartyProvider fromProvider(String provider) {
        for (ThirdPartyProvider type : values()) {
            if (type.getProvider().equals(provider)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ThirdPartyProvider provider: " + provider);
    }
}