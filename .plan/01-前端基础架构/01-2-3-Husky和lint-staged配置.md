# 01-2-3: Husky和lint-staged配置

## 任务概述
**时间**: 第2天上午 (30分钟)  
**目标**: 配置Git hooks实现提交前代码检查  
**优先级**: 高  
**依赖**: 01-2-2-Prettier配置完成

## 详细任务清单

### 执行步骤

#### 1. 安装Husky和lint-staged
```bash
npm install --save-dev husky lint-staged
```

#### 2. 初始化Husky
```bash
npx husky install
```

#### 3. 配置package.json
在package.json中添加以下配置：
```json
{
  "scripts": {
    "prepare": "husky install",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "lint:fix": "eslint . --ext ts,tsx --fix",
    "format": "prettier --write .",
    "type-check": "tsc --noEmit"
  },
  "lint-staged": {
    "*.{ts,tsx}": [
      "eslint --fix",
      "prettier --write",
      "bash -c 'tsc --noEmit'"
    ],
    "*.{js,jsx}": [
      "eslint --fix",
      "prettier --write"
    ],
    "*.{json,css,scss,less,md}": [
      "prettier --write"
    ]
  }
}
```

#### 4. 创建pre-commit钩子
```bash
npx husky add .husky/pre-commit "npx lint-staged"
```

#### 5. 创建commit-msg钩子（为后续commitlint准备）
```bash
npx husky add .husky/commit-msg "npx --no -- commitlint --edit \$1"
```

#### 6. 配置Git钩子文件权限
确保钩子文件有执行权限：
```bash
chmod +x .husky/pre-commit
chmod +x .husky/commit-msg
```

#### 7. 创建husky配置说明文档
创建 `docs/git-hooks.md`：
```markdown
# Git Hooks配置说明

## 已配置的钩子

### pre-commit
- 运行lint-staged
- 对暂存文件进行ESLint检查和修复
- 对暂存文件进行Prettier格式化
- 对TypeScript文件进行类型检查

### commit-msg
- 运行commitlint检查提交信息格式
- 确保提交信息符合约定式提交规范

## lint-staged配置

### TypeScript/React文件 (*.ts, *.tsx)
1. ESLint检查和自动修复
2. Prettier格式化
3. TypeScript类型检查

### JavaScript文件 (*.js, *.jsx)
1. ESLint检查和自动修复
2. Prettier格式化

### 其他文件 (*.json, *.css, *.scss, *.less, *.md)
1. Prettier格式化

## 跳过钩子
如果需要跳过钩子（紧急情况下）：
```bash
git commit --no-verify -m "emergency fix"
```

## 故障排除
- 如果钩子不执行，检查文件权限
- 如果lint-staged失败，手动运行相应命令调试
- 确保所有依赖都已正确安装
```

#### 8. 测试钩子配置
创建测试文件 `src/test-hooks.tsx`：
```typescript
// 故意写一些不规范的代码来测试钩子
import   React from "react"
import{useState}from 'react'

const TestHooks=()=>{
const [value,setValue]=useState("")
const unusedVar = 'test'

console.log("testing hooks")

return<div>{value}</div>
}

export default TestHooks
```

然后尝试提交：
```bash
git add src/test-hooks.tsx
git commit -m "test hooks"
```

## 验收标准
- [x] Husky成功安装并初始化
- [x] pre-commit钩子正常工作
- [x] Git提交前自动运行代码检查
- [x] 代码格式问题自动修复
- [x] 类型错误阻止提交
- [x] 不符合规范的代码无法提交
- [x] lint-staged只处理暂存文件，提高效率

## 交付物
- [x] 配置好的Husky
- [x] `.husky/pre-commit` 钩子文件
- [x] `.husky/commit-msg` 钩子文件
- [x] 配置的lint-staged规则
- [x] Git钩子说明文档

## 技术要点
- **暂存文件处理**: lint-staged只处理git add的文件，提高性能
- **钩子链**: 多个工具按顺序执行，任一失败都会阻止提交
- **类型检查**: 在提交前进行TypeScript类型检查
- **自动修复**: 能自动修复的问题会被自动处理

## 常见问题
- **权限问题**: 确保钩子文件有执行权限
- **路径问题**: 在项目根目录执行husky命令
- **依赖问题**: 确保所有工具都已正确安装
- **性能问题**: lint-staged只处理暂存文件，避免全项目检查

## 工作流程
1. 开发者修改代码
2. `git add` 添加到暂存区
3. `git commit` 触发pre-commit钩子
4. lint-staged处理暂存文件
5. ESLint检查和修复
6. Prettier格式化
7. TypeScript类型检查
8. 所有检查通过后，提交成功

## 下一步
完成后进入 `01-2-4-Commitizen和Commitlint配置`