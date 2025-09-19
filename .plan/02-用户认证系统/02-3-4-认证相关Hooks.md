# 02-3-4: 认证相关Hooks

## 任务概述
**时间估算**: 1.5小时  
**优先级**: 高  
**依赖关系**: 02-3-3 认证状态管理完成  
**负责模块**: 前端React应用

## 详细任务清单

### 1. 创建useAuth基础Hook
- [ ] 创建hooks/useAuth.ts文件
- [ ] 封装认证状态选择器
- [ ] 提供用户信息和认证状态
- [ ] 实现登出功能
- [ ] 提供加载和错误状态

### 2. 实现useLogin登录Hook
- [ ] 创建hooks/useLogin.ts文件
- [ ] 封装邮箱登录逻辑
- [ ] 处理第三方登录
- [ ] 管理登录表单状态
- [ ] 提供登录成功回调

### 3. 实现useRegister注册Hook
- [ ] 创建hooks/useRegister.ts文件
- [ ] 封装邮箱注册逻辑
- [ ] 管理注册表单状态
- [ ] 处理验证码发送
- [ ] 提供注册成功回调

### 4. 创建表单验证Hooks
- [ ] 创建hooks/useFormValidation.ts文件
- [ ] 实现通用表单验证逻辑
- [ ] 支持实时验证
- [ ] 提供验证错误状态
- [ ] 支持自定义验证规则

### 5. 实现验证码管理Hook
- [ ] 创建hooks/useVerificationCode.ts文件
- [ ] 管理验证码发送状态
- [ ] 实现倒计时功能
- [ ] 处理发送频率限制
- [ ] 提供发送成功/失败状态

### 6. 创建Token管理Hook
- [ ] 创建hooks/useToken.ts文件
- [ ] 封装Token刷新逻辑
- [ ] 监听Token过期
- [ ] 自动刷新Token
- [ ] 处理Token失效

### 7. 实现路由守卫Hook
- [ ] 创建hooks/useAuthGuard.ts文件
- [ ] 实现路由权限检查
- [ ] 未认证用户重定向
- [ ] 认证状态加载处理

### 8. 创建第三方登录Hooks
- [ ] 创建hooks/useWeChatLogin.ts文件
- [ ] 创建hooks/useGoogleLogin.ts文件
- [ ] 封装第三方登录SDK
- [ ] 处理授权回调
- [ ] 统一错误处理

## 验收标准
- [x] 所有认证Hooks功能完整
- [x] Hook接口设计合理易用
- [x] 状态管理逻辑正确
- [x] 错误处理完善
- [x] 类型定义完整
- [x] 可复用性强

## 交付物
1. **基础认证Hook**（useAuth.ts）
2. **登录Hook**（useLogin.ts）
3. **注册Hook**（useRegister.ts）
4. **表单验证Hook**（useFormValidation.ts）
5. **验证码Hook**（useVerificationCode.ts）
6. **Token管理Hook**（useToken.ts）
7. **路由守卫Hook**（useAuthGuard.ts）
8. **第三方登录Hooks**（useWeChatLogin.ts、useGoogleLogin.ts）

## 技术要点

### useAuth基础Hook
```typescript
// hooks/useAuth.ts
export const useAuth = () => {
  const dispatch = useAppDispatch();
  const {
    user,
    isAuthenticated,
    loading,
    error,
    accessToken,
  } = useAppSelector(selectAuth);

  const logout = useCallback(() => {
    dispatch(authActions.logout());
    // 跳转到登录页
    navigate('/auth/login');
  }, [dispatch]);

  const clearError = useCallback(() => {
    dispatch(authActions.clearError());
  }, [dispatch]);

  return {
    user,
    isAuthenticated,
    loading,
    error,
    accessToken,
    logout,
    clearError,
  };
};
```

### useLogin登录Hook
```typescript
// hooks/useLogin.ts
export const useLogin = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { loading, error } = useAuth();

  const loginByEmail = useCallback(async (
    credentials: EmailLoginRequest,
    options?: { redirect?: string }
  ) => {
    try {
      const result = await dispatch(loginByEmailAsync(credentials));
      if (loginByEmailAsync.fulfilled.match(result)) {
        const redirectTo = options?.redirect || '/dashboard';
        navigate(redirectTo);
        return { success: true };
      }
      return { success: false, error: result.payload };
    } catch (error) {
      return { success: false, error: '登录失败' };
    }
  }, [dispatch, navigate]);

  const loginByWeChat = useCallback(async (code: string) => {
    try {
      const result = await dispatch(loginByWeChatAsync({ code }));
      if (loginByWeChatAsync.fulfilled.match(result)) {
        navigate('/dashboard');
        return { success: true };
      }
      return { success: false, error: result.payload };
    } catch (error) {
      return { success: false, error: '微信登录失败' };
    }
  }, [dispatch, navigate]);

  return {
    loginByEmail,
    loginByWeChat,
    loading,
    error,
  };
};
```

