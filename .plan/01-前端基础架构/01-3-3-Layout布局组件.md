# 01-3-3-Layout布局组件

## 任务概述
- **时间估算**: 45分钟
- **优先级**: 高
- **依赖关系**: 
  - 依赖：01-3-2-路由结构设计（路由配置完成）
  - 依赖：01-6-2-Ant Design集成（UI组件库支持）
- **执行阶段**: 第一阶段-前端基础架构

## 详细任务清单

### 3.3.1 设计布局组件架构
- [ ] 分析系统布局需求
- [ ] 设计布局组件层次结构
- [ ] 定义布局组件接口规范
- [ ] 创建布局组件目录结构

### 3.3.2 创建基础布局组件
- [ ] 实现BasicLayout主布局
- [ ] 创建Header头部组件
- [ ] 实现Sidebar侧边栏组件
- [ ] 开发Footer底部组件

### 3.3.3 创建特殊布局组件
- [ ] 实现AuthLayout认证布局
- [ ] 创建BlankLayout空白布局
- [ ] 开发ErrorLayout错误页面布局
- [ ] 实现FullscreenLayout全屏布局

### 3.3.4 布局组件集成
- [ ] 将布局组件集成到路由配置
- [ ] 实现布局切换逻辑
- [ ] 添加响应式布局支持
- [ ] 测试各种屏幕尺寸适配

## 验收标准

### 功能验收
- [ ] 基础布局正常渲染显示
- [ ] 侧边栏折叠/展开功能正常
- [ ] 头部组件功能完整
- [ ] 响应式布局在各设备正常显示

### 代码质量验收
- [ ] 组件代码结构清晰
- [ ] TypeScript类型定义完整
- [ ] 样式代码规范统一
- [ ] 组件可复用性良好

### 用户体验验收
- [ ] 布局切换流畅无闪烁
- [ ] 移动端适配效果良好
- [ ] 组件交互反馈及时
- [ ] 无明显的性能问题

## 交付物

### 1. 布局组件文件
```
src/layouts/
├── BasicLayout/
│   ├── index.tsx          # 主布局组件
│   ├── Header.tsx         # 头部组件
│   ├── Sidebar.tsx        # 侧边栏组件
│   ├── Footer.tsx         # 底部组件
│   └── styles.module.css  # 样式文件
├── AuthLayout/
│   ├── index.tsx          # 认证布局
│   └── styles.module.css  # 样式文件
├── BlankLayout/
│   └── index.tsx          # 空白布局
├── ErrorLayout/
│   └── index.tsx          # 错误页面布局
└── index.ts               # 布局组件导出
```

### 2. 布局类型定义
```
src/types/layout.ts - 布局相关类型定义
```

### 3. 布局样式文件
```
src/styles/layout/ - 布局相关样式文件
```

## 技术要点

### BasicLayout主布局组件
```typescript
// src/layouts/BasicLayout/index.tsx
import React, { useState } from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Sidebar from './Sidebar';
import Footer from './Footer';
import styles from './styles.module.css';

const { Content } = Layout;

const BasicLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false);

  const toggleSidebar = () => {
    setCollapsed(!collapsed);
  };

  return (
    <Layout className={styles.basicLayout}>
      <Sidebar collapsed={collapsed} />
      <Layout className={styles.contentLayout}>
        <Header 
          collapsed={collapsed} 
          onToggleCollapse={toggleSidebar} 
        />
        <Content className={styles.content}>
          <Outlet />
        </Content>
        <Footer />
      </Layout>
    </Layout>
  );
};

export default BasicLayout;
```

