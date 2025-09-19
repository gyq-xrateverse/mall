# 01-6-1-Tailwind CSS集成

## 任务概述
- **时间估算**: 30分钟
- **优先级**: 高
- **依赖关系**: 
  - 依赖：01-1-3-Vite配置优化（Vite基础配置）
  - 依赖：01-4-4-UI状态管理（主题状态管理）
  - 前置：项目基础结构完成
- **执行阶段**: 第一阶段-前端基础架构

## 详细任务清单

### 6.1.1 安装Tailwind CSS
- [ ] 安装Tailwind CSS核心包
- [ ] 安装相关工具和插件
- [ ] 配置PostCSS和autoprefixer
- [ ] 安装Tailwind CSS类型定义

### 6.1.2 配置Tailwind CSS
- [ ] 创建Tailwind配置文件
- [ ] 设置内容路径扫描
- [ ] 配置主题定制
- [ ] 添加自定义工具类

### 6.1.3 集成到项目中
- [ ] 导入Tailwind基础样式
- [ ] 配置Vite支持
- [ ] 设置开发环境优化
- [ ] 添加生产环境优化

### 6.1.4 创建设计系统基础
- [ ] 定义设计令牌
- [ ] 创建主题变量
- [ ] 设置响应式断点
- [ ] 配置暗黑模式支持

## 验收标准

### 功能验收
- [ ] Tailwind CSS类名正常工作
- [ ] 响应式设计功能正常
- [ ] 主题切换功能正确
- [ ] 暗黑模式支持完整

### 代码质量验收
- [ ] Tailwind配置结构清晰
- [ ] 样式系统设计合理
- [ ] 类型定义完整准确
- [ ] 文档注释清晰

### 性能验收
- [ ] CSS产物大小合理
- [ ] 构建时间优化
- [ ] 开发体验良好
- [ ] 生产环境优化

## 交付物

### 1. Tailwind配置文件
```
├── tailwind.config.js     # Tailwind主配置
├── postcss.config.js      # PostCSS配置
└── src/styles/
    ├── tailwind.css       # Tailwind基础样式
    ├── components.css     # 组件样式
    └── utilities.css      # 工具类样式
```

### 2. 设计系统文件
```
src/styles/
├── theme/
│   ├── colors.ts          # 颜色配置
│   ├── typography.ts      # 字体配置
│   ├── spacing.ts         # 间距配置
│   └── breakpoints.ts     # 断点配置
└── design-tokens.ts       # 设计令牌
```

### 3. 样式工具文件
```
src/utils/styles/
├── classNames.ts          # 类名工具函数
├── themeUtils.ts          # 主题工具函数
└── responsiveUtils.ts     # 响应式工具
```

## 技术要点

### 安装Tailwind CSS依赖
```bash
# 核心依赖
pnpm add -D tailwindcss postcss autoprefixer

# 额外插件
pnpm add -D @tailwindcss/forms           # 表单样式
pnpm add -D @tailwindcss/typography      # 排版插件
pnpm add -D @tailwindcss/aspect-ratio    # 纵横比插件
pnpm add -D @tailwindcss/container-queries # 容器查询

# 工具
pnpm add clsx                            # 条件类名工具
pnpm add tailwind-merge                  # Tailwind类名合并
```

