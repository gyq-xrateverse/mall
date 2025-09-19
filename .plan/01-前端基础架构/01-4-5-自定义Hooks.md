# 01-4-5-自定义Hooks

## 任务概述
- **时间估算**: 45分钟
- **优先级**: 中
- **依赖关系**: 
  - 依赖：01-4-4-UI状态管理（状态管理完善）
  - 依赖：01-5-1-HTTP请求封装（API调用支持）
  - 前置：Redux状态管理基础完成
- **执行阶段**: 第一阶段-前端基础架构

## 详细任务清单

### 4.5.1 创建基础Hooks
- [ ] 实现useLocalStorage Hook
- [ ] 创建useSessionStorage Hook
- [ ] 开发useDebounce Hook
- [ ] 实现useThrottle Hook

### 4.5.2 开发业务Hooks
- [ ] 创建useAsyncOperation Hook
- [ ] 实现useFormValidation Hook
- [ ] 开发useTableData Hook
- [ ] 创建usePagination Hook

### 4.5.3 实现工具Hooks
- [ ] 开发useEventListener Hook
- [ ] 创建useClickOutside Hook
- [ ] 实现useKeyPress Hook
- [ ] 开发useMediaQuery Hook

### 4.5.4 集成状态管理Hooks
- [ ] 创建useAppState Hook
- [ ] 实现useAuth Hook
- [ ] 开发usePermission Hook
- [ ] 创建useSettings Hook

## 验收标准

### 功能验收
- [ ] 所有Hooks功能正常运行
- [ ] 状态管理集成正确
- [ ] 错误处理机制完善
- [ ] 性能优化措施到位

### 代码质量验收
- [ ] Hook代码结构清晰
- [ ] TypeScript类型定义完整
- [ ] 依赖项配置正确
- [ ] 可复用性良好

### 性能验收
- [ ] 无不必要的重新渲染
- [ ] 内存使用合理
- [ ] 清理机制完善
- [ ] 缓存策略有效

## 交付物

### 1. 基础Hooks文件
```
src/hooks/
├── useLocalStorage.ts     # 本地存储Hook
├── useSessionStorage.ts   # 会话存储Hook
├── useDebounce.ts         # 防抖Hook
├── useThrottle.ts         # 节流Hook
└── useAsyncOperation.ts   # 异步操作Hook
```

### 2. 业务Hooks文件
```
src/hooks/business/
├── useFormValidation.ts   # 表单验证Hook
├── useTableData.ts        # 表格数据Hook
├── usePagination.ts       # 分页Hook
└── useSearch.ts           # 搜索Hook
```

### 3. 工具Hooks文件
```
src/hooks/utils/
├── useEventListener.ts    # 事件监听Hook
├── useClickOutside.ts     # 点击外部Hook
├── useKeyPress.ts         # 按键监听Hook
└── useMediaQuery.ts       # 媒体查询Hook
```

### 4. 状态管理Hooks
```
src/hooks/store/
├── useAppState.ts         # 应用状态Hook
├── useAuth.ts             # 认证Hook
├── usePermission.ts       # 权限Hook
└── useSettings.ts         # 设置Hook
```

## 技术要点

