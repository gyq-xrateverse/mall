# 05-3-2: RTK Query API集成

## 任务概述
**时间估算**: 35分钟  
**优先级**: 高  
**依赖关系**: 05-3-1-支付状态管理完成  
**技术栈**: React/TypeScript, RTK Query, 数据缓存  

## 详细任务清单

### 1. API基础配置
- [ ] 创建paymentApi基础Query配置
- [ ] 设置baseUrl和请求头处理
- [ ] 配置身份验证token
- [ ] 定义tagTypes缓存标签
- [ ] 实现错误处理机制

### 2. 支付相关Endpoints
- [ ] createPayment创建支付订单mutation
- [ ] getPaymentStatus查询支付状态query
- [ ] cancelPayment取消支付mutation
- [ ] getPaymentMethods获取支付方式query
- [ ] getPaymentRecords获取支付记录query

### 3. 缓存和失效策略
- [ ] 配置providesTags提供缓存标签
- [ ] 配置invalidatesTags失效缓存标签
- [ ] 实现自动重新获取机制
- [ ] 添加轮询查询支持
- [ ] 优化缓存策略

### 4. Hook导出和使用
- [ ] 导出所有生成的hooks
- [ ] 添加类型安全的API调用
- [ ] 实现自动loading状态
- [ ] 提供error处理支持
- [ ] 支持条件查询

## 核心代码实现

### paymentApi.ts
```typescript
// api/paymentApi.ts
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';
import type { RootState } from '@/store';
import type {
  PaymentOrder,
  PaymentStatus,
  PaymentRecord,
  PaymentMethod,
  PaymentDetail,
  PaymentStatistics,
  RefundResult
} from '@/types/payment';

export const paymentApi = createApi({
  reducerPath: 'paymentApi',
  baseQuery: fetchBaseQuery({
    baseUrl: '/api/payment',
    prepareHeaders: (headers, { getState }) => {
      const state = getState() as RootState;
      const token = state.auth.token;
      
      if (token) {
        headers.set('authorization', `Bearer ${token}`);
      }
      
      return headers;
    },
  }),
  tagTypes: ['Payment', 'PaymentRecord', 'PaymentMethods'],
  
  endpoints: (builder) => ({
    // 创建支付订单
    createPayment: builder.mutation<PaymentOrder, {
      orderId: number;
      paymentMethod: string;
      clientType?: string;
    }>({
      query: (data) => ({
        url: '/create',
        method: 'POST',
        body: data,
      }),
      invalidatesTags: ['Payment'],
    }),
    
    // 查询支付状态
    getPaymentStatus: builder.query<PaymentStatus, string>({
      query: (orderSn) => `/status/${orderSn}`,
      providesTags: ['Payment'],
    }),
    
    // 取消支付
    cancelPayment: builder.mutation<string, string>({
      query: (orderSn) => ({
        url: `/cancel/${orderSn}`,
        method: 'POST',
      }),
      invalidatesTags: ['Payment'],
    }),
    
    // 获取支付方式
    getPaymentMethods: builder.query<PaymentMethod[], void>({
      query: () => '/methods',
      providesTags: ['PaymentMethods'],
    }),
    
    // 获取支付记录
    getPaymentRecords: builder.query<{
      list: PaymentRecord[];
      total: number;
      pageNum: number;
      pageSize: number;
    }, {
      pageNum?: number;
      pageSize?: number;
      paymentStatus?: number;
      paymentMethod?: string;
      startDate?: string;
      endDate?: string;
    }>({
      query: (params) => ({
        url: '/record/list',
        params,
      }),
      providesTags: ['PaymentRecord'],
    }),
    
    // 获取支付详情
    getPaymentDetail: builder.query<PaymentDetail, number>({
      query: (paymentId) => `/record/detail/${paymentId}`,
      providesTags: ['PaymentRecord'],
    }),
    
    // 申请退款
    requestRefund: builder.mutation<RefundResult, {
      orderId: number;
      refundAmount: number;
      refundReason: string;
    }>({
      query: (data) => ({
        url: '/refund',
        method: 'POST',
        body: data,
      }),
      invalidatesTags: ['PaymentRecord'],
    }),
    
    // 获取支付统计
    getPaymentStatistics: builder.query<PaymentStatistics, number>({
      query: (days = 30) => ({
        url: '/record/statistics',
        params: { days },
      }),
    }),
  }),
});

export const {
  useCreatePaymentMutation,
  useGetPaymentStatusQuery,
  useLazyGetPaymentStatusQuery,
  useCancelPaymentMutation,
  useGetPaymentMethodsQuery,
  useGetPaymentRecordsQuery,
  useLazyGetPaymentRecordsQuery,
  useGetPaymentDetailQuery,
  useRequestRefundMutation,
  useGetPaymentStatisticsQuery,
} = paymentApi;
```

