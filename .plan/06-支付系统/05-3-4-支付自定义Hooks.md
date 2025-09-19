# 05-3-4: 支付自定义Hooks

## 任务概述
**时间估算**: 30-45分钟  
**优先级**: 中  
**依赖关系**: 05-3-2-RTK Query API集成 完成

## 详细任务清单

### 1. 支付管理Hook开发
- [ ] usePayment Hook实现
- [ ] 支付状态管理逻辑
- [ ] 错误处理封装
- [ ] 状态重置功能

### 2. 支付状态轮询Hook开发  
- [ ] usePaymentPolling Hook实现
- [ ] 轮询控制逻辑
- [ ] 超时处理机制
- [ ] 回调事件处理

### 3. 支付表单Hook开发
- [ ] usePaymentForm Hook实现
- [ ] 表单验证逻辑
- [ ] 支付方式切换
- [ ] 表单状态管理

### 4. Hook性能优化
- [ ] 避免不必要的重渲染
- [ ] 内存泄漏防护
- [ ] 轮询优化控制
- [ ] 依赖项优化

## 验收标准
- [ ] Hooks功能完整
- [ ] 状态管理正确
- [ ] 性能优化合理
- [ ] 错误处理完善
- [ ] 类型安全保证

## 交付物
- [ ] 支付管理Hook
- [ ] 支付轮询Hook
- [ ] 支付表单Hook
- [ ] Hook使用文档

## 技术要点

### 支付管理Hook实现
```typescript
// hooks/usePayment.ts
import { useAppSelector, useAppDispatch } from './redux';
import { 
  createPaymentOrder,
  fetchPaymentStatus,
  cancelPayment,
  clearError,
  resetPaymentState 
} from '@/store/payment/paymentSlice';
import { useCreatePaymentMutation, useGetPaymentStatusQuery } from '@/api/paymentApi';

export const usePayment = () => {
  const dispatch = useAppDispatch();
  const payment = useAppSelector(state => state.payment);
  
  const clearPaymentError = () => {
    dispatch(clearError());
  };

  const resetState = () => {
    dispatch(resetPaymentState());
  };

  return {
    ...payment,
    clearPaymentError,
    resetState,
  };
};
```

### 支付状态轮询Hook
```typescript
// hooks/usePaymentPolling.ts
import { useState, useEffect, useRef } from 'react';
import { useGetPaymentStatusQuery } from '@/api/paymentApi';

export const usePaymentPolling = (
  orderSn: string,
  options: {
    onSuccess?: (status: PaymentStatus) => void;
    onFailure?: (status: PaymentStatus) => void;
    onCancel?: (status: PaymentStatus) => void;
    interval?: number;
    maxAttempts?: number;
  } = {}
) => {
  const [isPolling, setIsPolling] = useState(true);
  const [attempts, setAttempts] = useState(0);
  const maxAttempts = options.maxAttempts || 300; // 最多轮询5分钟
  const interval = options.interval || 2000;

  const { data: paymentStatus, error } = useGetPaymentStatusQuery(orderSn, {
    pollingInterval: isPolling ? interval : 0,
    skip: !orderSn || !isPolling,
  });

  const prevStatusRef = useRef<number>();

  useEffect(() => {
    if (paymentStatus && paymentStatus.paymentStatus !== prevStatusRef.current) {
      prevStatusRef.current = paymentStatus.paymentStatus;

      switch (paymentStatus.paymentStatus) {
        case 1: // 支付成功
          setIsPolling(false);
          options.onSuccess?.(paymentStatus);
          break;
        case 2: // 支付失败
          setIsPolling(false);
          options.onFailure?.(paymentStatus);
          break;
        case 3: // 已取消
          setIsPolling(false);
          options.onCancel?.(paymentStatus);
          break;
      }
    }
  }, [paymentStatus, options]);

  useEffect(() => {
    if (attempts >= maxAttempts) {
      setIsPolling(false);
    } else if (isPolling) {
      setAttempts(prev => prev + 1);
    }
  }, [paymentStatus, attempts, maxAttempts, isPolling]);

  const stopPolling = () => {
    setIsPolling(false);
  };

  const startPolling = () => {
    setIsPolling(true);
    setAttempts(0);
  };

  return {
    paymentStatus,
    isPolling,
    error,
    stopPolling,
    startPolling,
    attemptsRemaining: Math.max(0, maxAttempts - attempts),
  };
};
```

