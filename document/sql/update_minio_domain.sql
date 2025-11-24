-- ============================================================================
-- 批量更新 MinIO 域名脚本
-- 将旧域名 minio.xrateverse.com 替换为新域名 minio.ratev.ai
-- 执行时间：2025-01-XX
-- ============================================================================

USE agent;

-- 1. 更新 ai_project 表中的视频链接
UPDATE ai_project
SET result_video_url = REPLACE(result_video_url, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE result_video_url LIKE '%minio.xrateverse.com%';

-- 2. 更新 ai_workflow_execution 表中的结果URL
UPDATE ai_workflow_execution
SET result_url = REPLACE(result_url, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE result_url LIKE '%minio.xrateverse.com%';

UPDATE ai_workflow_execution
SET error_message = REPLACE(error_message, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE error_message LIKE '%minio.xrateverse.com%';

-- 3. 更新 ai_workflow_execution_node 表中的输出
UPDATE ai_workflow_execution_node
SET output = REPLACE(output, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE output LIKE '%minio.xrateverse.com%';

UPDATE ai_workflow_execution_node
SET error_message = REPLACE(error_message, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE error_message LIKE '%minio.xrateverse.com%';

-- 4. 更新 ai_workflow_result 表（如果存在）
-- UPDATE ai_workflow_result
-- SET file_url = REPLACE(file_url, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
-- WHERE file_url LIKE '%minio.xrateverse.com%';

-- 切换到 mall 数据库
USE mall;

-- 5. 更新 ums_member 表中的头像URL
UPDATE ums_member
SET icon = REPLACE(icon, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE icon LIKE '%minio.xrateverse.com%';

-- 6. 更新 pms_product 表中的商品图片
UPDATE pms_product
SET pic = REPLACE(pic, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE pic LIKE '%minio.xrateverse.com%';

UPDATE pms_product
SET album_pics = REPLACE(album_pics, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE album_pics LIKE '%minio.xrateverse.com%';

-- 7. 更新 pms_product_attribute_value 表
UPDATE pms_product_attribute_value
SET value = REPLACE(value, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE value LIKE '%minio.xrateverse.com%';

-- 8. 更新 pms_sku_stock 表中的SKU图片
UPDATE pms_sku_stock
SET pic = REPLACE(pic, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE pic LIKE '%minio.xrateverse.com%';

-- 9. 更新 cms_subject 表中的专题图片
UPDATE cms_subject
SET pic = REPLACE(pic, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE pic LIKE '%minio.xrateverse.com%';

-- 10. 更新 sms_home_advertise 表中的广告图片
UPDATE sms_home_advertise
SET pic = REPLACE(pic, 'http://minio.xrateverse.com/', 'http://minio.ratev.ai/')
WHERE pic LIKE '%minio.xrateverse.com%';

-- ============================================================================
-- 验证更新结果
-- ============================================================================

-- 检查 agent 数据库
USE agent;
SELECT '=== agent 数据库检查 ===' as info;
SELECT 'ai_project 剩余旧链接:' as table_name, COUNT(*) as count FROM ai_project WHERE result_video_url LIKE '%minio.xrateverse.com%'
UNION ALL
SELECT 'ai_workflow_execution 剩余旧链接:', COUNT(*) FROM ai_workflow_execution WHERE result_url LIKE '%minio.xrateverse.com%' OR error_message LIKE '%minio.xrateverse.com%'
UNION ALL
SELECT 'ai_workflow_execution_node 剩余旧链接:', COUNT(*) FROM ai_workflow_execution_node WHERE output LIKE '%minio.xrateverse.com%' OR error_message LIKE '%minio.xrateverse.com%';

-- 检查 mall 数据库
USE mall;
SELECT '=== mall 数据库检查 ===' as info;
SELECT 'ums_member 剩余旧链接:' as table_name, COUNT(*) as count FROM ums_member WHERE icon LIKE '%minio.xrateverse.com%'
UNION ALL
SELECT 'pms_product 剩余旧链接:', COUNT(*) FROM pms_product WHERE pic LIKE '%minio.xrateverse.com%' OR album_pics LIKE '%minio.xrateverse.com%'
UNION ALL
SELECT 'pms_sku_stock 剩余旧链接:', COUNT(*) FROM pms_sku_stock WHERE pic LIKE '%minio.xrateverse.com%';

-- ============================================================================
-- 完成
-- ============================================================================
SELECT '✅ 域名更新完成！请检查上方验证结果' as status;