### useLocalStorage Hook
```typescript
// src/hooks/useLocalStorage.ts
import { useState, useCallback, useEffect } from 'react';

type SetValue<T> = (value: T | ((prevValue: T) => T)) => void;

export function useLocalStorage<T>(
  key: string,
  initialValue: T
): [T, SetValue<T>, () => void] {
  // 读取初始值
  const [storedValue, setStoredValue] = useState<T>(() => {
    try {
      const item = localStorage.getItem(key);
      return item ? JSON.parse(item) : initialValue;
    } catch (error) {
      console.warn(`Error reading localStorage key "${key}":`, error);
      return initialValue;
    }
  });

  // 设置值的函数
  const setValue: SetValue<T> = useCallback(
    (value) => {
      try {
        const valueToStore = value instanceof Function ? value(storedValue) : value;
        setStoredValue(valueToStore);
        localStorage.setItem(key, JSON.stringify(valueToStore));
        
        // 触发自定义事件，通知其他组件
        window.dispatchEvent(
          new CustomEvent('localStorage-changed', {
            detail: { key, value: valueToStore },
          })
        );
      } catch (error) {
        console.error(`Error setting localStorage key "${key}":`, error);
      }
    },
    [key, storedValue]
  );

  // 删除值的函数
  const removeValue = useCallback(() => {
    try {
      localStorage.removeItem(key);
      setStoredValue(initialValue);
      
      window.dispatchEvent(
        new CustomEvent('localStorage-changed', {
          detail: { key, value: null },
        })
      );
    } catch (error) {
      console.error(`Error removing localStorage key "${key}":`, error);
    }
  }, [key, initialValue]);

  // 监听其他标签页的变化
  useEffect(() => {
    const handleStorageChange = (e: StorageEvent | CustomEvent) => {
      if ('key' in e && e.key === key) {
        try {
          const newValue = e.newValue ? JSON.parse(e.newValue) : initialValue;
          setStoredValue(newValue);
        } catch (error) {
          console.warn(`Error parsing localStorage value for key "${key}":`, error);
        }
      } else if ('detail' in e && e.detail.key === key) {
        setStoredValue(e.detail.value || initialValue);
      }
    };

    window.addEventListener('storage', handleStorageChange as EventListener);
    window.addEventListener('localStorage-changed', handleStorageChange as EventListener);

    return () => {
      window.removeEventListener('storage', handleStorageChange as EventListener);
      window.removeEventListener('localStorage-changed', handleStorageChange as EventListener);
    };
  }, [key, initialValue]);

  return [storedValue, setValue, removeValue];
}
```

### useDebounce Hook
```typescript
// src/hooks/useDebounce.ts
import { useState, useEffect, useRef, useCallback } from 'react';

export function useDebounce<T>(value: T, delay: number): T {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
}

export function useDebouncedCallback<T extends (...args: any[]) => void>(
  callback: T,
  delay: number
): T {
  const callbackRef = useRef<T>(callback);
  const timeoutRef = useRef<NodeJS.Timeout>();

  // 更新回调引用
  useEffect(() => {
    callbackRef.current = callback;
  }, [callback]);

  // 清理定时器
  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  return useCallback(
    ((...args: Parameters<T>) => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }

      timeoutRef.current = setTimeout(() => {
        callbackRef.current(...args);
      }, delay);
    }) as T,
    [delay]
  );
}
```

### useAsyncOperation Hook
```typescript
// src/hooks/useAsyncOperation.ts
import { useState, useCallback, useRef, useEffect } from 'react';

interface AsyncOperationState<T> {
  data: T | null;
  loading: boolean;
  error: Error | null;
}

interface AsyncOperationOptions {
  immediate?: boolean;
  onSuccess?: (data: any) => void;
  onError?: (error: Error) => void;
}

export function useAsyncOperation<T, P extends any[] = []>(
  asyncFunction: (...params: P) => Promise<T>,
  options: AsyncOperationOptions = {}
) {
  const { immediate = false, onSuccess, onError } = options;
  
  const [state, setState] = useState<AsyncOperationState<T>>({
    data: null,
    loading: false,
    error: null,
  });

  const mountedRef = useRef(true);
  const currentPromiseRef = useRef<Promise<T> | null>(null);

  useEffect(() => {
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const execute = useCallback(
    async (...params: P): Promise<T | undefined> => {
      if (!mountedRef.current) return;

      setState((prev) => ({ ...prev, loading: true, error: null }));

      try {
        const promise = asyncFunction(...params);
        currentPromiseRef.current = promise;

        const data = await promise;

        // 检查是否还是当前的请求
        if (currentPromiseRef.current === promise && mountedRef.current) {
          setState({ data, loading: false, error: null });
          onSuccess?.(data);
          return data;
        }
      } catch (error) {
        const errorObj = error instanceof Error ? error : new Error(String(error));
        
        if (mountedRef.current) {
          setState((prev) => ({ ...prev, loading: false, error: errorObj }));
          onError?.(errorObj);
        }
        throw errorObj;
      }
    },
    [asyncFunction, onSuccess, onError]
  );

  const reset = useCallback(() => {
    if (mountedRef.current) {
      setState({
        data: null,
        loading: false,
        error: null,
      });
      currentPromiseRef.current = null;
    }
  }, []);

  // 立即执行
  useEffect(() => {
    if (immediate) {
      execute();
    }
  }, [immediate, execute]);

  return {
    ...state,
    execute,
    reset,
  };
}
```