### Tailwind配置文件
```javascript
// tailwind.config.js
const { fontFamily } = require('tailwindcss/defaultTheme');

/** @type {import('tailwindcss').Config} */
module.exports = {
  // 内容路径
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  
  // 暗黑模式
  darkMode: 'class',
  
  theme: {
    extend: {
      // 颜色系统
      colors: {
        primary: {
          50: '#eff6ff',
          100: '#dbeafe',
          200: '#bfdbfe',
          300: '#93c5fd',
          400: '#60a5fa',
          500: '#3b82f6',
          600: '#2563eb',
          700: '#1d4ed8',
          800: '#1e40af',
          900: '#1e3a8a',
        },
        gray: {
          50: '#f9fafb',
          100: '#f3f4f6',
          200: '#e5e7eb',
          300: '#d1d5db',
          400: '#9ca3af',
          500: '#6b7280',
          600: '#4b5563',
          700: '#374151',
          800: '#1f2937',
          900: '#111827',
        },
        success: {
          50: '#f0fdf4',
          100: '#dcfce7',
          200: '#bbf7d0',
          300: '#86efac',
          400: '#4ade80',
          500: '#22c55e',
          600: '#16a34a',
          700: '#15803d',
          800: '#166534',
          900: '#14532d',
        },
        warning: {
          50: '#fffbeb',
          100: '#fef3c7',
          200: '#fde68a',
          300: '#fcd34d',
          400: '#fbbf24',
          500: '#f59e0b',
          600: '#d97706',
          700: '#b45309',
          800: '#92400e',
          900: '#78350f',
        },
        error: {
          50: '#fef2f2',
          100: '#fee2e2',
          200: '#fecaca',
          300: '#fca5a5',
          400: '#f87171',
          500: '#ef4444',
          600: '#dc2626',
          700: '#b91c1c',
          800: '#991b1b',
          900: '#7f1d1d',
        },
      },
      
      // 字体系统
      fontFamily: {
        sans: [
          'Inter',
          '-apple-system',
          'BlinkMacSystemFont',
          'PingFang SC',
          'Hiragino Sans GB',
          'Microsoft YaHei',
          'SimSun',
          'sans-serif',
          ...fontFamily.sans,
        ],
        mono: [
          'JetBrains Mono',
          'Fira Code',
          'Monaco',
          'Consolas',
          ...fontFamily.mono,
        ],
      },
      
      // 字号系统
      fontSize: {
        xs: ['0.75rem', { lineHeight: '1rem' }],
        sm: ['0.875rem', { lineHeight: '1.25rem' }],
        base: ['1rem', { lineHeight: '1.5rem' }],
        lg: ['1.125rem', { lineHeight: '1.75rem' }],
        xl: ['1.25rem', { lineHeight: '1.75rem' }],
        '2xl': ['1.5rem', { lineHeight: '2rem' }],
        '3xl': ['1.875rem', { lineHeight: '2.25rem' }],
        '4xl': ['2.25rem', { lineHeight: '2.5rem' }],
        '5xl': ['3rem', { lineHeight: '1' }],
      },
      
      // 间距系统
      spacing: {
        '4.5': '1.125rem',
        '5.5': '1.375rem',
        '13': '3.25rem',
        '15': '3.75rem',
        '17': '4.25rem',
        '18': '4.5rem',
        '19': '4.75rem',
        '21': '5.25rem',
        '22': '5.5rem',
      },
      
      // 断点系统
      screens: {
        'xs': '475px',
        'sm': '640px',
        'md': '768px',
        'lg': '1024px',
        'xl': '1280px',
        '2xl': '1536px',
      },
      
      // 动画系统
      animation: {
        'fade-in': 'fadeIn 0.5s ease-in-out',
        'fade-out': 'fadeOut 0.5s ease-in-out',
        'slide-in': 'slideIn 0.3s ease-out',
        'slide-out': 'slideOut 0.3s ease-in',
        'bounce-in': 'bounceIn 0.5s ease-out',
        'shake': 'shake 0.5s ease-in-out',
      },
      
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        fadeOut: {
          '0%': { opacity: '1' },
          '100%': { opacity: '0' },
        },
        slideIn: {
          '0%': { transform: 'translateY(-10px)', opacity: '0' },
          '100%': { transform: 'translateY(0)', opacity: '1' },
        },
        slideOut: {
          '0%': { transform: 'translateY(0)', opacity: '1' },
          '100%': { transform: 'translateY(-10px)', opacity: '0' },
        },
        bounceIn: {
          '0%': { transform: 'scale(0.3)', opacity: '0' },
          '50%': { transform: 'scale(1.05)' },
          '70%': { transform: 'scale(0.9)' },
          '100%': { transform: 'scale(1)', opacity: '1' },
        },
        shake: {
          '0%, 100%': { transform: 'translateX(0)' },
          '10%, 30%, 50%, 70%, 90%': { transform: 'translateX(-10px)' },
          '20%, 40%, 60%, 80%': { transform: 'translateX(10px)' },
        },
      },
      
      // 阴影系统
      boxShadow: {
        'sm': '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
        'DEFAULT': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
        'md': '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
        'lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
        'xl': '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
        '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
        'inner': 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)',
      },
      
      // 圆角系统
      borderRadius: {
        'none': '0',
        'sm': '0.125rem',
        'DEFAULT': '0.25rem',
        'md': '0.375rem',
        'lg': '0.5rem',
        'xl': '0.75rem',
        '2xl': '1rem',
        '3xl': '1.5rem',
      },
    },
  },
  
  plugins: [
    require('@tailwindcss/forms')({
      strategy: 'class',
    }),
    require('@tailwindcss/typography'),
    require('@tailwindcss/aspect-ratio'),
    require('@tailwindcss/container-queries'),
    
    // 自定义插件
    function({ addUtilities, addComponents, theme }) {
      // 添加自定义工具类
      addUtilities({
        '.scrollbar-hide': {
          '-ms-overflow-style': 'none',
          'scrollbar-width': 'none',
          '&::-webkit-scrollbar': {
            display: 'none',
          },
        },
        '.scrollbar-thin': {
          'scrollbar-width': 'thin',
          '&::-webkit-scrollbar': {
            width: '6px',
            height: '6px',
          },
        },
      });
      
      // 添加自定义组件类
      addComponents({
        '.btn': {
          padding: `${theme('spacing.2')} ${theme('spacing.4')}`,
          borderRadius: theme('borderRadius.md'),
          fontWeight: theme('fontWeight.medium'),
          display: 'inline-flex',
          alignItems: 'center',
          justifyContent: 'center',
          transition: 'all 0.2s ease-in-out',
          '&:focus': {
            outline: 'none',
            boxShadow: `0 0 0 2px ${theme('colors.primary.500')}`,
          },
        },
        '.card': {
          backgroundColor: theme('colors.white'),
          borderRadius: theme('borderRadius.lg'),
          boxShadow: theme('boxShadow.md'),
          padding: theme('spacing.6'),
          '.dark &': {
            backgroundColor: theme('colors.gray.800'),
          },
        },
      });
    },
  ],
};
```

