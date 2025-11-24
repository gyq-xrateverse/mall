-- ============================================================
-- 初版意图识别优化 - 镜头设计槽位字段迁移脚本
-- 版本: 202502XX_intent_slots_support
-- 日期: 2025-02-XX
-- 描述: 为 AIProject 添加 slots 字段, 持久化意图识别生成的槽位数据(含镜头设计)
-- 说明: 适用于 MySQL 5.7+ (需要支持 JSON 类型)
-- ============================================================

-- 开始事务
START TRANSACTION;

-- ============================================================
-- 第一步: 在 ai_project 表中添加 slots 字段
-- ============================================================
-- 说明:
-- - 用于存储意图识别阶段累积的槽位数据
-- - 包含镜头设计相关信息:
--   {
--     "video": {
--       "shots": {
--         "count": 3,
--         "types": ["near_push_in", "medium_orbit"]
--       }
--     }
--   }

ALTER TABLE ai_project
ADD COLUMN slots JSON NULL
COMMENT '意图识别生成的槽位数据(含镜头设计等)';

-- 提交事务
COMMIT;

-- ============================================================
-- 验证脚本执行结果
-- ============================================================

-- 1. 检查 slots 字段是否添加成功
SELECT
    COLUMN_NAME,
    COLUMN_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME = 'ai_project'
  AND COLUMN_NAME = 'slots';

-- 2. 简单检查示例: 统计 slots 不为空的项目数量
SELECT
    COUNT(*) AS projects_with_slots
FROM ai_project
WHERE slots IS NOT NULL;

-- ============================================================
-- 回滚脚本 (如需回滚, 请单独执行以下语句)
-- ============================================================
/*
START TRANSACTION;

ALTER TABLE ai_project
DROP COLUMN slots;

COMMIT;
*/

-- 迁移结束
SELECT '🎉 slots 字段迁移脚本执行完毕, 请核对上述验证结果' AS message;