### useFormValidation Hook
```typescript
// src/hooks/business/useFormValidation.ts
import { useState, useCallback, useMemo } from 'react';

type ValidationRule<T> = (value: T) => string | null;
type ValidationRules<T> = {
  [K in keyof T]?: ValidationRule<T[K]>[];
};

interface FormValidationOptions {
  validateOnChange?: boolean;
  validateOnBlur?: boolean;
}

export function useFormValidation<T extends Record<string, any>>(
  initialValues: T,
  validationRules: ValidationRules<T>,
  options: FormValidationOptions = {}
) {
  const { validateOnChange = true, validateOnBlur = true } = options;
  
  const [values, setValues] = useState<T>(initialValues);
  const [errors, setErrors] = useState<Partial<Record<keyof T, string>>>({});
  const [touched, setTouched] = useState<Partial<Record<keyof T, boolean>>>({});

  // 验证单个字段
  const validateField = useCallback(
    (name: keyof T, value: T[keyof T]): string | null => {
      const rules = validationRules[name];
      if (!rules) return null;

      for (const rule of rules) {
        const error = rule(value);
        if (error) return error;
      }
      return null;
    },
    [validationRules]
  );

  // 验证所有字段
  const validateForm = useCallback((): boolean => {
    const newErrors: Partial<Record<keyof T, string>> = {};
    let isValid = true;

    Object.keys(values).forEach((key) => {
      const fieldName = key as keyof T;
      const error = validateField(fieldName, values[fieldName]);
      if (error) {
        newErrors[fieldName] = error;
        isValid = false;
      }
    });

    setErrors(newErrors);
    return isValid;
  }, [values, validateField]);

  // 设置字段值
  const setFieldValue = useCallback(
    (name: keyof T, value: T[keyof T]) => {
      setValues((prev) => ({ ...prev, [name]: value }));

      if (validateOnChange) {
        const error = validateField(name, value);
        setErrors((prev) => ({ ...prev, [name]: error }));
      }
    },
    [validateField, validateOnChange]
  );

  // 设置字段错误
  const setFieldError = useCallback((name: keyof T, error: string | null) => {
    setErrors((prev) => ({ ...prev, [name]: error }));
  }, []);

  // 设置字段已触摸
  const setFieldTouched = useCallback(
    (name: keyof T, isTouched = true) => {
      setTouched((prev) => ({ ...prev, [name]: isTouched }));

      if (validateOnBlur && isTouched) {
        const error = validateField(name, values[name]);
        setErrors((prev) => ({ ...prev, [name]: error }));
      }
    },
    [validateField, validateOnBlur, values]
  );

  // 重置表单
  const resetForm = useCallback(() => {
    setValues(initialValues);
    setErrors({});
    setTouched({});
  }, [initialValues]);

  // 计算衍生状态
  const isValid = useMemo(() => {
    return Object.keys(errors).every((key) => !errors[key as keyof T]);
  }, [errors]);

  const isDirty = useMemo(() => {
    return Object.keys(values).some((key) => {
      const fieldName = key as keyof T;
      return values[fieldName] !== initialValues[fieldName];
    });
  }, [values, initialValues]);

  const isSubmitting = useMemo(() => {
    return Object.keys(touched).length > 0 && isValid;
  }, [touched, isValid]);

  return {
    values,
    errors,
    touched,
    isValid,
    isDirty,
    isSubmitting,
    setFieldValue,
    setFieldError,
    setFieldTouched,
    validateField,
    validateForm,
    resetForm,
  };
}
```