### 支付表单Hook
```typescript
// hooks/usePaymentForm.ts
import { useState, useCallback } from 'react';
import { useGetPaymentMethodsQuery } from '@/api/paymentApi';

interface PaymentFormData {
  paymentMethod: string;
  clientType: string;
  agreed: boolean;
}

export const usePaymentForm = (initialValues?: Partial<PaymentFormData>) => {
  const [formData, setFormData] = useState<PaymentFormData>({
    paymentMethod: 'alipay',
    clientType: 'web',
    agreed: false,
    ...initialValues,
  });

  const [errors, setErrors] = useState<Partial<Record<keyof PaymentFormData, string>>>({});
  
  const { data: paymentMethods, isLoading: methodsLoading } = useGetPaymentMethodsQuery();

  const updateField = useCallback((field: keyof PaymentFormData, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
    
    // 清除对应字段的错误
    if (errors[field]) {
      setErrors(prev => ({
        ...prev,
        [field]: undefined,
      }));
    }
  }, [errors]);

  const validate = useCallback((): boolean => {
    const newErrors: Partial<Record<keyof PaymentFormData, string>> = {};

    if (!formData.paymentMethod) {
      newErrors.paymentMethod = '请选择支付方式';
    }

    if (!formData.agreed) {
      newErrors.agreed = '请同意支付协议';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }, [formData]);

  const reset = useCallback(() => {
    setFormData({
      paymentMethod: 'alipay',
      clientType: 'web',
      agreed: false,
      ...initialValues,
    });
    setErrors({});
  }, [initialValues]);

  const isValid = Object.keys(errors).length === 0 && formData.agreed;

  return {
    formData,
    errors,
    paymentMethods,
    methodsLoading,
    updateField,
    validate,
    reset,
    isValid,
  };
};
```

### 支付重试Hook
```typescript
// hooks/usePaymentRetry.ts
import { useState, useCallback } from 'react';
import { useCreatePaymentMutation } from '@/api/paymentApi';

export const usePaymentRetry = () => {
  const [retryCount, setRetryCount] = useState(0);
  const [lastError, setLastError] = useState<string | null>(null);
  const maxRetries = 3;

  const [createPayment, { isLoading }] = useCreatePaymentMutation();

  const executeWithRetry = useCallback(async (
    paymentData: { orderId: number; paymentMethod: string; clientType?: string },
    onSuccess?: (result: any) => void,
    onFailure?: (error: string) => void
  ) => {
    try {
      const result = await createPayment(paymentData).unwrap();
      setRetryCount(0);
      setLastError(null);
      onSuccess?.(result);
      return result;
    } catch (error: any) {
      const errorMessage = error.data?.message || '支付创建失败';
      setLastError(errorMessage);
      
      if (retryCount < maxRetries) {
        setRetryCount(prev => prev + 1);
        // 延迟重试
        setTimeout(() => {
          executeWithRetry(paymentData, onSuccess, onFailure);
        }, 1000 * retryCount); // 递增延迟
      } else {
        onFailure?.(errorMessage);
      }
    }
  }, [createPayment, retryCount, maxRetries]);

  const reset = useCallback(() => {
    setRetryCount(0);
    setLastError(null);
  }, []);

  return {
    executeWithRetry,
    reset,
    retryCount,
    maxRetries,
    lastError,
    isLoading,
    canRetry: retryCount < maxRetries,
  };
};
```

### Hook测试用例
```typescript
// hooks/__tests__/usePaymentPolling.test.ts
import { renderHook, waitFor } from '@testing-library/react';
import { usePaymentPolling } from '../usePaymentPolling';

describe('usePaymentPolling', () => {
  it('should start polling automatically', () => {
    const { result } = renderHook(() => 
      usePaymentPolling('TEST_ORDER_123')
    );

    expect(result.current.isPolling).toBe(true);
  });

  it('should call onSuccess when payment succeeds', async () => {
    const onSuccess = jest.fn();
    
    const { result } = renderHook(() => 
      usePaymentPolling('TEST_ORDER_123', { onSuccess })
    );

    // Mock successful payment status
    mockPaymentStatusResponse(1); // 支付成功

    await waitFor(() => {
      expect(onSuccess).toHaveBeenCalled();
      expect(result.current.isPolling).toBe(false);
    });
  });

  it('should stop polling on failure', async () => {
    const onFailure = jest.fn();
    
    const { result } = renderHook(() => 
      usePaymentPolling('TEST_ORDER_123', { onFailure })
    );

    // Mock failed payment status
    mockPaymentStatusResponse(2); // 支付失败

    await waitFor(() => {
      expect(onFailure).toHaveBeenCalled();
      expect(result.current.isPolling).toBe(false);
    });
  });
});
```

## 下一步
完成后支付前端功能开发全部完成