# 01-4-1-安装Redux Toolkit

## 任务概述
- **时间估算**: 30分钟
- **优先级**: 高
- **依赖关系**: 
  - 依赖：01-1-1-创建Vite项目（项目基础）
  - 依赖：01-1-4-TypeScript配置优化（类型支持）
  - 前置：项目基础结构完成
- **执行阶段**: 第一阶段-前端基础架构

## 详细任务清单

### 4.1.1 安装Redux Toolkit相关包
- [ ] 安装@reduxjs/toolkit核心包
- [ ] 安装react-redux绑定包
- [ ] 安装TypeScript类型定义
- [ ] 验证依赖包安装成功

### 4.1.2 配置开发工具支持
- [ ] 安装Redux DevTools扩展支持
- [ ] 配置开发环境调试工具
- [ ] 设置类型检查支持
- [ ] 添加性能监控工具

### 4.1.3 创建基础目录结构
- [ ] 创建store目录结构
- [ ] 创建slices目录
- [ ] 创建hooks目录
- [ ] 创建types目录

### 4.1.4 配置TypeScript集成
- [ ] 更新tsconfig.json支持Redux
- [ ] 配置类型推导
- [ ] 设置严格类型检查
- [ ] 添加路径别名支持

## 验收标准

### 功能验收
- [ ] Redux Toolkit成功安装
- [ ] React Redux正常集成
- [ ] TypeScript类型支持完整
- [ ] 开发工具正常连接

### 代码质量验收
- [ ] 目录结构清晰合理
- [ ] TypeScript配置正确
- [ ] 依赖版本兼容性良好
- [ ] 没有类型错误

### 开发体验验收
- [ ] Redux DevTools正常工作
- [ ] 代码自动完成功能正常
- [ ] 类型提示准确完整
- [ ] 热更新支持Redux状态

## 交付物

### 1. 安装记录文件
```
package.json - 更新的依赖配置
pnpm-lock.yaml - 锁定的依赖版本
```

### 2. 基础目录结构
```
src/store/
├── index.ts              # Store配置入口
├── hooks.ts              # 类型化的hooks
├── slices/               # 状态切片目录
│   └── index.ts          # 切片导出文件
├── middleware/           # 自定义中间件
│   └── index.ts          # 中间件配置
└── types.ts              # Store相关类型
```

### 3. 配置文件更新
```
tsconfig.json - 添加Redux相关配置
vite.config.ts - 开发工具配置（如需要）
```

## 技术要点

### 安装命令
```bash
# 核心依赖
pnpm add @reduxjs/toolkit react-redux

# 开发依赖（TypeScript支持）
pnpm add -D @types/react-redux

# 可选：Redux persist（数据持久化）
pnpm add redux-persist

# 可选：Reselect（选择器优化）
pnpm add reselect
```

### Store基础配置
```typescript
// src/store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';

export const store = configureStore({
  reducer: {
    // 这里会添加各个slice
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // 忽略这些 action types
        ignoredActions: [
          'persist/PERSIST',
          'persist/REHYDRATE',
          'persist/PAUSE',
          'persist/PURGE',
          'persist/REGISTER',
        ],
      },
    }),
  devTools: process.env.NODE_ENV !== 'production',
});

// 设置监听器，用于 RTK Query
setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

### 类型化的Hooks
```typescript
// src/store/hooks.ts
import { useDispatch, useSelector, type TypedUseSelectorHook } from 'react-redux';
import type { RootState, AppDispatch } from './index';

// 类型化的useDispatch hook
export const useAppDispatch = () => useDispatch<AppDispatch>();

// 类型化的useSelector hook
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;

// 批量导出，方便使用
export { useAppDispatch as useDispatch, useAppSelector as useSelector };
```

### TypeScript配置更新
```json
// tsconfig.json (部分配置)
{
  "compilerOptions": {
    // ... 其他配置
    "paths": {
      "@/*": ["src/*"],
      "@/store/*": ["src/store/*"],
      "@/slices/*": ["src/store/slices/*"]
    },
    // 启用严格模式以获得更好的类型检查
    "strict": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true
  }
}
```

### Store类型定义
```typescript
// src/store/types.ts
import { Action, ThunkAction } from '@reduxjs/toolkit';
import { RootState } from './index';

// 异步action类型
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  RootState,
  unknown,
  Action<string>
>;

// 基础状态接口
export interface BaseState {
  loading: boolean;
  error: string | null;
}

// API状态接口
export interface ApiState extends BaseState {
  lastFetch: number | null;
}

// 分页状态接口
export interface PaginationState {
  current: number;
  pageSize: number;
  total: number;
}
```

### 基础中间件配置
```typescript
// src/store/middleware/index.ts
import { Middleware } from '@reduxjs/toolkit';
import { RootState } from '../index';

// 日志中间件（仅开发环境）
export const loggerMiddleware: Middleware<{}, RootState> = (store) => (next) => (action) => {
  if (process.env.NODE_ENV === 'development') {
    console.group(`Action: ${action.type}`);
    console.log('Previous State:', store.getState());
    console.log('Action:', action);
    const result = next(action);
    console.log('Next State:', store.getState());
    console.groupEnd();
    return result;
  }
  return next(action);
};

// 错误处理中间件
export const errorMiddleware: Middleware<{}, RootState> = () => (next) => (action) => {
  try {
    return next(action);
  } catch (error) {
    console.error('Redux Error:', error);
    // 这里可以添加错误上报逻辑
    throw error;
  }
};
```

### 开发工具配置
```typescript
// vite.config.ts 添加Redux DevTools配置
import { defineConfig } from 'vite';

export default defineConfig({
  define: {
    // 确保Redux DevTools在开发环境可用
    __REDUX_DEVTOOLS_EXTENSION__: 'undefined',
  },
  // ... 其他配置
});
```

### 基础切片模板
```typescript
// src/store/slices/template.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { BaseState } from '../types';

interface TemplateState extends BaseState {
  // 具体状态定义
}

const initialState: TemplateState = {
  loading: false,
  error: null,
  // 具体初始状态
};

const templateSlice = createSlice({
  name: 'template',
  initialState,
  reducers: {
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    reset: () => initialState,
  },
});

export const { setLoading, setError, reset } = templateSlice.actions;
export default templateSlice.reducer;
```

## 下一步
- **后续任务**: 01-4-2-Store配置
- **关联任务**: 基于安装的依赖配置完整的Store
- **注意事项**: 
  - 确保版本兼容性，特别是React和Redux的版本
  - TypeScript严格模式可能需要额外的类型处理
  - 开发工具配置要区分开发和生产环境

## 常见问题解决

### Q1: TypeScript类型错误
- 检查@types/react-redux版本兼容性
- 确认tsconfig.json配置正确
- 验证Redux Toolkit版本与React版本匹配

### Q2: Redux DevTools不工作
- 确认浏览器安装了Redux DevTools扩展
- 检查store配置中devTools选项
- 验证开发环境变量设置

### Q3: 依赖版本冲突
- 使用pnpm ls检查依赖树
- 查看官方兼容性文档
- 必要时使用resolutions锁定版本

### Q4: 路径别名不生效
- 确认tsconfig.json中paths配置
- 检查vite.config.ts中alias配置
- 重启开发服务器使配置生效