### useTableData Hook
```typescript
// src/hooks/business/useTableData.ts
import { useState, useCallback, useMemo } from 'react';
import { useAsyncOperation } from '../useAsyncOperation';

interface PaginationConfig {
  current: number;
  pageSize: number;
  total: number;
}

interface SortConfig {
  field: string;
  order: 'ascend' | 'descend';
}

interface FilterConfig {
  [key: string]: any;
}

interface TableDataOptions<T> {
  initialPageSize?: number;
  initialSorting?: SortConfig;
  initialFilters?: FilterConfig;
  onDataChange?: (data: T[]) => void;
}

export function useTableData<T>(
  fetchFunction: (params: {
    pagination: PaginationConfig;
    sorting?: SortConfig;
    filters?: FilterConfig;
  }) => Promise<{ data: T[]; total: number }>,
  options: TableDataOptions<T> = {}
) {
  const {
    initialPageSize = 10,
    initialSorting,
    initialFilters = {},
    onDataChange,
  } = options;

  const [pagination, setPagination] = useState<PaginationConfig>({
    current: 1,
    pageSize: initialPageSize,
    total: 0,
  });

  const [sorting, setSorting] = useState<SortConfig | undefined>(initialSorting);
  const [filters, setFilters] = useState<FilterConfig>(initialFilters);

  // 使用异步操作Hook
  const {
    data: response,
    loading,
    error,
    execute: fetchData,
  } = useAsyncOperation(fetchFunction, {
    onSuccess: (result) => {
      setPagination((prev) => ({ ...prev, total: result.total }));
      onDataChange?.(result.data);
    },
  });

  const data = response?.data || [];

  // 获取数据
  const refresh = useCallback(() => {
    fetchData({ pagination, sorting, filters });
  }, [fetchData, pagination, sorting, filters]);

  // 分页变化
  const handlePaginationChange = useCallback(
    (page: number, pageSize?: number) => {
      setPagination((prev) => ({
        ...prev,
        current: page,
        pageSize: pageSize || prev.pageSize,
      }));
    },
    []
  );

  // 排序变化
  const handleSortingChange = useCallback((newSorting: SortConfig | undefined) => {
    setSorting(newSorting);
    setPagination((prev) => ({ ...prev, current: 1 })); // 重置到第一页
  }, []);

  // 筛选变化
  const handleFiltersChange = useCallback((newFilters: FilterConfig) => {
    setFilters(newFilters);
    setPagination((prev) => ({ ...prev, current: 1 })); // 重置到第一页
  }, []);

  // 重置
  const reset = useCallback(() => {
    setPagination({
      current: 1,
      pageSize: initialPageSize,
      total: 0,
    });
    setSorting(initialSorting);
    setFilters(initialFilters);
  }, [initialPageSize, initialSorting, initialFilters]);

  // 自动刷新当参数变化时
  useMemo(() => {
    refresh();
  }, [pagination.current, pagination.pageSize, sorting, filters]);

  return {
    data,
    loading,
    error,
    pagination,
    sorting,
    filters,
    refresh,
    reset,
    handlePaginationChange,
    handleSortingChange,
    handleFiltersChange,
  };
}
```

