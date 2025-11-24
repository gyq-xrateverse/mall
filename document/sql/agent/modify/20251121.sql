-- ========================================
-- 数据库迁移脚本: 添加项目封面字段
-- 创建时间: 2025-11-21
-- 描述: 为 ai_project 表添加 cover_image 和 cover_type 字段
-- ========================================

-- 1. 添加 cover_image 字段 (封面图URL)
ALTER TABLE `ai_project`
ADD COLUMN `cover_image` VARCHAR(500) NULL COMMENT '项目封面图URL(可以是MinIO URL或默认占位图路径)'
AFTER `slots`;

-- 2. 添加 cover_type 字段 (封面类型)
ALTER TABLE `ai_project`
ADD COLUMN `cover_type` VARCHAR(20) NULL DEFAULT 'default' COMMENT '封面类型: image/video/audio/text/default'
AFTER `cover_image`;