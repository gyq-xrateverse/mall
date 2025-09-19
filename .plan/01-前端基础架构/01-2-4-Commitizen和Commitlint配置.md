# 01-2-4: Commitizen和Commitlint配置

## 任务概述
**时间**: 第2天上午 (30分钟)  
**目标**: 配置规范化的Git提交信息  
**优先级**: 中  
**依赖**: 01-2-3-Husky和lint-staged配置完成

## 详细任务清单

### 执行步骤

#### 1. 安装相关依赖
```bash
npm install --save-dev @commitlint/config-conventional @commitlint/cli commitizen cz-conventional-changelog
```

#### 2. 创建commitlint配置
创建 `commitlint.config.js` 文件：
```javascript
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      [
        'feat',     // 新功能
        'fix',      // 修复bug
        'docs',     // 文档更新
        'style',    // 代码格式修改
        'refactor', // 重构
        'perf',     // 性能优化
        'test',     // 测试相关
        'build',    // 构建系统
        'ci',       // CI/CD相关
        'chore',    // 构建过程或工具变动
        'revert',   // 撤销提交
      ],
    ],
    'type-case': [2, 'always', 'lower-case'],
    'type-empty': [2, 'never'],
    'scope-empty': [0, 'never'],
    'scope-case': [2, 'always', 'lower-case'],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'subject-case': [0, 'never'],
    'header-max-length': [2, 'always', 100],
  },
};
```

#### 3. 配置Commitizen
在package.json中添加配置：
```json
{
  "scripts": {
    "commit": "cz"
  },
  "config": {
    "commitizen": {
      "path": "./node_modules/cz-conventional-changelog"
    }
  }
}
```

#### 4. 创建提交信息模板
创建 `.gitmessage` 文件：
```
# <类型>[可选 范围]: <描述>
#
# [可选 正文]
#
# [可选 脚注]
#
# 类型说明：
# feat: 新功能
# fix: 修复bug
# docs: 文档更新
# style: 代码格式修改（不影响代码逻辑）
# refactor: 重构（既不修复bug也不添加功能）
# perf: 性能优化
# test: 测试相关
# build: 构建系统相关
# ci: CI/CD相关
# chore: 构建过程或辅助工具变动
# revert: 撤销提交
#
# 范围示例：auth, api, ui, store, utils
# 描述：简明扼要说明本次提交的目的
```

#### 5. 配置Git使用提交模板
```bash
git config commit.template .gitmessage
```

## 验收标准
- [x] 使用 `npm run commit` 进行规范化提交
- [x] 不规范的提交信息被拒绝
- [x] 提交信息格式统一
- [x] commit-msg钩子正常工作
- [x] 提交信息符合约定式提交规范

## 交付物
- [x] `commitlint.config.js` - commitlint配置
- [x] 配置好的Commitizen
- [x] `.gitmessage` - 提交信息模板
- [x] 更新的package.json配置

## 提交信息规范
```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

### 示例
```
feat(auth): add Google OAuth login

Implement Google OAuth 2.0 integration for user authentication.
This allows users to sign in using their Google accounts.

Closes #123
```

## 下一步
完成后进入 `01-2-5-EditorConfig配置`