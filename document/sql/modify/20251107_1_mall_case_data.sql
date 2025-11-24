-- ========================================
-- 案例表结构扩展 - 适配工作台前端字段
-- 执行时间: 建议在业务低峰期执行
-- 作者: Claude Code
-- 日期: 2025-11-07
-- ========================================

USE mall;

-- 备份提示
-- 执行前请先备份数据库: mysqldump -u root -p mall > mall_backup_20251107.sql

-- ========================================
-- 第一步: 案例数据表字段扩展
-- ========================================

-- 1. 添加作品类型字段
ALTER TABLE `case_data`
ADD COLUMN `type` VARCHAR(20) DEFAULT 'video' COMMENT '作品类型：video-视频, image-图片'
AFTER `video`;

-- 2. 添加作者字段
ALTER TABLE `case_data`
ADD COLUMN `author` VARCHAR(100) DEFAULT '匿名用户' COMMENT '作者名称'
AFTER `type`;

-- 3. 添加作者头像字段
ALTER TABLE `case_data`
ADD COLUMN `author_avatar` VARCHAR(500) DEFAULT '' COMMENT '作者头像URL'
AFTER `author`;

-- 4. 为新字段添加索引（提升查询性能）
CREATE INDEX idx_case_type ON `case_data`(`type`);
CREATE INDEX idx_case_author ON `case_data`(`author`);

-- ========================================
-- 第二步: 分类表字段扩展
-- ========================================

-- 添加分类英文标识字段（用于前端的字符串ID）
ALTER TABLE `case_category`
ADD COLUMN `label` VARCHAR(50) DEFAULT NULL COMMENT '分类英文标识(如branding, poster等)'
AFTER `name`;

-- 为label字段添加唯一索引
CREATE UNIQUE INDEX idx_case_category_label ON `case_category`(`label`);

-- ========================================
-- 第三步: 更新现有数据的默认值
-- ========================================

-- 临时关闭安全更新模式
SET SQL_SAFE_UPDATES = 0;

-- 更新案例数据的默认值
UPDATE `case_data` SET `type` = 'video' WHERE `type` IS NULL OR `type` = '';
UPDATE `case_data` SET `author` = '匿名用户' WHERE `author` IS NULL OR `author` = '';
UPDATE `case_data` SET `author_avatar` = '' WHERE `author_avatar` IS NULL;

-- 更新现有分类的label值（根据实际分类名称调整）
UPDATE `case_category` SET `label` = 'clothing' WHERE `name` = '服装' AND `label` IS NULL;
UPDATE `case_category` SET `label` = 'model' WHERE `name` = '模特' AND `label` IS NULL;
UPDATE `case_category` SET `label` = 'food' WHERE `name` = '餐饮' AND `label` IS NULL;
UPDATE `case_category` SET `label` = 'architecture' WHERE `name` = '建筑' AND `label` IS NULL;
UPDATE `case_category` SET `label` = 'art' WHERE `name` = '艺术' AND `label` IS NULL;
UPDATE `case_category` SET `label` = 'tech' WHERE `name` = '科技' AND `label` IS NULL;
UPDATE `case_category` SET `label` = 'nature' WHERE `name` = '自然' AND `label` IS NULL;

-- 恢复安全更新模式
SET SQL_SAFE_UPDATES = 1;

-- ========================================
-- 第四步: 验证表结构
-- ========================================

-- 验证案例数据表结构
DESC `case_data`;

-- 验证分类表结构
DESC `case_category`;

-- 查看更新后的分类数据
SELECT id, name, label, sort, status FROM `case_category` ORDER BY sort;

-- 查看案例数据示例
SELECT id, title, type, author, author_avatar, view_count, like_count
FROM `case_data`
LIMIT 5;

-- ========================================
-- 完成提示
-- ========================================
-- 执行成功后,请验证:
-- 1. case_data 表是否包含 type, author, author_avatar 字段
-- 2. case_category 表是否包含 label 字段
-- 3. 现有数据是否已更新默认值
-- 4. 索引是否创建成功
-- ========================================
