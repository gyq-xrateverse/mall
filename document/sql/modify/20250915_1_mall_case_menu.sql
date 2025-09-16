-- =====================================================
-- 案例管理菜单权限数据添加脚本
-- =====================================================
-- 创建时间: 2025-09-15
-- 版本: V1.0
-- 说明: 为案例管理功能添加菜单权限数据
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

-- =====================================================
-- 第一部分: 添加案例管理菜单权限
-- =====================================================

-- 添加案例管理主菜单
INSERT INTO `ums_menu` (`id`, `parent_id`, `create_time`, `title`, `level`, `sort`, `name`, `icon`, `hidden`) VALUES
(26, 0, NOW(), '案例管理', 0, 5, 'case', 'cms', 0);

-- 添加案例分类子菜单
INSERT INTO `ums_menu` (`id`, `parent_id`, `create_time`, `title`, `level`, `sort`, `name`, `icon`, `hidden`) VALUES
(27, 26, NOW(), '案例分类', 1, 0, 'caseCategory', 'product-cate', 0);

-- 添加案例数据子菜单
INSERT INTO `ums_menu` (`id`, `parent_id`, `create_time`, `title`, `level`, `sort`, `name`, `icon`, `hidden`) VALUES
(28, 26, NOW(), '案例管理', 1, 1, 'caseData', 'product-list', 0);

-- =====================================================
-- 第二部分: 为超级管理员角色分配菜单权限
-- =====================================================

-- 为角色ID=1(超级管理员)分配案例管理菜单权限
INSERT INTO `ums_role_menu_relation` (`role_id`, `menu_id`) VALUES
(1, 26),  -- 案例管理主菜单
(1, 27),  -- 案例分类菜单
(1, 28);  -- 案例数据菜单

-- =====================================================
-- 第三部分: 数据验证
-- =====================================================

-- 验证菜单添加成功
SELECT '案例管理菜单权限添加验证' AS verification_type;
SELECT * FROM ums_menu WHERE id IN (26, 27, 28);

-- 验证角色菜单关联添加成功
SELECT '角色菜单关联验证' AS verification_type;
SELECT r.*, m.title FROM ums_role_menu_relation r
LEFT JOIN ums_menu m ON r.menu_id = m.id
WHERE r.menu_id IN (26, 27, 28);

-- 查看完整的菜单层级结构
SELECT '完整菜单结构' AS verification_type;
SELECT
    id,
    parent_id,
    title,
    level,
    sort,
    name,
    icon,
    CASE WHEN hidden = 0 THEN '显示' ELSE '隐藏' END AS display_status
FROM ums_menu
ORDER BY parent_id, sort, id;

-- =====================================================
-- 脚本执行完成
-- =====================================================

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

-- 执行成功信息
SELECT
    '案例管理菜单权限添加完成！' AS message,
    NOW() AS completion_time,
    'V1.0' AS version;

-- =====================================================
-- 使用说明
-- =====================================================
/*
1. 添加的菜单权限：
   - ID 26: 案例管理(主菜单)
   - ID 27: 案例分类(子菜单)
   - ID 28: 案例管理(子菜单)

2. 权限分配：
   - 超级管理员角色(ID=1)自动获得所有案例管理权限
   - 其他角色需要手动分配权限

3. 菜单特性：
   - 使用 cms 图标作为主菜单图标
   - 子菜单使用 product-cate 和 product-list 图标
   - 所有菜单默认显示(hidden=0)
   - 主菜单排序值为5，位于权限管理之后

4. 注意事项：
   - 执行此脚本前确保数据库已有 ums_menu 和 ums_role_menu_relation 表
   - 如需为其他角色分配权限，请手动添加 ums_role_menu_relation 记录
*/