### PostCSS配置
```javascript
// postcss.config.js
module.exports = {
  plugins: {
    tailwindcss: {},
    autoprefixer: {},
    ...(process.env.NODE_ENV === 'production' ? { cssnano: {} } : {}),
  },
};
```

### Tailwind基础样式
```css
/* src/styles/tailwind.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

/* 基础样式重置 */
@layer base {
  html {
    @apply scroll-smooth;
  }
  
  body {
    @apply font-sans antialiased;
  }
  
  /* 中文字体优化 */
  @font-face {
    font-family: 'Inter';
    src: url('/fonts/Inter-Regular.woff2') format('woff2');
    font-weight: 400;
    font-display: swap;
  }
  
  /* 滚动条样式 */
  ::-webkit-scrollbar {
    @apply w-2 h-2;
  }
  
  ::-webkit-scrollbar-track {
    @apply bg-gray-100 dark:bg-gray-800;
  }
  
  ::-webkit-scrollbar-thumb {
    @apply bg-gray-300 dark:bg-gray-600 rounded-full;
  }
  
  ::-webkit-scrollbar-thumb:hover {
    @apply bg-gray-400 dark:bg-gray-500;
  }
  
  /* 选择文本样式 */
  ::selection {
    @apply bg-primary-100 text-primary-900;
  }
  
  /* 暗黑模式选择样式 */
  .dark ::selection {
    @apply bg-primary-800 text-primary-100;
  }
}

/* 组件样式 */
@layer components {
  /* 按钮变体 */
  .btn-primary {
    @apply btn bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500;
  }
  
  .btn-secondary {
    @apply btn bg-gray-200 text-gray-900 hover:bg-gray-300 focus:ring-gray-500 dark:bg-gray-700 dark:text-gray-100 dark:hover:bg-gray-600;
  }
  
  .btn-success {
    @apply btn bg-success-600 text-white hover:bg-success-700 focus:ring-success-500;
  }
  
  .btn-warning {
    @apply btn bg-warning-600 text-white hover:bg-warning-700 focus:ring-warning-500;
  }
  
  .btn-error {
    @apply btn bg-error-600 text-white hover:bg-error-700 focus:ring-error-500;
  }
  
  .btn-ghost {
    @apply btn bg-transparent text-gray-700 hover:bg-gray-100 focus:ring-gray-500 dark:text-gray-300 dark:hover:bg-gray-800;
  }
  
  /* 表单元素 */
  .form-input {
    @apply block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-primary-500 focus:border-primary-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white;
  }
  
  .form-textarea {
    @apply form-input resize-none;
  }
  
  .form-select {
    @apply form-input pr-10 bg-white dark:bg-gray-700;
  }
  
  .form-checkbox {
    @apply h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 rounded dark:border-gray-600;
  }
  
  .form-radio {
    @apply h-4 w-4 text-primary-600 focus:ring-primary-500 border-gray-300 dark:border-gray-600;
  }
  
  /* 卡片变体 */
  .card-hover {
    @apply card transition-transform hover:scale-105 hover:shadow-lg;
  }
  
  .card-bordered {
    @apply card border border-gray-200 dark:border-gray-700;
  }
  
  /* 文本样式 */
  .text-muted {
    @apply text-gray-600 dark:text-gray-400;
  }
  
  .text-emphasis {
    @apply text-gray-900 dark:text-gray-100 font-medium;
  }
}

/* 工具类样式 */
@layer utilities {
  /* 背景渐变 */
  .bg-gradient-primary {
    @apply bg-gradient-to-r from-primary-500 to-primary-600;
  }
  
  .bg-gradient-success {
    @apply bg-gradient-to-r from-success-500 to-success-600;
  }
  
  /* 文本渐变 */
  .text-gradient-primary {
    @apply bg-gradient-to-r from-primary-500 to-primary-600 bg-clip-text text-transparent;
  }
  
  /* 安全区域 */
  .safe-top {
    padding-top: env(safe-area-inset-top);
  }
  
  .safe-bottom {
    padding-bottom: env(safe-area-inset-bottom);
  }
  
  /* 响应式工具 */
  .mobile-only {
    @apply block sm:hidden;
  }
  
  .desktop-only {
    @apply hidden sm:block;
  }
  
  /* 可访问性工具 */
  .sr-only-focusable:focus {
    @apply not-sr-only;
  }
}
```