### useAuth Hook
```typescript
// src/hooks/store/useAuth.ts
import { useCallback } from 'react';
import { useAppSelector, useAppDispatch } from '@/store/hooks';
import {
  loginAsync,
  logoutAsync,
  refreshTokenAsync,
  updateUserProfileAsync,
} from '@/store/slices/authSlice';
import {
  selectIsAuthenticated,
  selectCurrentUser,
  selectUserRoles,
  selectUserPermissions,
  selectIsLoading,
  selectAuthError,
  selectLoginError,
  selectHasRole,
  selectHasPermission,
  selectCanAccess,
} from '@/store/slices/authSelectors';
import { LoginRequest } from '@/store/slices/authTypes';

export const useAuth = () => {
  const dispatch = useAppDispatch();

  // 选择器
  const isAuthenticated = useAppSelector(selectIsAuthenticated);
  const user = useAppSelector(selectCurrentUser);
  const roles = useAppSelector(selectUserRoles);
  const permissions = useAppSelector(selectUserPermissions);
  const isLoading = useAppSelector(selectIsLoading);
  const error = useAppSelector(selectAuthError);
  const loginError = useAppSelector(selectLoginError);

  // 权限检查函数
  const hasRole = useAppSelector(selectHasRole);
  const hasPermission = useAppSelector(selectHasPermission);
  const canAccess = useAppSelector(selectCanAccess);

  // 动作函数
  const login = useCallback(
    (credentials: LoginRequest) => {
      return dispatch(loginAsync(credentials));
    },
    [dispatch]
  );

  const logout = useCallback(() => {
    return dispatch(logoutAsync());
  }, [dispatch]);

  const refreshToken = useCallback(() => {
    return dispatch(refreshTokenAsync());
  }, [dispatch]);

  const updateProfile = useCallback(
    (profileData: any) => {
      return dispatch(updateUserProfileAsync(profileData));
    },
    [dispatch]
  );

  return {
    // 状态
    isAuthenticated,
    user,
    roles,
    permissions,
    isLoading,
    error,
    loginError,
    
    // 权限检查
    hasRole,
    hasPermission,
    canAccess,
    
    // 动作
    login,
    logout,
    refreshToken,
    updateProfile,
  };
};
```

### usePermission Hook
```typescript
// src/hooks/store/usePermission.ts
import { useMemo } from 'react';
import { useAuth } from './useAuth';

export const usePermission = () => {
  const { roles, permissions, hasRole, hasPermission, canAccess } = useAuth();

  // 权限检查工具函数
  const checkPermissions = useMemo(() => ({
    // 检查单个权限
    can: (permission: string) => hasPermission(permission),
    
    // 检查多个权限（任意一个）
    canAny: (permissionList: string[]) => 
      permissionList.some(hasPermission),
    
    // 检查多个权限（全部）
    canAll: (permissionList: string[]) => 
      permissionList.every(hasPermission),
    
    // 检查角色权限
    hasRole: (role: string) => hasRole(role),
    
    // 检查多个角色（任意一个）
    hasAnyRole: (roleList: string[]) => 
      roleList.some(hasRole),
    
    // 检查多个角色（全部）
    hasAllRoles: (roleList: string[]) => 
      roleList.every(hasRole),
    
    // 综合权限检查
    canAccess: (requiredRoles?: string[], requiredPermissions?: string[]) =>
      canAccess(requiredRoles, requiredPermissions),
  }), [hasRole, hasPermission, canAccess]);

  // 常用权限检查
  const commonPermissions = useMemo(() => ({
    canRead: checkPermissions.can('read'),
    canWrite: checkPermissions.can('write'),
    canDelete: checkPermissions.can('delete'),
    canManage: checkPermissions.can('manage'),
    isAdmin: checkPermissions.hasRole('admin'),
    isModerator: checkPermissions.hasRole('moderator'),
  }), [checkPermissions]);

  return {
    roles,
    permissions,
    ...checkPermissions,
    ...commonPermissions,
  };
};
```

## 下一步
- **后续任务**: 01-4-6-数据持久化
- **关联任务**: 实现数据的持久化存储机制
- **注意事项**: 
  - Hook要考虑组件卸载时的清理
  - 异步操作要处理竞态条件
  - 状态管理Hook要保持与store的同步

## 常见问题解决

### Q1: Hook导致无限重新渲染
- 检查依赖数组配置
- 确认回调函数使用useCallback
- 验证状态更新逻辑

### Q2: 异步Hook内存泄漏
- 确保组件卸载时清理
- 检查定时器和事件监听器清理
- 验证异步操作取消机制

### Q3: 表单验证Hook性能问题
- 使用useMemo优化计算
- 避免不必要的验证触发
- 优化验证规则执行

### Q4: 状态管理Hook同步问题
- 确认选择器使用正确
- 检查状态更新时机
- 验证dispatch函数调用