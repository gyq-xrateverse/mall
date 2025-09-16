CREATE TABLE `case_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort` int(11) DEFAULT '0' COMMENT '排序字段',
  `status` int(1) DEFAULT '1' COMMENT '状态：0->禁用；1->启用',
  `show_status` int(1) DEFAULT '1' COMMENT '显示状态：0->不显示；1->显示',
  `delete_status` int(1) DEFAULT '0' COMMENT '删除状态：0->未删除；1->已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人',
  `modify_by` varchar(100) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_sort` (`sort`) USING BTREE COMMENT '排序索引',
  KEY `idx_status` (`status`, `show_status`) USING BTREE COMMENT '状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='案例分类表';

CREATE TABLE `case_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '案例ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类类型ID',
  `title` varchar(200) NOT NULL COMMENT '案例标题',
  `description` text COMMENT '案例描述',
  `preview_image_url` varchar(500) DEFAULT NULL COMMENT '预览图URL',
  `video_url` varchar(500) DEFAULT NULL COMMENT '视频URL',
  `video_preview_url` varchar(500) DEFAULT NULL COMMENT '视频预览地址',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞数',
  `view_count` int(11) DEFAULT '0' COMMENT '预览次数',
  `hot_score` decimal(10,2) DEFAULT '0' COMMENT '热度分数',
  `hot_update_time` datetime DEFAULT NULL COMMENT '热度更新时间',
  `sort` int(11) DEFAULT '0' COMMENT '排序字段',
  `status` int(1) DEFAULT '1' COMMENT '状态：0->禁用；1->启用',
  `show_status` int(1) DEFAULT '1' COMMENT '显示状态：0->不显示；1->显示',
  `delete_status` int(1) DEFAULT '0' COMMENT '删除状态：0->未删除；1->已删除',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建人',
  `modify_by` varchar(100) DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_category_id` (`category_id`) USING BTREE COMMENT '分类索引',
  KEY `idx_status` (`status`, `show_status`) USING BTREE COMMENT '状态索引',
  KEY `idx_hot_score` (`hot_score` DESC) USING BTREE COMMENT '热度排序索引',
  KEY `idx_create_time` (`create_time` DESC) USING BTREE COMMENT '最新排序索引',
  KEY `idx_view_count` (`view_count` DESC) USING BTREE COMMENT '浏览量索引',
  KEY `idx_like_count` (`like_count` DESC) USING BTREE COMMENT '点赞量索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='案例数据表';

INSERT INTO `case_category` (`name`, `sort`, `status`, `show_status`, `create_time`) VALUES
('服装', 1, 1, 1, NOW()),
('模特', 2, 1, 1, NOW()),
('餐饮', 3, 1, 1, NOW()),
('建筑', 4, 1, 1, NOW()),
('艺术', 5, 1, 1, NOW()),
('科技', 6, 1, 1, NOW()),
('自然', 7, 1, 1, NOW());