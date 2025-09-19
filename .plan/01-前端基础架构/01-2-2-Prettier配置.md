# 01-2-2: Prettier配置

## 任务概述
**时间**: 第2天上午 (30分钟)  
**目标**: 配置Prettier实现代码自动格式化  
**优先级**: 高  
**依赖**: 01-2-1-ESLint配置完成

## 详细任务清单

### 执行步骤

#### 1. 安装Prettier相关依赖
```bash
npm install --save-dev prettier eslint-config-prettier eslint-plugin-prettier
```

#### 2. 创建Prettier配置文件
创建 `.prettierrc` 文件：
```json
{
  "printWidth": 80,
  "tabWidth": 2,
  "useTabs": false,
  "semi": true,
  "singleQuote": true,
  "quoteProps": "as-needed",
  "jsxSingleQuote": true,
  "trailingComma": "es5",
  "bracketSpacing": true,
  "bracketSameLine": false,
  "arrowParens": "avoid",
  "rangeStart": 0,
  "rangeEnd": "Infinity",
  "requirePragma": false,
  "insertPragma": false,
  "proseWrap": "preserve",
  "htmlWhitespaceSensitivity": "css",
  "endOfLine": "lf",
  "embeddedLanguageFormatting": "auto"
}
```

#### 3. 创建Prettier忽略文件
创建 `.prettierignore` 文件：
```
# 构建产物
dist/
build/

# 依赖
node_modules/

# 自动生成的文件
coverage/
*.log

# 特殊格式文件
*.md
*.yml
*.yaml
package.json
package-lock.json

# 配置文件
.eslintrc.cjs
*.config.js
*.config.ts
```

#### 4. 集成ESLint和Prettier
更新 `.eslintrc.cjs` 的extends配置：
```javascript
module.exports = {
  // ... 现有配置
  extends: [
    'eslint:recommended',
    '@typescript-eslint/recommended',
    'plugin:react/recommended',
    'plugin:react-hooks/recommended',
    'plugin:react/jsx-runtime',
    'plugin:prettier/recommended', // 添加这一行，必须放在最后
  ],
  // ... 其他配置
};
```

#### 5. 更新package.json脚本
在package.json中添加格式化脚本：
```json
{
  "scripts": {
    "format": "prettier --write .",
    "format:check": "prettier --check .",
    "format:ts": "prettier --write \"src/**/*.{ts,tsx}\"",
    "format:js": "prettier --write \"src/**/*.{js,jsx}\"",
    "format:css": "prettier --write \"src/**/*.{css,scss,less}\"",
    "format:json": "prettier --write \"*.json\""
  }
}
```

#### 6. 配置VSCode Prettier扩展
更新 `.vscode/settings.json`：
```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "prettier.enable": true,
  "prettier.requireConfig": true,
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[typescriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[javascript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[javascriptreact]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[json]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "[css]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode",
    "editor.formatOnSave": true
  },
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  }
}
```

#### 7. 解决ESLint与Prettier冲突
创建 `docs/prettier-eslint-integration.md`：
```markdown
# Prettier与ESLint集成说明

## 配置原则
1. Prettier负责代码格式化（空格、换行、引号等）
2. ESLint负责代码质量检查（未使用变量、类型错误等）
3. 使用eslint-config-prettier禁用ESLint中与Prettier冲突的格式化规则
4. 使用eslint-plugin-prettier在ESLint中运行Prettier

## 可能的冲突规则
- `indent` vs Prettier的tabWidth
- `quotes` vs Prettier的singleQuote  
- `semi` vs Prettier的semi
- `comma-dangle` vs Prettier的trailingComma

## 解决方案
在ESLint配置中移除与Prettier冲突的格式化规则，保留代码质量规则。
```

#### 8. 创建格式化测试文件
创建 `src/test-prettier.tsx` 用于测试格式化：
```typescript
// 格式化前的代码（故意写得不规范）
import React    from "react";
import {useState} from 'react'


const TestComponent:React.FC=()=>{
const [count,setCount]=useState<number>(0)


const handleClick=()=>
{
setCount(prev=>prev+1)
}

return(<div onClick={handleClick}>
Count: {count}
</div>)
}


export default TestComponent
```

## 验收标准
- [x] Prettier自动格式化代码
- [x] ESLint和Prettier规则不冲突
- [x] 编辑器保存时自动格式化
- [x] `npm run format` 命令正常工作
- [x] 格式化配置在团队中保持一致
- [x] 支持TypeScript、React、CSS等文件格式化

## 交付物
- [x] `.prettierrc` - Prettier主配置文件
- [x] `.prettierignore` - Prettier忽略文件
- [x] 更新的ESLint配置（集成Prettier）
- [x] 更新的package.json格式化脚本
- [x] VSCode Prettier配置
- [x] Prettier与ESLint集成说明文档

## 技术要点
- **配置优先级**: .prettierrc > package.json中的prettier字段
- **集成策略**: 通过eslint-plugin-prettier在ESLint中运行Prettier
- **冲突解决**: 使用eslint-config-prettier禁用冲突的ESLint规则
- **编辑器支持**: 配置VSCode自动格式化和保存时格式化

## Prettier配置详解
- `printWidth: 80`: 每行最大长度
- `tabWidth: 2`: 缩进宽度
- `singleQuote: true`: 使用单引号
- `trailingComma: "es5"`: ES5中支持的位置添加尾随逗号
- `bracketSpacing: true`: 对象字面量的大括号间添加空格
- `arrowParens: "avoid"`: 箭头函数参数尽量不使用括号
- `endOfLine: "lf"`: 使用LF换行符

## 测试方法
1. 创建格式化测试文件（不规范的代码格式）
2. 运行 `npm run format` 进行格式化
3. 检查代码是否按照配置进行了格式化
4. 在编辑器中保存文件，验证自动格式化功能

```bash
# 测试命令
npm run format:check  # 检查格式化状态
npm run format       # 格式化所有文件
```

## 下一步
完成后进入 `01-2-3-Husky和lint-staged配置`