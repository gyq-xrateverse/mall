# 01-1-4: TypeScript配置优化

## 任务概述
**时间**: 第1天上午 (30分钟)  
**目标**: 优化TypeScript配置，确保类型安全和开发体验  
**优先级**: 高  
**依赖**: 01-1-3-Vite配置优化完成

## 详细任务清单

### 执行步骤

#### 1. 更新tsconfig.json主配置
```json
{
  "compilerOptions": {
    // 编译目标和模块设置
    "target": "ES2020",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "moduleResolution": "bundler",
    
    // 严格类型检查
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "noImplicitReturns": true,
    "noImplicitOverride": true,
    "exactOptionalPropertyTypes": true,
    
    // 模块解析
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "useDefineForClassFields": true,
    "skipLibCheck": true,
    
    // JSX设置
    "jsx": "react-jsx",
    "jsxImportSource": "react",
    
    // 输出设置
    "noEmit": true,
    "declaration": false,
    
    // 路径映射 - 与vite.config.ts保持一致
    "baseUrl": "./src",
    "paths": {
      "@/*": ["./*"],
      "@components/*": ["./components/*"],
      "@pages/*": ["./pages/*"],
      "@utils/*": ["./utils/*"],
      "@api/*": ["./api/*"],
      "@store/*": ["./store/*"],
      "@hooks/*": ["./hooks/*"],
      "@types/*": ["./types/*"],
      "@assets/*": ["./assets/*"],
      "@styles/*": ["./styles/*"]
    },
    
    // 类型声明
    "types": ["vite/client", "node"]
  },
  "include": [
    "src/**/*",
    "src/**/*.tsx",
    "src/**/*.ts",
    "vite.config.ts"
  ],
  "exclude": [
    "node_modules",
    "dist",
    "build"
  ],
  "references": [
    { "path": "./tsconfig.node.json" }
  ]
}
```

#### 2. 创建tsconfig.node.json
```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "types": ["node"]
  },
  "include": [
    "vite.config.ts",
    "vite.config.*.ts"
  ]
}
```

#### 3. 创建全局类型声明文件
创建 `src/types/global.d.ts` 文件：
```typescript
// 全局类型声明

// 常用的React组件Props类型
declare global {
  namespace React {
    interface BaseProps {
      className?: string;
      style?: React.CSSProperties;
      children?: React.ReactNode;
    }
  }
}

// API响应基础结构
declare global {
  interface ApiResponse<T = any> {
    code: number;
    message: string;
    data: T;
    timestamp: number;
  }
  
  interface PaginationResponse<T = any> {
    list: T[];
    total: number;
    page: number;
    size: number;
  }
}

// 用户相关类型
declare global {
  interface User {
    id: number;
    email: string;
    username: string;
    avatar?: string;
    registerType: 'email' | 'wechat' | 'google';
    emailVerified: boolean;
    accountStatus: 'normal' | 'frozen' | 'disabled';
    createdAt: string;
    updatedAt: string;
  }
  
  interface LoginCredentials {
    email: string;
    password: string;
    verificationCode?: string;
  }
  
  interface RegisterData {
    email: string;
    password: string;
    confirmPassword: string;
    verificationCode: string;
    username?: string;
  }
}

export {};
```

#### 4. 创建工具类型文件
创建 `src/types/utils.ts` 文件：
```typescript
// 实用工具类型

/**
 * 使某些属性可选
 */
export type PartialBy<T, K extends keyof T> = Omit<T, K> & Partial<Pick<T, K>>;

/**
 * 使某些属性必需
 */
export type RequiredBy<T, K extends keyof T> = T & Required<Pick<T, K>>;

/**
 * 深度可选
 */
export type DeepPartial<T> = {
  [P in keyof T]?: T[P] extends object ? DeepPartial<T[P]> : T[P];
};

/**
 * 深度必需
 */
export type DeepRequired<T> = {
  [P in keyof T]-?: T[P] extends object ? DeepRequired<T[P]> : T[P];
};

/**
 * 从联合类型中排除null和undefined
 */
export type NonNullable<T> = T extends null | undefined ? never : T;

/**
 * 函数类型工具
 */
export type AsyncFunction<T extends any[] = any[], R = any> = (...args: T) => Promise<R>;
export type VoidFunction<T extends any[] = any[]> = (...args: T) => void;

/**
 * 事件处理器类型
 */
export type EventHandler<T = Element> = (event: React.SyntheticEvent<T>) => void;
export type ChangeHandler<T = HTMLInputElement> = (event: React.ChangeEvent<T>) => void;
export type ClickHandler<T = HTMLElement> = (event: React.MouseEvent<T>) => void;

/**
 * 组件Props类型推导
 */
export type ComponentProps<T> = T extends React.ComponentType<infer P> 
  ? P 
  : T extends React.Component<infer P> 
  ? P 
  : never;

/**
 * 状态更新类型
 */
export type SetState<T> = React.Dispatch<React.SetStateAction<T>>;

/**
 * 键值对类型
 */
export type KeyValuePair<K extends string | number | symbol = string, V = any> = {
  [key in K]: V;
};
```

#### 5. 更新IDE配置
创建 `.vscode/settings.json` 文件（如果使用VSCode）：
```json
{
  "typescript.preferences.includePackageJsonAutoImports": "on",
  "typescript.suggest.autoImports": true,
  "typescript.updateImportsOnFileMove.enabled": "always",
  "editor.codeActionsOnSave": {
    "source.fixAll": true,
    "source.organizeImports": true
  },
  "files.associations": {
    "*.ts": "typescript",
    "*.tsx": "typescriptreact"
  }
}
```

## 验收标准
- [x] TypeScript配置文件语法正确
- [x] 路径映射与Vite配置完全一致
- [x] 严格类型检查全部启用
- [x] 编辑器智能提示和自动完成正常工作
- [x] 类型错误能正确显示和定位
- [x] 全局类型声明生效
- [x] 工具类型可正常使用
- [x] 编译和构建无类型错误

## 交付物
- [x] 优化的`tsconfig.json`主配置
- [x] `tsconfig.node.json`节点配置
- [x] `src/types/global.d.ts`全局类型声明
- [x] `src/types/utils.ts`工具类型
- [x] `.vscode/settings.json`IDE配置

## 技术要点
- **严格类型检查**: 启用所有严格模式选项，确保类型安全
- **路径映射**: 与Vite配置保持一致，支持路径别名
- **全局类型**: 声明项目中常用的全局类型
- **工具类型**: 提供常用的类型工具，提高开发效率
- **IDE支持**: 配置编辑器以获得最佳TypeScript体验

## 类型安全最佳实践
- **避免any**: 尽量使用具体类型而不是any
- **严格null检查**: 明确处理null和undefined
- **接口定义**: 为对象定义清晰的接口
- **泛型使用**: 合理使用泛型提高代码复用性
- **类型断言**: 谨慎使用类型断言，优先使用类型守卫

## 测试方法
```typescript
// 测试全局类型
const user: User = {
  id: 1,
  email: 'test@example.com',
  username: 'test',
  registerType: 'email',
  emailVerified: true,
  accountStatus: 'normal',
  createdAt: '2023-01-01',
  updatedAt: '2023-01-01'
};

// 测试工具类型
import type { PartialBy, EventHandler } from '@types/utils';

type OptionalUser = PartialBy<User, 'avatar' | 'username'>;
const handleClick: EventHandler = (e) => console.log(e);
```

## 下一步
完成后进入 `01-2-1-ESLint配置`