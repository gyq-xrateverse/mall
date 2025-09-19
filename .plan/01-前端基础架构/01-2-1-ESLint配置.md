# 01-2-1: ESLint配置

## 任务概述
**时间**: 第2天上午 (45分钟)  
**目标**: 配置ESLint进行代码质量检查  
**优先级**: 最高  
**依赖**: 01-1-4-TypeScript配置优化完成

## 详细任务清单

### 执行步骤

#### 1. 安装ESLint相关依赖
```bash
npm install --save-dev eslint @eslint/js @types/eslint__js typescript-eslint eslint-plugin-react eslint-plugin-react-hooks eslint-plugin-react-refresh
```

#### 2. 创建ESLint配置文件
创建 `.eslintrc.cjs` 文件：
```javascript
module.exports = {
  root: true,
  env: { 
    browser: true, 
    es2020: true,
    node: true 
  },
  extends: [
    'eslint:recommended',
    '@typescript-eslint/recommended',
    'plugin:react/recommended',
    'plugin:react-hooks/recommended',
    'plugin:react/jsx-runtime',
  ],
  ignorePatterns: ['dist', '.eslintrc.cjs', '*.config.js'],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 'latest',
    sourceType: 'module',
    ecmaFeatures: {
      jsx: true
    }
  },
  plugins: ['react-refresh', '@typescript-eslint', 'react'],
  settings: {
    react: {
      version: 'detect'
    }
  },
  rules: {
    // React相关规则
    'react-refresh/only-export-components': [
      'warn',
      { allowConstantExport: true },
    ],
    'react/prop-types': 'off', // 使用TypeScript进行类型检查
    'react/react-in-jsx-scope': 'off', // React 17+不需要导入React
    
    // TypeScript相关规则
    '@typescript-eslint/no-unused-vars': 'error',
    '@typescript-eslint/no-explicit-any': 'warn',
    '@typescript-eslint/explicit-function-return-type': 'off',
    '@typescript-eslint/explicit-module-boundary-types': 'off',
    '@typescript-eslint/no-non-null-assertion': 'warn',
    
    // Hooks相关规则
    'react-hooks/rules-of-hooks': 'error',
    'react-hooks/exhaustive-deps': 'warn',
    
    // 通用代码质量规则
    'no-console': 'warn',
    'no-debugger': 'error',
    'no-alert': 'warn',
    'no-unused-vars': 'off', // 由@typescript-eslint/no-unused-vars处理
    'prefer-const': 'error',
    'no-var': 'error',
    'eqeqeq': ['error', 'always'],
    'curly': ['error', 'all'],
    
    // 代码风格
    'indent': ['error', 2],
    'quotes': ['error', 'single'],
    'semi': ['error', 'always'],
    'comma-dangle': ['error', 'always-multiline'],
  },
};
```

#### 3. 创建ESLint忽略文件
创建 `.eslintignore` 文件：
```
# 构建产物
dist/
build/

# 依赖
node_modules/

# 配置文件
*.config.js
*.config.ts
.eslintrc.cjs

# 其他
coverage/
*.log
*.lock
```

#### 4. 更新package.json脚本
在package.json中添加lint脚本：
```json
{
  "scripts": {
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "lint:fix": "eslint . --ext ts,tsx --fix",
    "lint:check": "eslint . --ext ts,tsx --report-unused-disable-directives"
  }
}
```

#### 5. 配置VSCode ESLint扩展
创建或更新 `.vscode/settings.json`：
```json
{
  "eslint.enable": true,
  "eslint.format.enable": true,
  "eslint.lintTask.enable": true,
  "eslint.validate": [
    "javascript",
    "javascriptreact",
    "typescript",
    "typescriptreact"
  ],
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  }
}
```

#### 6. 创建ESLint规则说明文档
创建 `docs/eslint-rules.md`：
```markdown
# ESLint规则说明

## React相关规则
- `react-refresh/only-export-components`: 确保React Fast Refresh正常工作
- `react/prop-types`: 关闭，使用TypeScript类型检查
- `react-hooks/rules-of-hooks`: 强制执行Hooks规则

## TypeScript相关规则
- `@typescript-eslint/no-unused-vars`: 禁止未使用的变量
- `@typescript-eslint/no-explicit-any`: 警告使用any类型
- `@typescript-eslint/no-non-null-assertion`: 警告使用非空断言

## 代码质量规则
- `no-console`: 警告使用console
- `no-debugger`: 禁止使用debugger
- `prefer-const`: 优先使用const
- `eqeqeq`: 强制使用严格相等

## 代码风格规则
- `indent`: 2空格缩进
- `quotes`: 单引号
- `semi`: 强制分号
- `comma-dangle`: 多行时末尾逗号
```

## 验收标准
- [x] ESLint能正确检查TypeScript和React代码
- [x] 编辑器实时显示ESLint错误和警告
- [x] 命令行 `npm run lint` 正常工作
- [x] `npm run lint:fix` 能自动修复可修复的问题
- [x] ESLint配置覆盖React、TypeScript、Hooks等规则
- [x] 代码提交前能进行ESLint检查

## 交付物
- [x] `.eslintrc.cjs` - ESLint主配置文件
- [x] `.eslintignore` - ESLint忽略文件
- [x] 更新的package.json脚本
- [x] VSCode ESLint配置
- [x] ESLint规则说明文档

## 技术要点
- **配置分离**: 主配置和忽略文件分离管理
- **规则分级**: 错误、警告、关闭三个级别的规则配置
- **插件支持**: React、TypeScript、Hooks等专用插件
- **编辑器集成**: 与VSCode等编辑器深度集成

## 常见问题解决
- **导入路径错误**: 确保ESLint识别TypeScript路径映射
- **React版本检测**: 设置`react: { version: 'detect' }`
- **Parser错误**: 确保TypeScript parser正确配置
- **规则冲突**: 某些规则可能与Prettier冲突，需要后续调整

## 测试方法
```typescript
// 创建测试文件 src/test-eslint.tsx
import React from 'react';

// 测试未使用变量（应该报错）
const unusedVar = 'test';

// 测试console使用（应该警告）
console.log('test');

// 测试any类型（应该警告）
const anyVar: any = 'test';

// 测试组件定义
const TestComponent: React.FC = () => {
  return <div>Test</div>;
};

export default TestComponent;
```

运行 `npm run lint` 查看ESLint检查结果。

## 下一步
完成后进入 `01-2-2-Prettier配置`