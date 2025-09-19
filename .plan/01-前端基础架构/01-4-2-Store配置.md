# 01-4-2-Store配置

## 任务概述
- **时间估算**: 45分钟
- **优先级**: 高
- **依赖关系**: 
  - 依赖：01-4-1-安装Redux Toolkit（依赖包安装）
  - 前置：Redux Toolkit基础依赖完成
- **执行阶段**: 第一阶段-前端基础架构

## 详细任务清单

### 4.2.1 创建Store核心配置
- [ ] 配置configureStore
- [ ] 设置中间件配置
- [ ] 添加开发工具集成
- [ ] 配置类型推导

### 4.2.2 设置中间件系统
- [ ] 配置默认中间件
- [ ] 添加自定义中间件
- [ ] 设置错误处理中间件
- [ ] 配置日志中间件

### 4.2.3 配置持久化存储
- [ ] 集成redux-persist
- [ ] 配置存储策略
- [ ] 设置持久化白名单
- [ ] 添加数据迁移逻辑

### 4.2.4 优化Store性能
- [ ] 配置序列化检查
- [ ] 设置不可变性检查
- [ ] 优化开发工具性能
- [ ] 配置懒加载支持

## 验收标准

### 功能验收
- [ ] Store正常创建和运行
- [ ] 中间件配置生效
- [ ] 持久化存储功能正常
- [ ] 类型推导准确完整

### 代码质量验收
- [ ] Store配置代码结构清晰
- [ ] 中间件配置合理
- [ ] TypeScript类型定义完整
- [ ] 错误处理机制完善

### 性能验收
- [ ] 开发环境性能良好
- [ ] 生产环境配置优化
- [ ] 内存使用合理
- [ ] 序列化性能优化

## 交付物

### 1. Store配置文件
```
src/store/
├── index.ts              # 主Store配置
├── rootReducer.ts        # 根Reducer配置
├── persistConfig.ts      # 持久化配置
└── middleware.ts         # 中间件配置
```

### 2. 开发工具配置
```
src/store/devtools/
├── devtools.ts           # DevTools配置
└── enhancers.ts          # Store增强器
```

### 3. 工具函数
```
src/store/utils/
├── preloadedState.ts     # 初始状态工具
└── storeUtils.ts         # Store工具函数
```

## 技术要点

### 主Store配置
```typescript
// src/store/index.ts
import { configureStore, combineReducers } from '@reduxjs/toolkit';
import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from 'redux-persist';
import storage from 'redux-persist/lib/storage';

import { authSlice } from './slices/authSlice';
import { uiSlice } from './slices/uiSlice';
import { customMiddleware } from './middleware';
import { setupDevtools } from './devtools';

// 根Reducer配置
const rootReducer = combineReducers({
  auth: authSlice.reducer,
  ui: uiSlice.reducer,
});

// 持久化配置
const persistConfig = {
  key: 'root',
  storage,
  // 指定要持久化的reducer
  whitelist: ['auth', 'ui'],
  // 指定不持久化的reducer
  blacklist: [],
  // 版本控制
  version: 1,
  // 数据迁移
  migrate: (state: any, version: number) => {
    if (version === 0) {
      // 迁移逻辑
      return {
        ...state,
        // 数据转换
      };
    }
    return state;
  },
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

// Store配置
export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
        // 忽略特定的路径
        ignoredPaths: ['register', 'rehydrate'],
      },
      // 不可变性检查配置
      immutableCheck: {
        ignoredPaths: ['register', 'rehydrate'],
      },
    }).concat(customMiddleware),
  
  // 开发工具配置
  devTools: process.env.NODE_ENV !== 'production' && setupDevtools(),
  
  // 预加载状态
  preloadedState: undefined,
  
  // Store增强器
  enhancers: (defaultEnhancers) => [
    ...defaultEnhancers,
    // 自定义增强器
  ],
});

// 创建持久化store
export const persistor = persistStore(store);

// 类型定义
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// 开发环境热模块替换
if (process.env.NODE_ENV === 'development' && module.hot) {
  module.hot.accept('./slices', () => {
    store.replaceReducer(persistedReducer);
  });
}
```