### 样式工具函数
```typescript
// src/utils/styles/classNames.ts
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

/**
 * 合并Tailwind CSS类名，避免冲突
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

/**
 * 条件类名工具
 */
export const conditional = (condition: boolean, trueClass: string, falseClass?: string) => {
  return condition ? trueClass : falseClass || '';
};

/**
 * 变体类名生成器
 */
export const variants = <T extends Record<string, string>>(
  base: string,
  variants: T,
  defaultVariant?: keyof T
) => {
  return (variant?: keyof T) => {
    const selectedVariant = variant || defaultVariant;
    return cn(base, selectedVariant ? variants[selectedVariant] : '');
  };
};

// 示例使用
export const buttonVariants = variants(
  'btn', // 基础类
  {
    primary: 'btn-primary',
    secondary: 'btn-secondary',
    success: 'btn-success',
    warning: 'btn-warning',
    error: 'btn-error',
    ghost: 'btn-ghost',
  },
  'primary' // 默认变体
);
```

### 主题工具函数
```typescript
// src/utils/styles/themeUtils.ts
import { useAppSelector } from '@/store/hooks';
import { selectActualTheme } from '@/store/slices/uiSelectors';

/**
 * 主题相关的类名工具
 */
export const useThemeClasses = () => {
  const theme = useAppSelector(selectActualTheme);
  const isDark = theme === 'dark';

  return {
    isDark,
    themeClass: isDark ? 'dark' : 'light',
    
    // 主题相关的类名生成器
    bg: (lightClass: string, darkClass: string) => 
      isDark ? darkClass : lightClass,
    
    text: (lightClass: string, darkClass: string) => 
      isDark ? darkClass : lightClass,
    
    border: (lightClass: string, darkClass: string) => 
      isDark ? darkClass : lightClass,
  };
};

/**
 * CSS变量主题系统
 */
export const themeVariables = {
  light: {
    '--color-bg-primary': '#ffffff',
    '--color-bg-secondary': '#f9fafb',
    '--color-text-primary': '#111827',
    '--color-text-secondary': '#6b7280',
    '--color-border': '#e5e7eb',
  },
  dark: {
    '--color-bg-primary': '#1f2937',
    '--color-bg-secondary': '#111827',
    '--color-text-primary': '#f9fafb',
    '--color-text-secondary': '#9ca3af',
    '--color-border': '#374151',
  },
};
```

