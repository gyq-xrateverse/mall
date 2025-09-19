# 02-4-2: Google OAuth登录集成

## 任务概述
**时间估算**: 2小时  
**优先级**: 中  
**依赖关系**: 02-4-1 微信扫码登录集成完成  
**负责模块**: 前端React + 后端Java

## 详细任务清单

### 1. Google Console配置
- [ ] 创建Google Cloud Platform项目
- [ ] 启用Google+ API和Google Sign-In API
- [ ] 创建OAuth 2.0客户端ID
- [ ] 配置授权的重定向URI
- [ ] 获取客户端ID和客户端密钥
- [ ] 配置OAuth同意屏幕

### 2. 后端Google登录服务实现
- [ ] 创建GoogleLoginService服务类
- [ ] 添加Google API Client依赖
- [ ] 配置Google OAuth参数
- [ ] 实现ID Token验证
- [ ] 实现用户信息获取
- [ ] 处理Google API异常
- [ ] 用户信息映射到系统格式

### 3. 前端Google APIs集成
- [ ] 安装Google APIs相关依赖
- [ ] 配置Google OAuth客户端
- [ ] 创建Google登录组件
- [ ] 实现一键登录按钮
- [ ] 处理OAuth授权流程

### 4. Google登录组件开发
- [ ] 创建GoogleLogin组件
- [ ] 集成Google Sign-In JavaScript API
- [ ] 实现登录按钮和状态管理
- [ ] 处理登录成功回调
- [ ] 添加登录失败处理
- [ ] 实现登出功能

### 5. ID Token验证处理
- [ ] 后端验证ID Token签名
- [ ] 验证Token的有效期和签发者
- [ ] 提取用户信息（邮箱、姓名、头像）
- [ ] 处理Token验证失败情况
- [ ] 实现Token刷新机制

### 6. Google用户信息处理
- [ ] 定义Google用户信息接口
- [ ] 实现用户信息映射逻辑
- [ ] 处理用户邮箱验证状态
- [ ] 设置用户注册来源为Google
- [ ] 关联Google ID到用户记录

### 7. 错误处理和用户体验
- [ ] 处理网络错误和API异常
- [ ] 显示用户友好的错误信息
- [ ] 实现重试机制
- [ ] 添加加载状态指示
- [ ] 优化移动端登录体验

## 验收标准
- [x] Google OAuth登录功能正常工作
- [x] ID Token验证安全可靠
- [x] 用户信息正确获取和存储
- [x] 首次登录自动创建用户账户
- [x] 错误处理完善用户体验好
- [x] 符合Google OAuth安全要求

## 交付物
1. **Google登录服务**（GoogleLoginService.java）
2. **Google登录组件**（GoogleLogin.tsx）
3. **Google API配置**（google.config.ts）
4. **Google登录Hook**（useGoogleLogin.ts）
5. **Google OAuth依赖配置**

## 技术要点

### 后端依赖配置
```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.google.api-client</groupId>
    <artifactId>google-api-client</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>com.google.auth</groupId>
    <artifactId>google-auth-library-oauth2-http</artifactId>
    <version>1.17.0</version>
</dependency>
```

### 后端Google登录服务
```java
@Service
@Slf4j
public class GoogleLoginService {
    
    @Value("${google.oauth.client-id}")
    private String clientId;
    
    private GoogleIdTokenVerifier verifier;
    
    @PostConstruct
    public void init() {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        
        verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(clientId))
            .build();
    }
    
    public ThirdPartyUserInfo getUserInfoByIdToken(String idTokenString) {
        try {
            // 验证ID Token
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new BusinessException("Invalid Google ID token");
            }
            
            // 提取用户信息
            GoogleIdToken.Payload payload = idToken.getPayload();
            
            ThirdPartyUserInfo userInfo = new ThirdPartyUserInfo();
            userInfo.setThirdPartyId(payload.getSubject());
            userInfo.setEmail(payload.getEmail());
            userInfo.setName((String) payload.get("name"));
            userInfo.setAvatar((String) payload.get("picture"));
            userInfo.setProvider("google");
            
            // 验证邮箱是否已验证
            Boolean emailVerified = payload.getEmailVerified();
            if (emailVerified == null || !emailVerified) {
                log.warn("Google account email not verified for user: {}", payload.getEmail());
            }
            
            return userInfo;
            
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to verify Google ID token", e);
            throw new BusinessException("Google登录验证失败: " + e.getMessage());
        }
    }
    
    public boolean isTokenValid(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            return idToken != null;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### 前端Google APIs配置
```typescript
// config/google.config.ts
export const GOOGLE_CONFIG = {
  clientId: process.env.REACT_APP_GOOGLE_CLIENT_ID || '',
  scope: 'profile email',
  plugin_name: 'beilv-ai'
};