### 中间件配置
```typescript
// src/store/middleware.ts
import { Middleware, isRejectedWithValue } from '@reduxjs/toolkit';
import { message } from 'antd';
import { RootState } from './index';

// 错误处理中间件
export const errorMiddleware: Middleware<{}, RootState> = (api) => (next) => (action) => {
  if (isRejectedWithValue(action)) {
    const error = action.payload;
    
    // 处理不同类型的错误
    if (error?.status === 401) {
      // 处理认证错误
      message.error('认证失败，请重新登录');
      // 可以派发登出action
      // api.dispatch(authSlice.actions.logout());
    } else if (error?.status >= 500) {
      // 处理服务器错误
      message.error('服务器错误，请稍后再试');
    } else if (error?.message) {
      // 显示具体错误信息
      message.error(error.message);
    }
    
    // 记录错误日志
    console.error('Redux Error:', action);
  }
  
  return next(action);
};

// 日志中间件
export const loggerMiddleware: Middleware<{}, RootState> = (api) => (next) => (action) => {
  if (process.env.NODE_ENV === 'development') {
    const startTime = Date.now();
    const result = next(action);
    const endTime = Date.now();
    
    console.group(`🎯 Action: ${action.type}`);
    console.log('⏰ Duration:', `${endTime - startTime}ms`);
    console.log('📤 Payload:', action.payload);
    console.log('📊 New State:', api.getState());
    console.groupEnd();
    
    return result;
  }
  return next(action);
};

// API调用中间件
export const apiMiddleware: Middleware<{}, RootState> = (api) => (next) => (action) => {
  // 在这里可以拦截API相关的action
  if (action.type.endsWith('/pending')) {
    console.log('🔄 API Request started:', action.type);
  } else if (action.type.endsWith('/fulfilled')) {
    console.log('✅ API Request succeeded:', action.type);
  } else if (action.type.endsWith('/rejected')) {
    console.log('❌ API Request failed:', action.type);
  }
  
  return next(action);
};

// 组合中间件
export const customMiddleware = [
  errorMiddleware,
  loggerMiddleware,
  apiMiddleware,
];
```

### 开发工具配置
```typescript
// src/store/devtools.ts
interface DevToolsOptions {
  name?: string;
  trace?: boolean;
  traceLimit?: number;
  actionSanitizer?: (action: any) => any;
  stateSanitizer?: (state: any) => any;
  actionsBlacklist?: string[];
  actionsWhitelist?: string[];
  predicate?: (state: any, action: any) => boolean;
  shouldRecordChanges?: boolean;
  pauseActionType?: string;
  autoPause?: boolean;
  shouldStartLocked?: boolean;
  shouldHotReload?: boolean;
  shouldCatchErrors?: boolean;
  features?: {
    pause?: boolean;
    lock?: boolean;
    persist?: boolean;
    export?: boolean;
    import?: string;
    jump?: boolean;
    skip?: boolean;
    reorder?: boolean;
    dispatch?: boolean;
    test?: boolean;
  };
}

export const setupDevtools = (): DevToolsOptions => {
  return {
    name: 'BeiLv Agent',
    trace: true,
    traceLimit: 25,
    
    // Action清理函数
    actionSanitizer: (action) => ({
      ...action,
      // 清理敏感信息
      payload: action.type.includes('password') || action.type.includes('token')
        ? '***SANITIZED***'
        : action.payload,
    }),
    
    // State清理函数
    stateSanitizer: (state) => ({
      ...state,
      auth: {
        ...state.auth,
        // 隐藏敏感信息
        token: state.auth?.token ? '***TOKEN***' : null,
        refreshToken: state.auth?.refreshToken ? '***REFRESH_TOKEN***' : null,
      },
    }),
    
    // 黑名单action（不记录）
    actionsBlacklist: [
      'persist/PERSIST',
      'persist/REHYDRATE',
    ],
    
    // 功能配置
    features: {
      pause: true,
      lock: true,
      persist: true,
      export: true,
      import: 'custom',
      jump: true,
      skip: true,
      reorder: true,
      dispatch: true,
      test: true,
    },
    
    // 其他配置
    shouldRecordChanges: true,
    shouldHotReload: true,
    shouldCatchErrors: true,
    shouldStartLocked: false,
    autoPause: false,
  };
};
```