### 响应式工具函数
```typescript
// src/utils/styles/responsiveUtils.ts
import { useCallback, useEffect, useState } from 'react';

// 断点定义
export const breakpoints = {
  xs: 475,
  sm: 640,
  md: 768,
  lg: 1024,
  xl: 1280,
  '2xl': 1536,
} as const;

export type Breakpoint = keyof typeof breakpoints;

/**
 * 响应式断点Hook
 */
export const useBreakpoint = () => {
  const [currentBreakpoint, setCurrentBreakpoint] = useState<Breakpoint>('lg');

  const getBreakpoint = useCallback((width: number): Breakpoint => {
    if (width >= breakpoints['2xl']) return '2xl';
    if (width >= breakpoints.xl) return 'xl';
    if (width >= breakpoints.lg) return 'lg';
    if (width >= breakpoints.md) return 'md';
    if (width >= breakpoints.sm) return 'sm';
    return 'xs';
  }, []);

  useEffect(() => {
    const handleResize = () => {
      setCurrentBreakpoint(getBreakpoint(window.innerWidth));
    };

    handleResize(); // 初始化
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [getBreakpoint]);

  return {
    current: currentBreakpoint,
    isXs: currentBreakpoint === 'xs',
    isSm: currentBreakpoint === 'sm',
    isMd: currentBreakpoint === 'md',
    isLg: currentBreakpoint === 'lg',
    isXl: currentBreakpoint === 'xl',
    is2Xl: currentBreakpoint === '2xl',
    isMobile: currentBreakpoint === 'xs' || currentBreakpoint === 'sm',
    isTablet: currentBreakpoint === 'md',
    isDesktop: ['lg', 'xl', '2xl'].includes(currentBreakpoint),
  };
};

/**
 * 媒体查询Hook
 */
export const useMediaQuery = (query: string) => {
  const [matches, setMatches] = useState(false);

  useEffect(() => {
    const mediaQuery = window.matchMedia(query);
    setMatches(mediaQuery.matches);

    const handleChange = (e: MediaQueryListEvent) => {
      setMatches(e.matches);
    };

    mediaQuery.addEventListener('change', handleChange);
    return () => mediaQuery.removeEventListener('change', handleChange);
  }, [query]);

  return matches;
};
```

### Vite集成配置更新
```typescript
// vite.config.ts (更新CSS部分)
export default defineConfig({
  css: {
    postcss: './postcss.config.js',
    modules: {
      localsConvention: 'camelCaseOnly',
      generateScopedName: isBuild 
        ? '[hash:base64:5]' 
        : '[name]_[local]_[hash:base64:5]',
    },
    preprocessorOptions: {
      scss: {
        additionalData: `
          @import "@/styles/variables.scss";
          @import "tailwindcss/base";
          @import "tailwindcss/components";
          @import "tailwindcss/utilities";
        `,
      },
    },
  },
});
```

## 下一步
- **后续任务**: 01-6-2-Ant Design集成
- **关联任务**: 在Tailwind CSS基础上集成Ant Design组件库
- **注意事项**: 
  - Tailwind配置要考虑与Ant Design的样式兼容性
  - 确保暗黑模式在两个系统中保持一致
  - 注意CSS产物大小优化

## 常见问题解决

### Q1: Tailwind样式不生效
- 检查PostCSS配置是否正确
- 确认内容路径扫描配置
- 验证CSS导入顺序

### Q2: 与现有CSS冲突
- 使用CSS层级(@layer)组织样式
- 检查样式优先级问题
- 使用Tailwind前缀避免冲突

### Q3: 构建产物过大
- 配置PurgeCSS正确扫描
- 检查未使用的样式
- 优化自定义组件类

### Q4: 暗黑模式不工作
- 检查dark模式配置
- 确认HTML类名切换
- 验证CSS变量定义