// 初始化Google APIs
export const initGoogleAuth = (): Promise<void> => {
  return new Promise((resolve, reject) => {
    window.gapi.load('auth2', {
      callback: () => {
        window.gapi.auth2
          .init({
            client_id: GOOGLE_CONFIG.clientId,
            scope: GOOGLE_CONFIG.scope
          })
          .then(resolve)
          .catch(reject);
      },
      onerror: reject
    });
  });
};
```

### 前端Google登录组件
```typescript
// components/GoogleLogin.tsx
interface GoogleLoginProps {
  onSuccess?: (tokenResponse: any) => void;
  onError?: (error: string) => void;
  disabled?: boolean;
}

export const GoogleLogin: React.FC<GoogleLoginProps> = ({
  onSuccess,
  onError,
  disabled = false
}) => {
  const [loading, setLoading] = useState(false);
  const [isGoogleReady, setIsGoogleReady] = useState(false);

  useEffect(() => {
    // 加载Google APIs
    const script = document.createElement('script');
    script.src = 'https://apis.google.com/js/platform.js';
    script.onload = () => {
      initGoogleAuth()
        .then(() => setIsGoogleReady(true))
        .catch(err => {
          console.error('Failed to initialize Google Auth:', err);
          onError?.('Google登录初始化失败');
        });
    };
    document.body.appendChild(script);

    return () => {
      if (script.parentNode) {
        script.parentNode.removeChild(script);
      }
    };
  }, [onError]);

  const handleGoogleLogin = async () => {
    if (!isGoogleReady || loading) return;

    setLoading(true);
    try {
      const authInstance = window.gapi.auth2.getAuthInstance();
      const googleUser = await authInstance.signIn();
      const idToken = googleUser.getAuthResponse().id_token;

      onSuccess?.({
        idToken,
        profile: googleUser.getBasicProfile()
      });

    } catch (error: any) {
      console.error('Google login error:', error);
      if (error.error === 'popup_closed_by_user') {
        onError?.('用户取消了Google登录');
      } else {
        onError?.('Google登录失败，请重试');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <button
      onClick={handleGoogleLogin}
      disabled={disabled || loading || !isGoogleReady}
      className="flex items-center justify-center px-4 py-2 border border-gray-300 rounded-md shadow-sm bg-white text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
    >
      {loading ? (
        <Spinner className="w-4 h-4 mr-2" />
      ) : (
        <GoogleIcon className="w-4 h-4 mr-2" />
      )}
      {loading ? '登录中...' : 'Google登录'}
    </button>
  );
};
```

### useGoogleLogin Hook
```typescript
// hooks/useGoogleLogin.ts
export const useGoogleLogin = () => {
  const { loginByGoogle } = useLogin();
  const [loading, setLoading] = useState(false);

  const handleGoogleLogin = useCallback(async (tokenResponse: any) => {
    setLoading(true);
    try {
      const result = await loginByGoogle(tokenResponse.idToken);
      
      if (result.success) {
        // 登录成功，页面会自动跳转
        return { success: true };
      } else {
        throw new Error(result.error || 'Google登录失败');
      }
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Google登录失败';
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  }, [loginByGoogle]);

  const handleGoogleError = useCallback((error: string) => {
    console.error('Google login error:', error);
    // 可以在这里添加错误上报逻辑
  }, []);

  return {
    handleGoogleLogin,
    handleGoogleError,
    loading
  };
};
```

### 配置文件
```yaml
# application.yml
google:
  oauth:
    client-id: ${GOOGLE_CLIENT_ID:your-google-client-id}
    client-secret: ${GOOGLE_CLIENT_SECRET:your-google-client-secret}
```

```typescript
// .env.local
REACT_APP_GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
```

## 安全考虑
- ID Token在后端验证签名和有效性
- 不在前端存储客户端密钥
- 验证Token的audience和issuer
- 使用HTTPS确保Token传输安全
- 合理设置Token过期时间

## 用户体验优化
- 一键登录简化流程
- 加载状态清晰指示
- 错误信息用户友好
- 支持多语言显示
- 移动端优化体验

## Google APIs集成
- 正确加载Google Platform Library
- 处理API加载失败情况
- 管理Google Auth实例
- 处理用户取消授权
- 实现登出功能

## 下一步
完成后进入 `02-4-3-第三方登录服务`