### 持久化配置
```typescript
// src/store/persistConfig.ts
import storage from 'redux-persist/lib/storage';
import { Transform } from 'redux-persist';
import { RootState } from './index';

// 自定义转换器，用于数据处理
const authTransform: Transform<any, any> = {
  in: (inboundState, key) => {
    // 存储时的数据转换
    return {
      ...inboundState,
      // 可以在这里加密敏感信息
      token: inboundState.token ? btoa(inboundState.token) : null,
    };
  },
  out: (outboundState, key) => {
    // 读取时的数据转换
    return {
      ...outboundState,
      // 可以在这里解密敏感信息
      token: outboundState.token ? atob(outboundState.token) : null,
    };
  },
};

export const persistConfig = {
  key: 'beilv-agent',
  storage,
  version: 1,
  
  // 持久化配置
  whitelist: ['auth', 'ui'],
  blacklist: ['api'],
  
  // 转换器
  transforms: [authTransform],
  
  // 数据迁移
  migrate: (state: any, version: number) => {
    if (version < 1) {
      // 版本0到版本1的迁移
      return {
        ...state,
        ui: {
          ...state.ui,
          theme: state.ui?.darkMode ? 'dark' : 'light',
        },
      };
    }
    return state;
  },
  
  // 调试选项
  debug: process.env.NODE_ENV === 'development',
  
  // 序列化检查
  serialize: true,
  
  // 超时配置
  timeout: 0,
  
  // 写入延迟
  writeFailHandler: (error) => {
    console.error('Redux Persist Write Error:', error);
  },
};

// 特定slice的持久化配置
export const authPersistConfig = {
  key: 'auth',
  storage,
  blacklist: ['loading', 'error'],
};

export const uiPersistConfig = {
  key: 'ui',
  storage,
  whitelist: ['theme', 'language', 'sidebarCollapsed'],
};
```

### Store工具函数
```typescript
// src/store/utils/storeUtils.ts
import { store, RootState } from '../index';

// 获取当前状态
export const getCurrentState = (): RootState => store.getState();

// 安全的状态选择器
export const selectSafely = <T>(
  selector: (state: RootState) => T,
  fallback: T
): T => {
  try {
    return selector(getCurrentState());
  } catch (error) {
    console.warn('State selection failed:', error);
    return fallback;
  }
};

// 状态重置工具
export const resetStore = () => {
  // 可以派发重置action
  // store.dispatch({ type: 'RESET_STORE' });
};

// Store健康检查
export const checkStoreHealth = (): boolean => {
  try {
    const state = getCurrentState();
    return typeof state === 'object' && state !== null;
  } catch {
    return false;
  }
};
```

## 下一步
- **后续任务**: 01-4-3-Auth状态管理
- **关联任务**: 基于Store配置实现认证状态管理
- **注意事项**: 
  - Store配置要考虑性能影响
  - 持久化配置要注意安全性
  - 中间件顺序很重要

## 常见问题解决

### Q1: 持久化不工作
- 检查persistConfig配置是否正确
- 确认storage是否可用
- 验证PersistGate组件是否正确使用

### Q2: DevTools连接失败
- 确认浏览器扩展安装状态
- 检查devTools配置选项
- 验证开发环境变量设置

### Q3: 中间件执行顺序问题
- 确认中间件数组顺序
- 检查中间件内部逻辑
- 验证action处理流程

### Q4: 类型推导不准确
- 检查RootState类型定义
- 确认reducer类型正确
- 验证TypeScript配置