### 高级查询Hooks
```typescript
// hooks/usePaymentQueries.ts
import { useState, useEffect } from 'react';
import {
  useGetPaymentStatusQuery,
  useGetPaymentRecordsQuery,
  useLazyGetPaymentStatusQuery
} from '@/api/paymentApi';

// 支付状态轮询Hook
export const usePaymentStatusPolling = (
  orderSn: string,
  options: {
    enabled?: boolean;
    pollingInterval?: number;
    onSuccess?: (status: PaymentStatus) => void;
    onFailure?: (status: PaymentStatus) => void;
  } = {}
) => {
  const {
    enabled = true,
    pollingInterval = 2000,
    onSuccess,
    onFailure
  } = options;
  
  const { data, error, isLoading } = useGetPaymentStatusQuery(orderSn, {
    pollingInterval: enabled ? pollingInterval : 0,
    skip: !orderSn || !enabled,
  });
  
  const prevStatusRef = useRef<number>();
  
  useEffect(() => {
    if (data && data.paymentStatus !== prevStatusRef.current) {
      prevStatusRef.current = data.paymentStatus;
      
      if (data.paymentStatus === 1) {
        onSuccess?.(data);
      } else if (data.paymentStatus === 2) {
        onFailure?.(data);
      }
    }
  }, [data, onSuccess, onFailure]);
  
  return { data, error, isLoading };
};

// 支付记录分页Hook
export const usePaymentRecordsPagination = () => {
  const [params, setParams] = useState({
    pageNum: 1,
    pageSize: 10,
    paymentStatus: undefined as number | undefined,
    paymentMethod: undefined as string | undefined,
    startDate: undefined as string | undefined,
    endDate: undefined as string | undefined,
  });
  
  const { data, error, isLoading, refetch } = useGetPaymentRecordsQuery(params);
  
  const updateParams = (newParams: Partial<typeof params>) => {
    setParams(prev => ({ ...prev, ...newParams }));
  };
  
  const resetParams = () => {
    setParams({
      pageNum: 1,
      pageSize: 10,
      paymentStatus: undefined,
      paymentMethod: undefined,
      startDate: undefined,
      endDate: undefined,
    });
  };
  
  return {
    data,
    error,
    isLoading,
    params,
    updateParams,
    resetParams,
    refetch,
  };
};
```

## 验收标准
1. **API配置完整性**: 所有支付相关API endpoint配置完整
2. **缓存策略合理性**: 合理的缓存和失效策略
3. **类型安全性**: 完整的TypeScript类型支持
4. **Hook可用性**: 所有生成的hooks正常工作
5. **性能优化**: 有效的数据缓存和重复请求避免

## 交付物
- [x] paymentApi.ts RTK Query API定义
- [x] usePaymentQueries.ts 高级查询Hooks
- [x] 缓存和失效策略配置
- [x] 类型安全的API调用
- [x] 自动生成的hooks导出

## 技术要点
1. **RTK Query**: 强大的数据获取和缓存解决方案
2. **自动缓存**: 智能的数据缓存和失效机制
3. **类型安全**: 完整的TypeScript支持
4. **自动重试**: 内置的错误重试机制
5. **轮询查询**: 支持实时数据更新

## 测试检查点
- [ ] API基础配置测试
- [ ] 创建支付订单API测试
- [ ] 查询支付状态API测试
- [ ] 支付记录查询API测试
- [ ] 缓存失效机制测试
- [ ] 轮询查询功能测试
- [ ] 错误处理机制测试
- [ ] Hook导出和使用测试

## 下一步
完成后进入 `05-3-3-支付组件开发`，开发支付相关的React组件。