### Header头部组件
```typescript
// src/layouts/BasicLayout/Header.tsx
import React from 'react';
import { Layout, Button, Dropdown, Avatar } from 'antd';
import { 
  MenuFoldOutlined, 
  MenuUnfoldOutlined,
  UserOutlined 
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import styles from './styles.module.css';

const { Header: AntHeader } = Layout;

interface HeaderProps {
  collapsed: boolean;
  onToggleCollapse: () => void;
}

const Header: React.FC<HeaderProps> = ({ collapsed, onToggleCollapse }) => {
  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      label: '个人中心',
    },
    {
      key: 'settings',
      label: '设置',
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      label: '退出登录',
    },
  ];

  return (
    <AntHeader className={styles.header}>
      <div className={styles.headerLeft}>
        <Button
          type="text"
          icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          onClick={onToggleCollapse}
          className={styles.collapseBtn}
        />
      </div>
      
      <div className={styles.headerRight}>
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
          <div className={styles.userInfo}>
            <Avatar size="small" icon={<UserOutlined />} />
            <span className={styles.userName}>管理员</span>
          </div>
        </Dropdown>
      </div>
    </AntHeader>
  );
};

export default Header;
```

### Sidebar侧边栏组件
```typescript
// src/layouts/BasicLayout/Sidebar.tsx
import React from 'react';
import { Layout, Menu } from 'antd';
import { useNavigate, useLocation } from 'react-router-dom';
import { 
  DashboardOutlined,
  UserOutlined,
  SettingOutlined 
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import styles from './styles.module.css';

const { Sider } = Layout;

interface SidebarProps {
  collapsed: boolean;
}

const Sidebar: React.FC<SidebarProps> = ({ collapsed }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems: MenuProps['items'] = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '控制台',
    },
    {
      key: '/users',
      icon: <UserOutlined />,
      label: '用户管理',
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '系统设置',
    },
  ];

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    navigate(key);
  };

  return (
    <Sider 
      trigger={null} 
      collapsible 
      collapsed={collapsed}
      className={styles.sidebar}
      width={240}
      collapsedWidth={60}
    >
      <div className={styles.logo}>
        {collapsed ? 'L' : 'Logo'}
      </div>
      <Menu
        theme="dark"
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={handleMenuClick}
      />
    </Sider>
  );
};

export default Sidebar;
```

### AuthLayout认证布局
```typescript
// src/layouts/AuthLayout/index.tsx
import React from 'react';
import { Layout } from 'antd';
import { Outlet } from 'react-router-dom';
import styles from './styles.module.css';

const { Content } = Layout;

const AuthLayout: React.FC = () => {
  return (
    <Layout className={styles.authLayout}>
      <Content className={styles.authContent}>
        <div className={styles.authContainer}>
          <div className={styles.authBox}>
            <Outlet />
          </div>
        </div>
      </Content>
    </Layout>
  );
};

export default AuthLayout;
```

### 布局样式配置
```css
/* src/layouts/BasicLayout/styles.module.css */
.basicLayout {
  min-height: 100vh;
}

.contentLayout {
  margin-left: 0;
  transition: all 0.2s;
}

.header {
  background: #fff;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.headerLeft {
  display: flex;
  align-items: center;
}

.headerRight {
  display: flex;
  align-items: center;
}

.collapseBtn {
  font-size: 16px;
}

.userInfo {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
}

.userInfo:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

.userName {
  margin-left: 8px;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.logo {
  height: 32px;
  margin: 16px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: bold;
}

.content {
  margin: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: calc(100vh - 112px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.3s;
  }
  
  .sidebar.visible {
    transform: translateX(0);
  }
  
  .content {
    margin: 8px;
    padding: 16px;
  }
}
```

## 下一步
- **后续任务**: 01-3-4-路由守卫实现
- **关联任务**: 在布局基础上实现路由权限控制
- **注意事项**: 
  - 布局组件要考虑性能优化
  - 响应式设计要兼容主流设备
  - 为路由守卫预留权限控制接口

## 常见问题解决

### Q1: 布局组件渲染异常
- 检查Outlet组件是否正确放置
- 确认路由配置与布局组件匹配
- 验证CSS样式是否正确加载

### Q2: 侧边栏折叠不生效
- 检查Sider组件collapsed属性绑定
- 确认状态管理逻辑正确
- 验证CSS transition动画配置

### Q3: 响应式布局问题
- 检查CSS媒体查询语法
- 确认断点设置合理
- 验证移动端viewport配置