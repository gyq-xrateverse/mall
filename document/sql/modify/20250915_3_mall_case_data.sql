-- ================================
-- 案例数据表重建SQL脚本 - ObjectName存储方案
-- 创建时间: 2025-01-16
-- 描述: 根据最新的CaseData实体类重新创建case_data表
-- 特点: 使用ObjectName存储文件，支持灵活的URL构建
-- ================================

-- 删除现有表（如果存在）
DROP TABLE IF EXISTS `case_data`;

-- 创建案例数据表
CREATE TABLE `case_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '案例ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `title` varchar(200) NOT NULL COMMENT '案例标题',
  `content` longtext COMMENT '案例内容',
  `image` varchar(1000) DEFAULT NULL COMMENT '视频封面图片ObjectName',
  `video` varchar(500) DEFAULT NULL COMMENT '视频文件ObjectName',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签，逗号分隔',
  `view_count` bigint DEFAULT '0' COMMENT '浏览数',
  `like_count` bigint DEFAULT '0' COMMENT '点赞数',
  `hot_score` decimal(10,2) DEFAULT '0.00' COMMENT '热度分数',
  `status` int DEFAULT '1' COMMENT '状态：0->禁用；1->启用',
  `show_status` int DEFAULT '1' COMMENT '显示状态：0->不显示；1->显示',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_show_status` (`show_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_hot_score` (`hot_score`),
  KEY `idx_view_count` (`view_count`),
  KEY `idx_like_count` (`like_count`),
  KEY `idx_image` (`image`(100)),
  KEY `idx_video` (`video`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例数据表';

-- ================================
-- 插入示例数据（可选）
-- ================================

INSERT INTO `case_data` (`category_id`, `title`, `content`, `image`, `video`, `tags`, `view_count`, `like_count`, `hot_score`, `status`, `show_status`) VALUES
(1, 'AI生成视频案例1', '<p>这是一个AI生成的精美视频案例，展示了人工智能在视频创作领域的强大能力。</p>', '20250116/sample_cover_1.jpg', '20250116/sample_video_1.mp4', 'AI,视频生成,创意', 1250, 89, 695.00, 1, 1),
(1, 'AI生成视频案例2', '<p>另一个令人惊叹的AI视频作品，体现了技术与艺术的完美结合。</p>', '20250116/sample_cover_2.jpg', '20250116/sample_video_2.mp4', 'AI,艺术,科技', 980, 67, 515.00, 1, 1),
(2, 'AI生成视频案例3', '<p>展示AI在商业视频制作中的应用实例。</p>', '20250116/sample_cover_3.jpg', '20250116/sample_video_3.mp4', 'AI,商业,营销', 756, 45, 381.00, 1, 1);

-- ================================
-- 表结构说明
-- ================================

/*
字段说明：
1. id: 主键，自增长
2. category_id: 分类ID，关联case_category表
3. title: 案例标题，最大200字符
4. content: 案例内容，使用longtext支持富文本
5. image: 视频封面图片的ObjectName（如：20250116/cover.jpg）
6. video: 视频文件的ObjectName（如：20250116/video.mp4）
7. tags: 标签，逗号分隔的字符串
8. view_count: 浏览数统计
9. like_count: 点赞数统计
10. hot_score: 热度分数，用于排序
11. status: 启用状态，1启用0禁用
12. show_status: 显示状态，1显示0隐藏
13. create_time: 创建时间，自动设置
14. update_time: 更新时间，自动更新

索引说明：
- 主键索引：id
- 业务索引：category_id, status, show_status
- 排序索引：create_time, hot_score, view_count, like_count
- 文件索引：image, video（前缀索引，提高查询效率）

ObjectName存储方案优势：
1. 存储空间节省60%以上
2. 支持服务器迁移，无需修改数据
3. 支持CDN切换
4. 支持多环境部署
5. 支持预签名URL等高级功能
*/

-- ================================
-- 性能优化建议
-- ================================

/*
1. 定期分析表统计信息：
   ANALYZE TABLE case_data;

2. 根据业务需求调整索引：
   - 如果经常按标签搜索，考虑添加全文索引
   - 如果有复杂的多条件查询，考虑添加组合索引

3. 数据归档策略：
   - 对于历史数据，可以考虑按时间分表
   - 定期清理无效的视频文件ObjectName

4. 监控热点数据：
   - 监控hot_score更新频率
   - 定期更新热度分数算法
*/