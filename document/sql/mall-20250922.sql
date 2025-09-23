-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: mall
-- ------------------------------------------------------
-- Server version	5.7.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `case_category`
--

DROP TABLE IF EXISTS `case_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `case_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` text COMMENT '分类描述',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标URL',
  `sort` int(11) DEFAULT '0' COMMENT '排序值，数字越小越靠前',
  `status` tinyint(4) DEFAULT '1' COMMENT '启用状态：0->禁用；1->启用',
  `show_status` tinyint(4) DEFAULT '1' COMMENT '显示状态：0->不显示；1->显示',
  `case_count` int(11) DEFAULT '0' COMMENT '案例数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_case_category_status` (`status`),
  KEY `idx_case_category_show_status` (`show_status`),
  KEY `idx_case_category_sort` (`sort`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='案例分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `case_category`
--

LOCK TABLES `case_category` WRITE;
/*!40000 ALTER TABLE `case_category` DISABLE KEYS */;
INSERT INTO `case_category` VALUES (1,'服装','时尚服装设计案例','/icons/clothing.svg',1,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58'),(2,'模特','模特摄影与形象设计','/icons/model.svg',2,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58'),(3,'餐饮','美食摄影与餐厅设计','/icons/food.svg',3,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58'),(4,'建筑','建筑设计与空间艺术','/icons/building.svg',4,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58'),(5,'艺术','创意艺术与设计作品','/icons/art.svg',5,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58'),(6,'科技','科技产品与UI设计','/icons/tech.svg',6,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58'),(7,'自然','自然风光与生态设计','/icons/nature.svg',7,1,1,0,'2025-09-15 05:49:58','2025-09-15 05:49:58');
/*!40000 ALTER TABLE `case_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `case_data`
--

DROP TABLE IF EXISTS `case_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `case_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '案例ID',
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '案例标题',
  `content` longtext COLLATE utf8mb4_unicode_ci COMMENT '案例内容',
  `image` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频封面图片ObjectName',
  `video` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频文件ObjectName',
  `tags` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，逗号分隔',
  `view_count` bigint(20) DEFAULT '0' COMMENT '浏览数',
  `like_count` bigint(20) DEFAULT '0' COMMENT '点赞数',
  `hot_score` decimal(10,2) DEFAULT '0.00' COMMENT '热度分数',
  `status` int(11) DEFAULT '1' COMMENT '状态：0->禁用；1->启用',
  `show_status` int(11) DEFAULT '1' COMMENT '显示状态：0->不显示；1->显示',
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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='案例数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `case_data`
--

LOCK TABLES `case_data` WRITE;
/*!40000 ALTER TABLE `case_data` DISABLE KEYS */;
INSERT INTO `case_data` VALUES (6,2,'行李箱','<p>行李箱</p>','20250917/09eb8b8783f34110b2f484fc8b220bf9.png','20250917/9dd8df1a0b414721b250987f2d6a6d0b.mp4','',0,0,0.00,1,1,'2025-09-17 10:45:49','2025-09-17 10:45:49'),(7,2,'模特','<p>模特</p>','http://localhost:9090/mall/20250917/ef70a4328b644444b25493fc33d3fe88.png','http://localhost:9090/mall/20250917/9dc9c1434281469b9d38b7142734a56d.mp4','',0,0,0.00,1,1,'2025-09-17 11:22:58','2025-09-17 12:52:38'),(8,2,'香水','<p>香水</p>','20250917/acbc100d7a094aa3b7792cfcc477cae2.png','20250917/0824456c994c4aababa8163274e9b1bd.mp4','',0,0,0.00,1,1,'2025-09-17 11:23:40','2025-09-17 11:23:40'),(9,1,'围巾','<p>围巾</p>','20250917/8add6ec9ce8b414695aec61c65efd5cf.png','20250917/d19887541af2410eb26e0842179d6cd3.mp4','',0,0,0.00,1,1,'2025-09-17 11:24:01','2025-09-17 11:24:01'),(10,1,'美妆','<p>美妆</p>','20250917/cc95347cc2c548edbf29492405c2aa7c.png','20250917/14227222aa64422aafa4cdf1ca2be075.mp4','',0,0,0.00,1,1,'2025-09-17 11:24:58','2025-09-17 11:24:58'),(16,2,'背包','<p>背包</p>','20250918/e6144fa98e864f85a60f0412574a4a8a.png','20250918/2d22fa88f497442b8de638259f38f219.mp4','',0,0,0.00,1,1,'2025-09-18 10:31:48','2025-09-18 10:31:48'),(17,1,'围巾','<p>围巾</p>','20250918/456863ec28e645c6bcb3435e862c4259.png','20250918/4f6e23848a4f416bb4a443b0762f0ea9.mp4','',0,0,0.00,1,1,'2025-09-18 10:32:14','2025-09-18 10:32:14'),(18,1,'冲锋衣','<p>冲锋衣</p>','20250918/43f0b63a39da4fc59d11f31b19df523a.png','20250918/8117500aa60c46d4af186c99374637f8.mp4','',0,0,0.00,1,1,'2025-09-18 10:32:43','2025-09-18 10:32:43'),(19,2,'墨镜','<p>墨镜</p>','20250918/7f197c7b7d00418a874425889bde67f6.jpg','20250918/d3d5800d1f2b43b5aaffe18ae9a10370.mp4','',0,0,0.00,1,1,'2025-09-18 10:33:35','2025-09-18 10:33:35'),(20,2,'手表','<p>手表</p>','20250918/d984113d239641d8a3bc1253ec1c37bb.jpg','20250918/3483c8a3e0864bbf80ebed29f8610ca5.mp4','',0,0,0.00,1,1,'2025-09-18 10:33:56','2025-09-18 10:33:56'),(21,2,'护腕','<p>护腕</p>','20250918/b8606a0130654652b017bbe34ae530fe.jpg','20250918/ab1bade9feb24681ac520c5d658598c2.mp4','',0,0,0.00,1,1,'2025-09-18 10:34:23','2025-09-18 10:34:23');
/*!40000 ALTER TABLE `case_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_help`
--

DROP TABLE IF EXISTS `cms_help`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_help` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) DEFAULT NULL,
  `icon` varchar(500) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `show_status` int(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `read_count` int(1) DEFAULT NULL,
  `content` text,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='帮助表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_help`
--

LOCK TABLES `cms_help` WRITE;
/*!40000 ALTER TABLE `cms_help` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_help` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_help_category`
--

DROP TABLE IF EXISTS `cms_help_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_help_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `icon` varchar(500) DEFAULT NULL COMMENT '分类图标',
  `help_count` int(11) DEFAULT NULL COMMENT '专题数量',
  `show_status` int(2) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='帮助分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_help_category`
--

LOCK TABLES `cms_help_category` WRITE;
/*!40000 ALTER TABLE `cms_help_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_help_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_member_report`
--

DROP TABLE IF EXISTS `cms_member_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_member_report` (
  `id` bigint(20) DEFAULT NULL,
  `report_type` int(1) DEFAULT NULL COMMENT '举报类型：0->商品评价；1->话题内容；2->用户评论',
  `report_member_name` varchar(100) DEFAULT NULL COMMENT '举报人',
  `create_time` datetime DEFAULT NULL,
  `report_object` varchar(100) DEFAULT NULL,
  `report_status` int(1) DEFAULT NULL COMMENT '举报状态：0->未处理；1->已处理',
  `handle_status` int(1) DEFAULT NULL COMMENT '处理结果：0->无效；1->有效；2->恶意',
  `note` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户举报表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_member_report`
--

LOCK TABLES `cms_member_report` WRITE;
/*!40000 ALTER TABLE `cms_member_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_member_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_prefrence_area`
--

DROP TABLE IF EXISTS `cms_prefrence_area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_prefrence_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `sub_title` varchar(255) DEFAULT NULL,
  `pic` varbinary(500) DEFAULT NULL COMMENT '展示图片',
  `sort` int(11) DEFAULT NULL,
  `show_status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优选专区';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_prefrence_area`
--

LOCK TABLES `cms_prefrence_area` WRITE;
/*!40000 ALTER TABLE `cms_prefrence_area` DISABLE KEYS */;
INSERT INTO `cms_prefrence_area` VALUES (1,'让音质更出众','音质不打折 完美现场感',NULL,NULL,1),(2,'让音质更出众22','让音质更出众22',NULL,NULL,NULL),(3,'让音质更出众33',NULL,NULL,NULL,NULL),(4,'让音质更出众44',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `cms_prefrence_area` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_prefrence_area_product_relation`
--

DROP TABLE IF EXISTS `cms_prefrence_area_product_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_prefrence_area_product_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `prefrence_area_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优选专区和产品关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_prefrence_area_product_relation`
--

LOCK TABLES `cms_prefrence_area_product_relation` WRITE;
/*!40000 ALTER TABLE `cms_prefrence_area_product_relation` DISABLE KEYS */;
INSERT INTO `cms_prefrence_area_product_relation` VALUES (1,1,12),(2,1,13),(3,1,14),(4,1,18),(5,1,7),(6,2,7),(7,1,22),(24,1,23);
/*!40000 ALTER TABLE `cms_prefrence_area_product_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_subject`
--

DROP TABLE IF EXISTS `cms_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) DEFAULT NULL,
  `title` varchar(100) DEFAULT NULL,
  `pic` varchar(500) DEFAULT NULL COMMENT '专题主图',
  `product_count` int(11) DEFAULT NULL COMMENT '关联产品数量',
  `recommend_status` int(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `collect_count` int(11) DEFAULT NULL,
  `read_count` int(11) DEFAULT NULL,
  `comment_count` int(11) DEFAULT NULL,
  `album_pics` varchar(1000) DEFAULT NULL COMMENT '画册图片用逗号分割',
  `description` varchar(1000) DEFAULT NULL,
  `show_status` int(1) DEFAULT NULL COMMENT '显示状态：0->不显示；1->显示',
  `content` text,
  `forward_count` int(11) DEFAULT NULL COMMENT '转发数',
  `category_name` varchar(200) DEFAULT NULL COMMENT '专题分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='专题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_subject`
--

LOCK TABLES `cms_subject` WRITE;
/*!40000 ALTER TABLE `cms_subject` DISABLE KEYS */;
INSERT INTO `cms_subject` VALUES (1,1,'polo衬衫的也时尚',NULL,NULL,NULL,'2018-11-11 13:26:55',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'服装专题'),(2,2,'大牌手机低价秒',NULL,NULL,NULL,'2018-11-12 13:27:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'手机专题'),(3,2,'晓龙845新品上市',NULL,NULL,NULL,'2018-11-13 13:27:05',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'手机专题'),(4,1,'夏天应该穿什么',NULL,NULL,NULL,'2018-11-01 13:27:09',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'服装专题'),(5,1,'夏季精选',NULL,NULL,NULL,'2018-11-06 13:27:18',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'服装专题'),(6,2,'品牌手机降价',NULL,NULL,NULL,'2018-11-07 13:27:21',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'手机专题');
/*!40000 ALTER TABLE `cms_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_subject_category`
--

DROP TABLE IF EXISTS `cms_subject_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_subject_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `icon` varchar(500) DEFAULT NULL COMMENT '分类图标',
  `subject_count` int(11) DEFAULT NULL COMMENT '专题数量',
  `show_status` int(2) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='专题分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_subject_category`
--

LOCK TABLES `cms_subject_category` WRITE;
/*!40000 ALTER TABLE `cms_subject_category` DISABLE KEYS */;
INSERT INTO `cms_subject_category` VALUES (1,'服装专题',NULL,NULL,NULL,NULL),(2,'手机专题',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `cms_subject_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_subject_comment`
--

DROP TABLE IF EXISTS `cms_subject_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_subject_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_id` bigint(20) DEFAULT NULL,
  `member_nick_name` varchar(255) DEFAULT NULL,
  `member_icon` varchar(255) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `show_status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='专题评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_subject_comment`
--

LOCK TABLES `cms_subject_comment` WRITE;
/*!40000 ALTER TABLE `cms_subject_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_subject_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_subject_product_relation`
--

DROP TABLE IF EXISTS `cms_subject_product_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_subject_product_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='专题商品关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_subject_product_relation`
--

LOCK TABLES `cms_subject_product_relation` WRITE;
/*!40000 ALTER TABLE `cms_subject_product_relation` DISABLE KEYS */;
INSERT INTO `cms_subject_product_relation` VALUES (1,1,12),(2,1,13),(3,1,14),(4,1,18),(5,1,7),(6,2,7),(7,1,22),(29,1,23),(30,4,23),(31,5,23),(68,2,26),(69,3,26),(70,6,26);
/*!40000 ALTER TABLE `cms_subject_product_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_topic`
--

DROP TABLE IF EXISTS `cms_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `attend_count` int(11) DEFAULT NULL COMMENT '参与人数',
  `attention_count` int(11) DEFAULT NULL COMMENT '关注人数',
  `read_count` int(11) DEFAULT NULL,
  `award_name` varchar(100) DEFAULT NULL COMMENT '奖品名称',
  `attend_type` varchar(100) DEFAULT NULL COMMENT '参与方式',
  `content` text COMMENT '话题内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='话题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_topic`
--

LOCK TABLES `cms_topic` WRITE;
/*!40000 ALTER TABLE `cms_topic` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_topic_category`
--

DROP TABLE IF EXISTS `cms_topic_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_topic_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `icon` varchar(500) DEFAULT NULL COMMENT '分类图标',
  `subject_count` int(11) DEFAULT NULL COMMENT '专题数量',
  `show_status` int(2) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='话题分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_topic_category`
--

LOCK TABLES `cms_topic_category` WRITE;
/*!40000 ALTER TABLE `cms_topic_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_topic_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cms_topic_comment`
--

DROP TABLE IF EXISTS `cms_topic_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cms_topic_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_nick_name` varchar(255) DEFAULT NULL,
  `topic_id` bigint(20) DEFAULT NULL,
  `member_icon` varchar(255) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `show_status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='专题评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cms_topic_comment`
--

LOCK TABLES `cms_topic_comment` WRITE;
/*!40000 ALTER TABLE `cms_topic_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `cms_topic_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_cart_item`
--

DROP TABLE IF EXISTS `oms_cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `product_sku_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `quantity` int(11) DEFAULT NULL COMMENT '购买数量',
  `price` decimal(10,2) DEFAULT NULL COMMENT '添加到购物车的价格',
  `product_pic` varchar(1000) DEFAULT NULL COMMENT '商品主图',
  `product_name` varchar(500) DEFAULT NULL COMMENT '商品名称',
  `product_sub_title` varchar(500) DEFAULT NULL COMMENT '商品副标题（卖点）',
  `product_sku_code` varchar(200) DEFAULT NULL COMMENT '商品sku条码',
  `member_nickname` varchar(500) DEFAULT NULL COMMENT '会员昵称',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_date` datetime DEFAULT NULL COMMENT '修改时间',
  `delete_status` int(1) DEFAULT '0' COMMENT '是否删除',
  `product_category_id` bigint(20) DEFAULT NULL COMMENT '商品分类',
  `product_brand` varchar(200) DEFAULT NULL,
  `product_sn` varchar(200) DEFAULT NULL,
  `product_attr` varchar(500) DEFAULT NULL COMMENT '商品销售属性:[{"key":"颜色","value":"颜色"},{"key":"容量","value":"4G"}]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='购物车表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_cart_item`
--

LOCK TABLES `oms_cart_item` WRITE;
/*!40000 ALTER TABLE `oms_cart_item` DISABLE KEYS */;
INSERT INTO `oms_cart_item` VALUES (12,26,90,1,1,3788.00,NULL,'华为 HUAWEI P20','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2018-08-27 16:53:44',NULL,1,19,NULL,NULL,NULL),(13,27,98,1,3,2699.00,NULL,'小米8','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2018-08-27 17:11:53',NULL,1,19,NULL,NULL,NULL),(14,28,102,1,1,649.00,NULL,'红米5A','8天超长待机，137g轻巧机身，高通骁龙处理器小米6X低至1299，点击抢购','201808270028001','windir','2018-08-27 17:18:02',NULL,1,19,NULL,NULL,NULL),(15,28,103,1,1,699.00,NULL,'红米5A','8天超长待机，137g轻巧机身，高通骁龙处理器小米6X低至1299，点击抢购','201808270028001','windir','2018-08-28 10:22:45',NULL,1,19,NULL,NULL,NULL),(16,29,106,1,1,5499.00,NULL,'Apple iPhone 8 Plus','【限时限量抢购】Apple产品年中狂欢节，好物尽享，美在智慧！速来 >> 勾选[保障服务][原厂保2年]，获得AppleCare+全方位服务计划，原厂延保售后无忧。','201808270029001','windir','2018-08-28 10:50:50',NULL,1,19,NULL,NULL,NULL),(19,36,163,1,3,100.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','202002210036001','windir','2020-02-25 15:51:59',NULL,1,29,'NIKE','6799345','[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(20,36,164,1,2,120.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','202002210036001','windir','2020-02-25 15:54:23',NULL,1,29,'NIKE','6799345','[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(21,36,164,1,2,120.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','202002210036001','windir','2020-02-25 16:49:53',NULL,1,29,'NIKE','6799345','[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(22,26,110,1,3,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-04 15:34:24',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(23,27,98,1,7,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-04 15:35:43',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(24,26,110,1,4,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-04 16:58:17',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(25,27,98,1,2,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-04 16:58:23',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(26,28,102,1,4,649.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','小米 红米5A 全网通版 3GB+32GB 香槟金 移动联通电信4G手机 双卡双待','8天超长待机，137g轻巧机身，高通骁龙处理器小米6X低至1299，点击抢购','201808270028001','windir','2020-05-04 16:58:26',NULL,1,19,'小米','7437789','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(27,29,106,1,1,4999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus 64GB 红色特别版 移动联通电信4G手机','【限时限量抢购】Apple产品年中狂欢节，好物尽享，美在智慧！速来 >> 勾选[保障服务][原厂保2年]，获得AppleCare+全方位服务计划，原厂延保售后无忧。','201808270029001','windir','2020-05-04 16:58:29',NULL,1,19,'苹果','7437799','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(28,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-04 17:07:20',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(29,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-04 17:07:23',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(30,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-04 17:08:14',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(31,29,106,1,1,4999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus 64GB 红色特别版 移动联通电信4G手机','【限时限量抢购】Apple产品年中狂欢节，好物尽享，美在智慧！速来 >> 勾选[保障服务][原厂保2年]，获得AppleCare+全方位服务计划，原厂延保售后无忧。','201808270029001','windir','2020-05-04 17:09:56',NULL,1,19,'苹果','7437799','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(32,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-04 17:13:50',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(33,27,98,1,2,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-04 17:16:15',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(34,36,164,1,1,120.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','202002210036002','windir','2020-05-04 17:19:20',NULL,1,29,'NIKE','6799345','[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(35,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-05 10:41:39',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(36,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-05 10:41:55',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(37,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-05 10:42:57',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(38,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-05 14:29:28',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(39,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-05 14:32:52',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(40,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-05 14:33:20',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(41,27,98,1,2,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-05 14:49:13',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(42,26,111,1,1,3999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026002','windir','2020-05-05 15:26:05',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(43,28,102,1,1,649.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','小米 红米5A 全网通版 3GB+32GB 香槟金 移动联通电信4G手机 双卡双待','8天超长待机，137g轻巧机身，高通骁龙处理器小米6X低至1299，点击抢购','201808270028001','windir','2020-05-16 15:16:04',NULL,1,19,'小米','7437789','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(44,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-16 15:18:00',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(45,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 15:00:16',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(46,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-17 15:00:22',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(47,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 15:14:14',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(48,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 15:20:03',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(49,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 15:21:54',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(50,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 16:07:22',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(51,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-17 16:07:26',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(52,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 19:33:36',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(53,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-17 19:33:39',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(54,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 19:39:07',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(55,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-17 19:41:26',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(56,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-18 16:50:00',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(57,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-18 20:22:04',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(58,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-18 20:22:08',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(59,27,98,1,2,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-23 16:21:13',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(60,27,98,1,2,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-05-23 17:01:28',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(61,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-24 09:36:50',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(62,26,110,1,2,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-05-24 09:44:39',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(63,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-06-07 17:01:48',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(64,27,98,1,2,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-06-14 15:24:40',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(65,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-06-21 14:27:13',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(66,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-06-21 15:12:14',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(67,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-06-21 15:12:53',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(68,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','windir','2020-06-21 15:15:10',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(69,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2020-06-27 10:27:48',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(70,27,98,1,1,2699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','201808270027001','windir','2022-10-28 14:50:46',NULL,1,19,'小米','7437788','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(71,37,201,1,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 紫色 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','windir','2022-10-28 15:27:32',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(72,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','windir','2022-11-09 15:14:46',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(73,38,213,1,1,3599.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）','【11.11大爱超大爱】iPad9代限量抢购，价格优惠，更享以旧换新至高补贴325元！！快来抢购吧！！ ','202210280038001','windir','2022-11-09 15:25:28',NULL,1,53,'苹果','100044025833','[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(74,37,201,1,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','windir','2022-11-09 15:26:04',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(75,45,239,1,1,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_01.jpg','OPPO Reno8 8GB+128GB 鸢尾紫 新配色上市 80W超级闪充 5000万水光人像三摄 3200万前置索尼镜头 5G手机','【11.11提前购机享价保，好货不用等，系统申请一键价保补差!】【Reno8Pro爆款优惠】 ','202211080045001','windir','2022-11-09 16:16:23',NULL,1,19,'OPPO','10052147850350','[{\"key\":\"颜色\",\"value\":\"鸢尾紫\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(76,45,239,1,1,2299.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_01.jpg','OPPO Reno8 8GB+128GB 鸢尾紫 新配色上市 80W超级闪充 5000万水光人像三摄 3200万前置索尼镜头 5G手机','【11.11提前购机享价保，好货不用等，系统申请一键价保补差!】【Reno8Pro爆款优惠】 ','202211080045001','windir','2022-11-09 16:18:36',NULL,1,19,'OPPO','10052147850350','[{\"key\":\"颜色\",\"value\":\"鸢尾紫\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(77,41,225,1,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量 墨羽 12GB+256GB 5G智能手机 小米 红米','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','windir','2022-11-10 15:19:36',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(78,37,201,1,2,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','windir','2022-11-10 15:19:44',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(79,38,213,1,1,3599.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）','【11.11大爱超大爱】iPad9代限量抢购，价格优惠，更享以旧换新至高补贴325元！！快来抢购吧！！ ','202210280038001','windir','2022-11-11 15:37:40',NULL,1,53,'苹果','100044025833','[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(80,38,213,1,1,3599.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）','【11.11大爱超大爱】iPad9代限量抢购，价格优惠，更享以旧换新至高补贴325元！！快来抢购吧！！ ','202210280038001','windir','2022-11-11 15:38:12',NULL,1,53,'苹果','100044025833','[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(81,38,213,1,3,3599.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）','【11.11大爱超大爱】iPad9代限量抢购，价格优惠，更享以旧换新至高补贴325元！！快来抢购吧！！ ','202210280038001','windir','2022-11-11 15:38:22',NULL,1,53,'苹果','100044025833','[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(82,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','windir','2022-11-11 16:07:23',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(83,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','windir','2022-11-11 16:13:11',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(84,37,201,1,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','windir','2022-11-11 16:15:05',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(85,28,102,1,1,649.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','小米 红米5A 全网通版 3GB+32GB 香槟金 移动联通电信4G手机 双卡双待','8天超长待机，137g轻巧机身，高通骁龙处理器小米6X低至1299，点击抢购','201808270028001','windir','2022-11-11 16:21:05',NULL,1,19,'小米','7437789','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(86,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','windir','2022-11-16 10:22:47',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(87,41,225,1,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','windir','2022-11-16 10:22:51',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(88,39,217,1,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg','小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑','【双十一大促来袭】指定型号至高优惠1000，以旧换新至高补贴1000元，晒单赢好礼','202210280039001','windir','2022-11-16 10:22:54',NULL,1,54,'小米','100023207945','[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(89,37,201,1,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','windir','2022-11-16 10:23:16',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(90,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','test','2022-12-21 15:49:00',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(91,37,201,1,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','test','2022-12-21 15:49:42',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(92,41,225,1,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','test','2022-12-21 15:49:53',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(93,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','test','2022-12-21 15:51:03',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(94,41,225,1,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','test','2022-12-21 15:51:28',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(95,41,225,1,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','test','2022-12-21 16:45:16',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(96,40,221,1,2,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','test','2022-12-21 16:46:41',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(97,40,221,1,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','test','2022-12-21 16:50:10',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(98,40,221,1,2,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','test','2022-12-23 09:55:11',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(99,26,110,1,1,3788.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','test','2023-01-10 15:39:03',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(100,26,111,1,1,3899.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026002','test','2023-01-10 16:58:08',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(101,26,110,1,1,3699.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','201806070026001','test','2023-01-10 17:10:26',NULL,1,19,'华为','6946605','[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(102,40,221,11,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','member','2023-05-11 15:24:33',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(103,41,225,11,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','member','2023-05-11 15:24:37',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(104,38,213,11,1,3599.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款','【11.11大爱超大爱】iPad9代限量抢购，价格优惠，更享以旧换新至高补贴325元！！快来抢购吧！！ ','202210280038001','member','2023-05-11 15:30:32',NULL,1,53,'苹果','100044025833','[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(105,39,217,11,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg','小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑','【双十一大促来袭】指定型号至高优惠1000，以旧换新至高补贴1000元，晒单赢好礼','202210280039001','member','2023-05-11 15:31:38',NULL,1,54,'小米','100023207945','[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(106,44,235,11,1,369.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_02.jpg','三星（SAMSUNG）500GB SSD固态硬盘 M.2接口(NVMe协议)','【满血无缓存！进店抽百元E卡，部分型号白条三期免息】兼具速度与可靠性！读速高达3500MB/s，全功率模式！点击 ','202211080044001','member','2023-05-11 15:32:16',NULL,1,55,'三星','100018768480','[{\"key\":\"颜色\",\"value\":\"新品980｜NVMe PCIe3.0*4\"},{\"key\":\"版本\",\"value\":\"512GB\"}]'),(107,42,229,11,1,4999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_01.jpg','HUAWEI Mate 50 直屏旗舰 超光变XMAGE影像 北斗卫星消息','【华为Mate50新品上市】内置66W华为充电套装，超光变XMAGE影像,北斗卫星消息，鸿蒙操作系统3.0！立即抢购！华为新品可持续计划，猛戳》 ','202211040042001','member','2023-05-11 15:32:44',NULL,1,19,'华为','100035295081','[{\"key\":\"颜色\",\"value\":\"曜金黑\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(108,37,201,11,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','202210280037001','member','2023-05-11 15:34:32',NULL,1,19,'苹果','100038005189','[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(109,40,221,11,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','member','2023-05-11 15:35:02',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(110,41,225,11,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','member','2023-05-11 15:35:21',NULL,1,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(111,40,221,11,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','member','2023-05-11 15:36:57',NULL,1,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(112,39,217,11,1,5999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg','小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑','【双十一大促来袭】指定型号至高优惠1000，以旧换新至高补贴1000元，晒单赢好礼','202210280039001','member','2023-05-11 15:37:04',NULL,1,54,'小米','100023207945','[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(113,40,221,11,1,2999.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','202211040040001','member','2023-05-11 15:37:57',NULL,0,19,'小米','100027789721','[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(114,41,225,11,1,2099.00,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','202211040041001','member','2023-05-11 15:38:03',NULL,0,19,'小米','100035246702','[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]');
/*!40000 ALTER TABLE `oms_cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_company_address`
--

DROP TABLE IF EXISTS `oms_company_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_company_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_name` varchar(200) DEFAULT NULL COMMENT '地址名称',
  `send_status` int(1) DEFAULT NULL COMMENT '默认发货地址：0->否；1->是',
  `receive_status` int(1) DEFAULT NULL COMMENT '是否默认收货地址：0->否；1->是',
  `name` varchar(64) DEFAULT NULL COMMENT '收发货人姓名',
  `phone` varchar(64) DEFAULT NULL COMMENT '收货人电话',
  `province` varchar(64) DEFAULT NULL COMMENT '省/直辖市',
  `city` varchar(64) DEFAULT NULL COMMENT '市',
  `region` varchar(64) DEFAULT NULL COMMENT '区',
  `detail_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='公司收发货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_company_address`
--

LOCK TABLES `oms_company_address` WRITE;
/*!40000 ALTER TABLE `oms_company_address` DISABLE KEYS */;
INSERT INTO `oms_company_address` VALUES (1,'深圳发货点',1,1,'大梨','18000000000','广东省','深圳市','南山区','科兴科学园'),(2,'北京发货点',0,0,'大梨','18000000000','北京市',NULL,'南山区','科兴科学园'),(3,'南京发货点',0,0,'大梨','18000000000','江苏省','南京市','南山区','科兴科学园');
/*!40000 ALTER TABLE `oms_company_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order`
--

DROP TABLE IF EXISTS `oms_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单id',
  `member_id` bigint(20) NOT NULL,
  `coupon_id` bigint(20) DEFAULT NULL,
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '提交时间',
  `member_username` varchar(64) DEFAULT NULL COMMENT '用户帐号',
  `total_amount` decimal(10,2) DEFAULT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) DEFAULT NULL COMMENT '应付金额（实际支付金额）',
  `freight_amount` decimal(10,2) DEFAULT NULL COMMENT '运费金额',
  `promotion_amount` decimal(10,2) DEFAULT NULL COMMENT '促销优化金额（促销价、满减、阶梯价）',
  `integration_amount` decimal(10,2) DEFAULT NULL COMMENT '积分抵扣金额',
  `coupon_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠券抵扣金额',
  `discount_amount` decimal(10,2) DEFAULT NULL COMMENT '管理员后台调整订单使用的折扣金额',
  `pay_type` int(1) DEFAULT NULL COMMENT '支付方式：0->未支付；1->支付宝；2->微信',
  `source_type` int(1) DEFAULT NULL COMMENT '订单来源：0->PC订单；1->app订单',
  `status` int(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `delivery_company` varchar(64) DEFAULT NULL COMMENT '物流公司(配送方式)',
  `delivery_sn` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `auto_confirm_day` int(11) DEFAULT NULL COMMENT '自动确认时间（天）',
  `integration` int(11) DEFAULT NULL COMMENT '可以获得的积分',
  `growth` int(11) DEFAULT NULL COMMENT '可以活动的成长值',
  `promotion_info` varchar(100) DEFAULT NULL COMMENT '活动信息',
  `bill_type` int(1) DEFAULT NULL COMMENT '发票类型：0->不开发票；1->电子发票；2->纸质发票',
  `bill_header` varchar(200) DEFAULT NULL COMMENT '发票抬头',
  `bill_content` varchar(200) DEFAULT NULL COMMENT '发票内容',
  `bill_receiver_phone` varchar(32) DEFAULT NULL COMMENT '收票人电话',
  `bill_receiver_email` varchar(64) DEFAULT NULL COMMENT '收票人邮箱',
  `receiver_name` varchar(100) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(32) NOT NULL COMMENT '收货人电话',
  `receiver_post_code` varchar(32) DEFAULT NULL COMMENT '收货人邮编',
  `receiver_province` varchar(32) DEFAULT NULL COMMENT '省份/直辖市',
  `receiver_city` varchar(32) DEFAULT NULL COMMENT '城市',
  `receiver_region` varchar(32) DEFAULT NULL COMMENT '区',
  `receiver_detail_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
  `note` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `confirm_status` int(1) DEFAULT NULL COMMENT '确认收货状态：0->未确认；1->已确认',
  `delete_status` int(1) NOT NULL DEFAULT '0' COMMENT '删除状态：0->未删除；1->已删除',
  `use_integration` int(11) DEFAULT NULL COMMENT '下单时使用的积分',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `comment_time` datetime DEFAULT NULL COMMENT '评价时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `subscription_start_date` date DEFAULT NULL COMMENT '套餐开始日期',
  `subscription_end_date` date DEFAULT NULL COMMENT '套餐结束日期',
  `subscription_status` tinyint(4) DEFAULT '0' COMMENT '订阅状态: 0-待生效, 1-有效, 2-过期, 3-已取消',
  `auto_renew` tinyint(4) DEFAULT '0' COMMENT '是否自动续费: 0-否, 1-是',
  `order_type` tinyint(4) DEFAULT '0' COMMENT '订单类型: 0-普通商品, 1-套餐订单, 2-积分充值订单',
  `business_type` tinyint(4) DEFAULT '0' COMMENT '业务类型: 0-商品购买, 1-套餐订阅, 2-积分充值, 3-套餐续费',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_oms_order_subscription_status` (`subscription_status`),
  KEY `idx_oms_order_subscription_end` (`subscription_end_date`),
  KEY `idx_oms_order_type` (`order_type`),
  KEY `idx_oms_order_business_type` (`business_type`)
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order`
--

LOCK TABLES `oms_order` WRITE;
/*!40000 ALTER TABLE `oms_order` DISABLE KEYS */;
INSERT INTO `oms_order` VALUES (12,1,2,'201809150101000001','2018-09-15 12:24:27','test',18732.00,16377.75,20.00,2344.25,0.00,10.00,10.00,0,1,4,'','',15,13284,13284,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','江苏省','常州市','天宁区','东晓街道','111',0,0,NULL,NULL,NULL,NULL,NULL,'2019-11-09 16:50:28',NULL,NULL,0,0,0,0),(13,1,2,'201809150102000002','2018-09-15 14:24:29','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,1,1,1,'','',15,13284,13284,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,1000,'2018-10-11 14:04:19',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(14,1,2,'201809130101000001','2018-09-13 16:57:40','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,2,1,3,'顺丰快递','201707196398345',15,13284,13284,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2018-10-13 13:44:04','2018-10-16 13:43:41','2022-11-11 16:19:34',NULL,NULL,NULL,NULL,0,0,0,0),(15,1,2,'201809130102000002','2018-09-13 17:03:00','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,1,1,3,'顺丰快递','201707196398346',15,13284,13284,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,1,NULL,'2018-10-13 13:44:54','2018-10-16 13:45:01','2018-10-18 14:05:31',NULL,NULL,NULL,NULL,0,0,0,0),(16,1,2,'201809140101000001','2018-09-14 16:16:16','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,2,1,4,NULL,NULL,15,13284,13284,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(17,1,2,'201809150101000003','2018-09-15 12:24:27','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,0,1,4,'顺丰快递','201707196398345',15,NULL,NULL,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,'2018-10-12 14:01:28',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(18,1,2,'201809150102000004','2018-09-15 14:24:29','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,1,1,1,'圆通快递','xx',15,NULL,NULL,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,1000,NULL,'2018-10-16 14:42:17',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(19,1,2,'201809130101000003','2018-09-13 16:57:40','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,2,1,2,NULL,NULL,15,NULL,NULL,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(20,1,2,'201809130102000004','2018-09-13 17:03:00','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,1,1,3,NULL,NULL,15,NULL,NULL,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(21,1,2,'201809140101000002','2018-09-14 16:16:16','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,2,1,4,NULL,NULL,15,18682,18682,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(22,1,2,'201809150101000005','2018-09-15 12:24:27','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,0,1,4,'顺丰快递','201707196398345',15,0,0,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,'2018-10-12 14:01:28',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(23,1,2,'201809150102000006','2018-09-15 14:24:29','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,1,1,1,'顺丰快递','xxx',15,0,0,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,1000,NULL,'2018-10-16 14:41:28',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(24,1,2,'201809130101000005','2018-09-13 16:57:40','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,2,1,2,NULL,NULL,15,18682,18682,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(25,1,2,'201809130102000006','2018-09-13 17:03:00','test',18732.00,16377.75,10.00,2344.25,0.00,10.00,5.00,1,1,4,NULL,NULL,15,18682,18682,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨22','18033441849','518000','北京市','北京城区','东城区','东城街道','xxx',0,1,NULL,NULL,NULL,NULL,NULL,'2018-10-30 15:08:31',NULL,NULL,0,0,0,0),(26,1,2,'201809140101000003','2018-09-14 16:16:16','test',18732.00,16377.75,0.00,2344.25,0.00,10.00,0.00,2,1,4,NULL,NULL,15,18682,18682,'单品促销,打折优惠：满3件，打7.50折,满减优惠：满1000.00元，减120.00元,满减优惠：满1000.00元，减120.00元,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(27,1,NULL,'202002250100000001','2020-02-25 15:59:20','test',540.00,540.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,NULL,0,0,'无优惠,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','南山区','科兴科学园',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(28,1,NULL,'202002250100000002','2020-02-25 16:05:47','test',540.00,540.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,NULL,0,0,'无优惠,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','南山区','科兴科学园',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(29,1,NULL,'202002250100000003','2020-02-25 16:07:58','test',540.00,540.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,NULL,0,0,'无优惠,无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','南山区','科兴科学园',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(30,1,NULL,'202002250100000004','2020-02-25 16:50:13','test',240.00,240.00,20.00,0.00,0.00,0.00,10.00,0,1,3,'顺丰快递','12333333',NULL,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','南山区','科兴科学园',NULL,1,0,NULL,'2020-02-25 16:53:29','2020-02-25 16:54:03','2020-05-17 19:38:15',NULL,'2020-02-25 16:52:51',NULL,NULL,0,0,0,0),(31,1,26,'202005160100000001','2020-05-16 15:16:54','test',13623.00,11842.40,0.00,1629.60,1.00,150.00,0.00,0,1,4,NULL,NULL,15,13623,13623,'满减优惠：满5000.00元，减500.00元;打折优惠：满2件，打8.00折;满减优惠：满500.00元，减50.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(32,1,NULL,'202005170100000001','2020-05-17 15:00:38','test',6487.00,6187.00,0.00,300.00,0.00,0.00,0.00,1,1,1,NULL,NULL,15,6487,6487,'满减优惠：满3000.00元，减300.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2020-05-17 15:33:28',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(33,1,NULL,'202005170100000002','2020-05-17 15:14:18','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(34,1,NULL,'202005170100000003','2020-05-17 15:20:10','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(35,1,NULL,'202005170100000004','2020-05-17 15:22:03','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,2,1,3,'顺丰快递','123',15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2020-05-17 15:29:07','2020-05-17 15:30:24','2020-05-17 15:41:45',NULL,NULL,NULL,NULL,0,0,0,0),(36,1,NULL,'202005170100000005','2020-05-17 16:59:26','test',10275.00,9775.00,0.00,500.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,10275,10275,'满减优惠：满5000.00元，减500.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(37,1,NULL,'202005170100000006','2020-05-17 19:33:48','test',6487.00,6187.00,0.00,300.00,0.00,0.00,0.00,1,1,3,'顺丰快递','aadd',15,6487,6487,'满减优惠：满3000.00元，减300.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2020-05-17 19:33:59','2020-05-17 19:34:59','2020-05-17 19:35:50',NULL,NULL,NULL,NULL,0,0,0,0),(38,1,NULL,'202005170100000007','2020-05-17 19:39:10','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(39,1,NULL,'202005170100000008','2020-05-17 19:41:30','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,1,1,3,'顺丰快递','sdf',15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,1,NULL,'2020-05-17 19:41:41','2020-05-17 19:42:07','2020-05-17 19:42:36',NULL,NULL,NULL,NULL,0,0,0,0),(40,1,NULL,'202005180100000001','2020-05-18 16:50:03','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2020-05-18 16:50:29',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(41,1,26,'202005180100000002','2020-05-18 20:22:24','test',6487.00,6037.00,0.00,300.00,0.00,150.00,0.00,1,1,3,'顺丰快递','12313',15,6487,6487,'满减优惠：满3000.00元，减300.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','清水河街道',NULL,1,0,NULL,'2020-05-18 20:22:29','2020-05-18 20:23:03','2020-05-18 20:23:20',NULL,NULL,NULL,NULL,0,0,0,0),(42,1,NULL,'202005230100000001','2020-05-23 16:21:27','test',5398.00,4318.40,0.00,1079.60,0.00,0.00,0.00,2,1,1,NULL,NULL,15,5398,5398,'打折优惠：满2件，打8.00折',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','清水河街道',NULL,0,0,NULL,'2020-05-23 16:21:30',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(43,1,NULL,'202005230100000002','2020-05-23 17:01:33','test',5398.00,4318.40,0.00,1079.60,0.00,0.00,0.00,0,1,4,NULL,NULL,15,5398,5398,'打折优惠：满2件，打8.00折',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(44,1,NULL,'202005240100000001','2020-05-24 09:37:07','test',7576.00,7076.00,0.00,500.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,7576,7576,'满减优惠：满5000.00元，减500.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(45,1,25,'202006070100000001','2020-06-07 17:02:04','test',10275.00,9674.90,0.00,500.00,0.00,100.10,0.00,1,1,1,NULL,NULL,15,10275,10275,'满减优惠：满5000.00元，减500.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','清水河街道',NULL,0,0,NULL,'2020-06-07 17:02:17',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(46,1,24,'202006210100000001','2020-06-21 14:27:34','test',9186.00,7796.40,0.00,1379.60,0.00,10.00,0.00,2,1,1,NULL,NULL,15,9186,9186,'满减优惠：满3000.00元，减300.00元;打折优惠：满2件，打8.00折',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2020-06-21 14:27:38',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(47,1,NULL,'202006210100000002','2020-06-21 15:13:06','test',6487.00,6187.00,0.00,300.00,0.00,0.00,0.00,1,1,3,'顺丰快递','123131',15,6487,6487,'满减优惠：满3000.00元，减300.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','清水河街道',NULL,1,0,NULL,'2020-06-21 15:13:12','2020-06-21 15:13:44','2020-06-21 15:13:58',NULL,NULL,NULL,NULL,0,0,0,0),(48,1,26,'202006210100000003','2020-06-21 15:15:18','test',3788.00,3338.00,0.00,300.00,0.00,150.00,0.00,2,1,3,'圆通快递','12313',15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2020-06-21 15:15:20','2020-06-21 15:15:48','2020-06-21 15:15:58',NULL,NULL,NULL,NULL,0,0,0,0),(49,1,NULL,'202006270100000001','2020-06-27 10:27:56','test',2699.00,2699.00,0.00,0.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,2699,2699,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','清水河街道',NULL,0,0,NULL,'2020-06-27 10:27:58',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(50,1,NULL,'202210280100000001','2022-10-28 14:50:58','test',2699.00,2699.00,0.00,0.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,2699,2699,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','清水河街道',NULL,0,0,NULL,'2022-10-28 14:51:02',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(51,1,NULL,'202210280100000002','2022-10-28 15:27:41','test',5999.00,5999.00,0.00,0.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-10-28 15:27:44',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(52,1,30,'202211090100000001','2022-11-09 15:14:58','test',2999.00,2799.00,0.00,0.00,0.00,200.00,0.00,2,1,3,'顺丰快递','1233',15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2022-11-09 15:15:00','2022-11-09 15:16:12','2022-11-09 15:16:31',NULL,NULL,NULL,NULL,0,0,0,0),(53,1,27,'202211090100000002','2022-11-09 15:25:38','test',3599.00,3589.00,0.00,0.00,0.00,10.00,0.00,2,1,1,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-11-09 15:25:41',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(54,1,29,'202211090100000003','2022-11-09 15:26:11','test',5999.00,5399.00,0.00,0.00,0.00,600.00,0.00,2,1,1,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-11-09 15:26:13',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(55,1,NULL,'202211100100000001','2022-11-10 16:57:59','test',11998.00,11998.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(56,1,28,'202211110100000001','2022-11-11 16:12:42','test',2999.00,2899.00,0.00,0.00,0.00,100.00,0.00,2,1,1,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-11-11 16:12:48',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(57,1,NULL,'202211110100000002','2022-11-11 16:13:14','test',2999.00,2999.00,0.00,0.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-11-11 16:13:21',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(58,1,NULL,'202211110100000003','2022-11-11 16:15:08','test',5999.00,5999.00,0.00,0.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-11-11 16:17:46',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(59,1,NULL,'202211110100000004','2022-11-11 16:21:12','test',649.00,599.00,0.00,50.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,649,649,'满减优惠：满500.00元，减50.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(60,1,NULL,'202211160100000001','2022-11-16 10:36:08','test',11097.00,11097.00,0.00,0.00,0.00,0.00,0.00,2,1,3,'顺丰快递','1234555',15,0,0,'无优惠;无优惠;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2022-11-16 10:37:25','2022-11-16 10:42:50','2022-11-16 10:44:40',NULL,NULL,NULL,NULL,0,0,0,0),(61,1,NULL,'202212210100000001','2022-12-21 15:49:08','test',2999.00,2999.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(62,1,NULL,'202212210100000002','2022-12-21 15:49:57','test',8098.00,8098.00,0.00,0.00,0.00,0.00,0.00,2,1,3,'顺丰快递','SDFERR7845',15,0,0,'无优惠;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2022-12-21 15:50:00','2022-12-21 15:50:23','2022-12-21 15:50:33',NULL,NULL,NULL,NULL,0,0,0,0),(63,1,NULL,'202212210100000003','2022-12-21 15:51:09','test',2999.00,2999.00,0.00,0.00,0.00,0.00,0.00,2,1,2,'顺丰快递','112',15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-12-21 15:51:11','2023-01-10 10:14:12',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(64,1,NULL,'202212210100000004','2022-12-21 15:51:35','test',2099.00,2099.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(65,1,28,'202212210100000005','2022-12-21 16:53:07','test',5098.00,4788.00,0.00,200.00,10.00,100.00,0.00,2,1,2,'圆通快递','115',15,0,0,'满减优惠：满2000.00元，减200.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2022-12-21 16:53:58','2023-01-10 10:14:12',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(66,1,NULL,'202301100100000001','2023-01-10 15:34:59','test',5998.00,5798.00,0.00,200.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,0,0,'满减优惠：满2000.00元，减200.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(67,1,NULL,'202301100100000002','2023-01-10 15:39:07','test',3788.00,3488.00,0.00,300.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,3788,3788,'满减优惠：满3000.00元，减300.00元',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2023-01-10 15:39:16',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(68,1,NULL,'202301100100000003','2023-01-10 16:58:19','test',3999.00,3899.00,0.00,100.00,0.00,0.00,0.00,2,1,1,NULL,NULL,15,3788,3788,'单品促销',NULL,NULL,NULL,NULL,NULL,'大梨','18033441849','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2023-01-10 16:58:21',NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(69,11,30,'202305110100000001','2023-05-11 15:28:56','member',5098.00,4698.00,0.00,200.00,0.00,200.00,0.00,2,1,3,'顺丰快递','1231313',15,0,0,'满减优惠：满2000.00元，减200.00元;无优惠',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2023-05-11 15:28:59','2023-05-11 15:30:08','2023-05-11 15:30:16',NULL,NULL,NULL,NULL,0,0,0,0),(70,11,NULL,'202305110100000002','2023-05-11 15:30:36','member',3599.00,3599.00,0.00,0.00,0.00,0.00,0.00,2,1,3,'顺丰快递','232342',15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2023-05-11 15:30:40','2023-05-11 15:31:22','2023-05-11 15:31:30',NULL,NULL,NULL,NULL,0,0,0,0),(71,11,NULL,'202305110100000003','2023-05-11 15:31:55','member',5999.00,5999.00,0.00,0.00,0.00,0.00,0.00,0,1,4,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(72,11,NULL,'202305110100000004','2023-05-11 15:33:13','member',5368.00,5368.00,0.00,0.00,0.00,0.00,0.00,2,1,2,'圆通快递','1231434',15,0,0,'无优惠;无优惠',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2023-05-11 15:33:21','2023-05-11 15:33:43',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(73,11,NULL,'202305110100000005','2023-05-11 15:34:39','member',5999.00,5999.00,0.00,0.00,0.00,0.00,0.00,0,1,0,NULL,NULL,15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,0,0,0),(74,11,NULL,'202305110100000006','2023-05-11 15:35:05','member',2999.00,2799.00,0.00,200.00,0.00,0.00,0.00,2,1,2,'顺丰快递','123131',15,0,0,'满减优惠：满2000.00元，减200.00元',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,0,0,NULL,'2023-05-11 15:35:08','2023-05-11 15:36:00',NULL,NULL,NULL,NULL,NULL,0,0,0,0),(75,11,NULL,'202305110100000007','2023-05-11 15:35:24','member',2099.00,2099.00,0.00,0.00,0.00,0.00,0.00,2,1,3,'顺丰快递','123131311',15,0,0,'无优惠',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2023-05-11 15:35:26','2023-05-11 15:36:11','2023-05-11 15:36:34',NULL,NULL,NULL,NULL,0,0,0,0),(76,11,28,'202305110100000008','2023-05-11 15:37:16','member',8998.00,8698.00,0.00,200.00,0.00,100.00,0.00,2,1,3,'顺丰快递','1231313',15,0,0,'无优惠;满减优惠：满2000.00元，减200.00元',NULL,NULL,NULL,NULL,NULL,'小李','18961511111','518000','广东省','深圳市','福田区','东晓街道',NULL,1,0,NULL,'2023-05-11 15:37:18','2023-05-11 15:37:33','2023-05-11 15:37:48',NULL,NULL,NULL,NULL,0,0,0,0);
/*!40000 ALTER TABLE `oms_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order_item`
--

DROP TABLE IF EXISTS `oms_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `product_id` bigint(20) DEFAULT NULL,
  `product_pic` varchar(500) DEFAULT NULL,
  `product_name` varchar(200) DEFAULT NULL,
  `product_brand` varchar(200) DEFAULT NULL,
  `product_sn` varchar(64) DEFAULT NULL,
  `product_price` decimal(10,2) DEFAULT NULL COMMENT '销售价格',
  `product_quantity` int(11) DEFAULT NULL COMMENT '购买数量',
  `product_sku_id` bigint(20) DEFAULT NULL COMMENT '商品sku编号',
  `product_sku_code` varchar(50) DEFAULT NULL COMMENT '商品sku条码',
  `product_category_id` bigint(20) DEFAULT NULL COMMENT '商品分类id',
  `promotion_name` varchar(200) DEFAULT NULL COMMENT '商品促销名称',
  `promotion_amount` decimal(10,2) DEFAULT NULL COMMENT '商品促销分解金额',
  `coupon_amount` decimal(10,2) DEFAULT NULL COMMENT '优惠券优惠分解金额',
  `integration_amount` decimal(10,2) DEFAULT NULL COMMENT '积分优惠分解金额',
  `real_amount` decimal(10,2) DEFAULT NULL COMMENT '该商品经过优惠后的分解金额',
  `gift_integration` int(11) DEFAULT '0',
  `gift_growth` int(11) DEFAULT '0',
  `product_attr` varchar(500) DEFAULT NULL COMMENT '商品销售属性:[{"key":"颜色","value":"颜色"},{"key":"容量","value":"4G"}]',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单中所包含的商品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order_item`
--

LOCK TABLES `oms_order_item` WRITE;
/*!40000 ALTER TABLE `oms_order_item` DISABLE KEYS */;
INSERT INTO `oms_order_item` VALUES (21,12,'201809150101000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','6946605',3788.00,1,90,'201806070026001',19,'单品促销',200.00,2.02,0.00,3585.98,3788,3788,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(22,12,'201809150101000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','7437788',2699.00,3,98,'201808270027001',19,'打折优惠：满3件，打7.50折',674.75,1.44,0.00,2022.81,2699,2699,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(23,12,'201809150101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',57.60,0.35,0.00,591.05,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(24,12,'201809150101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',699.00,1,103,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',62.40,0.37,0.00,636.23,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(25,12,'201809150101000001',29,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus','苹果','7437799',5499.00,1,106,'201808270029001',19,'无优惠',0.00,2.94,0.00,5496.06,5499,5499,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(26,13,'201809150102000002',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','6946605',3788.00,1,90,'201806070026001',19,'单品促销',200.00,2.02,0.00,3585.98,3788,3788,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(27,13,'201809150102000002',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','7437788',2699.00,3,98,'201808270027001',19,'打折优惠：满3件，打7.50折',674.75,1.44,0.00,2022.81,2699,2699,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(28,13,'201809150102000002',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',57.60,0.35,0.00,591.05,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(29,13,'201809150102000002',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',699.00,1,103,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',62.40,0.37,0.00,636.23,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(30,13,'201809150102000002',29,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus','苹果','7437799',5499.00,1,106,'201808270029001',19,'无优惠',0.00,2.94,0.00,5496.06,5499,5499,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(31,14,'201809130101000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','6946605',3788.00,1,90,'201806070026001',19,'单品促销',200.00,2.02,0.00,3585.98,3788,3788,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(32,14,'201809130101000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','7437788',2699.00,3,98,'201808270027001',19,'打折优惠：满3件，打7.50折',674.75,1.44,0.00,2022.81,2699,2699,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(33,14,'201809130101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',57.60,0.35,0.00,591.05,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(34,14,'201809130101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',699.00,1,103,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',62.40,0.37,0.00,636.23,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(35,14,'201809130101000001',29,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus','苹果','7437799',5499.00,1,106,'201808270029001',19,'无优惠',0.00,2.94,0.00,5496.06,5499,5499,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(36,15,'201809130101000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','6946605',3788.00,1,90,'201806070026001',19,'单品促销',200.00,2.02,0.00,3585.98,3788,3788,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(37,15,'201809130101000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','7437788',2699.00,3,98,'201808270027001',19,'打折优惠：满3件，打7.50折',674.75,1.44,0.00,2022.81,2699,2699,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(38,15,'201809130101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',57.60,0.35,0.00,591.05,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(39,15,'201809130101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',699.00,1,103,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',62.40,0.37,0.00,636.23,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(40,15,'201809130101000001',29,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus','苹果','7437799',5499.00,1,106,'201808270029001',19,'无优惠',0.00,2.94,0.00,5496.06,5499,5499,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(41,16,'201809140101000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','6946605',3788.00,1,90,'201806070026001',19,'单品促销',200.00,2.02,0.00,3585.98,3788,3788,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(42,16,'201809140101000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','7437788',2699.00,3,98,'201808270027001',19,'打折优惠：满3件，打7.50折',674.75,1.44,0.00,2022.81,2699,2699,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(43,16,'201809140101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',57.60,0.35,0.00,591.05,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(44,16,'201809140101000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','7437789',699.00,1,103,'201808270028001',19,'满减优惠：满1000.00元，减120.00元',62.40,0.37,0.00,636.23,649,649,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(45,16,'201809140101000001',29,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','Apple iPhone 8 Plus','苹果','7437799',5499.00,1,106,'201808270029001',19,'无优惠',0.00,2.94,0.00,5496.06,5499,5499,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(46,27,'202002250100000001',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',100.00,3,163,'202002210036001',29,'无优惠',0.00,0.00,0.00,100.00,0,0,NULL),(47,27,'202002250100000001',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',120.00,2,164,'202002210036001',29,'无优惠',0.00,0.00,0.00,120.00,0,0,NULL),(48,28,'202002250100000002',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',100.00,3,163,'202002210036001',29,'无优惠',0.00,0.00,0.00,100.00,0,0,NULL),(49,28,'202002250100000002',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',120.00,2,164,'202002210036001',29,'无优惠',0.00,0.00,0.00,120.00,0,0,NULL),(50,29,'202002250100000003',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',100.00,3,163,'202002210036001',29,'无优惠',0.00,0.00,0.00,100.00,0,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(51,29,'202002250100000003',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',120.00,2,164,'202002210036001',29,'无优惠',0.00,0.00,0.00,120.00,0,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(52,30,'202002250100000004',36,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','NIKE','6799345',120.00,2,164,'202002210036001',29,'无优惠',0.00,0.00,0.00,120.00,0,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(53,31,'202005160100000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,2,110,'201806070026001',19,'满减优惠：满5000.00元，减500.00元',250.00,75.00,0.28,3462.72,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(54,31,'202005160100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,2,98,'201808270027001',19,'打折优惠：满2件，打8.00折',539.80,0.00,0.20,2159.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(55,31,'202005160100000001',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','小米 红米5A 全网通版 3GB+32GB 香槟金 移动联通电信4G手机 双卡双待','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满500.00元，减50.00元',50.00,0.00,0.05,598.95,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(56,32,'202005170100000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(57,32,'202005170100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(58,33,'202005170100000002',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(59,34,'202005170100000003',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(60,35,'202005170100000004',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(61,36,'202005170100000005',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,2,110,'201806070026001',19,'满减优惠：满5000.00元，减500.00元',250.00,0.00,0.00,3538.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(62,36,'202005170100000005',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(63,37,'202005170100000006',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(64,37,'202005170100000006',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(65,38,'202005170100000007',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(66,39,'202005170100000008',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(67,40,'202005180100000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(68,41,'202005180100000002',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,150.00,0.00,3338.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(69,41,'202005180100000002',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(70,42,'202005230100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,2,98,'201808270027001',19,'打折优惠：满2件，打8.00折',539.80,0.00,0.00,2159.20,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(71,43,'202005230100000002',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,2,98,'201808270027001',19,'打折优惠：满2件，打8.00折',539.80,0.00,0.00,2159.20,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(72,44,'202005240100000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,2,110,'201806070026001',19,'满减优惠：满5000.00元，减500.00元',250.00,0.00,0.00,3538.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(73,45,'202006070100000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,2,110,'201806070026001',19,'满减优惠：满5000.00元，减500.00元',250.00,36.90,0.00,3501.10,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(74,45,'202006070100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,26.30,0.00,2672.70,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(75,46,'202006210100000001',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,4.12,0.00,3483.88,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(76,46,'202006210100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,2,98,'201808270027001',19,'打折优惠：满2件，打8.00折',539.80,2.94,0.00,2156.26,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(77,47,'202006210100000002',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(78,47,'202006210100000002',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(79,48,'202006210100000003',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,150.00,0.00,3338.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(80,49,'202006270100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(81,50,'202210280100000001',27,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','小米','7437788',2699.00,1,98,'201808270027001',19,'无优惠',0.00,0.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(82,51,'202210280100000002',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 紫色 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,1,201,'202210280037001',19,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(83,52,'202211090100000001',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','小米','100027789721',2999.00,1,221,'202211040040001',19,'无优惠',0.00,200.00,0.00,2799.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(84,53,'202211090100000002',38,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）','苹果','100044025833',3599.00,1,213,'202210280038001',53,'无优惠',0.00,10.00,0.00,3589.00,0,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(85,54,'202211090100000003',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,1,201,'202210280037001',19,'无优惠',0.00,600.00,0.00,5399.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(86,55,'202211100100000001',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,2,201,'202210280037001',19,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(87,56,'202211110100000001',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','小米','100027789721',2999.00,1,221,'202211040040001',19,'无优惠',0.00,100.00,0.00,2899.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(88,57,'202211110100000002',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','小米','100027789721',2999.00,1,221,'202211040040001',19,'无优惠',0.00,0.00,0.00,2999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(89,58,'202211110100000003',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,1,201,'202210280037001',19,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(90,59,'202211110100000004',28,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','小米 红米5A 全网通版 3GB+32GB 香槟金 移动联通电信4G手机 双卡双待','小米','7437789',649.00,1,102,'201808270028001',19,'满减优惠：满500.00元，减50.00元',50.00,0.00,0.00,599.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(91,60,'202211160100000001',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,1,201,'202210280037001',19,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(92,60,'202211160100000001',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'无优惠',0.00,0.00,0.00,2999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(93,60,'202211160100000001',41,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','小米','100035246702',2099.00,1,225,'202211040041001',19,'无优惠',0.00,0.00,0.00,2099.00,0,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(94,61,'202212210100000001',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'无优惠',0.00,0.00,0.00,2999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(95,62,'202212210100000002',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,1,201,'202210280037001',19,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(96,62,'202212210100000002',41,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','小米','100035246702',2099.00,1,225,'202211040041001',19,'无优惠',0.00,0.00,0.00,2099.00,0,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(97,63,'202212210100000003',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'无优惠',0.00,0.00,0.00,2999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(98,64,'202212210100000004',41,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','小米','100035246702',2099.00,1,225,'202211040041001',19,'无优惠',0.00,0.00,0.00,2099.00,0,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(99,65,'202212210100000005',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'满减优惠：满2000.00元，减200.00元',200.00,58.80,5.88,2734.32,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(100,65,'202212210100000005',41,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','小米','100035246702',2099.00,1,225,'202211040041001',19,'无优惠',0.00,41.20,4.12,2053.68,0,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(101,66,'202301100100000001',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,2,221,'202211040040001',19,'满减优惠：满2000.00元，减200.00元',100.00,0.00,0.00,2899.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(102,67,'202301100100000002',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3788.00,1,110,'201806070026001',19,'满减优惠：满3000.00元，减300.00元',300.00,0.00,0.00,3488.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(103,68,'202301100100000003',26,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20 ','华为','6946605',3999.00,1,111,'201806070026002',19,'单品促销',100.00,0.00,0.00,3899.00,0,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(104,69,'202305110100000001',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'满减优惠：满2000.00元，减200.00元',200.00,117.60,0.00,2681.40,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(105,69,'202305110100000001',41,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','小米','100035246702',2099.00,1,225,'202211040041001',19,'无优惠',0.00,82.40,0.00,2016.60,0,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(106,70,'202305110100000002',38,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','Apple iPad 10.9英寸平板电脑 2022年款','苹果','100044025833',3599.00,1,213,'202210280038001',53,'无优惠',0.00,0.00,0.00,3599.00,0,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(107,71,'202305110100000003',39,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg','小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑','小米','100023207945',5999.00,1,217,'202210280039001',54,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(108,72,'202305110100000004',42,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_01.jpg','HUAWEI Mate 50 直屏旗舰 超光变XMAGE影像 北斗卫星消息','华为','100035295081',4999.00,1,229,'202211040042001',19,'无优惠',0.00,0.00,0.00,4999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"曜金黑\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(109,72,'202305110100000004',44,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_02.jpg','三星（SAMSUNG）500GB SSD固态硬盘 M.2接口(NVMe协议)','三星','100018768480',369.00,1,235,'202211080044001',55,'无优惠',0.00,0.00,0.00,369.00,0,0,'[{\"key\":\"颜色\",\"value\":\"新品980｜NVMe PCIe3.0*4\"},{\"key\":\"版本\",\"value\":\"512GB\"}]'),(110,73,'202305110100000005',37,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','苹果','100038005189',5999.00,1,201,'202210280037001',19,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(111,74,'202305110100000006',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'满减优惠：满2000.00元，减200.00元',200.00,0.00,0.00,2799.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(112,75,'202305110100000007',41,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','小米','100035246702',2099.00,1,225,'202211040041001',19,'无优惠',0.00,0.00,0.00,2099.00,0,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(113,76,'202305110100000008',39,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg','小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑','小米','100023207945',5999.00,1,217,'202210280039001',54,'无优惠',0.00,0.00,0.00,5999.00,0,0,'[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(114,76,'202305110100000008',40,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','小米','100027789721',2999.00,1,221,'202211040040001',19,'满减优惠：满2000.00元，减200.00元',200.00,100.00,0.00,2699.00,0,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]');
/*!40000 ALTER TABLE `oms_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order_operate_history`
--

DROP TABLE IF EXISTS `oms_order_operate_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order_operate_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `operate_man` varchar(100) DEFAULT NULL COMMENT '操作人：用户；系统；后台管理员',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  `order_status` int(1) DEFAULT NULL COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单操作历史记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order_operate_history`
--

LOCK TABLES `oms_order_operate_history` WRITE;
/*!40000 ALTER TABLE `oms_order_operate_history` DISABLE KEYS */;
INSERT INTO `oms_order_operate_history` VALUES (5,12,'后台管理员','2018-10-12 14:01:29',2,'完成发货'),(6,13,'后台管理员','2018-10-12 14:01:29',2,'完成发货'),(7,12,'后台管理员','2018-10-12 14:13:10',4,'订单关闭:买家退货'),(8,13,'后台管理员','2018-10-12 14:13:10',4,'订单关闭:买家退货'),(9,22,'后台管理员','2018-10-15 16:31:48',4,'订单关闭:xxx'),(10,22,'后台管理员','2018-10-15 16:35:08',4,'订单关闭:xxx'),(11,22,'后台管理员','2018-10-15 16:35:59',4,'订单关闭:xxx'),(12,17,'后台管理员','2018-10-15 16:43:40',4,'订单关闭:xxx'),(13,25,'后台管理员','2018-10-15 16:52:14',4,'订单关闭:xxx'),(14,26,'后台管理员','2018-10-15 16:52:14',4,'订单关闭:xxx'),(15,23,'后台管理员','2018-10-16 14:41:28',2,'完成发货'),(16,13,'后台管理员','2018-10-16 14:42:17',2,'完成发货'),(17,18,'后台管理员','2018-10-16 14:42:17',2,'完成发货'),(18,26,'后台管理员','2018-10-30 14:37:44',4,'订单关闭:关闭订单'),(19,25,'后台管理员','2018-10-30 15:07:01',0,'修改收货人信息'),(20,25,'后台管理员','2018-10-30 15:08:13',0,'修改费用信息'),(21,25,'后台管理员','2018-10-30 15:08:31',0,'修改备注信息：xxx'),(22,25,'后台管理员','2018-10-30 15:08:39',4,'订单关闭:2222'),(23,12,'后台管理员','2019-11-09 16:50:28',4,'修改备注信息：111'),(24,30,'后台管理员','2020-02-25 16:52:37',0,'修改费用信息'),(25,30,'后台管理员','2020-02-25 16:52:51',0,'修改费用信息'),(26,30,'后台管理员','2020-02-25 16:54:03',2,'完成发货'),(27,35,'后台管理员','2020-05-17 15:30:24',2,'完成发货'),(28,37,'后台管理员','2020-05-17 19:35:00',2,'完成发货'),(29,39,'后台管理员','2020-05-17 19:42:08',2,'完成发货'),(30,41,'后台管理员','2020-05-18 20:23:04',2,'完成发货'),(31,47,'后台管理员','2020-06-21 15:13:44',2,'完成发货'),(32,48,'后台管理员','2020-06-21 15:15:49',2,'完成发货'),(33,52,'后台管理员','2022-11-09 15:16:13',2,'完成发货'),(34,60,'后台管理员','2022-11-16 10:42:50',2,'完成发货'),(35,62,'后台管理员','2022-12-21 15:50:24',2,'完成发货'),(36,63,'后台管理员','2023-01-10 10:08:34',2,'完成发货'),(37,65,'后台管理员','2023-01-10 10:08:34',2,'完成发货'),(38,69,'后台管理员','2023-05-11 15:30:08',2,'完成发货'),(39,70,'后台管理员','2023-05-11 15:31:22',2,'完成发货'),(40,72,'后台管理员','2023-05-11 15:33:43',2,'完成发货'),(41,74,'后台管理员','2023-05-11 15:36:00',2,'完成发货'),(42,75,'后台管理员','2023-05-11 15:36:11',2,'完成发货'),(43,76,'后台管理员','2023-05-11 15:37:34',2,'完成发货');
/*!40000 ALTER TABLE `oms_order_operate_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order_payment`
--

DROP TABLE IF EXISTS `oms_order_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `payment_method` varchar(32) NOT NULL COMMENT '支付方式',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `transaction_id` varchar(128) DEFAULT NULL COMMENT '第三方交易号',
  `status` tinyint(4) DEFAULT '0' COMMENT '支付状态: 0-待支付, 1-支付成功, 2-支付失败',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `payment_scene` varchar(32) DEFAULT NULL COMMENT '支付场景: subscription-套餐订阅, credits-积分充值, renewal-套餐续费',
  `callback_status` tinyint(4) DEFAULT '0' COMMENT '回调处理状态: 0-未处理, 1-处理成功, 2-处理失败',
  `callback_retry_count` int(11) DEFAULT '0' COMMENT '回调重试次数',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_payment_scene` (`payment_scene`),
  KEY `idx_callback_status` (`callback_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单支付记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order_payment`
--

LOCK TABLES `oms_order_payment` WRITE;
/*!40000 ALTER TABLE `oms_order_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `oms_order_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order_return_apply`
--

DROP TABLE IF EXISTS `oms_order_return_apply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order_return_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单id',
  `company_address_id` bigint(20) DEFAULT NULL COMMENT '收货地址表id',
  `product_id` bigint(20) DEFAULT NULL COMMENT '退货商品id',
  `order_sn` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `create_time` datetime DEFAULT NULL COMMENT '申请时间',
  `member_username` varchar(64) DEFAULT NULL COMMENT '会员用户名',
  `return_amount` decimal(10,2) DEFAULT NULL COMMENT '退款金额',
  `return_name` varchar(100) DEFAULT NULL COMMENT '退货人姓名',
  `return_phone` varchar(100) DEFAULT NULL COMMENT '退货人电话',
  `status` int(1) DEFAULT NULL COMMENT '申请状态：0->待处理；1->退货中；2->已完成；3->已拒绝',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `product_pic` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `product_name` varchar(200) DEFAULT NULL COMMENT '商品名称',
  `product_brand` varchar(200) DEFAULT NULL COMMENT '商品品牌',
  `product_attr` varchar(500) DEFAULT NULL COMMENT '商品销售属性：颜色：红色；尺码：xl;',
  `product_count` int(11) DEFAULT NULL COMMENT '退货数量',
  `product_price` decimal(10,2) DEFAULT NULL COMMENT '商品单价',
  `product_real_price` decimal(10,2) DEFAULT NULL COMMENT '商品实际支付单价',
  `reason` varchar(200) DEFAULT NULL COMMENT '原因',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `proof_pics` varchar(1000) DEFAULT NULL COMMENT '凭证图片，以逗号隔开',
  `handle_note` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `handle_man` varchar(100) DEFAULT NULL COMMENT '处理人员',
  `receive_man` varchar(100) DEFAULT NULL COMMENT '收货人',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `receive_note` varchar(500) DEFAULT NULL COMMENT '收货备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单退货申请';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order_return_apply`
--

LOCK TABLES `oms_order_return_apply` WRITE;
/*!40000 ALTER TABLE `oms_order_return_apply` DISABLE KEYS */;
INSERT INTO `oms_order_return_apply` VALUES (3,12,1,26,'201809150101000001','2018-10-17 14:34:57','test',0.00,'大梨','18000000000',2,'2022-11-11 10:16:18','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg,http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','111','admin','admin','2022-11-11 10:16:26',NULL),(4,12,2,27,'201809150101000001','2018-10-17 14:40:21','test',3585.98,'大梨','18000000000',1,'2018-10-18 13:54:10','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','','已经处理了','admin',NULL,NULL,NULL),(5,12,3,28,'201809150101000001','2018-10-17 14:44:18','test',3585.98,'大梨','18000000000',2,'2018-10-18 13:55:28','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','','已经处理了','admin','admin','2018-10-18 13:55:58','已经处理了'),(8,13,NULL,28,'201809150102000002','2018-10-17 14:44:18','test',NULL,'大梨','18000000000',3,'2018-10-18 13:57:12','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','','理由不够充分','admin',NULL,NULL,NULL),(9,14,2,26,'201809130101000001','2018-10-17 14:34:57','test',3500.00,'大梨','18000000000',2,'2018-10-24 15:44:56','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','','呵呵','admin','admin','2018-10-24 15:46:35','收货了'),(10,14,NULL,27,'201809130101000001','2018-10-17 14:40:21','test',NULL,'大梨','18000000000',3,'2018-10-24 15:46:57','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','','就是不退','admin',NULL,NULL,NULL),(11,14,2,28,'201809130101000001','2018-10-17 14:44:18','test',591.05,'大梨','18000000000',1,'2018-10-24 17:09:04','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','','可以退款','admin',NULL,NULL,NULL),(12,15,3,26,'201809130102000002','2018-10-17 14:34:57','test',3500.00,'大梨','18000000000',2,'2018-10-24 17:22:54','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','','退货了','admin','admin','2018-10-24 17:23:06','收货了'),(13,15,NULL,27,'201809130102000002','2018-10-17 14:40:21','test',NULL,'大梨','18000000000',3,'2018-10-24 17:23:30','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','','无法退货','admin',NULL,NULL,NULL),(15,16,NULL,26,'201809140101000001','2018-10-17 14:34:57','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','',NULL,NULL,NULL,NULL,NULL),(16,16,NULL,27,'201809140101000001','2018-10-17 14:40:21','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','',NULL,NULL,NULL,NULL,NULL),(17,16,NULL,28,'201809140101000001','2018-10-17 14:44:18','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','',NULL,NULL,NULL,NULL,NULL),(18,17,NULL,26,'201809150101000003','2018-10-17 14:34:57','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','',NULL,NULL,NULL,NULL,NULL),(19,17,NULL,27,'201809150101000003','2018-10-17 14:40:21','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','',NULL,NULL,NULL,NULL,NULL),(20,17,NULL,28,'201809150101000003','2018-10-17 14:44:18','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','',NULL,NULL,NULL,NULL,NULL),(21,18,NULL,26,'201809150102000004','2018-10-17 14:34:57','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','',NULL,NULL,NULL,NULL,NULL),(22,18,NULL,27,'201809150102000004','2018-10-17 14:40:21','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','',NULL,NULL,NULL,NULL,NULL),(23,18,NULL,28,'201809150102000004','2018-10-17 14:44:18','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','',NULL,NULL,NULL,NULL,NULL),(24,19,NULL,26,'201809130101000003','2018-10-17 14:34:57','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','华为 HUAWEI P20','华为','颜色：金色;内存：16G',1,3788.00,3585.98,'质量问题','老是卡','',NULL,NULL,NULL,NULL,NULL),(25,19,NULL,27,'201809130101000003','2018-10-17 14:40:21','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','小米8','小米','颜色：黑色;内存：32G',1,2699.00,2022.81,'质量问题','不够高端','',NULL,NULL,NULL,NULL,NULL),(26,19,NULL,28,'201809130101000003','2018-10-17 14:44:18','test',NULL,'大梨','18000000000',0,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','红米5A','小米','颜色：金色;内存：16G',1,649.00,591.05,'质量问题','颜色太土','',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `oms_order_return_apply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order_return_reason`
--

DROP TABLE IF EXISTS `oms_order_return_reason`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order_return_reason` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '退货类型',
  `sort` int(11) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '状态：0->不启用；1->启用',
  `create_time` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='退货原因表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order_return_reason`
--

LOCK TABLES `oms_order_return_reason` WRITE;
/*!40000 ALTER TABLE `oms_order_return_reason` DISABLE KEYS */;
INSERT INTO `oms_order_return_reason` VALUES (1,'质量问题',1,1,'2018-10-17 10:00:45'),(2,'尺码太大',1,1,'2018-10-17 10:01:03'),(3,'颜色不喜欢',1,1,'2018-10-17 10:01:13'),(4,'7天无理由退货',1,1,'2018-10-17 10:01:47'),(5,'价格问题',1,0,'2018-10-17 10:01:57'),(12,'发票问题',0,1,'2018-10-19 16:28:36'),(13,'其他问题',0,1,'2018-10-19 16:28:51'),(14,'物流问题',0,1,'2018-10-19 16:29:01'),(15,'售后问题',0,1,'2018-10-19 16:29:11');
/*!40000 ALTER TABLE `oms_order_return_reason` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oms_order_setting`
--

DROP TABLE IF EXISTS `oms_order_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oms_order_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flash_order_overtime` int(11) DEFAULT NULL COMMENT '秒杀订单超时关闭时间(分)',
  `normal_order_overtime` int(11) DEFAULT NULL COMMENT '正常订单超时时间(分)',
  `confirm_overtime` int(11) DEFAULT NULL COMMENT '发货后自动确认收货时间（天）',
  `finish_overtime` int(11) DEFAULT NULL COMMENT '自动完成交易时间，不能申请售后（天）',
  `comment_overtime` int(11) DEFAULT NULL COMMENT '订单完成后自动好评时间（天）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='订单设置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oms_order_setting`
--

LOCK TABLES `oms_order_setting` WRITE;
/*!40000 ALTER TABLE `oms_order_setting` DISABLE KEYS */;
INSERT INTO `oms_order_setting` VALUES (1,60,120,15,7,7);
/*!40000 ALTER TABLE `oms_order_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_album`
--

DROP TABLE IF EXISTS `pms_album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_album` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `cover_pic` varchar(1000) DEFAULT NULL,
  `pic_count` int(11) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='相册表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_album`
--

LOCK TABLES `pms_album` WRITE;
/*!40000 ALTER TABLE `pms_album` DISABLE KEYS */;
/*!40000 ALTER TABLE `pms_album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_album_pic`
--

DROP TABLE IF EXISTS `pms_album_pic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_album_pic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `album_id` bigint(20) DEFAULT NULL,
  `pic` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='画册图片表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_album_pic`
--

LOCK TABLES `pms_album_pic` WRITE;
/*!40000 ALTER TABLE `pms_album_pic` DISABLE KEYS */;
/*!40000 ALTER TABLE `pms_album_pic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_brand`
--

DROP TABLE IF EXISTS `pms_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `first_letter` varchar(8) DEFAULT NULL COMMENT '首字母',
  `sort` int(11) DEFAULT NULL,
  `factory_status` int(1) DEFAULT NULL COMMENT '是否为品牌制造商：0->不是；1->是',
  `show_status` int(1) DEFAULT NULL,
  `product_count` int(11) DEFAULT NULL COMMENT '产品数量',
  `product_comment_count` int(11) DEFAULT NULL COMMENT '产品评论数量',
  `logo` varchar(255) DEFAULT NULL COMMENT '品牌logo',
  `big_pic` varchar(255) DEFAULT NULL COMMENT '专区大图',
  `brand_story` text COMMENT '品牌故事',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='品牌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_brand`
--

LOCK TABLES `pms_brand` WRITE;
/*!40000 ALTER TABLE `pms_brand` DISABLE KEYS */;
INSERT INTO `pms_brand` VALUES (1,'万和','W',0,1,1,100,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5b07ca8aN4e127d2f.jpg','http://img13.360buyimg.com/cms/jfs/t1/121860/35/2430/187800/5ec4e294E22f3ffcc/1e233b65b94ba192.jpg','万和成立于1993年8月，总部位于广东顺德国家级高新技术开发区内，是国内生产规模最大的燃气具专业制造企业，也是中国燃气具发展战略的首倡者和推动者、中国五金制品协会燃气用具分会第三届理事长单位。'),(2,'三星','S',100,1,1,100,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/57201b47N7bf15715.jpg','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_banner_01.png','三星集团（英文：SAMSUNG、韩文：삼성）是韩国最大的跨国企业集团，三星集团包括众多的国际下属企业，旗下子公司有：三星电子、三星物产、三星人寿保险等，业务涉及电子、金融、机械、化学等众多领域。'),(3,'华为','H',100,1,1,100,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5abf6f26N31658aa2.jpg','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/huawei_banner_01.png','荣耀品牌成立于2013年,是华为旗下手机双品牌之一。荣耀以“创新、品质、服务”为核心战略,为全球年轻人提供潮酷的全场景智能化体验,打造年轻人向往的先锋文化和潮流生活方式'),(4,'格力','G',30,1,1,100,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/timg (3).jpg',NULL,'Victoria\'s Secret的故事'),(5,'方太','F',20,1,1,100,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/timg (4).jpg',NULL,'Victoria\'s Secret的故事'),(6,'小米','M',500,1,1,100,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5565f5a2N0b8169ae.jpg','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/xiaomi_banner_01.png','小米公司正式成立于2010年4月，是一家专注于高端智能手机、互联网电视自主研发的创新型科技企业。主要由前谷歌、微软、摩托、金山等知名公司的顶尖人才组建。'),(21,'OPPO','O',0,1,1,88,500,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/timg(6).jpg','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_banner_01.png','OPPO于2008年推出第一款“笑脸手机”，由此开启探索和引领至美科技之旅。今天，OPPO凭借以Find和R系列手机为核心的智能终端产品，以及OPPO+等互联网服务，让全球消费者尽享至美科技。'),(49,'七匹狼','S',200,1,1,77,400,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190525/qipilang.png',NULL,'BOOB的故事'),(50,'海澜之家','H',200,1,1,66,300,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a5c69b9N5d6c5696.jpg','http://img10.360buyimg.com/cms/jfs/t1/133148/4/1605/470028/5edaf5ccEd7a687a9/e0a007631361ff75.jpg','“海澜之家”（英文缩写：HLA）是海澜之家股份有限公司旗下的服装品牌，总部位于中国江苏省无锡市江阴市，主要采用连锁零售的模式，销售男性服装、配饰与相关产品。'),(51,'苹果','A',200,1,1,55,200,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/49b30bb0377030d1.jpg','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/apple_banner_01.png','苹果公司(Apple Inc. )是美国的一家高科技公司。 由史蒂夫·乔布斯、斯蒂夫·沃兹尼亚克和罗·韦恩(Ron Wayne)等人于1976年4月1日创立,并命名为美国苹果电脑公司(Apple Computer Inc. ),2007年1月9日更名为苹果公司,总部位于加利福尼亚州的...'),(58,'NIKE','N',0,1,0,33,100,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/timg (51).jpg','','NIKE的故事'),(59,'测试品牌','C',0,0,0,NULL,NULL,'http://localhost:9000/mall/20220609/Snipaste_2022-06-08_14-35-53.png','http://localhost:9000/mall/20220609/biji_05.jpg','12345');
/*!40000 ALTER TABLE `pms_brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_comment`
--

DROP TABLE IF EXISTS `pms_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `member_nick_name` varchar(255) DEFAULT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `star` int(3) DEFAULT NULL COMMENT '评价星数：0->5',
  `member_ip` varchar(64) DEFAULT NULL COMMENT '评价的ip',
  `create_time` datetime DEFAULT NULL,
  `show_status` int(1) DEFAULT NULL,
  `product_attribute` varchar(255) DEFAULT NULL COMMENT '购买时的商品属性',
  `collect_couont` int(11) DEFAULT NULL,
  `read_count` int(11) DEFAULT NULL,
  `content` text,
  `pics` varchar(1000) DEFAULT NULL COMMENT '上传图片地址，以逗号隔开',
  `member_icon` varchar(255) DEFAULT NULL COMMENT '评论用户头像',
  `replay_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品评价表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_comment`
--

LOCK TABLES `pms_comment` WRITE;
/*!40000 ALTER TABLE `pms_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `pms_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_comment_replay`
--

DROP TABLE IF EXISTS `pms_comment_replay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_comment_replay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment_id` bigint(20) DEFAULT NULL,
  `member_nick_name` varchar(255) DEFAULT NULL,
  `member_icon` varchar(255) DEFAULT NULL,
  `content` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `type` int(1) DEFAULT NULL COMMENT '评论人员类型；0->会员；1->管理员',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='产品评价回复表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_comment_replay`
--

LOCK TABLES `pms_comment_replay` WRITE;
/*!40000 ALTER TABLE `pms_comment_replay` DISABLE KEYS */;
/*!40000 ALTER TABLE `pms_comment_replay` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_feight_template`
--

DROP TABLE IF EXISTS `pms_feight_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_feight_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `charge_type` int(1) DEFAULT NULL COMMENT '计费类型:0->按重量；1->按件数',
  `first_weight` decimal(10,2) DEFAULT NULL COMMENT '首重kg',
  `first_fee` decimal(10,2) DEFAULT NULL COMMENT '首费（元）',
  `continue_weight` decimal(10,2) DEFAULT NULL,
  `continme_fee` decimal(10,2) DEFAULT NULL,
  `dest` varchar(255) DEFAULT NULL COMMENT '目的地（省、市）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='运费模版';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_feight_template`
--

LOCK TABLES `pms_feight_template` WRITE;
/*!40000 ALTER TABLE `pms_feight_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `pms_feight_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_member_price`
--

DROP TABLE IF EXISTS `pms_member_price`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_member_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `member_level_id` bigint(20) DEFAULT NULL,
  `member_price` decimal(10,2) DEFAULT NULL COMMENT '会员价格',
  `member_level_name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=426 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品会员价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_member_price`
--

LOCK TABLES `pms_member_price` WRITE;
/*!40000 ALTER TABLE `pms_member_price` DISABLE KEYS */;
INSERT INTO `pms_member_price` VALUES (26,7,1,500.00,NULL),(27,8,1,500.00,NULL),(28,9,1,500.00,NULL),(29,10,1,500.00,NULL),(30,11,1,500.00,NULL),(31,12,1,500.00,NULL),(32,13,1,500.00,NULL),(33,14,1,500.00,NULL),(37,18,1,500.00,NULL),(44,7,2,480.00,NULL),(45,7,3,450.00,NULL),(52,22,1,NULL,NULL),(53,22,2,NULL,NULL),(54,22,3,NULL,NULL),(58,24,1,NULL,NULL),(59,24,2,NULL,NULL),(60,24,3,NULL,NULL),(112,23,1,88.00,'黄金会员'),(113,23,2,88.00,'白金会员'),(114,23,3,66.00,'钻石会员'),(246,36,1,NULL,'黄金会员'),(247,36,2,NULL,'白金会员'),(248,36,3,NULL,'钻石会员'),(249,35,1,NULL,'黄金会员'),(250,35,2,NULL,'白金会员'),(251,35,3,NULL,'钻石会员'),(258,30,1,NULL,'黄金会员'),(259,30,2,NULL,'白金会员'),(260,30,3,NULL,'钻石会员'),(261,31,1,NULL,'黄金会员'),(262,31,2,NULL,'白金会员'),(263,31,3,NULL,'钻石会员'),(264,32,1,NULL,'黄金会员'),(265,32,2,NULL,'白金会员'),(266,32,3,NULL,'钻石会员'),(270,33,1,NULL,'黄金会员'),(271,33,2,NULL,'白金会员'),(272,33,3,NULL,'钻石会员'),(273,34,1,NULL,'黄金会员'),(274,34,2,NULL,'白金会员'),(275,34,3,NULL,'钻石会员'),(285,27,1,NULL,'黄金会员'),(286,27,2,NULL,'白金会员'),(287,27,3,NULL,'钻石会员'),(294,28,1,NULL,'黄金会员'),(295,28,2,NULL,'白金会员'),(296,28,3,NULL,'钻石会员'),(297,29,1,NULL,'黄金会员'),(298,29,2,NULL,'白金会员'),(299,29,3,NULL,'钻石会员'),(315,37,1,NULL,'黄金会员'),(316,37,2,NULL,'白金会员'),(317,37,3,NULL,'钻石会员'),(381,38,1,NULL,'黄金会员'),(382,38,2,NULL,'白金会员'),(383,38,3,NULL,'钻石会员'),(384,39,1,NULL,'黄金会员'),(385,39,2,NULL,'白金会员'),(386,39,3,NULL,'钻石会员'),(387,41,1,NULL,'黄金会员'),(388,41,2,NULL,'白金会员'),(389,41,3,NULL,'钻石会员'),(390,42,1,NULL,'黄金会员'),(391,42,2,NULL,'白金会员'),(392,42,3,NULL,'钻石会员'),(393,43,1,NULL,'黄金会员'),(394,43,2,NULL,'白金会员'),(395,43,3,NULL,'钻石会员'),(396,44,1,NULL,'黄金会员'),(397,44,2,NULL,'白金会员'),(398,44,3,NULL,'钻石会员'),(399,45,1,NULL,'黄金会员'),(400,45,2,NULL,'白金会员'),(401,45,3,NULL,'钻石会员'),(402,40,1,NULL,'黄金会员'),(403,40,2,NULL,'白金会员'),(404,40,3,NULL,'钻石会员'),(423,26,1,NULL,'黄金会员'),(424,26,2,NULL,'白金会员'),(425,26,3,NULL,'钻石会员');
/*!40000 ALTER TABLE `pms_member_price` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product`
--

DROP TABLE IF EXISTS `pms_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand_id` bigint(20) DEFAULT NULL,
  `product_category_id` bigint(20) DEFAULT NULL,
  `feight_template_id` bigint(20) DEFAULT NULL,
  `product_attribute_category_id` bigint(20) DEFAULT NULL,
  `name` varchar(200) NOT NULL,
  `pic` varchar(255) DEFAULT NULL,
  `product_sn` varchar(64) NOT NULL COMMENT '货号',
  `delete_status` int(1) DEFAULT NULL COMMENT '删除状态：0->未删除；1->已删除',
  `publish_status` int(1) DEFAULT NULL COMMENT '上架状态：0->下架；1->上架',
  `new_status` int(1) DEFAULT NULL COMMENT '新品状态:0->不是新品；1->新品',
  `recommand_status` int(1) DEFAULT NULL COMMENT '推荐状态；0->不推荐；1->推荐',
  `verify_status` int(1) DEFAULT NULL COMMENT '审核状态：0->未审核；1->审核通过',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `sale` int(11) DEFAULT NULL COMMENT '销量',
  `price` decimal(10,2) DEFAULT NULL,
  `promotion_price` decimal(10,2) DEFAULT NULL COMMENT '促销价格',
  `gift_growth` int(11) DEFAULT '0' COMMENT '赠送的成长值',
  `gift_point` int(11) DEFAULT '0' COMMENT '赠送的积分',
  `use_point_limit` int(11) DEFAULT NULL COMMENT '限制使用的积分数',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '副标题',
  `description` text COMMENT '商品描述',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '市场价',
  `stock` int(11) DEFAULT NULL COMMENT '库存',
  `low_stock` int(11) DEFAULT NULL COMMENT '库存预警值',
  `unit` varchar(16) DEFAULT NULL COMMENT '单位',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '商品重量，默认为克',
  `preview_status` int(1) DEFAULT NULL COMMENT '是否为预告商品：0->不是；1->是',
  `service_ids` varchar(64) DEFAULT NULL COMMENT '以逗号分割的产品服务：1->无忧退货；2->快速退款；3->免费包邮',
  `keywords` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `album_pics` varchar(255) DEFAULT NULL COMMENT '画册图片，连产品图片限制为5张，以逗号分割',
  `detail_title` varchar(255) DEFAULT NULL,
  `detail_desc` text,
  `detail_html` text COMMENT '产品详情网页内容',
  `detail_mobile_html` text COMMENT '移动端网页详情',
  `promotion_start_time` datetime DEFAULT NULL COMMENT '促销开始时间',
  `promotion_end_time` datetime DEFAULT NULL COMMENT '促销结束时间',
  `promotion_per_limit` int(11) DEFAULT NULL COMMENT '活动限购数量',
  `promotion_type` int(1) DEFAULT NULL COMMENT '促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购',
  `brand_name` varchar(255) DEFAULT NULL COMMENT '品牌名称',
  `product_category_name` varchar(255) DEFAULT NULL COMMENT '商品分类名称',
  `product_type` tinyint(4) DEFAULT '1' COMMENT '商品类型: 1-普通商品, 2-积分商品, 3-套餐商品',
  `credit_amount` int(11) DEFAULT NULL COMMENT '积分数量(积分商品时使用)',
  `subscription_period` int(11) DEFAULT NULL COMMENT '套餐周期(天): 30-月套餐, 90-季套餐, 365-年套餐',
  `daily_credits` int(11) DEFAULT NULL COMMENT '每日积分额度',
  `total_credits` int(11) DEFAULT NULL COMMENT '总积分额度(用于显示)',
  `max_concurrent_tasks` int(11) DEFAULT '1' COMMENT '最大并发任务数',
  `priority_level` tinyint(4) DEFAULT '0' COMMENT '任务优先级: 0-普通, 1-高, 2-最高',
  `features` json DEFAULT NULL COMMENT '套餐特性配置JSON',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_pms_product_type` (`product_type`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product`
--

LOCK TABLES `pms_product` WRITE;
/*!40000 ALTER TABLE `pms_product` DISABLE KEYS */;
INSERT INTO `pms_product` VALUES (1,49,7,0,0,'银色星芒刺绣网纱底裤','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,1,1,1,1,100,0,100.00,NULL,0,100,NULL,'111','111',120.00,100,20,'件',1000.00,0,NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤',NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤',NULL,NULL,NULL,0,'七匹狼','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(2,49,7,0,0,'银色星芒刺绣网纱底裤2','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86578',1,1,1,1,1,1,0,100.00,NULL,0,100,NULL,'111','111',120.00,100,20,'件',1000.00,0,NULL,'银色星芒刺绣网纱底裤2','银色星芒刺绣网纱底裤',NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','<p>银色星芒刺绣网纱底裤</p>','<p>银色星芒刺绣网纱底裤</p>',NULL,NULL,NULL,0,'七匹狼','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(3,1,7,0,0,'银色星芒刺绣网纱底裤3','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86579',1,1,1,1,1,1,0,100.00,NULL,0,100,NULL,'111','111',120.00,100,20,'件',1000.00,0,NULL,'银色星芒刺绣网纱底裤3','银色星芒刺绣网纱底裤',NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤',NULL,NULL,NULL,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(4,1,7,0,0,'银色星芒刺绣网纱底裤4','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86580',1,1,1,1,1,1,0,100.00,NULL,0,100,NULL,'111','111',120.00,100,20,'件',1000.00,0,NULL,'银色星芒刺绣网纱底裤4','银色星芒刺绣网纱底裤',NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤',NULL,NULL,NULL,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(5,1,7,0,0,'银色星芒刺绣网纱底裤5','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86581',1,0,1,1,1,1,0,100.00,NULL,0,100,NULL,'111','111',120.00,100,20,'件',1000.00,0,NULL,'银色星芒刺绣网纱底裤5','银色星芒刺绣网纱底裤',NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤',NULL,NULL,NULL,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(6,1,7,0,0,'银色星芒刺绣网纱底裤6','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86582',1,1,1,1,1,1,0,100.00,NULL,0,100,NULL,'111','111',120.00,100,20,'件',1000.00,0,NULL,'银色星芒刺绣网纱底裤6','银色星芒刺绣网纱底裤',NULL,'银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤','银色星芒刺绣网纱底裤',NULL,NULL,NULL,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(7,1,7,0,1,'女式超柔软拉毛运动开衫','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,0,0,0,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(8,1,7,0,1,'女式超柔软拉毛运动开衫1','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,0,0,0,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(9,1,7,0,1,'女式超柔软拉毛运动开衫1','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,0,0,0,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(10,1,7,0,1,'女式超柔软拉毛运动开衫1','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,0,0,0,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(11,1,7,0,1,'女式超柔软拉毛运动开衫1','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,1,0,1,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(12,1,7,0,1,'女式超柔软拉毛运动开衫2','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,1,0,1,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(13,1,7,0,1,'女式超柔软拉毛运动开衫3','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,1,0,1,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(14,1,7,0,1,'女式超柔软拉毛运动开衫3','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,0,0,1,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(18,1,7,0,1,'女式超柔软拉毛运动开衫3','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180522/web.png','No86577',1,0,0,1,0,0,0,249.00,0.00,0,100,0,'匠心剪裁，垂感质地','匠心剪裁，垂感质地',299.00,100,0,'件',0.00,0,'string','女式超柔软拉毛运动开衫','string','string','string','string','string','string','2018-04-26 10:41:03','2018-04-26 10:41:03',0,0,'万和','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(22,6,7,0,1,'test','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/1522738681.jpg','',1,1,0,0,0,0,0,0.00,NULL,0,0,0,'test','',0.00,100,0,'',0.00,1,'1,2','','','','','','','',NULL,NULL,0,0,'小米','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(23,6,19,0,1,'毛衫测试','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/1522738681.jpg','NO.1098',1,1,1,1,0,0,0,99.00,NULL,99,99,1000,'毛衫测试11','xxx',109.00,100,0,'件',1000.00,1,'1,2,3','毛衫测试','毛衫测试','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/1522738681.jpg,http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/1522738681.jpg','毛衫测试','毛衫测试','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/155x54.bmp\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/APP登录bg1080.jpg\" width=\"500\" height=\"500\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180604/APP登录界面.jpg\" width=\"500\" height=\"500\" /></p>','',NULL,NULL,0,2,'小米','手机数码',1,NULL,NULL,NULL,NULL,1,0,NULL),(24,6,7,0,NULL,'xxx','','',1,0,0,0,0,0,0,0.00,NULL,0,0,0,'xxx','',0.00,100,0,'',0.00,0,'','','','','','','','',NULL,NULL,0,0,'小米','外套',1,NULL,NULL,NULL,NULL,1,0,NULL),(26,3,19,0,3,'华为 HUAWEI P20 ','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf58Ndefaac16.jpg','6946605',0,1,1,1,0,100,100,3788.00,3659.00,3788,3788,0,'AI智慧全面屏 6GB +64GB 亮黑色 全网通版 移动联通电信4G手机 双卡双待手机 双卡双待','',4288.00,1000,0,'件',0.00,1,'2,3,1','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ab46a3cN616bdc41.jpg,http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ac1bf5fN2522b9dc.jpg','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ad44f1cNf51f3bb0.jpg\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ad44fa8Nfcf71c10.jpg\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ad44fa9N40e78ee0.jpg\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ad457f4N1c94bdda.jpg\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5ad457f5Nd30de41d.jpg\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180607/5b10fb0eN0eb053fb.jpg\" /></p>','<p><img src=\"//img20.360buyimg.com/vc/jfs/t1/81293/35/5822/369414/5d3fe77cE619c5487/6e775a52850feea5.jpg!q70.dpg.webp\" alt=\"\" width=\"750\" height=\"776\" /></p>\n<p><img src=\"//img20.360buyimg.com/vc/jfs/t1/45300/25/11592/3658871/5d85ef66E92a8a911/083e47d8f662c582.jpg!q70.dpg.webp\" alt=\"\" width=\"596\" height=\"16383\" /></p>','2023-01-10 15:49:38','2023-01-31 00:00:00',0,1,'华为','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(27,6,19,0,3,'小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/xiaomi.jpg','7437788',0,1,1,1,0,0,99,2699.00,NULL,2699,2699,0,'骁龙845处理器，红外人脸解锁，AI变焦双摄，AI语音助手小米6X低至1299，点击抢购','小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待',2699.00,100,0,'',0.00,0,'1','','','','','','<p style=\"text-align: center;\"><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/95935/9/19330/159477/5e9ecc13E5b8db8ae/8e954021a0835fb7.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/99224/22/19596/137593/5e9ecc13E34ef2113/2b362b90d8378ee1.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/93131/25/19321/107691/5e9ecc13E41e8addf/202a5f84d9129878.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/101843/19/19533/66831/5e9ecc13Ecb7f9d53/4fdd807266583c1e.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/99629/36/19016/59882/5e9ecc13E1f5beef5/1e5af3528f366e70.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/72343/29/8945/84548/5d6e5c67Ea07b1125/702791816b90eb3d.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/65403/35/9017/129532/5d6e5c68E3f2d0546/9ec771eb6e04a75a.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/58261/33/9380/106603/5d6e5c68E05a99177/2b5b9e29eed05b08.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/51968/40/9688/113552/5d6e5c68E5052b312/8969d83124cb78a4.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/75491/9/9101/146883/5d6e5c68E3db89775/c1aa57e78ded4e44.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/75063/11/9107/127874/5d6e5c68E0b1dfc61/10dd6000ce213375.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/47452/25/9579/108279/5d6e5c68Ea9002f3b/865b5d32ffd9da75.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/82146/26/9077/87075/5d6e5c68Ef63bccc8/556de5665a35a3f2.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/82804/21/9019/124404/5d6e5c69E06a8f575/0f861f8c4636c546.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/46044/10/9734/107686/5d6e5c69Edd5e66c7/a8c7b9324e271dbd.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/40729/32/13755/45997/5d6e5c69Eafee3664/6a3457a4efdb79c5.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/76254/34/9039/96195/5d6e5c69E3c71c809/49033c0b7024c208.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/79825/20/9065/90864/5d6e5c69E1e62ef89/a4d3ce383425a666.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/49939/21/9618/106207/5d6e5c6aEf7b1d4fd/0f5e963c66be3d0c.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/64035/7/9001/115159/5d6e5c6aE6919dfdf/39dfe4840157ad81.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/53389/3/9616/99637/5d6e5c6aEa33b9f35/b8f6aa26e72616a3.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/63219/6/9026/81576/5d6e5c6aEa9c74b49/b4fa364437531012.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/42146/27/13902/80437/5d6e5c6bE30c31ce9/475d4d54c7334313.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/45317/28/9596/78175/5d6e5c6bEce31e4b7/5675858b6933565c.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/60080/1/9112/138722/5d6e5c6bEefd9fc62/7ece7460a36d2fcc.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/51525/13/9549/36018/5d6e5c73Ebbccae6c/99bc2770dccc042b.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/61948/20/9088/72918/5d6e5c73Eab7aef5c/6f21e2f85cf478fa.jpg!q70.dpg.webp\" /></p>','<p style=\"text-align: center;\"><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/95935/9/19330/159477/5e9ecc13E5b8db8ae/8e954021a0835fb7.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/99224/22/19596/137593/5e9ecc13E34ef2113/2b362b90d8378ee1.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/93131/25/19321/107691/5e9ecc13E41e8addf/202a5f84d9129878.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/101843/19/19533/66831/5e9ecc13Ecb7f9d53/4fdd807266583c1e.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWareDetail/jfs/t1/99629/36/19016/59882/5e9ecc13E1f5beef5/1e5af3528f366e70.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/72343/29/8945/84548/5d6e5c67Ea07b1125/702791816b90eb3d.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/65403/35/9017/129532/5d6e5c68E3f2d0546/9ec771eb6e04a75a.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/58261/33/9380/106603/5d6e5c68E05a99177/2b5b9e29eed05b08.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/51968/40/9688/113552/5d6e5c68E5052b312/8969d83124cb78a4.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/75491/9/9101/146883/5d6e5c68E3db89775/c1aa57e78ded4e44.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/75063/11/9107/127874/5d6e5c68E0b1dfc61/10dd6000ce213375.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/47452/25/9579/108279/5d6e5c68Ea9002f3b/865b5d32ffd9da75.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/82146/26/9077/87075/5d6e5c68Ef63bccc8/556de5665a35a3f2.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/82804/21/9019/124404/5d6e5c69E06a8f575/0f861f8c4636c546.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/46044/10/9734/107686/5d6e5c69Edd5e66c7/a8c7b9324e271dbd.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/40729/32/13755/45997/5d6e5c69Eafee3664/6a3457a4efdb79c5.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/76254/34/9039/96195/5d6e5c69E3c71c809/49033c0b7024c208.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/79825/20/9065/90864/5d6e5c69E1e62ef89/a4d3ce383425a666.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/49939/21/9618/106207/5d6e5c6aEf7b1d4fd/0f5e963c66be3d0c.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/64035/7/9001/115159/5d6e5c6aE6919dfdf/39dfe4840157ad81.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/53389/3/9616/99637/5d6e5c6aEa33b9f35/b8f6aa26e72616a3.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/63219/6/9026/81576/5d6e5c6aEa9c74b49/b4fa364437531012.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/42146/27/13902/80437/5d6e5c6bE30c31ce9/475d4d54c7334313.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/45317/28/9596/78175/5d6e5c6bEce31e4b7/5675858b6933565c.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/60080/1/9112/138722/5d6e5c6bEefd9fc62/7ece7460a36d2fcc.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/51525/13/9549/36018/5d6e5c73Ebbccae6c/99bc2770dccc042b.jpg!q70.dpg.webp\" /><img src=\"//img30.360buyimg.com/popWaterMark/jfs/t1/61948/20/9088/72918/5d6e5c73Eab7aef5c/6f21e2f85cf478fa.jpg!q70.dpg.webp\" /></p>',NULL,NULL,0,3,'小米','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(28,6,19,0,3,'小米 红米5A 全网通版 3GB+32GB 香槟金 移动联通电信4G手机 双卡双待','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a9d248cN071f4959.jpg','7437789',0,1,1,1,0,0,98,649.00,NULL,649,649,0,'8天超长待机，137g轻巧机身，高通骁龙处理器小米6X低至1299，点击抢购','',649.00,100,0,'',0.00,0,'','','','','','','','<div><img src=\"//img12.360buyimg.com/imgzone/jfs/t1/67362/12/14546/708984/5dc28512Eefdd817d/c82503af0da6c038.gif\" /><img src=\"//img13.360buyimg.com/imgzone/jfs/t1/61488/17/14551/995918/5dc28512E821c228d/41e52005ea5b1f36.gif\" /><img src=\"//img14.360buyimg.com/imgzone/jfs/t1/72961/36/14769/305883/5dc28512E65d77261/3df6be29e3d489d1.gif\" />\n<p>&nbsp;</p>\n</div>',NULL,NULL,0,4,'小米','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(29,51,19,0,3,'Apple iPhone 8 Plus 64GB 红色特别版 移动联通电信4G手机','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5acc5248N6a5f81cd.jpg','7437799',0,1,1,1,0,0,97,5499.00,4799.00,5499,5499,0,'【限时限量抢购】Apple产品年中狂欢节，好物尽享，美在智慧！速来 >> 勾选[保障服务][原厂保2年]，获得AppleCare+全方位服务计划，原厂延保售后无忧。','',5499.00,100,0,'',0.00,0,'1,2,3','','','','','','','<div><img src=\"//img10.360buyimg.com/cms/jfs/t1/20020/38/9725/228440/5c7f9208Eeaf4bf87/13a713066f71791d.jpg!q70.dpg.webp\" /> <img src=\"//img12.360buyimg.com/cms/jfs/t1/12128/39/9607/265349/5c7f9209Ecff29b88/08620ba570705634.jpg!q70.dpg.webp\" /> <img src=\"//img14.360buyimg.com/cms/jfs/t1/22731/40/9578/345163/5c7f9209E9ba056e5/a8a557060b84447e.jpg!q70.dpg.webp\" /> <img src=\"//img14.360buyimg.com/cms/jfs/t1/29506/38/9439/299492/5c7f9209E0e51eb29/15dedd95416f3c68.jpg!q70.dpg.webp\" /> <img src=\"//img14.360buyimg.com/cms/jfs/t1/26766/28/9574/257101/5c7f9209Eaca1b317/c7caa047b1566cf1.jpg!q70.dpg.webp\" /> <img src=\"//img13.360buyimg.com/cms/jfs/t1/11059/8/10473/286531/5c7f9208E05da0120/9034ad8799ad9553.jpg!q70.dpg.webp\" /> <img src=\"//img14.360buyimg.com/cms/jfs/t1/25641/2/9557/268826/5c7f9208Efbf0dc50/399580629e05e733.jpg!q70.dpg.webp\" /> <img src=\"//img13.360buyimg.com/cms/jfs/t1/28964/25/9527/305902/5c7f9208E275ffb9c/8464934d67e69b7a.jpg!q70.dpg.webp\" /></div>','2020-05-04 15:12:54','2020-05-30 00:00:00',0,1,'苹果','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(30,50,8,0,1,'HLA海澜之家简约动物印花短袖T恤','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5ad83a4fN6ff67ecd.jpg!cc_350x449.jpg','HNTBJ2E042A',0,1,1,1,0,0,0,98.00,NULL,0,0,0,'2018夏季新品微弹舒适新款短T男生 6月6日-6月20日，满300减30，参与互动赢百元礼券，立即分享赢大奖','',98.00,100,0,'',0.00,0,'','','','','','','','',NULL,NULL,0,0,'海澜之家','T恤',1,NULL,NULL,NULL,NULL,1,0,NULL),(31,50,8,0,1,'HLA海澜之家蓝灰花纹圆领针织布短袖T恤','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5ac98b64N70acd82f.jpg!cc_350x449.jpg','HNTBJ2E080A',0,1,0,0,0,0,0,98.00,NULL,0,0,0,'2018夏季新品短袖T恤男HNTBJ2E080A 蓝灰花纹80 175/92A/L80A 蓝灰花纹80 175/92A/L','',98.00,100,0,'',0.00,0,'','','','','','','','',NULL,NULL,0,0,'海澜之家','T恤',1,NULL,NULL,NULL,NULL,1,0,NULL),(32,50,8,0,1,'HLA海澜之家短袖T恤男基础款','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5a51eb88Na4797877.jpg','HNTBJ2E153A',0,1,0,0,0,0,0,68.00,NULL,0,0,0,'HLA海澜之家短袖T恤男基础款简约圆领HNTBJ2E153A藏青(F3)175/92A(50)','',68.00,100,0,'',0.00,0,'1,2','','','','','','','',NULL,NULL,0,0,'海澜之家','T恤',1,NULL,NULL,NULL,NULL,1,0,NULL),(33,6,35,0,12,'小米（MI）小米电视4A ','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b02804dN66004d73.jpg','4609652',0,1,0,0,0,0,0,2499.00,NULL,0,0,0,'小米（MI）小米电视4A 55英寸 L55M5-AZ/L55M5-AD 2GB+8GB HDR 4K超高清 人工智能网络液晶平板电视','',2499.00,100,0,'',0.00,0,'','','','','','','','',NULL,NULL,0,0,'小米','电视',1,NULL,NULL,NULL,NULL,1,0,NULL),(34,6,35,0,12,'小米（MI）小米电视4A 65英寸','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b028530N51eee7d4.jpg','4609660',0,1,0,0,0,0,0,3999.00,NULL,0,0,0,' L65M5-AZ/L65M5-AD 2GB+8GB HDR 4K超高清 人工智能网络液晶平板电视','',3999.00,100,0,'',0.00,0,'1,2','','','','','','','',NULL,NULL,0,0,'小米','电视',1,NULL,NULL,NULL,NULL,1,0,NULL),(35,58,29,0,11,'耐克NIKE 男子 休闲鞋 ROSHE RUN 运动鞋 511881-010黑色41码','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b235bb9Nf606460b.jpg','6799342',0,1,0,0,0,0,0,369.00,NULL,0,0,0,'耐克NIKE 男子 休闲鞋 ROSHE RUN 运动鞋 511881-010黑色41码','',369.00,100,0,'',0.00,0,'','','','','','','','',NULL,NULL,0,0,'NIKE','男鞋',1,NULL,NULL,NULL,NULL,1,0,NULL),(36,58,29,0,11,'耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20180615/5b19403eN9f0b3cb8.jpg','6799345',0,1,1,1,0,0,0,499.00,NULL,0,0,0,'耐克NIKE 男子 气垫 休闲鞋 AIR MAX 90 ESSENTIAL 运动鞋 AJ1285-101白色41码','',499.00,100,0,'',0.00,0,'','','','','','','','',NULL,NULL,0,0,'NIKE','男鞋',1,NULL,NULL,NULL,NULL,1,0,NULL),(37,51,19,0,3,'Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg','100038005189',0,1,0,0,0,200,0,5999.00,NULL,0,0,0,'【11.11大爱超大爱】指定iPhone14产品限时限量领券立减601元！！！部分iPhone产品现货抢购确认收货即送原厂手机壳10元优惠券！！！猛戳 ','',5999.00,1000,0,'',208.00,0,'1,2,3','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_002.jpg,http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_003.jpg,http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_004.jpg','','','','<div style=\"margin: 0 auto;\"><img style=\"max-width: 390px;\" src=\"//img13.360buyimg.com/cms/jfs/t1/58071/39/19839/119559/63190110Edac0cea7/b62a84f1b8775fef.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img20.360buyimg.com/cms/jfs/t1/138903/36/29400/86115/63190110E0a98c819/d2efbef39eeb2995.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img30.360buyimg.com/cms/jfs/t1/176347/20/28995/115695/63190110Ef5d766f9/aee3d2d866522f66.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img14.360buyimg.com/cms/jfs/t1/120515/39/28721/142961/63190110Eec31e31a/3486d6a065a18ddc.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img13.360buyimg.com/cms/jfs/t1/30161/12/17674/81508/63190110E1385cf61/f05a2a43f4d304ff.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img14.360buyimg.com/cms/jfs/t1/100037/16/31071/62177/6319010fE871efe01/b01a6f981c268e38.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img14.360buyimg.com/cms/jfs/t1/90843/33/25852/74752/63190110E373559f4/74b6b52a3fb08c66.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img14.360buyimg.com/cms/jfs/t1/181914/22/28400/126094/63190110Edefb838c/802a16e758be2b1d.jpg!q70.dpg.webp\" /></div>',NULL,NULL,0,0,'苹果','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(38,51,53,0,3,'Apple iPad 10.9英寸平板电脑 2022年款','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg','100044025833',0,1,0,0,0,0,0,3599.00,NULL,0,0,0,'【11.11大爱超大爱】iPad9代限量抢购，价格优惠，更享以旧换新至高补贴325元！！快来抢购吧！！ ','',3599.00,1000,0,'',0.00,0,'1,2,3','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_002.jpg','','','','<div style=\"margin: 0 auto;\"><img style=\"max-width: 390px;\" src=\"//img12.360buyimg.com/cms/jfs/t1/75040/28/21026/295081/634ed154E981e4d10/2ceef91d6f2b2273.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img13.360buyimg.com/cms/jfs/t1/191028/1/28802/401291/634ed15eEb234dc40/5ab89f83531e1023.jpg!q70.dpg.webp\" /> <img style=\"max-width: 390px;\" src=\"//img14.360buyimg.com/cms/jfs/t1/32758/24/18599/330590/634ed15eEc3db173c/c52953dc8008a576.jpg!q70.dpg.webp\" /></div>',NULL,NULL,0,0,'苹果','平板电脑',1,NULL,NULL,NULL,NULL,1,0,NULL),(39,6,54,0,13,'小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg','100023207945',0,1,0,1,0,0,0,5599.00,NULL,0,0,0,'【双十一大促来袭】指定型号至高优惠1000，以旧换新至高补贴1000元，晒单赢好礼','',5599.00,500,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_002.jpg','','','','<div class=\"ssd-module-mobile-wrap\">\n<div class=\"ssd-module M16667778180631\" data-id=\"M16667778180631\"><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_snipaste_03.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_snipaste_04.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_snipaste_05.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_snipaste_06.png\" /></div>\n<div class=\"ssd-module M16667778180631\" data-id=\"M16667778180631\">&nbsp;</div>\n<div class=\"ssd-module M16534569204241\" data-id=\"M16534569204241\">&nbsp;</div>\n<div class=\"ssd-module M16667778416884\" data-id=\"M16667778416884\">\n<div class=\"ssd-widget-text W16667778440496\">&nbsp;</div>\n</div>\n<div class=\"ssd-module M165303211067557\" data-id=\"M165303211067557\">&nbsp;</div>\n<div class=\"ssd-module M16530320459861\" data-id=\"M16530320459861\">&nbsp;</div>\n<div class=\"ssd-module M16530320460062\" data-id=\"M16530320460062\">&nbsp;</div>\n<div class=\"ssd-module M16530320460153\" data-id=\"M16530320460153\">&nbsp;</div>\n<div class=\"ssd-module M16530320460366\" data-id=\"M16530320460366\">&nbsp;</div>\n<div class=\"ssd-module M16530320460477\" data-id=\"M16530320460477\">&nbsp;</div>\n<div class=\"ssd-module M16530320460578\" data-id=\"M16530320460578\">&nbsp;</div>\n<div class=\"ssd-module M16530320460699\" data-id=\"M16530320460699\">&nbsp;</div>\n<div class=\"ssd-module M165303204608110\" data-id=\"M165303204608110\">&nbsp;</div>\n<div class=\"ssd-module M165303204609511\" data-id=\"M165303204609511\">&nbsp;</div>\n<div class=\"ssd-module M165303204611213\" data-id=\"M165303204611213\">&nbsp;</div>\n<div class=\"ssd-module M165303204612714\" data-id=\"M165303204612714\">&nbsp;</div>\n<div class=\"ssd-module M165303204614115\" data-id=\"M165303204614115\">&nbsp;</div>\n<div class=\"ssd-module M165303204615516\" data-id=\"M165303204615516\">&nbsp;</div>\n<div class=\"ssd-module M165303204617417\" data-id=\"M165303204617417\">&nbsp;</div>\n<div class=\"ssd-module M165303204618818\" data-id=\"M165303204618818\">&nbsp;</div>\n<div class=\"ssd-module M165303204620219\" data-id=\"M165303204620219\">&nbsp;</div>\n<div class=\"ssd-module M165303204621620\" data-id=\"M165303204621620\">&nbsp;</div>\n<div class=\"ssd-module M165303204622921\" data-id=\"M165303204622921\">&nbsp;</div>\n<div class=\"ssd-module M165303204624522\" data-id=\"M165303204624522\">&nbsp;</div>\n<div class=\"ssd-module M165303204626024\" data-id=\"M165303204626024\">&nbsp;</div>\n<div class=\"ssd-module M165303204627525\" data-id=\"M165303204627525\">&nbsp;</div>\n<div class=\"ssd-module M165303204629127\" data-id=\"M165303204629127\">&nbsp;</div>\n<div class=\"ssd-module M165303204632330\" data-id=\"M165303204632330\">&nbsp;</div>\n<div class=\"ssd-module M165303204634031\" data-id=\"M165303204634031\">&nbsp;</div>\n<div class=\"ssd-module M165303204635832\" data-id=\"M165303204635832\">&nbsp;</div>\n<div class=\"ssd-module M165303204637533\" data-id=\"M165303204637533\">&nbsp;</div>\n<div class=\"ssd-module M165303204639334\" data-id=\"M165303204639334\">&nbsp;</div>\n<div class=\"ssd-module M165303204642235\" data-id=\"M165303204642235\">&nbsp;</div>\n<div class=\"ssd-module M165303204647936\" data-id=\"M165303204647936\">&nbsp;</div>\n<div class=\"ssd-module M165303204651037\" data-id=\"M165303204651037\">&nbsp;</div>\n<div class=\"ssd-module M165303204653838\" data-id=\"M165303204653838\">&nbsp;</div>\n<div class=\"ssd-module M165303204656239\" data-id=\"M165303204656239\">&nbsp;</div>\n<div class=\"ssd-module M165303204659141\" data-id=\"M165303204659141\">&nbsp;</div>\n<div class=\"ssd-module M165303204661943\" data-id=\"M165303204661943\">&nbsp;</div>\n<div class=\"ssd-module M165303204665844\" data-id=\"M165303204665844\">&nbsp;</div>\n<div class=\"ssd-module M165303204668045\" data-id=\"M165303204668045\">&nbsp;</div>\n<div class=\"ssd-module M165303204670146\" data-id=\"M165303204670146\">&nbsp;</div>\n<div class=\"ssd-module M165303204672147\" data-id=\"M165303204672147\">&nbsp;</div>\n<div class=\"ssd-module M165303204674348\" data-id=\"M165303204674348\">&nbsp;</div>\n<div class=\"ssd-module M165303204676749\" data-id=\"M165303204676749\">&nbsp;</div>\n<div class=\"ssd-module M165303204681352\" data-id=\"M165303204681352\">&nbsp;</div>\n<div class=\"ssd-module M165303204683553\" data-id=\"M165303204683553\">&nbsp;</div>\n<div class=\"ssd-module M165303204685855\" data-id=\"M165303204685855\">&nbsp;</div>\n<div class=\"ssd-module M165303204688356\" data-id=\"M165303204688356\">&nbsp;</div>\n</div>',NULL,NULL,0,0,'小米','笔记本',1,NULL,NULL,NULL,NULL,1,0,NULL),(40,6,19,0,3,'小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg','100027789721',0,1,0,1,0,0,0,2999.00,NULL,0,0,0,'天玑9000+处理器、5160mAh大电量、2KAmoled超视感屏【点击购买小米11Ultra，戳】 ','',2999.00,500,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_02.jpg','','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_snipaste_03.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_snipaste_04.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_snipaste_05.png\" /></p>',NULL,NULL,0,4,'小米','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(41,6,19,0,3,'Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg','100035246702',0,1,0,0,0,0,0,2099.00,NULL,0,0,0,'【品质好物】天玑8100，2K直屏，5500mAh大电量【Note12Pro火热抢购中】 ','',2099.00,1000,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_02.jpg','','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_snipaste_03.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_snipaste_04.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_snipaste_05.png\" /></p>',NULL,NULL,0,0,'小米','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(42,3,19,0,3,'HUAWEI Mate 50 直屏旗舰 超光变XMAGE影像 北斗卫星消息','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_01.jpg','100035295081',0,1,0,0,0,0,0,4999.00,NULL,0,0,0,'【华为Mate50新品上市】内置66W华为充电套装，超光变XMAGE影像,北斗卫星消息，鸿蒙操作系统3.0！立即抢购！华为新品可持续计划，猛戳》 ','',4999.00,1000,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_02.jpg','','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_snipaste_03.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_snipaste_04.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_snipaste_05.png\" /></p>',NULL,NULL,0,0,'华为','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(43,1,39,0,14,'万和（Vanward)燃气热水器天然气家用四重防冻直流变频节能全新升级增压水伺服恒温高抗风','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_13L_01.png','10044060351752',0,1,0,0,0,0,0,1799.00,NULL,0,0,0,'【家电11.11享低价，抢到手价不高于1199】【发布种草秀享好礼！同价11.11买贵补差】爆款超一级能效零冷水】 ','',1799.00,1000,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_16L_01.jpg','','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_water_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_water_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_water_snipaste_03.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_water_snipaste_04.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_water_snipaste_05.png\" /></p>',NULL,NULL,0,0,'万和','厨卫大电',1,NULL,NULL,NULL,NULL,1,0,NULL),(44,2,55,0,15,'三星（SAMSUNG）500GB SSD固态硬盘 M.2接口(NVMe协议)','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_02.jpg','100018768480',0,1,0,0,0,0,0,369.00,NULL,0,0,0,'【满血无缓存！进店抽百元E卡，部分型号白条三期免息】兼具速度与可靠性！读速高达3500MB/s，全功率模式！点击 ','',369.00,1000,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_01.jpg','','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_snipaste_03.png\" /></p>',NULL,NULL,0,0,'三星','硬盘',1,NULL,NULL,NULL,NULL,1,0,NULL),(45,21,19,0,3,'OPPO Reno8 8GB+128GB 鸢尾紫 新配色上市 80W超级闪充 5000万水光人像三摄','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_01.jpg','10052147850350',0,1,0,0,0,0,0,2299.00,999.00,0,0,0,'【11.11提前购机享价保，好货不用等，系统申请一键价保补差!】【Reno8Pro爆款优惠】 ','',2299.00,1000,0,'',0.00,0,'','','','http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_02.jpg','','','','<p><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_snipaste_01.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_snipaste_02.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_snipaste_03.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_snipaste_04.png\" /><img class=\"wscnph\" src=\"http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_snipaste_05.png\" /></p>','2022-11-09 16:15:50','2022-11-25 00:00:00',0,4,'OPPO','手机通讯',1,NULL,NULL,NULL,NULL,1,0,NULL),(46,1,1,NULL,NULL,'基础积分包',NULL,'CREDIT_BASIC_1000',NULL,1,1,0,1,1,0,9.90,NULL,0,0,0,'1000积分','适合轻度使用用户，包含1000积分',12.90,999,NULL,'包',0.00,NULL,NULL,'积分,充值,基础',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,1000,NULL,NULL,NULL,1,0,NULL),(47,1,1,NULL,NULL,'标准积分包',NULL,'CREDIT_STANDARD_3000',NULL,1,1,1,1,2,0,26.90,NULL,0,0,0,'3000积分','适合日常使用用户，包含3000积分',35.90,999,NULL,'包',0.00,NULL,NULL,'积分,充值,标准',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,3000,NULL,NULL,NULL,1,0,NULL),(48,1,1,NULL,NULL,'专业积分包',NULL,'CREDIT_PRO_6000',NULL,1,1,1,1,3,0,49.90,NULL,0,0,0,'6000积分','适合专业设计师，包含6000积分',69.90,999,NULL,'包',0.00,NULL,NULL,'积分,充值,专业',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,6000,NULL,NULL,NULL,1,0,NULL),(49,1,1,NULL,NULL,'企业积分包',NULL,'CREDIT_ENTERPRISE_15000',NULL,1,0,1,1,4,0,119.90,NULL,0,0,0,'15000积分','适合团队协作，包含15000积分',169.90,999,NULL,'包',0.00,NULL,NULL,'积分,充值,企业',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,15000,NULL,NULL,NULL,1,0,NULL),(50,1,1,NULL,NULL,'旗舰积分包',NULL,'CREDIT_FLAGSHIP_30000',NULL,1,1,1,1,5,0,219.90,NULL,0,0,0,'30000积分','无限创作可能，包含30000积分',329.90,999,NULL,'包',0.00,NULL,NULL,'积分,充值,旗舰',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,30000,NULL,NULL,NULL,1,0,NULL),(51,1,2,NULL,NULL,'基础月套餐',NULL,'SUB_BASIC_MONTHLY',NULL,1,1,0,1,1,0,29.90,NULL,0,0,0,'适合个人轻量使用','每日500积分，基础功能完备',39.90,999,NULL,'月',0.00,NULL,NULL,'套餐,月付,基础',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,30,500,15000,1,0,'{\"features\": [\"基础AI模型\", \"标准生成速度\", \"邮件支持\"], \"limitations\": {\"max_history\": 30, \"max_file_size\": \"10MB\"}}'),(52,1,2,NULL,NULL,'标准月套餐',NULL,'SUB_STANDARD_MONTHLY',NULL,1,1,1,1,2,0,59.90,NULL,0,0,0,'适合专业设计师','每日1200积分，高级功能解锁',79.90,999,NULL,'月',0.00,NULL,NULL,'套餐,月付,标准',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,30,1200,36000,2,1,'{\"features\": [\"高级AI模型\", \"优先生成队列\", \"在线客服\"], \"limitations\": {\"max_history\": 90, \"max_file_size\": \"50MB\"}}'),(53,1,2,NULL,NULL,'专业月套餐',NULL,'SUB_PRO_MONTHLY',NULL,1,1,1,1,3,0,119.90,NULL,0,0,0,'适合专业团队','每日2500积分，专业功能全开',159.90,999,NULL,'月',0.00,NULL,NULL,'套餐,月付,专业',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,30,2500,75000,5,2,'{\"features\": [\"顶级AI模型\", \"最高优先级\", \"专属客服\", \"API接口\"], \"limitations\": {\"max_history\": 365, \"max_file_size\": \"200MB\"}}'),(54,1,2,NULL,NULL,'企业月套餐',NULL,'SUB_ENTERPRISE_MONTHLY',NULL,1,0,1,1,4,0,299.90,NULL,0,0,0,'适合大型团队','每日5000积分，企业级管理',399.90,999,NULL,'月',0.00,NULL,NULL,'套餐,月付,企业',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,30,5000,150000,10,2,'{\"features\": [\"企业级AI\", \"无限并发\", \"团队管理\", \"数据分析\", \"定制服务\"], \"limitations\": {\"max_history\": -1, \"max_file_size\": \"1GB\"}}'),(55,1,2,NULL,NULL,'标准季套餐',NULL,'SUB_STANDARD_QUARTERLY',NULL,1,1,1,1,12,0,152.75,NULL,0,0,0,'季付更优惠','标准版3个月，享85折优惠',179.70,999,NULL,'季',0.00,NULL,NULL,'套餐,季付,标准,优惠',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,90,1200,108000,2,1,'{\"features\": [\"高级AI模型\", \"优先生成队列\", \"在线客服\"], \"limitations\": {\"max_history\": 90, \"max_file_size\": \"50MB\"}}'),(56,1,2,NULL,NULL,'专业年套餐',NULL,'SUB_PRO_YEARLY',NULL,1,1,1,1,13,0,1079.10,NULL,0,0,0,'年付最划算','专业版12个月，享75折优惠',1439.40,999,NULL,'年',0.00,NULL,NULL,'套餐,年付,专业,最优惠',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,NULL,365,2500,912500,5,2,'{\"features\": [\"顶级AI模型\", \"最高优先级\", \"专属客服\", \"API接口\"], \"limitations\": {\"max_history\": 365, \"max_file_size\": \"200MB\"}}');
/*!40000 ALTER TABLE `pms_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_attribute`
--

DROP TABLE IF EXISTS `pms_product_attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_attribute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_attribute_category_id` bigint(20) DEFAULT NULL,
  `name` varchar(64) DEFAULT NULL,
  `select_type` int(1) DEFAULT NULL COMMENT '属性选择类型：0->唯一；1->单选；2->多选',
  `input_type` int(1) DEFAULT NULL COMMENT '属性录入方式：0->手工录入；1->从列表中选取',
  `input_list` varchar(255) DEFAULT NULL COMMENT '可选值列表，以逗号隔开',
  `sort` int(11) DEFAULT NULL COMMENT '排序字段：最高的可以单独上传图片',
  `filter_type` int(1) DEFAULT NULL COMMENT '分类筛选样式：1->普通；1->颜色',
  `search_type` int(1) DEFAULT NULL COMMENT '检索类型；0->不需要进行检索；1->关键字检索；2->范围检索',
  `related_status` int(1) DEFAULT NULL COMMENT '相同属性产品是否关联；0->不关联；1->关联',
  `hand_add_status` int(1) DEFAULT NULL COMMENT '是否支持手动新增；0->不支持；1->支持',
  `type` int(1) DEFAULT NULL COMMENT '属性的类型；0->规格；1->参数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品属性参数表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_attribute`
--

LOCK TABLES `pms_product_attribute` WRITE;
/*!40000 ALTER TABLE `pms_product_attribute` DISABLE KEYS */;
INSERT INTO `pms_product_attribute` VALUES (1,1,'尺寸',2,1,'M,X,XL,2XL,3XL,4XL',0,0,0,0,0,0),(7,1,'颜色',2,1,'黑色,红色,白色,粉色',100,0,0,0,1,0),(13,0,'上市年份',1,1,'2013年,2014年,2015年,2016年,2017年',0,0,0,0,0,1),(14,0,'上市年份1',1,1,'2013年,2014年,2015年,2016年,2017年',0,0,0,0,0,1),(15,0,'上市年份2',1,1,'2013年,2014年,2015年,2016年,2017年',0,0,0,0,0,1),(16,0,'上市年份3',1,1,'2013年,2014年,2015年,2016年,2017年',0,0,0,0,0,1),(17,0,'上市年份4',1,1,'2013年,2014年,2015年,2016年,2017年',0,0,0,0,0,1),(18,0,'上市年份5',1,1,'2013年,2014年,2015年,2016年,2017年',0,0,0,0,0,1),(19,0,'适用对象',1,1,'青年女性,中年女性',0,0,0,0,0,1),(20,0,'适用对象1',2,1,'青年女性,中年女性',0,0,0,0,0,1),(21,0,'适用对象3',2,1,'青年女性,中年女性',0,0,0,0,0,1),(24,1,'商品编号',1,0,'',0,0,0,0,0,1),(25,1,'适用季节',1,1,'春季,夏季,秋季,冬季',0,0,0,0,0,1),(32,2,'适用人群',0,1,'老年,青年,中年',0,0,0,0,0,1),(33,2,'风格',0,1,'嘻哈风格,基础大众,商务正装',0,0,0,0,0,1),(35,2,'颜色',0,0,'',100,0,0,0,1,0),(37,1,'适用人群',1,1,'儿童,青年,中年,老年',0,0,0,0,0,1),(38,1,'上市时间',1,1,'2017年秋,2017年冬,2018年春,2018年夏',0,0,0,0,0,1),(39,1,'袖长',1,1,'短袖,长袖,中袖',0,0,0,0,0,1),(40,2,'尺码',0,1,'29,30,31,32,33,34',0,0,0,0,0,0),(41,2,'适用场景',0,1,'居家,运动,正装',0,0,0,0,0,1),(42,2,'上市时间',0,1,'2018年春,2018年夏',0,0,0,0,0,1),(43,3,'颜色',0,0,'',100,0,0,0,1,0),(44,3,'容量',0,1,'16G,32G,64G,128G,256G,512G',0,0,0,0,0,0),(45,3,'屏幕尺寸',0,0,'',0,0,0,0,0,1),(46,3,'网络',0,1,'3G,4G,5G,WLAN',0,0,0,0,0,1),(47,3,'系统',0,1,'Android,IOS',0,0,0,0,0,1),(48,3,'电池容量',0,0,'',0,0,0,0,0,1),(49,11,'颜色',0,1,'红色,蓝色,绿色',0,1,0,0,0,0),(50,11,'尺寸',0,1,'38,39,40',0,0,0,0,0,0),(51,11,'风格',0,1,'夏季,秋季',0,0,0,0,0,0),(52,12,'尺寸',0,1,'50英寸,65英寸,70英寸',0,0,0,0,0,0),(53,12,'内存',0,1,'8G,16G,32G',0,0,0,0,0,0),(54,12,'商品编号',0,0,'',0,0,0,0,0,1),(55,12,'商品毛重',0,0,'',0,0,0,0,0,1),(56,12,'商品产地',0,1,'中国大陆,其他',0,0,0,0,0,1),(57,12,'电视类型',0,1,'大屏,教育电视,4K超清',0,0,0,0,0,1),(58,13,'颜色',0,0,'',0,0,0,0,1,0),(59,13,'版本',0,1,'R7 16G 512,R5 16G 512,I5 16G 512,I7 16G 512',0,0,0,0,0,0),(60,13,'屏幕尺寸',0,0,'',0,0,0,0,0,1),(61,13,'屏幕分辨率',0,0,'',0,0,0,0,0,1),(62,13,'CPU型号',0,0,'',0,0,0,0,0,1),(63,14,'系列',0,0,'',0,0,0,0,1,0),(64,14,'上市时间',0,0,'',0,0,0,0,0,1),(65,14,'毛重',0,0,'',0,0,0,0,0,1),(66,14,'额定功率',0,0,'',0,0,0,0,0,1),(67,15,'颜色',0,0,'',0,0,0,0,1,0),(68,15,'版本',0,1,'512GB,1TB',0,0,0,0,0,0),(69,15,'系列',0,0,'',0,0,0,0,0,1),(70,15,'型号',0,0,'',0,0,0,0,0,1),(71,15,'闪存类型',0,0,'',0,0,0,0,0,1),(72,15,'顺序读速',0,0,'',0,0,0,0,0,1),(73,15,'顺序写入',0,0,'',0,0,0,0,0,1);
/*!40000 ALTER TABLE `pms_product_attribute` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_attribute_category`
--

DROP TABLE IF EXISTS `pms_product_attribute_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_attribute_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `attribute_count` int(11) DEFAULT '0' COMMENT '属性数量',
  `param_count` int(11) DEFAULT '0' COMMENT '参数数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='产品属性分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_attribute_category`
--

LOCK TABLES `pms_product_attribute_category` WRITE;
/*!40000 ALTER TABLE `pms_product_attribute_category` DISABLE KEYS */;
INSERT INTO `pms_product_attribute_category` VALUES (1,'服装-T恤',2,5),(2,'服装-裤装',2,4),(3,'手机数码-手机通讯',2,4),(4,'配件',0,0),(5,'居家',0,0),(6,'洗护',0,0),(10,'测试分类',0,0),(11,'服装-鞋帽',3,0),(12,'家用电器-电视',2,4),(13,'电脑办公-笔记本',2,3),(14,'家用电器-厨卫大电',1,3),(15,'电脑办公-硬盘',2,5);
/*!40000 ALTER TABLE `pms_product_attribute_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_attribute_value`
--

DROP TABLE IF EXISTS `pms_product_attribute_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_attribute_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `product_attribute_id` bigint(20) DEFAULT NULL,
  `value` varchar(64) DEFAULT NULL COMMENT '手动添加规格或参数的值，参数单值，规格有多个时以逗号隔开',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=517 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='存储产品参数信息的表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_attribute_value`
--

LOCK TABLES `pms_product_attribute_value` WRITE;
/*!40000 ALTER TABLE `pms_product_attribute_value` DISABLE KEYS */;
INSERT INTO `pms_product_attribute_value` VALUES (1,9,1,'X'),(2,10,1,'X'),(3,11,1,'X'),(4,12,1,'X'),(5,13,1,'X'),(6,14,1,'X'),(7,18,1,'X'),(8,7,1,'X'),(9,7,1,'XL'),(10,7,1,'XXL'),(11,22,7,'x,xx'),(12,22,24,'no110'),(13,22,25,'春季'),(14,22,37,'青年'),(15,22,38,'2018年春'),(16,22,39,'长袖'),(124,23,7,'米白色,浅黄色'),(125,23,24,'no1098'),(126,23,25,'春季'),(127,23,37,'青年'),(128,23,38,'2018年春'),(129,23,39,'长袖'),(130,1,13,NULL),(131,1,14,NULL),(132,1,15,NULL),(133,1,16,NULL),(134,1,17,NULL),(135,1,18,NULL),(136,1,19,NULL),(137,1,20,NULL),(138,1,21,NULL),(139,2,13,NULL),(140,2,14,NULL),(141,2,15,NULL),(142,2,16,NULL),(143,2,17,NULL),(144,2,18,NULL),(145,2,19,NULL),(146,2,20,NULL),(147,2,21,NULL),(243,30,7,'蓝色,白色'),(244,30,24,'HNTBJ2E042A'),(245,30,25,'夏季'),(246,30,37,'青年'),(247,30,38,'2018年夏'),(248,30,39,'短袖'),(249,31,7,'浅灰色,深灰色'),(250,31,24,'HNTBJ2E080A'),(251,31,25,'夏季'),(252,31,37,'青年'),(253,31,38,'2018年夏'),(254,31,39,'短袖'),(255,32,7,'黑色,白色'),(256,32,24,'HNTBJ2E153A'),(257,32,25,'夏季'),(258,32,37,'青年'),(259,32,38,'2018年夏'),(260,32,39,'短袖'),(265,33,54,'4609652'),(266,33,55,'28.6kg'),(267,33,56,'中国大陆'),(268,33,57,'大屏'),(269,34,54,'4609660'),(270,34,55,'30.8kg'),(271,34,56,'中国大陆'),(272,34,57,'4K超清'),(288,27,43,'黑色,蓝色'),(289,27,45,'5.8'),(290,27,46,'4G'),(291,27,47,'Android'),(292,27,48,'3000ml'),(303,28,43,'金色,银色'),(304,28,45,'5.0'),(305,28,46,'4G'),(306,28,47,'Android'),(307,28,48,'2800ml'),(308,29,43,'金色,银色'),(309,29,45,'4.7'),(310,29,46,'4G'),(311,29,47,'IOS'),(312,29,48,'1960ml'),(338,37,43,'午夜色,星光色,紫色,蓝色'),(339,37,45,'6.1英寸'),(340,37,46,'5G'),(341,37,47,'IOS'),(342,37,48,'3000毫安'),(443,38,43,'银色,蓝色'),(444,38,45,'10.9英寸'),(445,38,46,'WLAN'),(446,38,47,'IOS'),(447,38,48,'6000毫安'),(448,39,58,'新小米Pro 14英寸 2.8K屏,新Redmi Pro 15英寸 3.2K屏'),(449,39,60,'15.6英寸'),(450,39,61,'3200*2000'),(451,39,62,'R5 6600H'),(452,41,43,'墨羽,银迹'),(453,41,45,'6.67英寸'),(454,41,46,'5G'),(455,41,47,'Android'),(456,41,48,'5500mAh'),(457,42,43,'曜金黑,冰霜银'),(458,42,45,'6.7英寸'),(459,42,46,'5G'),(460,42,47,'Android'),(461,42,48,'4460mAh'),(462,43,63,'JSQ25-565W13【13升】【恒温旗舰款】,JSQ30-565W16【16升】【恒温旗舰款】'),(463,43,64,'2021-05'),(464,43,65,'15.5kg'),(465,43,66,'30w'),(466,44,67,'新品980｜NVMe PCIe3.0*4,980 PRO｜NVMe PCIe 4.0'),(467,44,69,'980'),(468,44,70,'MZ-V8V500BW'),(469,44,71,'TLC'),(470,44,72,'3100MB/s'),(471,44,73,'2600MB/s'),(472,45,43,'鸢尾紫,晴空蓝'),(473,45,45,'6.43英寸'),(474,45,46,'5G'),(475,45,47,'Android'),(476,45,48,'4500mAh'),(477,40,43,'黑色,蓝色'),(478,40,45,'6.73英寸'),(479,40,46,'5G'),(480,40,47,'Android'),(481,40,48,'5160mAh'),(512,26,43,'金色,银色'),(513,26,45,'5.0'),(514,26,46,'4G'),(515,26,47,'Android'),(516,26,48,'3000');
/*!40000 ALTER TABLE `pms_product_attribute_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_category`
--

DROP TABLE IF EXISTS `pms_product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上机分类的编号：0表示一级分类',
  `name` varchar(64) DEFAULT NULL,
  `level` int(1) DEFAULT NULL COMMENT '分类级别：0->1级；1->2级',
  `product_count` int(11) DEFAULT NULL,
  `product_unit` varchar(64) DEFAULT NULL,
  `nav_status` int(1) DEFAULT NULL COMMENT '是否显示在导航栏：0->不显示；1->显示',
  `show_status` int(1) DEFAULT NULL COMMENT '显示状态：0->不显示；1->显示',
  `sort` int(11) DEFAULT NULL,
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `keywords` varchar(255) DEFAULT NULL,
  `description` text COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='产品分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_category`
--

LOCK TABLES `pms_product_category` WRITE;
/*!40000 ALTER TABLE `pms_product_category` DISABLE KEYS */;
INSERT INTO `pms_product_category` VALUES (1,0,'服装',0,100,'件',1,1,1,NULL,'服装','服装分类'),(2,0,'手机数码',0,100,'件',1,1,1,NULL,'手机数码','手机数码'),(3,0,'家用电器',0,100,'件',1,1,1,NULL,'家用电器','家用电器'),(4,0,'家具家装',0,100,'件',1,1,1,NULL,'家具家装','家具家装'),(5,0,'汽车用品',0,100,'件',1,1,1,NULL,'汽车用品','汽车用品'),(7,1,'外套',1,100,'件',1,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac4780cN6087feb5.jpg','外套','外套'),(8,1,'T恤',1,100,'件',1,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac47ffaN8a7b2e14.png','T恤','T恤'),(9,1,'休闲裤',1,100,'件',1,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac47845N7374a31d.jpg','休闲裤','休闲裤'),(10,1,'牛仔裤',1,100,'件',1,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac47841Nff658599.jpg','牛仔裤','牛仔裤'),(11,1,'衬衫',1,100,'件',1,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac48007Nb30b2118.jpg','衬衫','衬衫分类'),(13,12,'家电子分类1',1,1,'string',0,1,0,'string','string','string'),(14,12,'家电子分类2',1,1,'string',0,1,0,'string','string','string'),(19,2,'手机通讯',1,0,'件',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac48d27N3f5bb821.jpg','手机通讯','手机通讯'),(29,1,'男鞋',1,0,'',0,0,0,'','',''),(30,2,'手机配件',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5ac48672N11cf61fe.jpg','手机配件','手机配件'),(31,2,'摄影摄像',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5a1679f2Nc2f659b6.jpg','',''),(32,2,'影音娱乐',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5a167859N01d8198b.jpg','',''),(33,2,'数码配件',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190519/5a1676e9N1ba70a81.jpg','',''),(34,2,'智能设备',1,0,'',0,0,0,'','',''),(35,3,'电视',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a17f71eN25360979.jpg','',''),(36,3,'空调',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a17f6f6Ndfe746aa.jpg','',''),(37,3,'洗衣机',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a17f6eaN9ec936de.jpg','',''),(38,3,'冰箱',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a17f6c5Ne56d7e26.jpg','',''),(39,3,'厨卫大电',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a17f369N6a55ff3f.jpg','',''),(40,3,'厨房小电',1,0,'',0,0,0,'','',''),(41,3,'生活电器',1,0,'',0,0,0,'','',''),(42,3,'个护健康',1,0,'',0,0,0,'','',''),(43,4,'厨房卫浴',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a1eb12cN5ab932bb.jpg','',''),(44,4,'灯饰照明',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a1eb115Na5705672.jpg','',''),(45,4,'五金工具',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a28e743Nf6d99998.jpg','',''),(46,4,'卧室家具',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a1eb096N6326e0bd.jpg','',''),(47,4,'客厅家具',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a1eb080N38c2e7b7.jpg','',''),(48,5,'全新整车',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/ebe31b9cc535e122.png','',''),(49,5,'车载电器',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a1fb8d2N53bbd2ba.jpg','',''),(50,5,'维修保养',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a1fb8baNbe801af8.jpg','',''),(51,5,'汽车装饰',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20200607/5a28ae20N34461e66.jpg','',''),(52,0,'电脑办公',0,0,'件',1,1,1,'','电脑办公','电脑办公'),(53,52,'平板电脑',1,0,'件',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/pad_category_01.jpg','平板电脑','平板电脑'),(54,52,'笔记本',1,0,'件',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/computer_category_01.jpg','笔记本','笔记本'),(55,52,'硬盘',1,0,'',0,1,0,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/ssd_category_01.jpg','硬盘','');
/*!40000 ALTER TABLE `pms_product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_category_attribute_relation`
--

DROP TABLE IF EXISTS `pms_product_category_attribute_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_category_attribute_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_category_id` bigint(20) DEFAULT NULL,
  `product_attribute_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='产品的分类和属性的关系表，用于设置分类筛选条件（只支持一级分类）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_category_attribute_relation`
--

LOCK TABLES `pms_product_category_attribute_relation` WRITE;
/*!40000 ALTER TABLE `pms_product_category_attribute_relation` DISABLE KEYS */;
INSERT INTO `pms_product_category_attribute_relation` VALUES (1,24,24),(5,26,24),(7,28,24),(9,25,24),(10,25,25);
/*!40000 ALTER TABLE `pms_product_category_attribute_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_full_reduction`
--

DROP TABLE IF EXISTS `pms_product_full_reduction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_full_reduction` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `full_price` decimal(10,2) DEFAULT NULL,
  `reduce_price` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='产品满减表(只针对同商品)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_full_reduction`
--

LOCK TABLES `pms_product_full_reduction` WRITE;
/*!40000 ALTER TABLE `pms_product_full_reduction` DISABLE KEYS */;
INSERT INTO `pms_product_full_reduction` VALUES (1,7,100.00,20.00),(2,8,100.00,20.00),(3,9,100.00,20.00),(4,10,100.00,20.00),(5,11,100.00,20.00),(6,12,100.00,20.00),(7,13,100.00,20.00),(8,14,100.00,20.00),(9,18,100.00,20.00),(10,7,200.00,50.00),(11,7,300.00,100.00),(14,22,0.00,0.00),(16,24,0.00,0.00),(34,23,0.00,0.00),(78,36,0.00,0.00),(79,35,0.00,0.00),(83,30,0.00,0.00),(84,31,0.00,0.00),(85,32,0.00,0.00),(87,33,0.00,0.00),(88,34,0.00,0.00),(93,27,0.00,0.00),(96,28,500.00,50.00),(97,28,1000.00,120.00),(98,29,0.00,0.00),(104,37,0.00,0.00),(126,38,0.00,0.00),(127,39,0.00,0.00),(128,41,0.00,0.00),(129,42,0.00,0.00),(130,43,0.00,0.00),(131,44,0.00,0.00),(132,45,2000.00,100.00),(133,40,2000.00,200.00),(146,26,3000.00,300.00),(147,26,5000.00,500.00);
/*!40000 ALTER TABLE `pms_product_full_reduction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_ladder`
--

DROP TABLE IF EXISTS `pms_product_ladder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_ladder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `count` int(11) DEFAULT NULL COMMENT '满足的商品数量',
  `discount` decimal(10,2) DEFAULT NULL COMMENT '折扣',
  `price` decimal(10,2) DEFAULT NULL COMMENT '折后价格',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='产品阶梯价格表(只针对同商品)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_ladder`
--

LOCK TABLES `pms_product_ladder` WRITE;
/*!40000 ALTER TABLE `pms_product_ladder` DISABLE KEYS */;
INSERT INTO `pms_product_ladder` VALUES (1,7,3,0.70,0.00),(2,8,3,0.70,0.00),(3,9,3,0.70,0.00),(4,10,3,0.70,0.00),(5,11,3,0.70,0.00),(6,12,3,0.70,0.00),(7,13,3,0.70,0.00),(8,14,3,0.70,0.00),(12,18,3,0.70,0.00),(14,7,4,0.60,0.00),(15,7,5,0.50,0.00),(18,22,0,0.00,0.00),(20,24,0,0.00,0.00),(38,23,0,0.00,0.00),(83,36,0,0.00,0.00),(84,35,0,0.00,0.00),(88,30,0,0.00,0.00),(89,31,0,0.00,0.00),(90,32,0,0.00,0.00),(92,33,0,0.00,0.00),(93,34,0,0.00,0.00),(99,27,2,0.80,0.00),(100,27,3,0.75,0.00),(103,28,0,0.00,0.00),(104,29,0,0.00,0.00),(110,37,0,0.00,0.00),(133,38,0,0.00,0.00),(134,39,0,0.00,0.00),(135,41,0,0.00,0.00),(136,42,0,0.00,0.00),(137,43,0,0.00,0.00),(138,44,0,0.00,0.00),(139,45,1,0.70,0.00),(140,40,0,0.00,0.00),(147,26,0,0.00,0.00);
/*!40000 ALTER TABLE `pms_product_ladder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_operate_log`
--

DROP TABLE IF EXISTS `pms_product_operate_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_operate_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `price_old` decimal(10,2) DEFAULT NULL,
  `price_new` decimal(10,2) DEFAULT NULL,
  `sale_price_old` decimal(10,2) DEFAULT NULL,
  `sale_price_new` decimal(10,2) DEFAULT NULL,
  `gift_point_old` int(11) DEFAULT NULL COMMENT '赠送的积分',
  `gift_point_new` int(11) DEFAULT NULL,
  `use_point_limit_old` int(11) DEFAULT NULL,
  `use_point_limit_new` int(11) DEFAULT NULL,
  `operate_man` varchar(64) DEFAULT NULL COMMENT '操作人',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_operate_log`
--

LOCK TABLES `pms_product_operate_log` WRITE;
/*!40000 ALTER TABLE `pms_product_operate_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `pms_product_operate_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_product_vertify_record`
--

DROP TABLE IF EXISTS `pms_product_vertify_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_product_vertify_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `vertify_man` varchar(64) DEFAULT NULL COMMENT '审核人',
  `status` int(1) DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL COMMENT '反馈详情',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品审核记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_product_vertify_record`
--

LOCK TABLES `pms_product_vertify_record` WRITE;
/*!40000 ALTER TABLE `pms_product_vertify_record` DISABLE KEYS */;
INSERT INTO `pms_product_vertify_record` VALUES (1,1,'2018-04-27 16:36:41','test',1,'验证通过'),(2,2,'2018-04-27 16:36:41','test',1,'验证通过');
/*!40000 ALTER TABLE `pms_product_vertify_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pms_sku_stock`
--

DROP TABLE IF EXISTS `pms_sku_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pms_sku_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `sku_code` varchar(64) NOT NULL COMMENT 'sku编码',
  `price` decimal(10,2) DEFAULT NULL,
  `stock` int(11) DEFAULT '0' COMMENT '库存',
  `low_stock` int(11) DEFAULT NULL COMMENT '预警库存',
  `pic` varchar(255) DEFAULT NULL COMMENT '展示图片',
  `sale` int(11) DEFAULT NULL COMMENT '销量',
  `promotion_price` decimal(10,2) DEFAULT NULL COMMENT '单品促销价格',
  `lock_stock` int(11) DEFAULT '0' COMMENT '锁定库存',
  `sp_data` varchar(500) DEFAULT NULL COMMENT '商品销售属性，json格式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=243 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='sku的库存';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pms_sku_stock`
--

LOCK TABLES `pms_sku_stock` WRITE;
/*!40000 ALTER TABLE `pms_sku_stock` DISABLE KEYS */;
INSERT INTO `pms_sku_stock` VALUES (98,27,'201808270027001',2699.00,86,NULL,NULL,NULL,NULL,-24,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(99,27,'201808270027002',2999.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(100,27,'201808270027003',2699.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(101,27,'201808270027004',2999.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(102,28,'201808270028001',649.00,99,NULL,NULL,NULL,NULL,-8,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(103,28,'201808270028002',699.00,99,NULL,NULL,NULL,NULL,-8,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(104,28,'201808270028003',649.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(105,28,'201808270028004',699.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(106,29,'201808270029001',5499.00,99,NULL,NULL,NULL,4999.00,-8,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(107,29,'201808270029002',6299.00,100,NULL,NULL,NULL,5799.00,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(108,29,'201808270029003',5499.00,100,NULL,NULL,NULL,4999.00,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(109,29,'201808270029004',6299.00,100,NULL,NULL,NULL,5799.00,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(110,26,'201806070026001',3788.00,487,NULL,NULL,NULL,3699.00,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(111,26,'201806070026002',3999.00,499,NULL,NULL,NULL,3899.00,0,'[{\"key\":\"颜色\",\"value\":\"金色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(112,26,'201806070026003',3788.00,500,NULL,NULL,NULL,3699.00,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"16G\"}]'),(113,26,'201806070026004',3999.00,500,NULL,NULL,NULL,3899.00,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"32G\"}]'),(163,36,'202002210036001',100.00,100,25,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(164,36,'202002210036002',120.00,98,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(165,36,'202002210036003',100.00,100,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(166,36,'202002210036004',100.00,100,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(167,36,'202002210036005',100.00,100,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(168,36,'202002210036006',100.00,100,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(169,36,'202002210036007',100.00,100,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(170,36,'202002210036008',100.00,100,20,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(171,35,'202002250035001',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(172,35,'202002250035002',240.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(173,35,'202002250035003',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(174,35,'202002250035004',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"红色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(175,35,'202002250035005',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(176,35,'202002250035006',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"38\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(177,35,'202002250035007',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"夏季\"}]'),(178,35,'202002250035008',200.00,100,50,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"39\"},{\"key\":\"风格\",\"value\":\"秋季\"}]'),(179,30,'202004190030001',88.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"X\"}]'),(180,30,'202004190030002',88.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"XL\"}]'),(181,30,'202004190030003',88.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"尺寸\",\"value\":\"M\"}]'),(182,30,'202004190030004',88.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"白色\"},{\"key\":\"尺寸\",\"value\":\"X\"}]'),(183,30,'202004190030005',88.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"白色\"},{\"key\":\"尺寸\",\"value\":\"XL\"}]'),(184,30,'202004190030006',88.00,100,NULL,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"白色\"},{\"key\":\"尺寸\",\"value\":\"M\"}]'),(185,31,'202004190031001',88.00,80,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"浅灰色\"},{\"key\":\"尺寸\",\"value\":\"X\"}]'),(186,31,'202004190031002',88.00,80,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"浅灰色\"},{\"key\":\"尺寸\",\"value\":\"XL\"}]'),(187,31,'202004190031003',88.00,80,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"深灰色\"},{\"key\":\"尺寸\",\"value\":\"X\"}]'),(188,31,'202004190031004',88.00,80,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"深灰色\"},{\"key\":\"尺寸\",\"value\":\"XL\"}]'),(189,32,'202004190032001',99.00,100,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"尺寸\",\"value\":\"X\"}]'),(190,32,'202004190032002',99.00,100,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"尺寸\",\"value\":\"M\"}]'),(191,32,'202004190032003',99.00,100,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"白色\"},{\"key\":\"尺寸\",\"value\":\"X\"}]'),(192,32,'202004190032004',99.00,100,10,NULL,NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"白色\"},{\"key\":\"尺寸\",\"value\":\"M\"}]'),(193,33,'202004190033001',2499.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"50英寸\"},{\"key\":\"内存\",\"value\":\"8G\"}]'),(194,33,'202004190033002',2499.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"50英寸\"},{\"key\":\"内存\",\"value\":\"16G\"}]'),(195,33,'202004190033003',2499.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"65英寸\"},{\"key\":\"内存\",\"value\":\"8G\"}]'),(196,33,'202004190033004',2499.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"65英寸\"},{\"key\":\"内存\",\"value\":\"16G\"}]'),(197,34,'202004190034001',3999.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"65英寸\"},{\"key\":\"内存\",\"value\":\"16G\"}]'),(198,34,'202004190034002',3999.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"65英寸\"},{\"key\":\"内存\",\"value\":\"32G\"}]'),(199,34,'202004190034003',3999.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"70英寸\"},{\"key\":\"内存\",\"value\":\"16G\"}]'),(200,34,'202004190034004',3999.00,500,10,NULL,NULL,NULL,0,'[{\"key\":\"尺寸\",\"value\":\"70英寸\"},{\"key\":\"内存\",\"value\":\"32G\"}]'),(201,37,'202210280037001',5999.00,195,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg',NULL,NULL,1,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(202,37,'202210280037002',6899.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(203,37,'202210280037003',8699.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_001.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"午夜色\"},{\"key\":\"容量\",\"value\":\"512G\"}]'),(204,37,'202210280037004',5999.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"星光色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(205,37,'202210280037005',6899.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"星光色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(206,37,'202210280037006',8699.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"星光色\"},{\"key\":\"容量\",\"value\":\"512G\"}]'),(207,37,'202210280037007',5999.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_003.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"紫色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(208,37,'202210280037008',6899.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_003.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"紫色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(209,37,'202210280037009',8699.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_003.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"紫色\"},{\"key\":\"容量\",\"value\":\"512G\"}]'),(210,37,'202210280037010',5999.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_004.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(211,37,'202210280037011',6899.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_004.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(212,37,'202210280037012',8699.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/iphone14_004.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"512G\"}]'),(213,38,'202210280038001',3599.00,198,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(214,38,'202210280038002',4799.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_001.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"银色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(215,38,'202210280038003',3599.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"64G\"}]'),(216,38,'202210280038004',4799.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/ipad_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(217,39,'202210280039001',5999.00,499,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(218,39,'202210280039002',5599.00,500,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_001.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"新小米Pro 14英寸 2.8K屏\"},{\"key\":\"版本\",\"value\":\"R5 16G 512\"}]'),(219,39,'202210280039003',5499.00,500,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"新Redmi Pro 15英寸 3.2K屏\"},{\"key\":\"版本\",\"value\":\"R7 16G 512\"}]'),(220,39,'202210280039004',4999.00,500,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221028/xiaomi_computer_002.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"新Redmi Pro 15英寸 3.2K屏\"},{\"key\":\"版本\",\"value\":\"R5 16G 512\"}]'),(221,40,'202211040040001',2999.00,91,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(222,40,'202211040040002',3499.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"黑色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(223,40,'202211040040003',2999.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(224,40,'202211040040004',3499.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/xiaomi_12_pro_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"蓝色\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(225,41,'202211040041001',2099.00,195,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(226,41,'202211040041002',2299.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"墨羽\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(227,41,'202211040041003',2099.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"银迹\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(228,41,'202211040041004',2299.00,200,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/redmi_k50_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"银迹\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(229,42,'202211040042001',4999.00,99,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"曜金黑\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(230,42,'202211040042002',5499.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"曜金黑\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(231,42,'202211040042003',4999.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"冰霜银\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(232,42,'202211040042004',5499.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/huawei_mate50_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"冰霜银\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(233,43,'202211040043001',1649.00,500,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_13L_01.png',NULL,NULL,0,'[{\"key\":\"系列\",\"value\":\"JSQ25-565W13【13升】【恒温旗舰款】\"}]'),(234,43,'202211040043002',1799.00,500,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221104/wanhe_13L_01.png',NULL,NULL,0,'[{\"key\":\"系列\",\"value\":\"JSQ30-565W16【16升】【恒温旗舰款】\"}]'),(235,44,'202211080044001',369.00,99,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"新品980｜NVMe PCIe3.0*4\"},{\"key\":\"版本\",\"value\":\"512GB\"}]'),(236,44,'202211080044002',649.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"新品980｜NVMe PCIe3.0*4\"},{\"key\":\"版本\",\"value\":\"1TB\"}]'),(237,44,'202211080044003',549.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"980 PRO｜NVMe PCIe 4.0\"},{\"key\":\"版本\",\"value\":\"512GB\"}]'),(238,44,'202211080044004',899.00,100,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_ssd_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"980 PRO｜NVMe PCIe 4.0\"},{\"key\":\"版本\",\"value\":\"1TB\"}]'),(239,45,'202211080045001',2299.00,250,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"鸢尾紫\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(240,45,'202211080045002',2499.00,250,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_01.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"鸢尾紫\"},{\"key\":\"容量\",\"value\":\"256G\"}]'),(241,45,'202211080045003',2299.00,250,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"晴空蓝\"},{\"key\":\"容量\",\"value\":\"128G\"}]'),(242,45,'202211080045004',2499.00,250,NULL,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_r8_02.jpg',NULL,NULL,0,'[{\"key\":\"颜色\",\"value\":\"晴空蓝\"},{\"key\":\"容量\",\"value\":\"256G\"}]');
/*!40000 ALTER TABLE `pms_sku_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon`
--

DROP TABLE IF EXISTS `sms_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` int(1) DEFAULT NULL COMMENT '优惠券类型；0->全场赠券；1->会员赠券；2->购物赠券；3->注册赠券',
  `name` varchar(100) DEFAULT NULL,
  `platform` int(1) DEFAULT NULL COMMENT '使用平台：0->全部；1->移动；2->PC',
  `count` int(11) DEFAULT NULL COMMENT '数量',
  `amount` decimal(10,2) DEFAULT NULL COMMENT '金额',
  `per_limit` int(11) DEFAULT NULL COMMENT '每人限领张数',
  `min_point` decimal(10,2) DEFAULT NULL COMMENT '使用门槛；0表示无门槛',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `use_type` int(1) DEFAULT NULL COMMENT '使用类型：0->全场通用；1->指定分类；2->指定商品',
  `note` varchar(200) DEFAULT NULL COMMENT '备注',
  `publish_count` int(11) DEFAULT NULL COMMENT '发行数量',
  `use_count` int(11) DEFAULT NULL COMMENT '已使用数量',
  `receive_count` int(11) DEFAULT NULL COMMENT '领取数量',
  `enable_time` datetime DEFAULT NULL COMMENT '可以领取的日期',
  `code` varchar(64) DEFAULT NULL COMMENT '优惠码',
  `member_level` int(1) DEFAULT NULL COMMENT '可领取的会员类型：0->无限时',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon`
--

LOCK TABLES `sms_coupon` WRITE;
/*!40000 ALTER TABLE `sms_coupon` DISABLE KEYS */;
INSERT INTO `sms_coupon` VALUES (27,0,'全品类通用券',0,94,10.00,10,100.00,'2022-11-08 00:00:00','2023-11-30 00:00:00',0,NULL,100,0,6,'2022-11-08 00:00:00',NULL,NULL),(28,0,'手机分类专用券',0,995,100.00,5,1000.00,'2022-11-08 00:00:00','2023-11-30 00:00:00',1,NULL,1000,0,5,'2022-11-08 00:00:00',NULL,NULL),(29,0,'苹果手机专用券',0,998,600.00,1,4000.00,'2022-11-08 00:00:00','2023-11-30 00:00:00',2,NULL,1000,0,2,'2022-11-08 00:00:00',NULL,NULL),(30,0,'小米手机专用券',0,998,200.00,1,2000.00,'2022-11-08 00:00:00','2023-11-30 00:00:00',2,NULL,1000,0,2,'2022-11-08 00:00:00',NULL,NULL),(31,0,'限时优惠券',0,999,20.00,5,500.00,'2022-12-01 00:00:00','2022-12-22 00:00:00',0,NULL,1000,0,1,'2022-12-23 00:00:00',NULL,NULL);
/*!40000 ALTER TABLE `sms_coupon` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon_history`
--

DROP TABLE IF EXISTS `sms_coupon_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint(20) DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `coupon_code` varchar(64) DEFAULT NULL,
  `member_nickname` varchar(64) DEFAULT NULL COMMENT '领取人昵称',
  `get_type` int(1) DEFAULT NULL COMMENT '获取类型：0->后台赠送；1->主动获取',
  `create_time` datetime DEFAULT NULL,
  `use_status` int(1) DEFAULT NULL COMMENT '使用状态：0->未使用；1->已使用；2->已过期',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `order_id` bigint(20) DEFAULT NULL COMMENT '订单编号',
  `order_sn` varchar(100) DEFAULT NULL COMMENT '订单号码',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_member_id` (`member_id`) USING BTREE,
  KEY `idx_coupon_id` (`coupon_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券使用、领取历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon_history`
--

LOCK TABLES `sms_coupon_history` WRITE;
/*!40000 ALTER TABLE `sms_coupon_history` DISABLE KEYS */;
INSERT INTO `sms_coupon_history` VALUES (37,30,1,'7806895974110001','windir',1,'2022-11-09 15:14:29',1,'2022-11-09 15:14:58',NULL,NULL),(38,27,1,'7872472849240001','windir',1,'2022-11-09 15:25:25',1,'2022-11-09 15:25:38',NULL,NULL),(39,29,1,'7876204111480001','windir',1,'2022-11-09 15:26:02',1,'2022-11-09 15:26:11',NULL,NULL),(40,27,1,'7911945030190001','windir',1,'2022-11-09 15:31:59',0,NULL,NULL,NULL),(41,28,1,'8194868687790001','windir',1,'2022-11-09 16:19:09',1,'2022-11-11 16:12:42',NULL,NULL),(42,28,1,'1239565720390001','test',1,'2022-12-21 16:46:36',1,'2022-12-21 16:53:07',NULL,NULL),(43,31,1,'6030247208280001','test',1,'2022-12-23 09:51:42',0,NULL,NULL,NULL),(44,28,1,'6050939443480001','test',1,'2022-12-23 09:55:09',0,NULL,NULL,NULL),(45,27,1,'4182437014580001','test',1,'2023-01-10 17:10:24',0,NULL,NULL,NULL),(46,27,11,'9011281751500011','member',1,'2023-05-11 15:28:33',0,NULL,NULL,NULL),(47,28,11,'9011495851370011','member',1,'2023-05-11 15:28:35',1,'2023-05-11 15:37:16',NULL,NULL),(48,30,11,'9011677197430011','member',1,'2023-05-11 15:28:37',1,'2023-05-11 15:28:56',NULL,NULL),(49,27,11,'9046676666610011','member',1,'2023-05-11 15:34:27',0,NULL,NULL,NULL),(50,28,11,'9046909751910011','member',1,'2023-05-11 15:34:29',0,NULL,NULL,NULL),(51,29,11,'9047077722990011','member',1,'2023-05-11 15:34:31',0,NULL,NULL,NULL),(52,27,11,'9075818288630011','member',1,'2023-05-11 15:39:18',0,NULL,NULL,NULL);
/*!40000 ALTER TABLE `sms_coupon_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon_product_category_relation`
--

DROP TABLE IF EXISTS `sms_coupon_product_category_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon_product_category_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint(20) DEFAULT NULL,
  `product_category_id` bigint(20) DEFAULT NULL,
  `product_category_name` varchar(200) DEFAULT NULL COMMENT '产品分类名称',
  `parent_category_name` varchar(200) DEFAULT NULL COMMENT '父分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券和产品分类关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon_product_category_relation`
--

LOCK TABLES `sms_coupon_product_category_relation` WRITE;
/*!40000 ALTER TABLE `sms_coupon_product_category_relation` DISABLE KEYS */;
INSERT INTO `sms_coupon_product_category_relation` VALUES (11,28,19,'手机通讯','手机数码');
/*!40000 ALTER TABLE `sms_coupon_product_category_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_coupon_product_relation`
--

DROP TABLE IF EXISTS `sms_coupon_product_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_coupon_product_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `coupon_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `product_name` varchar(500) DEFAULT NULL COMMENT '商品名称',
  `product_sn` varchar(200) DEFAULT NULL COMMENT '商品编码',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='优惠券和产品的关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_coupon_product_relation`
--

LOCK TABLES `sms_coupon_product_relation` WRITE;
/*!40000 ALTER TABLE `sms_coupon_product_relation` DISABLE KEYS */;
INSERT INTO `sms_coupon_product_relation` VALUES (18,29,37,'Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机','100038005189'),(19,29,29,'Apple iPhone 8 Plus 64GB 红色特别版 移动联通电信4G手机','7437799'),(21,30,41,'Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量 墨羽 12GB+256GB 5G智能手机 小米 红米','100035246702'),(22,30,40,'小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机','100027789721');
/*!40000 ALTER TABLE `sms_coupon_product_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_flash_promotion`
--

DROP TABLE IF EXISTS `sms_flash_promotion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_flash_promotion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(200) DEFAULT NULL COMMENT '秒杀时间段名称',
  `start_date` date DEFAULT NULL COMMENT '开始日期',
  `end_date` date DEFAULT NULL COMMENT '结束日期',
  `status` int(1) DEFAULT NULL COMMENT '上下线状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='限时购表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_flash_promotion`
--

LOCK TABLES `sms_flash_promotion` WRITE;
/*!40000 ALTER TABLE `sms_flash_promotion` DISABLE KEYS */;
INSERT INTO `sms_flash_promotion` VALUES (14,'双11特卖活动','2022-11-09','2023-12-31',1,'2022-11-09 14:56:48');
/*!40000 ALTER TABLE `sms_flash_promotion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_flash_promotion_log`
--

DROP TABLE IF EXISTS `sms_flash_promotion_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_flash_promotion_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `member_id` int(11) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `member_phone` varchar(64) DEFAULT NULL,
  `product_name` varchar(100) DEFAULT NULL,
  `subscribe_time` datetime DEFAULT NULL COMMENT '会员订阅时间',
  `send_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='限时购通知记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_flash_promotion_log`
--

LOCK TABLES `sms_flash_promotion_log` WRITE;
/*!40000 ALTER TABLE `sms_flash_promotion_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_flash_promotion_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_flash_promotion_product_relation`
--

DROP TABLE IF EXISTS `sms_flash_promotion_product_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_flash_promotion_product_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `flash_promotion_id` bigint(20) DEFAULT NULL,
  `flash_promotion_session_id` bigint(20) DEFAULT NULL COMMENT '编号',
  `product_id` bigint(20) DEFAULT NULL,
  `flash_promotion_price` decimal(10,2) DEFAULT NULL COMMENT '限时购价格',
  `flash_promotion_count` int(11) DEFAULT NULL COMMENT '限时购数量',
  `flash_promotion_limit` int(11) DEFAULT NULL COMMENT '每人限购数量',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='商品限时购与商品关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_flash_promotion_product_relation`
--

LOCK TABLES `sms_flash_promotion_product_relation` WRITE;
/*!40000 ALTER TABLE `sms_flash_promotion_product_relation` DISABLE KEYS */;
INSERT INTO `sms_flash_promotion_product_relation` VALUES (1,2,1,26,3000.00,10,1,0),(2,2,1,27,2000.00,10,1,20),(3,2,1,28,599.00,19,1,0),(4,2,1,29,4999.00,10,1,100),(9,2,2,26,2999.00,100,1,0),(10,2,2,27,NULL,NULL,NULL,NULL),(11,2,2,28,NULL,NULL,NULL,NULL),(12,2,2,29,NULL,NULL,NULL,NULL),(13,2,2,30,NULL,NULL,NULL,NULL),(14,2,3,31,NULL,NULL,NULL,NULL),(15,2,3,32,NULL,NULL,NULL,NULL),(16,2,4,33,NULL,NULL,NULL,NULL),(17,2,4,34,NULL,NULL,NULL,NULL),(18,2,5,36,NULL,NULL,NULL,NULL),(19,2,6,33,NULL,NULL,NULL,NULL),(20,2,6,34,NULL,NULL,NULL,NULL),(21,14,1,26,NULL,NULL,NULL,NULL),(22,14,1,27,NULL,NULL,NULL,NULL),(23,14,1,28,NULL,NULL,NULL,NULL),(24,14,1,29,NULL,NULL,NULL,NULL),(25,14,2,30,NULL,NULL,NULL,NULL),(26,14,2,31,NULL,NULL,NULL,NULL),(27,14,2,32,NULL,NULL,NULL,NULL),(28,14,2,35,NULL,NULL,NULL,NULL),(29,14,3,33,NULL,NULL,NULL,NULL),(30,14,3,34,NULL,NULL,NULL,NULL),(31,14,3,36,NULL,NULL,NULL,NULL),(32,14,3,37,NULL,NULL,NULL,NULL),(33,14,4,37,5699.00,10,1,NULL),(34,14,4,38,NULL,NULL,NULL,NULL),(35,14,4,39,NULL,NULL,NULL,NULL),(36,14,4,40,NULL,NULL,NULL,NULL),(37,14,5,41,NULL,NULL,NULL,NULL),(38,14,5,42,NULL,NULL,NULL,NULL),(39,14,5,43,NULL,NULL,NULL,NULL),(40,14,5,44,NULL,NULL,NULL,NULL),(41,14,5,45,NULL,NULL,NULL,NULL),(42,14,6,26,NULL,NULL,NULL,NULL),(43,14,6,27,NULL,NULL,NULL,NULL),(44,14,6,28,NULL,NULL,NULL,NULL),(45,14,6,29,NULL,NULL,NULL,NULL),(46,14,6,30,NULL,NULL,NULL,NULL),(47,14,7,31,NULL,NULL,NULL,NULL),(48,14,7,32,NULL,NULL,NULL,NULL),(49,14,7,33,NULL,NULL,NULL,NULL),(50,14,7,34,NULL,NULL,NULL,NULL),(51,14,7,35,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `sms_flash_promotion_product_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_flash_promotion_session`
--

DROP TABLE IF EXISTS `sms_flash_promotion_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_flash_promotion_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(200) DEFAULT NULL COMMENT '场次名称',
  `start_time` time DEFAULT NULL COMMENT '每日开始时间',
  `end_time` time DEFAULT NULL COMMENT '每日结束时间',
  `status` int(1) DEFAULT NULL COMMENT '启用状态：0->不启用；1->启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='限时购场次表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_flash_promotion_session`
--

LOCK TABLES `sms_flash_promotion_session` WRITE;
/*!40000 ALTER TABLE `sms_flash_promotion_session` DISABLE KEYS */;
INSERT INTO `sms_flash_promotion_session` VALUES (1,'8:00','08:00:00','10:00:00',1,'2018-11-16 13:22:17'),(2,'10:00','10:00:00','12:00:00',1,'2018-11-16 13:22:34'),(3,'12:00','12:00:00','14:00:00',1,'2018-11-16 13:22:37'),(4,'14:00','14:00:00','16:00:00',1,'2018-11-16 13:22:41'),(5,'16:00','16:00:00','18:00:00',1,'2018-11-16 13:22:45'),(6,'18:00','18:00:00','20:00:00',1,'2018-11-16 13:21:34'),(7,'20:00','20:00:00','22:00:00',1,'2018-11-16 13:22:55');
/*!40000 ALTER TABLE `sms_flash_promotion_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_advertise`
--

DROP TABLE IF EXISTS `sms_home_advertise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_advertise` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `type` int(1) DEFAULT NULL COMMENT '轮播位置：0->PC首页轮播；1->app首页轮播',
  `pic` varchar(500) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '上下线状态：0->下线；1->上线',
  `click_count` int(11) DEFAULT NULL COMMENT '点击数',
  `order_count` int(11) DEFAULT NULL COMMENT '下单数',
  `url` varchar(500) DEFAULT NULL COMMENT '链接地址',
  `note` varchar(500) DEFAULT NULL COMMENT '备注',
  `sort` int(11) DEFAULT '0' COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='首页轮播广告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_advertise`
--

LOCK TABLES `sms_home_advertise` WRITE;
/*!40000 ALTER TABLE `sms_home_advertise` DISABLE KEYS */;
INSERT INTO `sms_home_advertise` VALUES (2,'夏季大热促销',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190525/ad1.jpg','2018-11-01 14:01:37','2018-11-15 14:01:37',0,0,0,NULL,'夏季大热促销',0),(3,'夏季大热促销1',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190525/ad1.jpg','2018-11-13 14:01:37','2018-11-13 14:01:37',0,0,0,NULL,'夏季大热促销1',0),(4,'夏季大热促销2',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20190525/ad2.jpg','2018-11-13 14:01:37','2018-11-13 14:01:37',0,0,0,NULL,'夏季大热促销2',0),(9,'电影推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20181113/movie_ad.jpg','2018-11-01 00:00:00','2018-11-24 00:00:00',0,0,0,'www.baidu.com','电影推荐广告',100),(10,'汽车促销广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20181113/car_ad.jpg','2018-11-13 00:00:00','2018-11-24 00:00:00',0,0,0,'xxx',NULL,99),(11,'汽车推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20181113/car_ad2.jpg','2018-11-13 00:00:00','2018-11-30 00:00:00',0,0,0,'xxx',NULL,98),(12,'小米推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/xiaomi_banner_01.png','2022-11-08 17:04:03','2023-11-08 17:04:05',1,0,0,'/pages/brand/brandDetail?id=6',NULL,0),(13,'华为推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/huawei_banner_01.png','2022-11-08 17:10:27','2023-11-08 17:10:28',1,0,0,'/pages/brand/brandDetail?id=3',NULL,0),(14,'苹果推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/apple_banner_01.png','2022-11-08 17:12:54','2023-11-08 17:12:55',1,0,0,'/pages/brand/brandDetail?id=51',NULL,0),(15,'三星推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/sanxing_banner_01.png','2022-11-08 17:15:38','2023-11-08 17:15:39',1,0,0,'/pages/brand/brandDetail?id=2',NULL,0),(16,'OPPO推荐广告',1,'http://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/images/20221108/oppo_banner_01.png','2022-11-08 17:20:10','2023-11-08 17:20:11',1,0,0,'/pages/brand/brandDetail?id=21',NULL,0);
/*!40000 ALTER TABLE `sms_home_advertise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_brand`
--

DROP TABLE IF EXISTS `sms_home_brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_brand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `brand_id` bigint(20) DEFAULT NULL,
  `brand_name` varchar(64) DEFAULT NULL,
  `recommend_status` int(1) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='首页推荐品牌表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_brand`
--

LOCK TABLES `sms_home_brand` WRITE;
/*!40000 ALTER TABLE `sms_home_brand` DISABLE KEYS */;
INSERT INTO `sms_home_brand` VALUES (6,6,'小米',1,300),(32,50,'海澜之家',1,198),(33,51,'苹果',1,199),(34,2,'三星',1,195),(35,3,'华为',1,200),(39,21,'OPPO',1,197),(45,1,'万和',1,0),(46,5,'方太',1,0),(47,4,'格力',1,0);
/*!40000 ALTER TABLE `sms_home_brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_new_product`
--

DROP TABLE IF EXISTS `sms_home_new_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_new_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `product_name` varchar(500) DEFAULT NULL,
  `recommend_status` int(1) DEFAULT NULL,
  `sort` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='新鲜好物表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_new_product`
--

LOCK TABLES `sms_home_new_product` WRITE;
/*!40000 ALTER TABLE `sms_home_new_product` DISABLE KEYS */;
INSERT INTO `sms_home_new_product` VALUES (19,37,'Apple iPhone 14 (A2884) 128GB 支持移动联通电信5G 双卡双待手机',1,197),(20,38,'Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）',1,0),(21,39,'小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑(新R5-6600H标压 16G 512G win11)',1,198),(22,40,'小米12 Pro 天玑版 天玑9000+处理器 5000万疾速影像 2K超视感屏 120Hz高刷 67W快充 12GB+256GB 黑色 5G手机',1,200),(23,41,'Redmi K50 天玑8100 2K柔性直屏 OIS光学防抖 67W快充 5500mAh大电量 墨羽 12GB+256GB 5G智能手机 小米 红米',1,199),(24,42,'HUAWEI Mate 50 直屏旗舰 超光变XMAGE影像 北斗卫星消息 低电量应急模式 128GB曜金黑华为鸿蒙手机',1,0),(25,44,'三星（SAMSUNG）500GB SSD固态硬盘 M.2接口(NVMe协议) 980（MZ-V8V500BW）',1,0),(26,45,'OPPO Reno8 8GB+128GB 鸢尾紫 新配色上市 80W超级闪充 5000万水光人像三摄 3200万前置索尼镜头 5G手机',1,0),(27,43,'万和（Vanward)燃气热水器天然气家用四重防冻直流变频节能全新升级增压水伺服恒温高抗风 JSQ30-565W16【16升】【恒温旗舰款】',1,0);
/*!40000 ALTER TABLE `sms_home_new_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_recommend_product`
--

DROP TABLE IF EXISTS `sms_home_recommend_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_recommend_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_id` bigint(20) DEFAULT NULL,
  `product_name` varchar(500) DEFAULT NULL,
  `recommend_status` int(1) DEFAULT NULL,
  `sort` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='人气推荐商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_recommend_product`
--

LOCK TABLES `sms_home_recommend_product` WRITE;
/*!40000 ALTER TABLE `sms_home_recommend_product` DISABLE KEYS */;
INSERT INTO `sms_home_recommend_product` VALUES (10,38,'Apple iPad 10.9英寸平板电脑 2022年款（64GB WLAN版/A14芯片/1200万像素/iPadOS MPQ03CH/A ）',1,0),(11,39,'小米 Xiaomi Book Pro 14 2022 锐龙版 2.8K超清大师屏 高端轻薄笔记本电脑(新R5-6600H标压 16G 512G win11)',1,0),(12,44,'三星（SAMSUNG）500GB SSD固态硬盘 M.2接口(NVMe协议) 980（MZ-V8V500BW）',1,0),(13,43,'万和（Vanward)燃气热水器天然气家用四重防冻直流变频节能全新升级增压水伺服恒温高抗风 JSQ30-565W16【16升】【恒温旗舰款】',1,0),(14,45,'OPPO Reno8 8GB+128GB 鸢尾紫 新配色上市 80W超级闪充 5000万水光人像三摄 3200万前置索尼镜头 5G手机',1,0);
/*!40000 ALTER TABLE `sms_home_recommend_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_home_recommend_subject`
--

DROP TABLE IF EXISTS `sms_home_recommend_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sms_home_recommend_subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_id` bigint(20) DEFAULT NULL,
  `subject_name` varchar(64) DEFAULT NULL,
  `recommend_status` int(1) DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='首页推荐专题表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_home_recommend_subject`
--

LOCK TABLES `sms_home_recommend_subject` WRITE;
/*!40000 ALTER TABLE `sms_home_recommend_subject` DISABLE KEYS */;
INSERT INTO `sms_home_recommend_subject` VALUES (14,1,'polo衬衫的也时尚',1,0),(15,2,'大牌手机低价秒',1,0),(16,3,'晓龙845新品上市',1,0),(17,4,'夏天应该穿什么',1,0),(18,5,'夏季精选',1,100),(19,6,'品牌手机降价',1,0);
/*!40000 ALTER TABLE `sms_home_recommend_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_admin`
--

DROP TABLE IF EXISTS `ums_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `icon` varchar(500) DEFAULT NULL COMMENT '头像',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nick_name` varchar(200) DEFAULT NULL COMMENT '昵称',
  `note` varchar(500) DEFAULT NULL COMMENT '备注信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` int(1) DEFAULT '1' COMMENT '帐号启用状态：0->禁用；1->启用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_admin`
--

LOCK TABLES `ums_admin` WRITE;
/*!40000 ALTER TABLE `ums_admin` DISABLE KEYS */;
INSERT INTO `ums_admin` VALUES (1,'test','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','https://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/icon/github_icon_02.png','test@qq.com','测试账号',NULL,'2018-09-29 13:55:30','2018-09-29 13:55:39',1),(3,'admin','$2a$10$vPcVMiG3M6NlTxla2uvNy.5Pjcnol4aptL0KOOkJozuOBptab2giC','https://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/icon/github_icon_01.png','admin@163.com','系统管理员','系统管理员','2018-10-08 13:32:47','2019-04-20 12:45:16',1),(4,'macro','$2a$10$Bx4jZPR7GhEpIQfefDQtVeS58GfT5n6mxs/b4nLLK65eMFa16topa','https://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/icon/github_icon_01.png','macro@qq.com','macro','macro专用','2019-10-06 15:53:51','2020-02-03 14:55:55',1),(6,'productAdmin','$2a$10$6/.J.p.6Bhn7ic4GfoB5D.pGd7xSiD1a9M6ht6yO0fxzlKJPjRAGm','https://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/icon/github_icon_03.png','product@qq.com','商品管理员','只有商品权限','2020-02-07 16:15:08',NULL,1),(7,'orderAdmin','$2a$10$UqEhA9UZXjHHA3B.L9wNG.6aerrBjC6WHTtbv1FdvYPUI.7lkL6E.','https://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/icon/github_icon_04.png','order@qq.com','订单管理员','只有订单管理权限','2020-02-07 16:15:50',NULL,1),(8,'test123','$2a$10$M1qJguEzwoAN0la7PB8UO.HOGe1xZGku7xw1iTaUUpY0ZVRCxrxoO','string','abc@qq.com','string','string','2022-08-07 14:45:42',NULL,1),(9,'test256','$2a$10$kTMBrt8mkXcO8T4eHThFWOf3KuNK6tqevkiAf4YbtXtaCJ6ocYkJa','string','abc@qq.com','string','string','2022-08-07 14:52:57',NULL,1),(10,'test1267','$2a$10$VUywDhXVPY13YZxMD/uPWeDqkzSozN7o8u/3MX6sBig2NK2VaTJZ2',NULL,'test1267@qq.com','test1267','test1267','2023-01-04 16:13:34',NULL,1);
/*!40000 ALTER TABLE `ums_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_admin_login_log`
--

DROP TABLE IF EXISTS `ums_admin_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_admin_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `user_agent` varchar(100) DEFAULT NULL COMMENT '浏览器登录类型',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=419 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户登录日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_admin_login_log`
--

LOCK TABLES `ums_admin_login_log` WRITE;
/*!40000 ALTER TABLE `ums_admin_login_log` DISABLE KEYS */;
INSERT INTO `ums_admin_login_log` VALUES (1,3,'2018-12-23 14:27:00','0:0:0:0:0:0:0:1',NULL,NULL),(2,3,'2019-04-07 16:04:39','0:0:0:0:0:0:0:1',NULL,NULL),(3,3,'2019-04-08 21:47:52','0:0:0:0:0:0:0:1',NULL,NULL),(4,3,'2019-04-08 21:48:18','0:0:0:0:0:0:0:1',NULL,NULL),(5,3,'2019-04-18 22:18:40','0:0:0:0:0:0:0:1',NULL,NULL),(6,3,'2019-04-20 12:45:16','0:0:0:0:0:0:0:1',NULL,NULL),(7,3,'2019-05-19 14:52:12','0:0:0:0:0:0:0:1',NULL,NULL),(8,3,'2019-05-25 15:00:17','0:0:0:0:0:0:0:1',NULL,NULL),(9,3,'2019-06-19 20:11:42','0:0:0:0:0:0:0:1',NULL,NULL),(10,3,'2019-06-30 10:33:48','0:0:0:0:0:0:0:1',NULL,NULL),(11,3,'2019-06-30 10:34:31','0:0:0:0:0:0:0:1',NULL,NULL),(12,3,'2019-06-30 10:35:34','0:0:0:0:0:0:0:1',NULL,NULL),(13,3,'2019-07-27 17:11:01','0:0:0:0:0:0:0:1',NULL,NULL),(14,3,'2019-07-27 17:13:18','0:0:0:0:0:0:0:1',NULL,NULL),(15,3,'2019-07-27 17:15:35','0:0:0:0:0:0:0:1',NULL,NULL),(16,3,'2019-07-27 17:17:11','0:0:0:0:0:0:0:1',NULL,NULL),(17,3,'2019-07-27 17:18:34','0:0:0:0:0:0:0:1',NULL,NULL),(18,3,'2019-07-27 21:21:52','0:0:0:0:0:0:0:1',NULL,NULL),(19,3,'2019-07-27 21:34:29','0:0:0:0:0:0:0:1',NULL,NULL),(20,3,'2019-07-27 21:35:17','0:0:0:0:0:0:0:1',NULL,NULL),(21,3,'2019-07-27 21:35:48','0:0:0:0:0:0:0:1',NULL,NULL),(22,3,'2019-07-27 21:40:33','0:0:0:0:0:0:0:1',NULL,NULL),(23,3,'2019-08-18 16:00:38','0:0:0:0:0:0:0:1',NULL,NULL),(24,3,'2019-08-18 16:01:06','0:0:0:0:0:0:0:1',NULL,NULL),(25,3,'2019-08-18 16:47:01','0:0:0:0:0:0:0:1',NULL,NULL),(26,3,'2019-10-06 15:54:23','0:0:0:0:0:0:0:1',NULL,NULL),(27,3,'2019-10-06 16:03:28','0:0:0:0:0:0:0:1',NULL,NULL),(28,3,'2019-10-06 16:04:51','0:0:0:0:0:0:0:1',NULL,NULL),(29,3,'2019-10-06 16:06:44','0:0:0:0:0:0:0:1',NULL,NULL),(30,3,'2019-10-06 16:14:51','0:0:0:0:0:0:0:1',NULL,NULL),(31,1,'2019-10-06 16:15:09','0:0:0:0:0:0:0:1',NULL,NULL),(32,1,'2019-10-06 16:16:14','0:0:0:0:0:0:0:1',NULL,NULL),(33,3,'2019-10-06 16:16:35','0:0:0:0:0:0:0:1',NULL,NULL),(34,3,'2019-10-06 16:16:42','0:0:0:0:0:0:0:1',NULL,NULL),(35,3,'2019-10-07 15:20:48','0:0:0:0:0:0:0:1',NULL,NULL),(36,3,'2019-10-07 15:40:07','0:0:0:0:0:0:0:1',NULL,NULL),(37,3,'2019-10-07 16:34:15','0:0:0:0:0:0:0:1',NULL,NULL),(38,3,'2019-10-09 21:19:08','0:0:0:0:0:0:0:1',NULL,NULL),(39,4,'2019-10-09 21:30:35','0:0:0:0:0:0:0:1',NULL,NULL),(40,4,'2019-10-09 21:31:30','0:0:0:0:0:0:0:1',NULL,NULL),(41,4,'2019-10-09 21:32:39','0:0:0:0:0:0:0:1',NULL,NULL),(42,4,'2019-10-09 21:33:27','0:0:0:0:0:0:0:1',NULL,NULL),(43,4,'2019-10-09 21:33:50','0:0:0:0:0:0:0:1',NULL,NULL),(44,3,'2019-10-20 16:02:53','0:0:0:0:0:0:0:1',NULL,NULL),(45,3,'2019-10-23 21:20:55','0:0:0:0:0:0:0:1',NULL,NULL),(46,3,'2019-10-27 21:41:45','0:0:0:0:0:0:0:1',NULL,NULL),(47,3,'2019-11-09 16:44:57','0:0:0:0:0:0:0:1',NULL,NULL),(48,3,'2019-11-09 16:46:56','0:0:0:0:0:0:0:1',NULL,NULL),(49,3,'2019-11-09 16:49:55','0:0:0:0:0:0:0:1',NULL,NULL),(50,3,'2019-11-23 14:17:16','0:0:0:0:0:0:0:1',NULL,NULL),(51,6,'2019-11-23 14:52:30','0:0:0:0:0:0:0:1',NULL,NULL),(52,3,'2019-11-23 15:07:24','0:0:0:0:0:0:0:1',NULL,NULL),(53,3,'2019-11-30 21:25:30','192.168.3.185',NULL,NULL),(54,3,'2019-11-30 21:27:54','192.168.3.185',NULL,NULL),(55,3,'2019-12-28 15:23:01','0:0:0:0:0:0:0:1',NULL,NULL),(56,3,'2020-01-01 15:21:46','0:0:0:0:0:0:0:1',NULL,NULL),(57,3,'2020-01-04 16:00:54','192.168.3.185',NULL,NULL),(58,3,'2020-02-01 15:05:19','0:0:0:0:0:0:0:1',NULL,NULL),(59,3,'2020-02-01 15:36:05','0:0:0:0:0:0:0:1',NULL,NULL),(60,3,'2020-02-01 15:36:36','0:0:0:0:0:0:0:1',NULL,NULL),(61,3,'2020-02-01 15:37:30','0:0:0:0:0:0:0:1',NULL,NULL),(62,3,'2020-02-01 15:37:46','0:0:0:0:0:0:0:1',NULL,NULL),(63,3,'2020-02-01 15:38:20','0:0:0:0:0:0:0:1',NULL,NULL),(64,3,'2020-02-01 15:38:33','0:0:0:0:0:0:0:1',NULL,NULL),(65,3,'2020-02-01 15:39:06','0:0:0:0:0:0:0:1',NULL,NULL),(66,3,'2020-02-01 15:41:31','0:0:0:0:0:0:0:1',NULL,NULL),(67,3,'2020-02-01 15:43:17','0:0:0:0:0:0:0:1',NULL,NULL),(68,3,'2020-02-01 15:44:34','0:0:0:0:0:0:0:1',NULL,NULL),(69,3,'2020-02-01 15:45:10','0:0:0:0:0:0:0:1',NULL,NULL),(70,3,'2020-02-01 15:46:04','0:0:0:0:0:0:0:1',NULL,NULL),(71,3,'2020-02-01 15:48:33','0:0:0:0:0:0:0:1',NULL,NULL),(72,3,'2020-02-01 16:00:07','0:0:0:0:0:0:0:1',NULL,NULL),(73,3,'2020-02-01 16:07:25','0:0:0:0:0:0:0:1',NULL,NULL),(74,3,'2020-02-01 16:08:22','0:0:0:0:0:0:0:1',NULL,NULL),(75,3,'2020-02-02 15:28:13','0:0:0:0:0:0:0:1',NULL,NULL),(76,3,'2020-02-02 15:44:37','0:0:0:0:0:0:0:1',NULL,NULL),(77,3,'2020-02-02 15:45:25','0:0:0:0:0:0:0:1',NULL,NULL),(78,3,'2020-02-02 15:52:32','0:0:0:0:0:0:0:1',NULL,NULL),(79,3,'2020-02-02 15:53:44','0:0:0:0:0:0:0:1',NULL,NULL),(80,3,'2020-02-02 15:54:36','0:0:0:0:0:0:0:1',NULL,NULL),(81,3,'2020-02-02 16:01:00','0:0:0:0:0:0:0:1',NULL,NULL),(82,3,'2020-02-02 16:05:19','0:0:0:0:0:0:0:1',NULL,NULL),(83,3,'2020-02-02 16:06:31','0:0:0:0:0:0:0:1',NULL,NULL),(84,3,'2020-02-02 16:17:26','0:0:0:0:0:0:0:1',NULL,NULL),(85,3,'2020-02-02 16:18:45','0:0:0:0:0:0:0:1',NULL,NULL),(86,3,'2020-02-02 16:19:05','0:0:0:0:0:0:0:1',NULL,NULL),(87,3,'2020-02-02 16:19:23','0:0:0:0:0:0:0:1',NULL,NULL),(88,3,'2020-02-02 16:22:27','0:0:0:0:0:0:0:1',NULL,NULL),(89,3,'2020-02-02 16:23:30','0:0:0:0:0:0:0:1',NULL,NULL),(90,3,'2020-02-02 16:23:48','0:0:0:0:0:0:0:1',NULL,NULL),(91,3,'2020-02-02 16:24:38','0:0:0:0:0:0:0:1',NULL,NULL),(92,3,'2020-02-02 16:25:22','0:0:0:0:0:0:0:1',NULL,NULL),(93,3,'2020-02-02 16:26:19','0:0:0:0:0:0:0:1',NULL,NULL),(94,3,'2020-02-02 16:26:31','0:0:0:0:0:0:0:1',NULL,NULL),(95,3,'2020-02-02 16:27:08','0:0:0:0:0:0:0:1',NULL,NULL),(96,3,'2020-02-02 16:31:02','0:0:0:0:0:0:0:1',NULL,NULL),(97,3,'2020-02-02 16:31:08','0:0:0:0:0:0:0:1',NULL,NULL),(98,3,'2020-02-02 16:31:25','0:0:0:0:0:0:0:1',NULL,NULL),(99,3,'2020-02-02 16:31:50','0:0:0:0:0:0:0:1',NULL,NULL),(100,3,'2020-02-02 16:33:22','0:0:0:0:0:0:0:1',NULL,NULL),(101,3,'2020-02-02 16:33:41','0:0:0:0:0:0:0:1',NULL,NULL),(102,3,'2020-02-02 16:34:58','0:0:0:0:0:0:0:1',NULL,NULL),(103,3,'2020-02-02 16:38:42','0:0:0:0:0:0:0:1',NULL,NULL),(104,3,'2020-02-02 16:39:41','0:0:0:0:0:0:0:1',NULL,NULL),(105,3,'2020-02-02 16:42:22','0:0:0:0:0:0:0:1',NULL,NULL),(106,3,'2020-02-02 16:46:21','0:0:0:0:0:0:0:1',NULL,NULL),(107,3,'2020-02-02 16:50:23','0:0:0:0:0:0:0:1',NULL,NULL),(108,3,'2020-02-02 16:51:11','0:0:0:0:0:0:0:1',NULL,NULL),(109,3,'2020-02-02 16:51:22','0:0:0:0:0:0:0:1',NULL,NULL),(110,3,'2020-02-02 16:52:00','0:0:0:0:0:0:0:1',NULL,NULL),(111,3,'2020-02-02 17:01:05','0:0:0:0:0:0:0:1',NULL,NULL),(112,3,'2020-02-03 10:43:22','0:0:0:0:0:0:0:1',NULL,NULL),(113,3,'2020-02-03 10:45:29','0:0:0:0:0:0:0:1',NULL,NULL),(114,3,'2020-02-03 10:46:33','0:0:0:0:0:0:0:1',NULL,NULL),(115,3,'2020-02-03 10:54:33','0:0:0:0:0:0:0:1',NULL,NULL),(116,3,'2020-02-03 14:24:47','0:0:0:0:0:0:0:1',NULL,NULL),(117,3,'2020-02-03 14:25:38','0:0:0:0:0:0:0:1',NULL,NULL),(118,5,'2020-02-03 15:22:28','0:0:0:0:0:0:0:1',NULL,NULL),(119,5,'2020-02-03 15:23:00','0:0:0:0:0:0:0:1',NULL,NULL),(120,5,'2020-02-03 15:24:29','0:0:0:0:0:0:0:1',NULL,NULL),(121,3,'2020-02-03 15:24:50','0:0:0:0:0:0:0:1',NULL,NULL),(122,5,'2020-02-03 15:27:18','0:0:0:0:0:0:0:1',NULL,NULL),(123,3,'2020-02-03 15:27:33','0:0:0:0:0:0:0:1',NULL,NULL),(124,3,'2020-02-03 15:29:06','0:0:0:0:0:0:0:1',NULL,NULL),(125,5,'2020-02-03 15:33:25','0:0:0:0:0:0:0:1',NULL,NULL),(126,3,'2020-02-03 15:33:51','0:0:0:0:0:0:0:1',NULL,NULL),(127,1,'2020-02-03 15:34:35','0:0:0:0:0:0:0:1',NULL,NULL),(128,3,'2020-02-03 15:34:47','0:0:0:0:0:0:0:1',NULL,NULL),(129,3,'2020-02-04 14:14:46','0:0:0:0:0:0:0:1',NULL,NULL),(130,3,'2020-02-05 10:33:35','0:0:0:0:0:0:0:1',NULL,NULL),(131,3,'2020-02-05 10:36:21','0:0:0:0:0:0:0:1',NULL,NULL),(132,3,'2020-02-05 16:34:37','0:0:0:0:0:0:0:1',NULL,NULL),(133,4,'2020-02-05 16:58:37','0:0:0:0:0:0:0:1',NULL,NULL),(134,3,'2020-02-05 16:59:03','0:0:0:0:0:0:0:1',NULL,NULL),(135,3,'2020-02-06 10:25:02','0:0:0:0:0:0:0:1',NULL,NULL),(136,3,'2020-02-07 14:34:34','0:0:0:0:0:0:0:1',NULL,NULL),(137,3,'2020-02-07 14:36:20','0:0:0:0:0:0:0:1',NULL,NULL),(138,1,'2020-02-07 14:43:34','0:0:0:0:0:0:0:1',NULL,NULL),(139,3,'2020-02-07 15:18:06','0:0:0:0:0:0:0:1',NULL,NULL),(140,3,'2020-02-07 15:20:07','0:0:0:0:0:0:0:1',NULL,NULL),(141,3,'2020-02-07 15:22:20','0:0:0:0:0:0:0:1',NULL,NULL),(142,3,'2020-02-07 15:22:28','0:0:0:0:0:0:0:1',NULL,NULL),(143,3,'2020-02-07 15:55:11','0:0:0:0:0:0:0:1',NULL,NULL),(144,3,'2020-02-07 15:56:04','0:0:0:0:0:0:0:1',NULL,NULL),(145,3,'2020-02-07 15:58:49','0:0:0:0:0:0:0:1',NULL,NULL),(146,6,'2020-02-07 16:16:21','0:0:0:0:0:0:0:1',NULL,NULL),(147,7,'2020-02-07 16:16:37','0:0:0:0:0:0:0:1',NULL,NULL),(148,3,'2020-02-07 16:18:39','0:0:0:0:0:0:0:1',NULL,NULL),(149,7,'2020-02-07 16:20:06','0:0:0:0:0:0:0:1',NULL,NULL),(150,3,'2020-02-07 16:20:44','0:0:0:0:0:0:0:1',NULL,NULL),(151,3,'2020-02-07 16:32:31','0:0:0:0:0:0:0:1',NULL,NULL),(152,3,'2020-02-07 19:32:34','0:0:0:0:0:0:0:1',NULL,NULL),(153,3,'2020-02-07 19:32:48','0:0:0:0:0:0:0:1',NULL,NULL),(154,3,'2020-02-07 19:33:01','0:0:0:0:0:0:0:1',NULL,NULL),(155,3,'2020-02-07 19:33:06','0:0:0:0:0:0:0:1',NULL,NULL),(156,3,'2020-02-07 19:33:21','0:0:0:0:0:0:0:1',NULL,NULL),(157,3,'2020-02-07 19:35:33','0:0:0:0:0:0:0:1',NULL,NULL),(158,3,'2020-02-07 19:37:10','0:0:0:0:0:0:0:1',NULL,NULL),(159,3,'2020-02-07 19:37:14','0:0:0:0:0:0:0:1',NULL,NULL),(160,3,'2020-02-07 19:37:25','0:0:0:0:0:0:0:1',NULL,NULL),(161,3,'2020-02-07 19:45:41','0:0:0:0:0:0:0:1',NULL,NULL),(162,3,'2020-02-07 19:47:45','0:0:0:0:0:0:0:1',NULL,NULL),(163,3,'2020-02-07 20:02:25','0:0:0:0:0:0:0:1',NULL,NULL),(164,6,'2020-02-07 20:10:55','0:0:0:0:0:0:0:1',NULL,NULL),(165,6,'2020-02-07 20:11:02','0:0:0:0:0:0:0:1',NULL,NULL),(166,6,'2020-02-07 20:13:44','0:0:0:0:0:0:0:1',NULL,NULL),(167,6,'2020-02-07 20:17:14','0:0:0:0:0:0:0:1',NULL,NULL),(168,3,'2020-02-07 20:17:44','0:0:0:0:0:0:0:1',NULL,NULL),(169,6,'2020-02-07 20:18:13','0:0:0:0:0:0:0:1',NULL,NULL),(170,3,'2020-02-10 10:28:14','0:0:0:0:0:0:0:1',NULL,NULL),(171,3,'2020-02-10 10:45:15','0:0:0:0:0:0:0:1',NULL,NULL),(172,3,'2020-02-10 10:57:46','0:0:0:0:0:0:0:1',NULL,NULL),(173,3,'2020-02-10 10:59:06','0:0:0:0:0:0:0:1',NULL,NULL),(174,3,'2020-02-10 11:04:19','0:0:0:0:0:0:0:1',NULL,NULL),(175,3,'2020-02-10 11:05:55','0:0:0:0:0:0:0:1',NULL,NULL),(176,3,'2020-02-10 11:06:45','0:0:0:0:0:0:0:1',NULL,NULL),(177,3,'2020-02-10 11:07:41','0:0:0:0:0:0:0:1',NULL,NULL),(178,3,'2020-02-10 11:08:13','0:0:0:0:0:0:0:1',NULL,NULL),(179,3,'2020-02-10 11:10:02','0:0:0:0:0:0:0:1',NULL,NULL),(180,6,'2020-02-10 14:25:17','0:0:0:0:0:0:0:1',NULL,NULL),(181,6,'2020-02-10 14:29:14','0:0:0:0:0:0:0:1',NULL,NULL),(182,3,'2020-02-10 16:09:16','0:0:0:0:0:0:0:1',NULL,NULL),(183,3,'2020-02-20 14:39:19','0:0:0:0:0:0:0:1',NULL,NULL),(184,8,'2020-02-20 17:14:58','0:0:0:0:0:0:0:1',NULL,NULL),(185,8,'2020-02-20 17:17:04','0:0:0:0:0:0:0:1',NULL,NULL),(186,8,'2020-02-20 17:17:42','0:0:0:0:0:0:0:1',NULL,NULL),(187,8,'2020-02-21 10:26:56','0:0:0:0:0:0:0:1',NULL,NULL),(188,8,'2020-02-21 10:28:54','0:0:0:0:0:0:0:1',NULL,NULL),(189,8,'2020-02-21 10:32:25','0:0:0:0:0:0:0:1',NULL,NULL),(190,8,'2020-02-21 10:33:41','0:0:0:0:0:0:0:1',NULL,NULL),(191,8,'2020-02-21 10:35:58','0:0:0:0:0:0:0:1',NULL,NULL),(192,8,'2020-02-21 10:36:49','0:0:0:0:0:0:0:1',NULL,NULL),(193,3,'2020-02-21 11:10:11','0:0:0:0:0:0:0:1',NULL,NULL),(194,3,'2020-02-25 16:11:13','0:0:0:0:0:0:0:1',NULL,NULL),(195,3,'2020-02-25 16:46:29','0:0:0:0:0:0:0:1',NULL,NULL),(196,3,'2020-03-07 16:33:59','192.168.3.185',NULL,NULL),(197,6,'2020-03-07 16:35:38','192.168.3.185',NULL,NULL),(198,3,'2020-03-07 17:00:09','192.168.3.185',NULL,NULL),(199,3,'2020-03-14 14:32:08','0:0:0:0:0:0:0:1',NULL,NULL),(200,8,'2020-03-14 14:32:59','0:0:0:0:0:0:0:1',NULL,NULL),(201,3,'2020-03-14 14:33:26','0:0:0:0:0:0:0:1',NULL,NULL),(202,8,'2020-03-14 14:34:57','0:0:0:0:0:0:0:1',NULL,NULL),(203,3,'2020-03-14 14:35:23','0:0:0:0:0:0:0:1',NULL,NULL),(204,8,'2020-03-14 14:36:31','0:0:0:0:0:0:0:1',NULL,NULL),(205,3,'2020-03-14 14:36:51','0:0:0:0:0:0:0:1',NULL,NULL),(206,8,'2020-03-14 14:37:31','0:0:0:0:0:0:0:1',NULL,NULL),(207,3,'2020-03-14 14:37:44','0:0:0:0:0:0:0:1',NULL,NULL),(208,8,'2020-03-14 14:38:30','0:0:0:0:0:0:0:1',NULL,NULL),(209,3,'2020-03-14 14:38:50','0:0:0:0:0:0:0:1',NULL,NULL),(210,8,'2020-03-14 14:39:26','0:0:0:0:0:0:0:1',NULL,NULL),(211,3,'2020-03-14 14:39:41','0:0:0:0:0:0:0:1',NULL,NULL),(212,3,'2020-03-15 14:23:54','0:0:0:0:0:0:0:1',NULL,NULL),(213,3,'2020-03-22 14:33:17','0:0:0:0:0:0:0:1',NULL,NULL),(214,3,'2020-03-22 14:59:51','0:0:0:0:0:0:0:1',NULL,NULL),(215,3,'2020-03-22 15:04:32','0:0:0:0:0:0:0:1',NULL,NULL),(216,3,'2020-03-29 16:14:37','0:0:0:0:0:0:0:1',NULL,NULL),(217,3,'2020-03-29 17:17:00','0:0:0:0:0:0:0:1',NULL,NULL),(218,3,'2020-04-06 16:54:49','0:0:0:0:0:0:0:1',NULL,NULL),(219,3,'2020-04-12 15:01:40','0:0:0:0:0:0:0:1',NULL,NULL),(220,3,'2020-04-19 09:50:59','0:0:0:0:0:0:0:1',NULL,NULL),(221,3,'2020-05-04 10:45:45','0:0:0:0:0:0:0:1',NULL,NULL),(222,3,'2020-05-05 11:02:44','0:0:0:0:0:0:0:1',NULL,NULL),(223,3,'2020-05-16 15:17:21','0:0:0:0:0:0:0:1',NULL,NULL),(224,3,'2020-05-17 14:58:35','0:0:0:0:0:0:0:1',NULL,NULL),(225,3,'2020-05-18 15:47:46','0:0:0:0:0:0:0:1',NULL,NULL),(226,3,'2020-05-23 16:51:15','0:0:0:0:0:0:0:1',NULL,NULL),(227,3,'2020-05-23 16:54:53','0:0:0:0:0:0:0:1',NULL,NULL),(228,3,'2020-05-23 17:00:33','0:0:0:0:0:0:0:1',NULL,NULL),(229,3,'2020-05-24 10:38:00','0:0:0:0:0:0:0:1',NULL,NULL),(230,3,'2020-06-07 09:39:31','0:0:0:0:0:0:0:1',NULL,NULL),(231,3,'2020-06-14 14:23:30','0:0:0:0:0:0:0:1',NULL,NULL),(232,3,'2020-06-14 17:11:59','0:0:0:0:0:0:0:1',NULL,NULL),(233,3,'2020-06-20 14:53:51','0:0:0:0:0:0:0:1',NULL,NULL),(234,3,'2020-06-21 14:57:36','0:0:0:0:0:0:0:1',NULL,NULL),(235,3,'2020-06-27 10:41:31','0:0:0:0:0:0:0:1',NULL,NULL),(236,3,'2020-07-05 15:54:21','0:0:0:0:0:0:0:1',NULL,NULL),(237,3,'2020-07-11 10:40:28','0:0:0:0:0:0:0:1',NULL,NULL),(238,3,'2020-07-11 10:45:01','0:0:0:0:0:0:0:1',NULL,NULL),(239,3,'2020-07-19 11:00:16','192.168.3.185',NULL,NULL),(240,3,'2020-07-19 11:46:27','192.168.3.185',NULL,NULL),(241,3,'2020-07-19 11:53:47','192.168.3.185',NULL,NULL),(242,3,'2020-07-19 14:17:37','192.168.3.185',NULL,NULL),(243,3,'2020-07-19 14:44:24','192.168.3.185',NULL,NULL),(244,3,'2020-07-19 14:44:58','192.168.3.185',NULL,NULL),(245,3,'2020-07-19 14:48:27','192.168.3.185',NULL,NULL),(246,3,'2020-07-19 14:48:57','192.168.3.185',NULL,NULL),(247,3,'2020-07-19 14:49:30','192.168.3.185',NULL,NULL),(248,3,'2020-07-19 14:50:18','192.168.3.185',NULL,NULL),(249,3,'2020-07-19 14:51:51','192.168.3.185',NULL,NULL),(250,3,'2020-07-19 14:55:27','192.168.3.185',NULL,NULL),(251,3,'2020-07-19 14:58:14','192.168.3.185',NULL,NULL),(252,6,'2020-07-19 15:04:21','192.168.3.185',NULL,NULL),(253,3,'2020-07-19 15:38:49','192.168.3.185',NULL,NULL),(254,6,'2020-07-19 16:26:24','0:0:0:0:0:0:0:1',NULL,NULL),(255,3,'2020-08-08 10:39:12','0:0:0:0:0:0:0:1',NULL,NULL),(256,3,'2020-08-09 11:06:31','0:0:0:0:0:0:0:1',NULL,NULL),(257,3,'2020-08-10 20:41:30','0:0:0:0:0:0:0:1',NULL,NULL),(258,3,'2020-08-15 10:12:25','0:0:0:0:0:0:0:1',NULL,NULL),(259,1,'2020-08-19 21:23:59','0:0:0:0:0:0:0:1',NULL,NULL),(260,1,'2020-08-19 21:25:06','0:0:0:0:0:0:0:1',NULL,NULL),(261,3,'2020-09-05 16:14:50','192.168.3.185',NULL,NULL),(262,3,'2020-09-05 16:15:37','192.168.3.185',NULL,NULL),(263,3,'2020-09-12 16:07:35','0:0:0:0:0:0:0:1',NULL,NULL),(264,6,'2020-09-12 16:08:14','0:0:0:0:0:0:0:1',NULL,NULL),(265,1,'2020-09-12 16:08:26','0:0:0:0:0:0:0:1',NULL,NULL),(266,6,'2020-09-12 16:09:08','0:0:0:0:0:0:0:1',NULL,NULL),(267,1,'2020-09-12 16:09:17','0:0:0:0:0:0:0:1',NULL,NULL),(268,3,'2020-09-13 14:22:25','0:0:0:0:0:0:0:1',NULL,NULL),(269,3,'2020-09-13 14:23:07','0:0:0:0:0:0:0:1',NULL,NULL),(270,3,'2020-09-13 14:24:21','0:0:0:0:0:0:0:1',NULL,NULL),(271,3,'2020-09-13 14:32:32','0:0:0:0:0:0:0:1',NULL,NULL),(272,1,'2020-09-19 15:43:31','192.168.3.185',NULL,NULL),(273,3,'2020-09-19 15:43:58','192.168.3.185',NULL,NULL),(274,3,'2020-09-19 15:49:44','192.168.3.185',NULL,NULL),(275,6,'2020-09-19 15:50:12','192.168.3.185',NULL,NULL),(276,6,'2020-09-19 15:50:18','192.168.3.185',NULL,NULL),(277,1,'2020-09-19 15:50:41','192.168.3.185',NULL,NULL),(278,6,'2020-09-19 15:52:28','192.168.3.185',NULL,NULL),(279,1,'2020-09-19 15:53:01','192.168.3.185',NULL,NULL),(280,1,'2020-09-19 15:53:48','192.168.3.185',NULL,NULL),(281,6,'2020-09-19 15:54:37','192.168.3.185',NULL,NULL),(282,3,'2020-09-20 11:13:50','192.168.3.185',NULL,NULL),(283,1,'2020-10-08 14:26:58','0:0:0:0:0:0:0:1',NULL,NULL),(284,1,'2020-10-08 14:30:49','192.168.3.185',NULL,NULL),(285,3,'2021-04-11 10:27:43','192.168.3.227',NULL,NULL),(286,1,'2021-04-11 10:29:19','192.168.3.227',NULL,NULL),(287,3,'2021-04-11 10:30:40','192.168.3.227',NULL,NULL),(288,3,'2021-04-11 10:37:50','192.168.3.227',NULL,NULL),(289,3,'2021-12-08 10:44:09','192.168.3.4',NULL,NULL),(290,3,'2022-04-15 15:04:51','192.168.56.1',NULL,NULL),(291,1,'2022-05-10 15:25:58','192.168.56.1',NULL,NULL),(292,1,'2022-05-10 15:31:08','192.168.56.1',NULL,NULL),(293,1,'2022-05-10 15:32:18','192.168.56.1',NULL,NULL),(294,1,'2022-05-10 15:34:17','192.168.56.1',NULL,NULL),(295,1,'2022-05-10 15:34:38','192.168.56.1',NULL,NULL),(296,1,'2022-05-10 15:54:50','192.168.56.1',NULL,NULL),(297,1,'2022-05-10 16:31:20','192.168.56.1',NULL,NULL),(298,1,'2022-05-10 16:33:45','192.168.56.1',NULL,NULL),(299,1,'2022-05-10 16:39:07','192.168.56.1',NULL,NULL),(300,1,'2022-05-18 14:53:41','192.168.56.1',NULL,NULL),(301,3,'2022-06-07 17:28:29','192.168.56.1',NULL,NULL),(302,3,'2022-06-08 09:48:32','192.168.56.1',NULL,NULL),(303,3,'2022-06-08 10:34:20','192.168.56.1',NULL,NULL),(304,3,'2022-06-08 17:09:29','192.168.56.1',NULL,NULL),(305,3,'2022-06-08 17:10:11','192.168.56.1',NULL,NULL),(306,6,'2022-06-08 17:10:55','192.168.56.1',NULL,NULL),(307,3,'2022-06-08 17:23:05','192.168.56.1',NULL,NULL),(308,3,'2022-06-08 17:30:51','192.168.56.1',NULL,NULL),(309,3,'2022-06-08 17:31:42','192.168.56.1',NULL,NULL),(310,3,'2022-06-08 17:31:48','192.168.56.1',NULL,NULL),(311,3,'2022-06-08 21:03:56','192.168.56.1',NULL,NULL),(312,3,'2022-06-09 10:41:31','192.168.56.1',NULL,NULL),(313,3,'2022-06-09 15:18:46','192.168.56.1',NULL,NULL),(314,3,'2022-06-10 15:43:15','192.168.56.1',NULL,NULL),(315,3,'2022-06-10 15:52:27','192.168.56.1',NULL,NULL),(316,3,'2022-06-15 14:41:59','192.168.56.1',NULL,NULL),(317,3,'2022-06-23 16:24:09','192.168.3.6',NULL,NULL),(318,3,'2022-06-23 16:41:33','192.168.3.6',NULL,NULL),(319,3,'2022-06-23 16:42:50','192.168.3.6',NULL,NULL),(320,3,'2022-06-23 16:42:51','192.168.3.6',NULL,NULL),(321,3,'2022-06-23 16:42:52','192.168.3.6',NULL,NULL),(322,3,'2022-06-23 16:42:53','192.168.3.6',NULL,NULL),(323,3,'2022-06-23 16:43:07','192.168.3.6',NULL,NULL),(324,3,'2022-06-23 16:44:56','192.168.3.6',NULL,NULL),(325,3,'2022-06-23 16:45:42','192.168.3.6',NULL,NULL),(326,3,'2022-06-23 16:50:08','192.168.3.6',NULL,NULL),(327,3,'2022-06-24 15:19:04','192.168.3.227',NULL,NULL),(328,3,'2022-06-24 15:25:52','192.168.3.227',NULL,NULL),(329,3,'2022-06-24 15:40:29','192.168.3.227',NULL,NULL),(330,3,'2022-06-25 15:13:52','192.168.56.1',NULL,NULL),(331,3,'2022-06-25 15:25:12','192.168.56.1',NULL,NULL),(332,3,'2022-06-27 16:38:37','192.168.56.1',NULL,NULL),(333,3,'2022-07-24 10:10:06','192.168.3.227',NULL,NULL),(334,1,'2022-07-25 17:15:55','192.168.56.1',NULL,NULL),(335,3,'2022-07-27 09:41:32','192.168.3.227',NULL,NULL),(336,1,'2022-07-28 16:56:10','192.168.56.1',NULL,NULL),(337,1,'2022-07-28 16:56:25','192.168.56.1',NULL,NULL),(338,8,'2022-08-07 14:46:00','192.168.56.1',NULL,NULL),(339,9,'2022-08-07 14:53:32','192.168.56.1',NULL,NULL),(340,9,'2022-08-07 14:53:46','192.168.56.1',NULL,NULL),(341,9,'2022-08-07 14:56:19','192.168.56.1',NULL,NULL),(342,1,'2022-08-09 10:53:55','192.168.56.1',NULL,NULL),(343,1,'2022-08-09 10:54:36','192.168.56.1',NULL,NULL),(344,1,'2022-08-09 11:00:06','192.168.56.1',NULL,NULL),(345,1,'2022-08-09 11:02:04','192.168.56.1',NULL,NULL),(346,1,'2022-08-09 15:48:14','192.168.56.1',NULL,NULL),(347,1,'2022-08-16 16:35:24','192.168.56.1',NULL,NULL),(348,1,'2022-08-17 15:03:52','192.168.56.1',NULL,NULL),(349,1,'2022-08-20 16:49:19','192.168.56.1',NULL,NULL),(350,1,'2022-08-23 14:54:06','192.168.56.1',NULL,NULL),(351,1,'2022-08-31 17:16:35','192.168.56.1',NULL,NULL),(352,3,'2022-10-14 15:31:42','192.168.56.1',NULL,NULL),(353,3,'2022-10-14 15:42:39','192.168.56.1',NULL,NULL),(354,3,'2022-10-14 15:44:08','192.168.56.1',NULL,NULL),(355,3,'2022-10-14 15:46:23','192.168.56.1',NULL,NULL),(356,3,'2022-10-14 15:46:35','192.168.56.1',NULL,NULL),(357,3,'2022-10-14 16:06:11','192.168.56.1',NULL,NULL),(358,3,'2022-10-14 16:24:08','192.168.56.1',NULL,NULL),(359,3,'2022-10-17 16:44:52','192.168.56.1',NULL,NULL),(360,3,'2022-10-17 16:49:42','192.168.56.1',NULL,NULL),(361,3,'2022-10-17 16:49:53','192.168.56.1',NULL,NULL),(362,3,'2022-10-17 17:03:10','192.168.56.1',NULL,NULL),(363,3,'2022-10-17 17:03:22','192.168.56.1',NULL,NULL),(364,3,'2022-10-17 21:00:48','192.168.56.1',NULL,NULL),(365,3,'2022-10-24 16:32:31','192.168.56.1',NULL,NULL),(366,3,'2022-10-26 16:31:17','192.168.56.1',NULL,NULL),(367,3,'2022-10-26 16:50:48','192.168.56.1',NULL,NULL),(368,3,'2022-10-26 16:52:28','192.168.56.1',NULL,NULL),(369,3,'2022-10-27 17:21:28','192.168.56.1',NULL,NULL),(370,3,'2022-10-28 10:52:22','192.168.56.1',NULL,NULL),(371,3,'2022-11-04 16:09:08','192.168.56.1',NULL,NULL),(372,3,'2022-11-08 10:27:27','192.168.56.1',NULL,NULL),(373,3,'2022-11-09 10:42:30','192.168.56.1',NULL,NULL),(374,3,'2022-11-10 15:03:08','192.168.56.1',NULL,NULL),(375,3,'2022-11-11 10:11:11','192.168.56.1',NULL,NULL),(376,3,'2022-11-14 16:33:30','192.168.56.1',NULL,NULL),(377,3,'2022-11-15 10:54:02','192.168.56.1',NULL,NULL),(378,3,'2022-11-15 15:16:35','192.168.56.1',NULL,NULL),(379,3,'2022-11-15 15:17:41','192.168.56.1',NULL,NULL),(380,3,'2022-11-15 15:17:47','192.168.56.1',NULL,NULL),(381,1,'2022-11-15 15:19:56','192.168.56.1',NULL,NULL),(382,3,'2022-11-15 15:20:12','192.168.56.1',NULL,NULL),(383,3,'2022-11-15 15:22:22','192.168.56.1',NULL,NULL),(384,3,'2022-11-16 10:39:08','192.168.56.1',NULL,NULL),(385,1,'2022-11-24 20:02:12','192.168.56.1',NULL,NULL),(386,3,'2022-11-28 15:24:02','192.168.56.1',NULL,NULL),(387,3,'2022-11-30 09:34:28','192.168.56.1',NULL,NULL),(388,3,'2022-12-05 09:43:58','192.168.56.1',NULL,NULL),(389,3,'2022-12-09 17:18:09','192.168.56.1',NULL,NULL),(390,3,'2022-12-15 14:53:39','192.168.56.1',NULL,NULL),(391,3,'2022-12-15 16:11:54','192.168.56.1',NULL,NULL),(392,3,'2022-12-20 15:55:18','192.168.56.1',NULL,NULL),(393,3,'2022-12-21 14:49:30','192.168.56.1',NULL,NULL),(394,3,'2022-12-23 09:49:50','192.168.56.1',NULL,NULL),(395,3,'2023-01-04 10:17:44','192.168.56.1',NULL,NULL),(396,3,'2023-01-04 15:28:47','192.168.56.1',NULL,NULL),(397,3,'2023-01-05 10:13:43','192.168.56.1',NULL,NULL),(398,3,'2023-01-06 09:49:23','192.168.56.1',NULL,NULL),(399,3,'2023-01-06 10:04:19','192.168.56.1',NULL,NULL),(400,3,'2023-01-06 10:07:06','192.168.56.1',NULL,NULL),(401,3,'2023-01-06 16:21:31','192.168.56.1',NULL,NULL),(402,3,'2023-01-09 16:50:30','192.168.56.1',NULL,NULL),(403,3,'2023-01-10 10:05:52','192.168.56.1',NULL,NULL),(404,3,'2023-01-10 17:09:44','192.168.56.1',NULL,NULL),(405,3,'2023-01-11 10:17:17','192.168.56.1',NULL,NULL),(406,3,'2023-01-13 09:35:37','192.168.56.1',NULL,NULL),(407,3,'2023-01-13 09:40:51','192.168.56.1',NULL,NULL),(408,3,'2023-01-31 10:46:52','192.168.56.1',NULL,NULL),(409,3,'2023-02-08 17:11:11','192.168.56.1',NULL,NULL),(410,3,'2023-02-09 15:46:41','192.168.56.1',NULL,NULL),(411,3,'2023-02-10 17:23:52','192.168.56.1',NULL,NULL),(412,3,'2023-05-11 15:29:54','192.168.56.1',NULL,NULL),(413,3,'2025-09-08 17:30:40','192.168.0.24',NULL,NULL),(414,3,'2025-09-15 10:11:48','192.168.0.24',NULL,NULL),(415,3,'2025-09-15 13:54:39','192.168.0.24',NULL,NULL),(416,3,'2025-09-16 16:02:17','192.168.0.24',NULL,NULL),(417,3,'2025-09-17 10:42:54','192.168.0.24',NULL,NULL),(418,3,'2025-09-17 10:44:00','192.168.0.24',NULL,NULL);
/*!40000 ALTER TABLE `ums_admin_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_admin_permission_relation`
--

DROP TABLE IF EXISTS `ums_admin_permission_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_admin_permission_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) DEFAULT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  `type` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户和权限关系表(除角色中定义的权限以外的加减权限)';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_admin_permission_relation`
--

LOCK TABLES `ums_admin_permission_relation` WRITE;
/*!40000 ALTER TABLE `ums_admin_permission_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_admin_permission_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_admin_role_relation`
--

DROP TABLE IF EXISTS `ums_admin_role_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_admin_role_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户和角色关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_admin_role_relation`
--

LOCK TABLES `ums_admin_role_relation` WRITE;
/*!40000 ALTER TABLE `ums_admin_role_relation` DISABLE KEYS */;
INSERT INTO `ums_admin_role_relation` VALUES (26,3,5),(27,6,1),(28,7,2),(29,1,5),(30,4,5),(31,8,1);
/*!40000 ALTER TABLE `ums_admin_role_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_growth_change_history`
--

DROP TABLE IF EXISTS `ums_growth_change_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_growth_change_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `change_type` int(1) DEFAULT NULL COMMENT '改变类型：0->增加；1->减少',
  `change_count` int(11) DEFAULT NULL COMMENT '积分改变数量',
  `operate_man` varchar(100) DEFAULT NULL COMMENT '操作人员',
  `operate_note` varchar(200) DEFAULT NULL COMMENT '操作备注',
  `source_type` int(1) DEFAULT NULL COMMENT '积分来源：0->购物；1->管理员修改',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='成长值变化历史记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_growth_change_history`
--

LOCK TABLES `ums_growth_change_history` WRITE;
/*!40000 ALTER TABLE `ums_growth_change_history` DISABLE KEYS */;
INSERT INTO `ums_growth_change_history` VALUES (1,1,'2018-08-29 17:16:35',0,1000,'test','测试使用',1);
/*!40000 ALTER TABLE `ums_growth_change_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_integration_change_history`
--

DROP TABLE IF EXISTS `ums_integration_change_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_integration_change_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `change_type` int(1) DEFAULT NULL COMMENT '改变类型：0->增加；1->减少',
  `change_count` int(11) DEFAULT NULL COMMENT '积分改变数量',
  `operate_man` varchar(100) DEFAULT NULL COMMENT '操作人员',
  `operate_note` varchar(200) DEFAULT NULL COMMENT '操作备注',
  `source_type` int(1) DEFAULT NULL COMMENT '积分来源：0->购物；1->管理员修改',
  `credit_type` tinyint(4) DEFAULT '2' COMMENT '积分类型: 1-免费积分, 2-永久积分',
  `business_id` varchar(64) DEFAULT NULL COMMENT '业务ID(如AI任务ID)',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_integration_history_credit_type` (`credit_type`),
  KEY `idx_integration_history_business_id` (`business_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='积分变化历史记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_integration_change_history`
--

LOCK TABLES `ums_integration_change_history` WRITE;
/*!40000 ALTER TABLE `ums_integration_change_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_integration_change_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_integration_consume_setting`
--

DROP TABLE IF EXISTS `ums_integration_consume_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_integration_consume_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deduction_per_amount` int(11) DEFAULT NULL COMMENT '每一元需要抵扣的积分数量',
  `max_percent_per_order` int(11) DEFAULT NULL COMMENT '每笔订单最高抵用百分比',
  `use_unit` int(11) DEFAULT NULL COMMENT '每次使用积分最小单位100',
  `coupon_status` int(1) DEFAULT NULL COMMENT '是否可以和优惠券同用；0->不可以；1->可以',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='积分消费设置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_integration_consume_setting`
--

LOCK TABLES `ums_integration_consume_setting` WRITE;
/*!40000 ALTER TABLE `ums_integration_consume_setting` DISABLE KEYS */;
INSERT INTO `ums_integration_consume_setting` VALUES (1,100,50,100,1);
/*!40000 ALTER TABLE `ums_integration_consume_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member`
--

DROP TABLE IF EXISTS `ums_member`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_level_id` bigint(20) DEFAULT NULL,
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(64) DEFAULT NULL COMMENT '手机号码',
  `status` int(1) DEFAULT NULL COMMENT '帐号启用状态:0->禁用；1->启用',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  `icon` varchar(500) DEFAULT NULL COMMENT '头像',
  `gender` int(1) DEFAULT NULL COMMENT '性别：0->未知；1->男；2->女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `city` varchar(64) DEFAULT NULL COMMENT '所做城市',
  `job` varchar(100) DEFAULT NULL COMMENT '职业',
  `personalized_signature` varchar(200) DEFAULT NULL COMMENT '个性签名',
  `source_type` int(1) DEFAULT NULL COMMENT '用户来源',
  `integration` int(11) DEFAULT NULL COMMENT '积分',
  `growth` int(11) DEFAULT NULL COMMENT '成长值',
  `luckey_count` int(11) DEFAULT NULL COMMENT '剩余抽奖次数',
  `history_integration` int(11) DEFAULT NULL COMMENT '历史积分数量',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱地址',
  `wechat_openid` varchar(64) DEFAULT NULL COMMENT '微信OpenID',
  `google_id` varchar(128) DEFAULT NULL COMMENT 'Google用户ID',
  `register_type` tinyint(4) DEFAULT '1' COMMENT '注册方式: 1-邮箱, 2-微信, 3-Google',
  `email_verified` tinyint(4) DEFAULT '0' COMMENT '邮箱验证状态: 0-未验证, 1-已验证',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `account_status` tinyint(4) DEFAULT '1' COMMENT '账户状态: 0-禁用, 1-正常, 2-锁定',
  `free_daily_credits` int(11) DEFAULT '200' COMMENT '每日免费积分',
  `used_today_free` int(11) DEFAULT '0' COMMENT '今日已使用免费积分',
  `last_reset_date` date DEFAULT NULL COMMENT '最后重置日期',
  `current_subscription_id` bigint(20) DEFAULT NULL COMMENT '当前有效套餐订单ID',
  `subscription_level` tinyint(4) DEFAULT '0' COMMENT '套餐等级: 0-免费, 1-基础, 2-标准, 3-专业, 4-企业',
  `daily_credits_limit` int(11) DEFAULT '200' COMMENT '每日积分限额',
  `daily_credits_used` int(11) DEFAULT '0' COMMENT '当日已使用积分',
  `subscription_end_date` date DEFAULT NULL COMMENT '套餐到期日期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_username` (`username`) USING BTREE,
  UNIQUE KEY `idx_phone` (`phone`) USING BTREE,
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `wechat_openid` (`wechat_openid`),
  UNIQUE KEY `google_id` (`google_id`),
  KEY `idx_ums_member_register_type` (`register_type`),
  KEY `idx_ums_member_last_login` (`last_login_time`),
  KEY `idx_ums_member_account_status` (`account_status`),
  KEY `idx_ums_member_last_reset` (`last_reset_date`),
  KEY `idx_ums_member_subscription_level` (`subscription_level`),
  KEY `idx_ums_member_subscription_end` (`subscription_end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member`
--

LOCK TABLES `ums_member` WRITE;
/*!40000 ALTER TABLE `ums_member` DISABLE KEYS */;
INSERT INTO `ums_member` VALUES (1,4,'test','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','test','18061581849',1,'2018-08-02 10:35:44','https://macro-oss.oss-cn-shenzhen.aliyuncs.com/mall/icon/github_icon_03.png',1,'2009-06-01','上海','学生','test',NULL,3900,1000,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(3,4,'windy','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','windy','18061581848',1,'2018-08-03 16:46:38',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(4,4,'zhengsan','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','zhengsan','18061581847',1,'2018-11-12 14:12:04',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(5,4,'lisi','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','lisi','18061581841',1,'2018-11-12 14:12:38',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(6,4,'wangwu','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','wangwu','18061581842',1,'2018-11-12 14:13:09',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(7,4,'lion','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','lion','18061581845',1,'2018-11-12 14:21:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(8,4,'shari','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','shari','18061581844',1,'2018-11-12 14:22:00',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(9,4,'aewen','$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG','aewen','18061581843',1,'2018-11-12 14:22:55',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(10,4,'guest','$2a$10$WQiD4RzEs1iJVWU.2HVu8OdSlExJHWKmwndaw3SUfMyqfKZmXe1vq',NULL,'18911111111',1,'2020-03-14 14:52:18',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,0,NULL,NULL,1,200,0,NULL,NULL,0,200,0,NULL),(33,4,'553588070','$2a$10$MpCUQJUu9XEeBdk0vkyCOOTSdtad7yoTYuGP46RmsdpqcJBLg/Pf.','553588070',NULL,1,'2025-09-12 14:06:08',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'553588070@qq.com',NULL,NULL,1,1,'2025-09-18 10:21:43',NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `ums_member` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_level`
--

DROP TABLE IF EXISTS `ums_member_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `growth_point` int(11) DEFAULT NULL,
  `default_status` int(1) DEFAULT NULL COMMENT '是否为默认等级：0->不是；1->是',
  `free_freight_point` decimal(10,2) DEFAULT NULL COMMENT '免运费标准',
  `comment_growth_point` int(11) DEFAULT NULL COMMENT '每次评价获取的成长值',
  `priviledge_free_freight` int(1) DEFAULT NULL COMMENT '是否有免邮特权',
  `priviledge_sign_in` int(1) DEFAULT NULL COMMENT '是否有签到特权',
  `priviledge_comment` int(1) DEFAULT NULL COMMENT '是否有评论获奖励特权',
  `priviledge_promotion` int(1) DEFAULT NULL COMMENT '是否有专享活动特权',
  `priviledge_member_price` int(1) DEFAULT NULL COMMENT '是否有会员价格特权',
  `priviledge_birthday` int(1) DEFAULT NULL COMMENT '是否有生日特权',
  `note` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员等级表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_level`
--

LOCK TABLES `ums_member_level` WRITE;
/*!40000 ALTER TABLE `ums_member_level` DISABLE KEYS */;
INSERT INTO `ums_member_level` VALUES (1,'黄金会员',1000,0,199.00,5,1,1,1,1,1,1,NULL),(2,'白金会员',5000,0,99.00,10,1,1,1,1,1,1,NULL),(3,'钻石会员',15000,0,69.00,15,1,1,1,1,1,1,NULL),(4,'普通会员',1,1,199.00,20,1,1,1,1,0,0,NULL);
/*!40000 ALTER TABLE `ums_member_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_login_log`
--

DROP TABLE IF EXISTS `ums_member_login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `ip` varchar(64) DEFAULT NULL,
  `city` varchar(64) DEFAULT NULL,
  `login_type` int(1) DEFAULT NULL COMMENT '登录类型：0->PC；1->android;2->ios;3->小程序',
  `province` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员登录记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_login_log`
--

LOCK TABLES `ums_member_login_log` WRITE;
/*!40000 ALTER TABLE `ums_member_login_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_member_login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_member_tag_relation`
--

DROP TABLE IF EXISTS `ums_member_member_tag_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_member_tag_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `tag_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户和标签关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_member_tag_relation`
--

LOCK TABLES `ums_member_member_tag_relation` WRITE;
/*!40000 ALTER TABLE `ums_member_member_tag_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_member_member_tag_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_product_category_relation`
--

DROP TABLE IF EXISTS `ums_member_product_category_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_product_category_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `product_category_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员与产品分类关系表（用户喜欢的分类）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_product_category_relation`
--

LOCK TABLES `ums_member_product_category_relation` WRITE;
/*!40000 ALTER TABLE `ums_member_product_category_relation` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_member_product_category_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_receive_address`
--

DROP TABLE IF EXISTS `ums_member_receive_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_receive_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL COMMENT '收货人名称',
  `phone_number` varchar(64) DEFAULT NULL,
  `default_status` int(1) DEFAULT NULL COMMENT '是否为默认',
  `post_code` varchar(100) DEFAULT NULL COMMENT '邮政编码',
  `province` varchar(100) DEFAULT NULL COMMENT '省份/直辖市',
  `city` varchar(100) DEFAULT NULL COMMENT '城市',
  `region` varchar(100) DEFAULT NULL COMMENT '区',
  `detail_address` varchar(128) DEFAULT NULL COMMENT '详细地址(街道)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员收货地址表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_receive_address`
--

LOCK TABLES `ums_member_receive_address` WRITE;
/*!40000 ALTER TABLE `ums_member_receive_address` DISABLE KEYS */;
INSERT INTO `ums_member_receive_address` VALUES (1,1,'大梨','18033441849',0,'518000','广东省','深圳市','南山区','科兴科学园'),(3,1,'大梨','18033441849',0,'518000','广东省','深圳市','福田区','清水河街道'),(4,1,'大梨','18033441849',1,'518000','广东省','深圳市','福田区','东晓街道'),(5,11,'小李','18961511111',1,'518000','广东省','深圳市','福田区','东晓街道'),(6,11,'小李','18961511111',NULL,'518000','广东省','深圳市','福田区','清水河街道');
/*!40000 ALTER TABLE `ums_member_receive_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_rule_setting`
--

DROP TABLE IF EXISTS `ums_member_rule_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_rule_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `continue_sign_day` int(11) DEFAULT NULL COMMENT '连续签到天数',
  `continue_sign_point` int(11) DEFAULT NULL COMMENT '连续签到赠送数量',
  `consume_per_point` decimal(10,2) DEFAULT NULL COMMENT '每消费多少元获取1个点',
  `low_order_amount` decimal(10,2) DEFAULT NULL COMMENT '最低获取点数的订单金额',
  `max_point_per_order` int(11) DEFAULT NULL COMMENT '每笔订单最高获取点数',
  `type` int(1) DEFAULT NULL COMMENT '类型：0->积分规则；1->成长值规则',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员积分成长规则表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_rule_setting`
--

LOCK TABLES `ums_member_rule_setting` WRITE;
/*!40000 ALTER TABLE `ums_member_rule_setting` DISABLE KEYS */;
INSERT INTO `ums_member_rule_setting` VALUES (1,1,200,1.00,0.00,-1,0);
/*!40000 ALTER TABLE `ums_member_rule_setting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_statistics_info`
--

DROP TABLE IF EXISTS `ums_member_statistics_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_statistics_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) DEFAULT NULL,
  `consume_amount` decimal(10,2) DEFAULT NULL COMMENT '累计消费金额',
  `order_count` int(11) DEFAULT NULL COMMENT '订单数量',
  `coupon_count` int(11) DEFAULT NULL COMMENT '优惠券数量',
  `comment_count` int(11) DEFAULT NULL COMMENT '评价数',
  `return_order_count` int(11) DEFAULT NULL COMMENT '退货数量',
  `login_count` int(11) DEFAULT NULL COMMENT '登录次数',
  `attend_count` int(11) DEFAULT NULL COMMENT '关注数量',
  `fans_count` int(11) DEFAULT NULL COMMENT '粉丝数量',
  `collect_product_count` int(11) DEFAULT NULL,
  `collect_subject_count` int(11) DEFAULT NULL,
  `collect_topic_count` int(11) DEFAULT NULL,
  `collect_comment_count` int(11) DEFAULT NULL,
  `invite_friend_count` int(11) DEFAULT NULL,
  `recent_order_time` datetime DEFAULT NULL COMMENT '最后一次下订单时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员统计信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_statistics_info`
--

LOCK TABLES `ums_member_statistics_info` WRITE;
/*!40000 ALTER TABLE `ums_member_statistics_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_member_statistics_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_tag`
--

DROP TABLE IF EXISTS `ums_member_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `finish_order_count` int(11) DEFAULT NULL COMMENT '自动打标签完成订单数量',
  `finish_order_amount` decimal(10,2) DEFAULT NULL COMMENT '自动打标签完成订单金额',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='用户标签表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_tag`
--

LOCK TABLES `ums_member_tag` WRITE;
/*!40000 ALTER TABLE `ums_member_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_member_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_member_task`
--

DROP TABLE IF EXISTS `ums_member_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_member_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `growth` int(11) DEFAULT NULL COMMENT '赠送成长值',
  `intergration` int(11) DEFAULT NULL COMMENT '赠送积分',
  `type` int(1) DEFAULT NULL COMMENT '任务类型：0->新手任务；1->日常任务',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='会员任务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_member_task`
--

LOCK TABLES `ums_member_task` WRITE;
/*!40000 ALTER TABLE `ums_member_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_member_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_menu`
--

DROP TABLE IF EXISTS `ums_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父级ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `title` varchar(100) DEFAULT NULL COMMENT '菜单名称',
  `level` int(4) DEFAULT NULL COMMENT '菜单级数',
  `sort` int(4) DEFAULT NULL COMMENT '菜单排序',
  `name` varchar(100) DEFAULT NULL COMMENT '前端名称',
  `icon` varchar(200) DEFAULT NULL COMMENT '前端图标',
  `hidden` int(1) DEFAULT NULL COMMENT '前端隐藏',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_menu`
--

LOCK TABLES `ums_menu` WRITE;
/*!40000 ALTER TABLE `ums_menu` DISABLE KEYS */;
INSERT INTO `ums_menu` VALUES (1,0,'2020-02-02 14:50:36','商品',0,0,'pms','product',0),(2,1,'2020-02-02 14:51:50','商品列表',1,0,'product','product-list',0),(3,1,'2020-02-02 14:52:44','添加商品',1,0,'addProduct','product-add',0),(4,1,'2020-02-02 14:53:51','商品分类',1,0,'productCate','product-cate',0),(5,1,'2020-02-02 14:54:51','商品类型',1,0,'productAttr','product-attr',0),(6,1,'2020-02-02 14:56:29','品牌管理',1,0,'brand','product-brand',0),(7,0,'2020-02-02 16:54:07','订单',0,0,'oms','order',0),(8,7,'2020-02-02 16:55:18','订单列表',1,0,'order','product-list',0),(9,7,'2020-02-02 16:56:46','订单设置',1,0,'orderSetting','order-setting',0),(10,7,'2020-02-02 16:57:39','退货申请处理',1,0,'returnApply','order-return',0),(11,7,'2020-02-02 16:59:40','退货原因设置',1,0,'returnReason','order-return-reason',0),(12,0,'2020-02-04 16:18:00','营销',0,0,'sms','sms',0),(13,12,'2020-02-04 16:19:22','秒杀活动列表',1,0,'flash','sms-flash',0),(14,12,'2020-02-04 16:20:16','优惠券列表',1,0,'coupon','sms-coupon',0),(16,12,'2020-02-07 16:22:38','品牌推荐',1,0,'homeBrand','product-brand',0),(17,12,'2020-02-07 16:23:14','新品推荐',1,0,'homeNew','sms-new',0),(18,12,'2020-02-07 16:26:38','人气推荐',1,0,'homeHot','sms-hot',0),(19,12,'2020-02-07 16:28:16','专题推荐',1,0,'homeSubject','sms-subject',0),(20,12,'2020-02-07 16:28:42','广告列表',1,0,'homeAdvertise','sms-ad',0),(21,0,'2020-02-07 16:29:13','权限',0,0,'ums','ums',0),(22,21,'2020-02-07 16:29:51','用户列表',1,0,'admin','ums-admin',0),(23,21,'2020-02-07 16:30:13','角色列表',1,0,'role','ums-role',0),(24,21,'2020-02-07 16:30:53','菜单列表',1,0,'menu','ums-menu',0),(25,21,'2020-02-07 16:31:13','资源列表',1,0,'resource','ums-resource',0),(26,0,'2025-09-15 06:40:08','案例管理',0,5,'case','cms',0),(27,26,'2025-09-15 06:40:08','案例分类',1,0,'caseCategory','product-cate',0),(28,26,'2025-09-15 06:40:08','案例管理',1,1,'caseData','product-list',0);
/*!40000 ALTER TABLE `ums_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_permission`
--

DROP TABLE IF EXISTS `ums_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) DEFAULT NULL COMMENT '父级权限id',
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `value` varchar(200) DEFAULT NULL COMMENT '权限值',
  `icon` varchar(500) DEFAULT NULL COMMENT '图标',
  `type` int(1) DEFAULT NULL COMMENT '权限类型：0->目录；1->菜单；2->按钮（接口绑定权限）',
  `uri` varchar(200) DEFAULT NULL COMMENT '前端资源路径',
  `status` int(1) DEFAULT NULL COMMENT '启用状态；0->禁用；1->启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_permission`
--

LOCK TABLES `ums_permission` WRITE;
/*!40000 ALTER TABLE `ums_permission` DISABLE KEYS */;
INSERT INTO `ums_permission` VALUES (1,0,'商品',NULL,NULL,0,NULL,1,'2018-09-29 16:15:14',0),(2,1,'商品列表','pms:product:read',NULL,1,'/pms/product/index',1,'2018-09-29 16:17:01',0),(3,1,'添加商品','pms:product:create',NULL,1,'/pms/product/add',1,'2018-09-29 16:18:51',0),(4,1,'商品分类','pms:productCategory:read',NULL,1,'/pms/productCate/index',1,'2018-09-29 16:23:07',0),(5,1,'商品类型','pms:productAttribute:read',NULL,1,'/pms/productAttr/index',1,'2018-09-29 16:24:43',0),(6,1,'品牌管理','pms:brand:read',NULL,1,'/pms/brand/index',1,'2018-09-29 16:25:45',0),(7,2,'编辑商品','pms:product:update',NULL,2,'/pms/product/updateProduct',1,'2018-09-29 16:34:23',0),(8,2,'删除商品','pms:product:delete',NULL,2,'/pms/product/delete',1,'2018-09-29 16:38:33',0),(9,4,'添加商品分类','pms:productCategory:create',NULL,2,'/pms/productCate/create',1,'2018-09-29 16:43:23',0),(10,4,'修改商品分类','pms:productCategory:update',NULL,2,'/pms/productCate/update',1,'2018-09-29 16:43:55',0),(11,4,'删除商品分类','pms:productCategory:delete',NULL,2,'/pms/productAttr/delete',1,'2018-09-29 16:44:38',0),(12,5,'添加商品类型','pms:productAttribute:create',NULL,2,'/pms/productAttr/create',1,'2018-09-29 16:45:25',0),(13,5,'修改商品类型','pms:productAttribute:update',NULL,2,'/pms/productAttr/update',1,'2018-09-29 16:48:08',0),(14,5,'删除商品类型','pms:productAttribute:delete',NULL,2,'/pms/productAttr/delete',1,'2018-09-29 16:48:44',0),(15,6,'添加品牌','pms:brand:create',NULL,2,'/pms/brand/add',1,'2018-09-29 16:49:34',0),(16,6,'修改品牌','pms:brand:update',NULL,2,'/pms/brand/update',1,'2018-09-29 16:50:55',0),(17,6,'删除品牌','pms:brand:delete',NULL,2,'/pms/brand/delete',1,'2018-09-29 16:50:59',0),(18,0,'首页',NULL,NULL,0,NULL,1,'2018-09-29 16:51:57',0);
/*!40000 ALTER TABLE `ums_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_resource`
--

DROP TABLE IF EXISTS `ums_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `name` varchar(200) DEFAULT NULL COMMENT '资源名称',
  `url` varchar(200) DEFAULT NULL COMMENT '资源URL',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `category_id` bigint(20) DEFAULT NULL COMMENT '资源分类ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_resource`
--

LOCK TABLES `ums_resource` WRITE;
/*!40000 ALTER TABLE `ums_resource` DISABLE KEYS */;
INSERT INTO `ums_resource` VALUES (1,'2020-02-04 17:04:55','商品品牌管理','/brand/**',NULL,1),(2,'2020-02-04 17:05:35','商品属性分类管理','/productAttribute/category/**',NULL,1),(3,'2020-02-04 17:06:13','商品属性管理','/productAttribute/**',NULL,1),(4,'2020-02-04 17:07:15','商品分类管理','/productCategory/**',NULL,1),(5,'2020-02-04 17:09:16','商品管理','/product/**',NULL,1),(6,'2020-02-04 17:09:53','商品库存管理','/sku/**',NULL,1),(8,'2020-02-05 14:43:37','订单管理','/order/**','',2),(9,'2020-02-05 14:44:22',' 订单退货申请管理','/returnApply/**','',2),(10,'2020-02-05 14:45:08','退货原因管理','/returnReason/**','',2),(11,'2020-02-05 14:45:43','订单设置管理','/orderSetting/**','',2),(12,'2020-02-05 14:46:23','收货地址管理','/companyAddress/**','',2),(13,'2020-02-07 16:37:22','优惠券管理','/coupon/**','',3),(14,'2020-02-07 16:37:59','优惠券领取记录管理','/couponHistory/**','',3),(15,'2020-02-07 16:38:28','限时购活动管理','/flash/**','',3),(16,'2020-02-07 16:38:59','限时购商品关系管理','/flashProductRelation/**','',3),(17,'2020-02-07 16:39:22','限时购场次管理','/flashSession/**','',3),(18,'2020-02-07 16:40:07','首页轮播广告管理','/home/advertise/**','',3),(19,'2020-02-07 16:40:34','首页品牌管理','/home/brand/**','',3),(20,'2020-02-07 16:41:06','首页新品管理','/home/newProduct/**','',3),(21,'2020-02-07 16:42:16','首页人气推荐管理','/home/recommendProduct/**','',3),(22,'2020-02-07 16:42:48','首页专题推荐管理','/home/recommendSubject/**','',3),(23,'2020-02-07 16:44:56',' 商品优选管理','/prefrenceArea/**','',5),(24,'2020-02-07 16:45:39','商品专题管理','/subject/**','',5),(25,'2020-02-07 16:47:34','后台用户管理','/admin/**','',4),(26,'2020-02-07 16:48:24','后台用户角色管理','/role/**','',4),(27,'2020-02-07 16:48:48','后台菜单管理','/menu/**','',4),(28,'2020-02-07 16:49:18','后台资源分类管理','/resourceCategory/**','',4),(29,'2020-02-07 16:49:45','后台资源管理','/resource/**','',4),(30,'2020-09-19 15:47:57','会员等级管理','/memberLevel/**','',7),(31,'2020-09-19 15:51:29','获取登录用户信息','/admin/info','用户登录必配',4),(32,'2020-09-19 15:53:34','用户登出','/admin/logout','用户登出必配',4),(33,'2025-09-15 07:43:59','案例分类管理','/caseCategory/**','案例分类相关操作',8),(34,'2025-09-15 07:43:59','案例数据管理','/caseData/**','案例数据相关操作',8);
/*!40000 ALTER TABLE `ums_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_resource_category`
--

DROP TABLE IF EXISTS `ums_resource_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_resource_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `name` varchar(200) DEFAULT NULL COMMENT '分类名称',
  `sort` int(4) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='资源分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_resource_category`
--

LOCK TABLES `ums_resource_category` WRITE;
/*!40000 ALTER TABLE `ums_resource_category` DISABLE KEYS */;
INSERT INTO `ums_resource_category` VALUES (1,'2020-02-05 10:21:44','商品模块',0),(2,'2020-02-05 10:22:34','订单模块',0),(3,'2020-02-05 10:22:48','营销模块',0),(4,'2020-02-05 10:23:04','权限模块',0),(5,'2020-02-07 16:34:27','内容模块',0),(7,'2020-09-19 15:49:08','其他模块',0),(8,'2025-09-15 07:43:59','案例管理模块',7);
/*!40000 ALTER TABLE `ums_resource_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_role`
--

DROP TABLE IF EXISTS `ums_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL COMMENT '名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `admin_count` int(11) DEFAULT NULL COMMENT '后台用户数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(1) DEFAULT '1' COMMENT '启用状态：0->禁用；1->启用',
  `sort` int(11) DEFAULT '0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_role`
--

LOCK TABLES `ums_role` WRITE;
/*!40000 ALTER TABLE `ums_role` DISABLE KEYS */;
INSERT INTO `ums_role` VALUES (1,'商品管理员','只能查看及操作商品',0,'2020-02-03 16:50:37',1,0),(2,'订单管理员','只能查看及操作订单',0,'2018-09-30 15:53:45',1,0),(5,'超级管理员','拥有所有查看和操作功能',0,'2020-02-02 15:11:05',1,0),(6,'案例管理员','管理所有案例数据',0,'2025-09-15 15:06:26',1,0);
/*!40000 ALTER TABLE `ums_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_role_menu_relation`
--

DROP TABLE IF EXISTS `ums_role_menu_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_role_menu_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint(20) DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=160 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台角色菜单关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_role_menu_relation`
--

LOCK TABLES `ums_role_menu_relation` WRITE;
/*!40000 ALTER TABLE `ums_role_menu_relation` DISABLE KEYS */;
INSERT INTO `ums_role_menu_relation` VALUES (53,2,7),(54,2,8),(55,2,9),(56,2,10),(57,2,11),(121,1,1),(122,1,2),(123,1,3),(124,1,4),(125,1,5),(126,1,6),(127,1,26),(128,1,27),(129,1,28),(130,6,26),(131,6,27),(132,6,28),(133,5,1),(134,5,2),(135,5,3),(136,5,4),(137,5,5),(138,5,6),(139,5,7),(140,5,8),(141,5,9),(142,5,10),(143,5,11),(144,5,12),(145,5,13),(146,5,14),(147,5,16),(148,5,17),(149,5,18),(150,5,19),(151,5,20),(152,5,21),(153,5,22),(154,5,23),(155,5,24),(156,5,25),(157,5,26),(158,5,27),(159,5,28);
/*!40000 ALTER TABLE `ums_role_menu_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_role_permission_relation`
--

DROP TABLE IF EXISTS `ums_role_permission_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_role_permission_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL,
  `permission_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台用户角色和权限关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_role_permission_relation`
--

LOCK TABLES `ums_role_permission_relation` WRITE;
/*!40000 ALTER TABLE `ums_role_permission_relation` DISABLE KEYS */;
INSERT INTO `ums_role_permission_relation` VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,7),(5,1,8),(6,2,4),(7,2,9),(8,2,10),(9,2,11),(10,3,5),(11,3,12),(12,3,13),(13,3,14),(14,4,6),(15,4,15),(16,4,16),(17,4,17);
/*!40000 ALTER TABLE `ums_role_permission_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_role_resource_relation`
--

DROP TABLE IF EXISTS `ums_role_resource_relation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_role_resource_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=284 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='后台角色资源关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_role_resource_relation`
--

LOCK TABLES `ums_role_resource_relation` WRITE;
/*!40000 ALTER TABLE `ums_role_resource_relation` DISABLE KEYS */;
INSERT INTO `ums_role_resource_relation` VALUES (232,2,8),(233,2,9),(234,2,10),(235,2,11),(236,2,12),(237,2,31),(238,2,32),(239,1,1),(240,1,2),(241,1,3),(242,1,4),(243,1,5),(244,1,6),(245,1,23),(246,1,24),(247,1,31),(248,1,32),(249,1,33),(250,1,34),(251,5,1),(252,5,2),(253,5,3),(254,5,4),(255,5,5),(256,5,6),(257,5,8),(258,5,9),(259,5,10),(260,5,11),(261,5,12),(262,5,13),(263,5,14),(264,5,15),(265,5,16),(266,5,17),(267,5,18),(268,5,19),(269,5,20),(270,5,21),(271,5,22),(272,5,23),(273,5,24),(274,5,25),(275,5,26),(276,5,27),(277,5,28),(278,5,29),(279,5,30),(280,5,33),(281,5,34),(282,6,33),(283,6,34);
/*!40000 ALTER TABLE `ums_role_resource_relation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_subscription_usage`
--

DROP TABLE IF EXISTS `ums_subscription_usage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_subscription_usage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) NOT NULL COMMENT '用户ID',
  `subscription_order_id` bigint(20) NOT NULL COMMENT '套餐订单ID',
  `usage_date` date NOT NULL COMMENT '使用日期',
  `credits_used` int(11) DEFAULT '0' COMMENT '当日使用积分',
  `tasks_created` int(11) DEFAULT '0' COMMENT '当日创建任务数',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_member_date` (`member_id`,`usage_date`),
  KEY `idx_subscription_order` (`subscription_order_id`),
  KEY `idx_usage_date` (`usage_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐使用统计表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_subscription_usage`
--

LOCK TABLES `ums_subscription_usage` WRITE;
/*!40000 ALTER TABLE `ums_subscription_usage` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_subscription_usage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_third_party_auth`
--

DROP TABLE IF EXISTS `ums_third_party_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_third_party_auth` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `member_id` bigint(20) NOT NULL COMMENT '会员ID',
  `provider` varchar(32) NOT NULL COMMENT '第三方平台: wechat, google',
  `third_party_id` varchar(128) NOT NULL COMMENT '第三方用户ID',
  `access_token` text COMMENT '访问令牌',
  `refresh_token` text COMMENT '刷新令牌',
  `expire_time` datetime DEFAULT NULL COMMENT '令牌过期时间',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_thirdid` (`provider`,`third_party_id`),
  KEY `idx_member_id` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方登录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_third_party_auth`
--

LOCK TABLES `ums_third_party_auth` WRITE;
/*!40000 ALTER TABLE `ums_third_party_auth` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_third_party_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ums_verification_codes`
--

DROP TABLE IF EXISTS `ums_verification_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ums_verification_codes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(128) NOT NULL COMMENT '邮箱地址',
  `code` varchar(6) NOT NULL COMMENT '验证码',
  `code_type` tinyint(4) NOT NULL COMMENT '验证码类型: 1-注册, 2-登录, 3-重置密码',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used_status` tinyint(4) DEFAULT '0' COMMENT '使用状态: 0-未使用, 1-已使用',
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_email_type` (`email`,`code_type`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱验证码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ums_verification_codes`
--

LOCK TABLES `ums_verification_codes` WRITE;
/*!40000 ALTER TABLE `ums_verification_codes` DISABLE KEYS */;
/*!40000 ALTER TABLE `ums_verification_codes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-22 11:02:49
