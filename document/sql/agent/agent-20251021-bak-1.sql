-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: agent
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
-- Table structure for table `ai_project`
--

DROP TABLE IF EXISTS `ai_project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目唯一标识',
  `name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `description` text COMMENT '项目描述',
  `prompt` text NOT NULL COMMENT '用户输入的提示词',
  `uploaded_files` json DEFAULT NULL COMMENT '上传文件objectName数组 - 存储MinIO中的文件引用',
  `status` int(11) DEFAULT '0' COMMENT '项目状态: 0-创建中, 1-执行中, 2-已完成, 3-失败',
  `workflow_json` text COMMENT 'LLM分析生成的工作流定义JSON',
  `current_step` int(11) DEFAULT '0' COMMENT '当前执行步骤索引',
  `total_steps` int(11) DEFAULT '0' COMMENT '工作流总步骤数',
  `result_files` json DEFAULT NULL COMMENT '最终结果文件objectName数组',
  `member_id` bigint(20) NOT NULL COMMENT '所属用户ID，关联UMS用户系统',
  `start_time` datetime DEFAULT NULL COMMENT '项目开始执行时间',
  `end_time` datetime DEFAULT NULL COMMENT '项目完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `uuid` varchar(36) NOT NULL COMMENT '项目UUID,用于外部访问',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uuid` (`uuid`),
  KEY `idx_member_id` (`member_id`) COMMENT '用户项目列表查询优化',
  KEY `idx_status` (`status`) COMMENT '状态筛选查询优化',
  KEY `idx_create_time` (`create_time`) COMMENT '时间排序查询优化',
  KEY `idx_uuid` (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='AI项目主表 - 存储项目基础信息、工作流定义和执行状态';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_project`
--

LOCK TABLES `ai_project` WRITE;
/*!40000 ALTER TABLE `ai_project` DISABLE KEYS */;
INSERT INTO `ai_project` VALUES (1,'生成一张狗崽的图片','生成一张狗崽的图片','生成一张狗崽的图片','[]',2,'{\"nodes\": [{\"node_id\": \"text_to_image_1\", \"tool_type\": \"text_to_image\", \"tool_uuid\": \"tool-text2img-001\", \"model_name\": \"doubao-seedream-3-0-t2i-250415\", \"endpoint\": \"doubao-seedream-3-0-t2i-250415\", \"status\": \"completed\", \"depends_on\": [], \"child_node_ids\": [], \"parent_node_id\": null, \"data_model_type\": \"model\", \"step_index\": 1, \"input_data\": {\"prompt\": \"一只可爱的狗崽，有着毛茸茸的身体，明亮的眼睛，正好奇地张望着周围\", \"model\": \"doubao-seedream-3-0-t2i-250415\", \"size\": \"720x1280\", \"seed\": -1, \"guidance_scale\": 2.5, \"response_format\": \"url\", \"watermark\": false}, \"output_data\": \"图像生成完成\", \"output_files\": [\"text_to_image/1761037512_cd6d4fe8.jpeg\"], \"original_files\": [\"https://ark-content-generation-v2-cn-beijing.tos-cn-beijing.volces.com/doubao-seedream-3-0-t2i/0217610375083866b45272b17213eae71876ce85b713a66806a27_0.jpeg?X-Tos-Algorithm=TOS4-HMAC-SHA256&X-Tos-Credential=AKLTYWJkZTExNjA1ZDUyNDc3YzhjNTM5OGIyNjBhNDcyOTQ%2F20251021%2Fcn-beijing%2Ftos%2Frequest&X-Tos-Date=20251021T090511Z&X-Tos-Expires=86400&X-Tos-Signature=a8e32eba42b6fc1f7a9be70d84b0613f7f95ed8bfb30ef8c31c865a38bc5cf5f&X-Tos-SignedHeaders=host\"]}], \"reasoning\": \"用户需要生成一张狗崽的图片，因此创建一个text_to_image节点来生成图片\", \"confidence\": 0.9, \"content_description\": \"生成一张狗崽的图片\", \"uploaded_file_count\": 0}',0,0,'[]',33,'2025-10-21 17:05:05','2025-10-21 17:05:14','2025-10-21 09:04:58','2025-10-21 09:05:14','1761037494658-yaun9g594mo'),(2,'生成一张狗崽的图片','生成一张狗崽的图片','生成一张狗崽的图片','[]',2,'{\"nodes\": [{\"node_id\": \"text_to_image_1\", \"tool_type\": \"text_to_image\", \"tool_uuid\": \"tool-text2img-001\", \"model_name\": \"doubao-seedream-3-0-t2i-250415\", \"endpoint\": \"doubao-seedream-3-0-t2i-250415\", \"status\": \"completed\", \"depends_on\": [], \"child_node_ids\": [], \"parent_node_id\": null, \"data_model_type\": \"model\", \"step_index\": 1, \"input_data\": {\"prompt\": \"一只可爱的狗崽，有着毛茸茸的身体，明亮的眼睛和俏皮的尾巴\", \"model\": \"doubao-seedream-3-0-t2i-250415\", \"size\": \"720x1280\", \"seed\": -1, \"guidance_scale\": 2.5, \"response_format\": \"url\", \"watermark\": false}, \"output_data\": \"图像生成完成\", \"output_files\": [], \"original_files\": []}], \"reasoning\": \"用户需求为生成一张狗崽的图片，因此创建一个text_to_image节点用于生成图片。\", \"confidence\": 0.9, \"content_description\": \"生成一张狗崽的图片\", \"uploaded_file_count\": 0}',0,0,'[]',33,'2025-10-21 17:06:23','2025-10-21 17:06:30','2025-10-21 09:06:15','2025-10-21 09:06:29','1761037568599-6vnebyxfa9m'),(3,'生成一张狗崽的图片','生成一张狗崽的图片','生成一张狗崽的图片','[]',2,'{\"nodes\": [{\"node_id\": \"text_to_image_1\", \"tool_type\": \"text_to_image\", \"tool_uuid\": \"tool-text2img-001\", \"model_name\": \"doubao-seedream-3-0-t2i-250415\", \"endpoint\": \"doubao-seedream-3-0-t2i-250415\", \"status\": \"completed\", \"depends_on\": [], \"child_node_ids\": [], \"parent_node_id\": null, \"data_model_type\": \"model\", \"step_index\": 1, \"input_data\": {\"prompt\": \"一只可爱的狗崽，有着毛茸茸的身体，明亮的眼睛，正好奇地张望着周围\", \"model\": \"doubao-seedream-3-0-t2i-250415\", \"size\": \"720x1280\", \"seed\": -1, \"guidance_scale\": 2.5, \"response_format\": \"url\", \"watermark\": false}, \"output_data\": \"图像生成完成\", \"output_files\": [], \"original_files\": []}], \"reasoning\": \"用户需求是生成一张狗崽的图片，因此只需要一个text_to_image节点用于生成图片。\", \"confidence\": 0.9, \"content_description\": \"生成一张狗崽的图片\", \"uploaded_file_count\": 0}',0,0,'[]',33,'2025-10-21 17:12:05','2025-10-21 17:12:12','2025-10-21 09:11:58','2025-10-21 09:12:11','1761037914292-3m8mwkv15x4');
/*!40000 ALTER TABLE `ai_project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_project_message`
--

DROP TABLE IF EXISTS `ai_project_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_project_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息唯一标识',
  `project_id` bigint(20) NOT NULL COMMENT '关联的项目ID',
  `message_type` varchar(50) NOT NULL COMMENT '消息类型: user-用户输入, system-系统消息, assistant-AI回复, tool_call-工具调用, tool_result-工具结果',
  `sender` varchar(100) DEFAULT NULL COMMENT '发送者标识 - user/system/assistant等',
  `content` text COMMENT '消息文本内容',
  `metadata` json DEFAULT NULL COMMENT '消息元数据 - 存储扩展信息如时间戳、配置参数等',
  `files` json DEFAULT NULL COMMENT '消息关联文件objectName数组 - 输入或输出的文件引用',
  `workflow_execution_id` bigint(20) DEFAULT NULL COMMENT '关联的工作流执行记录ID - 可选，用于关联具体执行步骤',
  `is_visible` tinyint(1) DEFAULT '1' COMMENT '是否在前端显示 - 控制消息可见性',
  `message_order` int(11) NOT NULL COMMENT '消息在项目中的顺序号 - 保证显示顺序正确',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_order` (`project_id`,`message_order`) COMMENT '项目对话历史查询优化',
  KEY `idx_visible` (`is_visible`) COMMENT '可见消息筛选优化',
  KEY `idx_message_type` (`message_type`) COMMENT '消息类型筛选优化'
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COMMENT='AI项目对话消息表 - 存储项目完整的交互历史，支持对话回放';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_project_message`
--

LOCK TABLES `ai_project_message` WRITE;
/*!40000 ALTER TABLE `ai_project_message` DISABLE KEYS */;
INSERT INTO `ai_project_message` VALUES (1,1,'user','user','生成一张狗崽的图片',NULL,'[]',NULL,1,1,'2025-10-21 09:04:58'),(2,1,'system','system','项目已创建,正在生成工作流...',NULL,NULL,NULL,1,2,'2025-10-21 09:04:58'),(3,1,'system','system','工作流生成完成，共 1 个节点，可以开始执行',NULL,NULL,NULL,1,3,'2025-10-21 09:05:04'),(4,1,'system','system','开始执行工作流',NULL,NULL,NULL,1,4,'2025-10-21 09:05:05'),(5,1,'tool_result','assistant','工具 text_to_image 执行成功,生成了 1 个文件','{\"status\": \"completed\", \"node_id\": \"text_to_image_1\", \"tool_name\": \"text_to_image\", \"tool_uuid\": \"tool-text2img-001\", \"execution_duration\": null}','[\"text_to_image/1761037512_cd6d4fe8.jpeg\"]',NULL,1,5,'2025-10-21 09:05:14'),(6,1,'system','system','工作流执行完成',NULL,NULL,NULL,1,6,'2025-10-21 09:05:14'),(7,2,'user','user','生成一张狗崽的图片',NULL,'[]',NULL,1,1,'2025-10-21 09:06:15'),(8,2,'system','system','项目已创建,正在生成工作流...',NULL,NULL,NULL,1,2,'2025-10-21 09:06:15'),(9,2,'system','system','工作流生成完成，共 1 个节点，可以开始执行',NULL,NULL,NULL,1,3,'2025-10-21 09:06:22'),(10,2,'system','system','开始执行工作流',NULL,NULL,NULL,1,4,'2025-10-21 09:06:22'),(11,2,'tool_result','assistant','工具 text_to_image 执行成功,生成了 0 个文件','{\"status\": \"completed\", \"node_id\": \"text_to_image_1\", \"tool_name\": \"text_to_image\", \"tool_uuid\": \"tool-text2img-001\", \"execution_duration\": null}','[]',NULL,1,5,'2025-10-21 09:06:29'),(12,2,'system','system','工作流执行完成',NULL,NULL,NULL,1,6,'2025-10-21 09:06:29'),(13,3,'user','user','生成一张狗崽的图片',NULL,'[]',NULL,1,1,'2025-10-21 09:11:58'),(14,3,'system','system','项目已创建,正在生成工作流...',NULL,NULL,NULL,1,2,'2025-10-21 09:11:58'),(15,3,'system','system','工作流生成完成，共 1 个节点，可以开始执行',NULL,NULL,NULL,1,3,'2025-10-21 09:12:03'),(16,3,'system','system','开始执行工作流',NULL,NULL,NULL,1,4,'2025-10-21 09:12:04'),(17,3,'tool_result','assistant','工具 text_to_image 执行成功,生成了 0 个文件','{\"status\": \"completed\", \"node_id\": \"text_to_image_1\", \"tool_name\": \"text_to_image\", \"tool_uuid\": \"tool-text2img-001\", \"execution_duration\": null}','[]',NULL,1,5,'2025-10-21 09:12:11'),(18,3,'system','system','工作流执行完成',NULL,NULL,NULL,1,6,'2025-10-21 09:12:11');
/*!40000 ALTER TABLE `ai_project_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_workflow_execution`
--

DROP TABLE IF EXISTS `ai_workflow_execution`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_workflow_execution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '执行记录唯一标识',
  `project_id` bigint(20) NOT NULL COMMENT '关联的项目ID',
  `node_id` varchar(100) NOT NULL COMMENT '工作流节点唯一标识',
  `tool_uuid` varchar(100) DEFAULT NULL COMMENT '工具唯一标识UUID',
  `model_name` varchar(100) DEFAULT NULL COMMENT 'AI模型名称',
  `endpoint` varchar(255) DEFAULT NULL COMMENT 'API调用端点',
  `tool_name` varchar(100) DEFAULT NULL COMMENT 'AI工具名称(可为空,根据tool_uuid查询)',
  `step_index` int(11) NOT NULL COMMENT '在工作流中的步骤序号',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '执行状态: 0-pending(待执行), 1-running(执行中), 2-completed(已完成), 3-failed(失败)',
  `input_data` text COMMENT '工具输入参数JSON - 记录完整的输入配置',
  `output_data` text COMMENT '工具输出结果JSON - 记录执行结果数据',
  `output_files` json DEFAULT NULL COMMENT '输出文件objectName数组 - 工具生成的文件引用',
  `original_files` json DEFAULT NULL COMMENT '第三方API返回的原始文件URL数组 - 临时链接备份',
  `child_node_ids` json DEFAULT NULL COMMENT '子节点ID数组',
  `parent_node_id` varchar(100) DEFAULT NULL COMMENT '父节点ID',
  `depends_on` json DEFAULT NULL COMMENT '依赖的节点ID数组',
  `data_model_type` varchar(50) DEFAULT NULL COMMENT '数据模型类型: model/tool',
  `preview_required` tinyint(1) DEFAULT '0' COMMENT '是否需要前端预览 - 控制UI显示',
  `error_message` text COMMENT '执行失败时的错误信息',
  `start_time` datetime DEFAULT NULL COMMENT '步骤开始执行时间',
  `end_time` datetime DEFAULT NULL COMMENT '步骤完成时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`) COMMENT '项目执行记录查询优化',
  KEY `idx_status` (`status`) COMMENT '状态筛选优化',
  KEY `idx_step_index` (`step_index`) COMMENT '步骤排序优化',
  KEY `idx_tool_name` (`tool_name`) COMMENT '工具统计查询优化',
  KEY `idx_tool_uuid` (`tool_uuid`),
  KEY `idx_parent_node_id` (`parent_node_id`),
  KEY `idx_data_model_type` (`data_model_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI工作流执行记录表 - 追踪每个工具节点的详细执行过程';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_workflow_execution`
--

LOCK TABLES `ai_workflow_execution` WRITE;
/*!40000 ALTER TABLE `ai_workflow_execution` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_workflow_execution` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-21 17:38:36