### useRegister注册Hook
```typescript
// hooks/useRegister.ts
export const useRegister = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { loading, error } = useAuth();

  const registerByEmail = useCallback(async (
    userData: EmailRegisterRequest
  ) => {
    try {
      const result = await dispatch(registerByEmailAsync(userData));
      if (registerByEmailAsync.fulfilled.match(result)) {
        // 注册成功自动跳转到欢迎页或主页
        navigate('/welcome');
        return { success: true };
      }
      return { success: false, error: result.payload };
    } catch (error) {
      return { success: false, error: '注册失败' };
    }
  }, [dispatch, navigate]);

  return {
    registerByEmail,
    loading,
    error,
  };
};
```

### useFormValidation表单验证Hook
```typescript
// hooks/useFormValidation.ts
export const useFormValidation = <T extends Record<string, any>>(
  initialValues: T,
  validationRules: ValidationRules<T>
) => {
  const [values, setValues] = useState<T>(initialValues);
  const [errors, setErrors] = useState<Partial<Record<keyof T, string>>>({});
  const [touched, setTouched] = useState<Partial<Record<keyof T, boolean>>>({});

  const validateField = useCallback((
    field: keyof T,
    value: any
  ): string | undefined => {
    const rule = validationRules[field];
    if (rule) {
      return rule(value, values);
    }
    return undefined;
  }, [validationRules, values]);

  const setFieldValue = useCallback((field: keyof T, value: any) => {
    setValues(prev => ({ ...prev, [field]: value }));
    
    // 实时验证
    if (touched[field]) {
      const error = validateField(field, value);
      setErrors(prev => ({ ...prev, [field]: error }));
    }
  }, [validateField, touched]);

  const setFieldTouched = useCallback((field: keyof T, isTouched = true) => {
    setTouched(prev => ({ ...prev, [field]: isTouched }));
    
    if (isTouched) {
      const error = validateField(field, values[field]);
      setErrors(prev => ({ ...prev, [field]: error }));
    }
  }, [validateField, values]);

  const validateAll = useCallback((): boolean => {
    const newErrors: Partial<Record<keyof T, string>> = {};
    let isValid = true;

    Object.keys(validationRules).forEach(field => {
      const error = validateField(field as keyof T, values[field]);
      if (error) {
        newErrors[field as keyof T] = error;
        isValid = false;
      }
    });

    setErrors(newErrors);
    setTouched(
      Object.keys(validationRules).reduce((acc, key) => ({
        ...acc,
        [key]: true
      }), {})
    );

    return isValid;
  }, [validationRules, validateField, values]);

  const resetForm = useCallback(() => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
  }, [initialValues]);

  return {
    values,
    errors,
    touched,
    setFieldValue,
    setFieldTouched,
    validateAll,
    resetForm,
    isValid: Object.keys(errors).length === 0,
  };
};
```

### useVerificationCode验证码Hook
```typescript
// hooks/useVerificationCode.ts
export const useVerificationCode = () => {
  const [countdown, setCountdown] = useState(0);
  const [sending, setSending] = useState(false);

  const startCountdown = useCallback(() => {
    setCountdown(60);
    const timer = setInterval(() => {
      setCountdown(prev => {
        if (prev <= 1) {
          clearInterval(timer);
          return 0;
        }
        return prev - 1;
      });
    }, 1000);
  }, []);

  const sendCode = useCallback(async (
    email: string,
    type: 'register' | 'login' | 'reset'
  ) => {
    if (countdown > 0 || sending) {
      return { success: false, error: '请勿频繁发送' };
    }

    setSending(true);
    try {
      await authApi.sendVerificationCode({ email, codeType: type });
      startCountdown();
      setSending(false);
      return { success: true };
    } catch (error) {
      setSending(false);
      return { success: false, error: '发送失败，请稍后重试' };
    }
  }, [countdown, sending, startCountdown]);

  return {
    countdown,
    sending,
    sendCode,
    canSend: countdown === 0 && !sending,
  };
};
```

### useAuthGuard路由守卫Hook
```typescript
// hooks/useAuthGuard.ts
export const useAuthGuard = (requireAuth = true) => {
  const { isAuthenticated, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (loading) return;

    if (requireAuth && !isAuthenticated) {
      // 保存当前路径用于登录后跳转
      navigate('/auth/login', {
        state: { from: location.pathname }
      });
    } else if (!requireAuth && isAuthenticated) {
      // 已登录用户访问登录页面，重定向到主页
      navigate('/dashboard');
    }
  }, [isAuthenticated, loading, requireAuth, navigate, location]);

  return {
    isAuthenticated,
    loading,
    canAccess: loading || (requireAuth ? isAuthenticated : !isAuthenticated)
  };
};
```

## Hook设计原则
- 单一职责：每个Hook专注一个功能
- 可复用性：通用逻辑抽取为独立Hook
- 类型安全：完整的TypeScript类型定义
- 错误处理：统一的错误处理机制
- 性能优化：合理使用useMemo和useCallback

## 使用示例
```typescript
// 在登录组件中使用
const LoginComponent = () => {
  const { loginByEmail, loading, error } = useLogin();
  const { sendCode, countdown, canSend } = useVerificationCode();
  const { values, errors, setFieldValue, validateAll } = useFormValidation(
    { email: '', code: '' },
    loginValidationRules
  );

  const handleSubmit = async () => {
    if (!validateAll()) return;
    
    const result = await loginByEmail(values);
    if (result.success) {
      // 登录成功处理
    }
  };

  return (
    // 组件JSX
  );
};
```

## 下一步
完成后进入 `02-4-1-微信扫码登录集成`