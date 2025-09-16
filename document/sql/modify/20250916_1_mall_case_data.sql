-- ========================================
-- 案例管理系统数据库优化脚本
-- 创建日期: 2025-09-16
-- 版本: v1.0
-- 描述: 案例管理功能数据库性能优化索引
-- ========================================

-- 使用说明:
-- 1. 本脚本用于优化案例管理系统的数据库查询性能
-- 2. 执行前请确保已备份数据库
-- 3. 建议在业务低峰期执行，避免影响线上服务
-- 4. 全文索引需要MySQL 5.7+版本支持

USE mall;

-- ========================================
-- 1. 案例表性能索引优化
-- ========================================

-- 复合索引：状态 + 显示状态 + 分类 + 创建时间
-- 用于优化案例列表查询，支持按状态、分类筛选并按时间排序
CREATE INDEX idx_case_status_category_time ON case_data(status, show_status, category_id, create_time DESC);

-- 热度排序索引
-- 用于优化热门案例查询，支持按热度分数和浏览量排序
CREATE INDEX idx_case_hot_score ON case_data(hot_score DESC, view_count DESC);

-- 标题和内容搜索索引（全文索引）
-- 用于优化案例搜索功能，支持标题和内容的全文检索
-- 注意：仅MySQL 5.7+支持，如果版本不够请注释掉此行
ALTER TABLE case_data ADD FULLTEXT INDEX idx_case_title_content(title, content);

-- 统计查询优化索引
-- 用于优化案例统计查询，支持按状态和显示状态统计
CREATE INDEX idx_case_stats ON case_data(status, show_status, create_time);

-- ========================================
-- 2. 案例分类表索引优化
-- ========================================

-- 分类排序索引
-- 用于优化分类列表查询，支持按排序字段排序
CREATE INDEX idx_case_category_sort ON case_category(sort ASC);

-- ========================================
-- 3. 查看创建的索引信息
-- ========================================

-- 查看案例表的所有索引
SHOW INDEX FROM case_data;

-- 查看分类表的所有索引
SHOW INDEX FROM case_category;

-- ========================================
-- 4. 索引使用说明和性能预期
-- ========================================

/*
索引说明:

1. idx_case_status_category_time
   - 覆盖场景: 案例列表查询，按分类筛选，按时间排序
   - 查询示例: SELECT * FROM case_data WHERE status=1 AND show_status=1 AND category_id=1 ORDER BY create_time DESC
   - 性能提升: 预计查询时间从200ms降低到50ms以内

2. idx_case_hot_score
   - 覆盖场景: 热门案例查询，按热度和浏览量排序
   - 查询示例: SELECT * FROM case_data WHERE status=1 ORDER BY hot_score DESC, view_count DESC LIMIT 10
   - 性能提升: 预计查询时间从100ms降低到20ms以内

3. idx_case_title_content (全文索引)
   - 覆盖场景: 案例搜索，支持标题和内容的模糊查询
   - 查询示例: SELECT * FROM case_data WHERE MATCH(title,content) AGAINST('搜索关键词' IN NATURAL LANGUAGE MODE)
   - 性能提升: 预计搜索时间从500ms降低到100ms以内

4. idx_case_stats
   - 覆盖场景: 案例统计查询，按状态分组统计
   - 查询示例: SELECT status, show_status, COUNT(*) FROM case_data GROUP BY status, show_status
   - 性能提升: 预计统计查询时间从300ms降低到50ms以内

5. idx_case_category_sort
   - 覆盖场景: 分类列表查询，按排序字段排序
   - 查询示例: SELECT * FROM case_category ORDER BY sort ASC
   - 性能提升: 预计查询时间从50ms降低到10ms以内

注意事项:
- 索引会增加磁盘存储空间占用，预计增加约100MB
- 索引会影响INSERT/UPDATE/DELETE操作性能，但影响很小（通常<5%）
- 定期使用ANALYZE TABLE命令更新索引统计信息以保持最佳性能
*/

-- ========================================
-- 5. 索引维护命令（可选执行）
-- ========================================

-- 更新表统计信息，优化查询计划
-- ANALYZE TABLE case_data;
-- ANALYZE TABLE case_category;

-- 检查表完整性
-- CHECK TABLE case_data;
-- CHECK TABLE case_category;

-- ========================================
-- 执行完成提示
-- ========================================

SELECT '案例管理系统数据库索引优化完成!' AS message,
       '请执行 SHOW INDEX FROM case_data; 查看创建的索引' AS next_step;