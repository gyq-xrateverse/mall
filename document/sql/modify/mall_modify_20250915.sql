-- =====================================================
-- 案例管理系统数据库表创建脚本
-- =====================================================
-- 创建时间: 2025-09-15
-- 版本: V1.0
-- 说明: 创建案例分类表和案例数据表，支持AI创意案例展示
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

DROP TABLE IF EXISTS `case_category`, `case_data`;

-- =====================================================
-- 第一部分: 案例分类表 (case_category)
-- =====================================================

-- 创建案例分类表
CREATE TABLE IF NOT EXISTS `case_category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `description` TEXT COMMENT '分类描述',
    `icon` VARCHAR(255) COMMENT '分类图标URL',
    `sort` INT DEFAULT 0 COMMENT '排序值，数字越小越靠前',
    `status` TINYINT DEFAULT 1 COMMENT '启用状态：0->禁用；1->启用',
    `show_status` TINYINT DEFAULT 1 COMMENT '显示状态：0->不显示；1->显示',
    `case_count` INT DEFAULT 0 COMMENT '案例数量',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_case_category_status` (`status`),
    INDEX `idx_case_category_show_status` (`show_status`),
    INDEX `idx_case_category_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='案例分类表';

-- =====================================================
-- 第二部分: 案例数据表 (case_data)
-- =====================================================

-- 创建案例数据表
CREATE TABLE IF NOT EXISTS `case_data` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '案例ID',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `title` VARCHAR(200) NOT NULL COMMENT '案例标题',
    `content` TEXT COMMENT '案例内容描述',
    `cover_image` VARCHAR(500) COMMENT '封面图片URL',
    `images` TEXT COMMENT '案例图片列表，JSON格式存储',
    `tags` VARCHAR(500) COMMENT '标签，逗号分隔',
    `view_count` BIGINT DEFAULT 0 COMMENT '浏览数',
    `like_count` BIGINT DEFAULT 0 COMMENT '点赞数',
    `hot_score` DECIMAL(10,2) DEFAULT 0.00 COMMENT '热度分数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0->禁用；1->启用',
    `show_status` TINYINT DEFAULT 1 COMMENT '显示状态：0->不显示；1->显示',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_case_data_category_id` (`category_id`),
    INDEX `idx_case_data_status` (`status`),
    INDEX `idx_case_data_show_status` (`show_status`),
    INDEX `idx_case_data_hot_score` (`hot_score` DESC),
    INDEX `idx_case_data_create_time` (`create_time` DESC),
    INDEX `idx_case_data_view_count` (`view_count` DESC),
    INDEX `idx_case_data_like_count` (`like_count` DESC),
    CONSTRAINT `fk_case_data_category` FOREIGN KEY (`category_id`) REFERENCES `case_category` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='案例数据表';

-- =====================================================
-- 第三部分: 初始化数据
-- =====================================================

-- 插入默认分类数据
INSERT INTO `case_category` (`name`, `description`, `icon`, `sort`, `status`, `show_status`, `case_count`) VALUES
('服装', '时尚服装设计案例', '/icons/clothing.svg', 1, 1, 1, 0),
('模特', '模特摄影与形象设计', '/icons/model.svg', 2, 1, 1, 0),
('餐饮', '美食摄影与餐厅设计', '/icons/food.svg', 3, 1, 1, 0),
('建筑', '建筑设计与空间艺术', '/icons/building.svg', 4, 1, 1, 0),
('艺术', '创意艺术与设计作品', '/icons/art.svg', 5, 1, 1, 0),
('科技', '科技产品与UI设计', '/icons/tech.svg', 6, 1, 1, 0),
('自然', '自然风光与生态设计', '/icons/nature.svg', 7, 1, 1, 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `description` = VALUES(`description`);

-- =====================================================
-- 第四部分: 数据验证
-- =====================================================

-- 验证表创建成功
SELECT 'Tables created successfully' AS Status;
SHOW TABLES LIKE 'case_category';
SHOW TABLES LIKE 'case_data';

-- 验证表结构
DESCRIBE case_category;
DESCRIBE case_data;

-- 验证初始数据
SELECT COUNT(*) AS category_count FROM case_category;
SELECT * FROM case_category ORDER BY sort;

-- 验证索引
SHOW INDEX FROM case_category;
SHOW INDEX FROM case_data;

-- =====================================================
-- 脚本执行完成
-- =====================================================

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

-- 执行成功信息
SELECT
    '案例管理系统数据库表创建完成！' AS message,
    NOW() AS completion_time,
    'V1.0' AS version;

-- =====================================================
-- 使用说明
-- =====================================================
/*
1. 表说明：
   - case_category: 案例分类表，存储分类信息
   - case_data: 案例数据表，存储具体案例内容

2. 字段说明：
   - 所有表都包含状态控制字段(status, show_status)
   - 支持软删除和显示控制
   - 包含完整的时间戳记录
   - 优化了查询索引

3. 初始数据：
   - 预置了7个基础分类
   - 分类包含：服装、模特、餐饮、建筑、艺术、科技、自然

4. 注意事项：
   - 使用外键约束保证数据一致性
   - 所有图片字段支持URL存储
   - images字段存储JSON格式的图片列表
   - tags字段支持逗号分隔的标签存储
*/