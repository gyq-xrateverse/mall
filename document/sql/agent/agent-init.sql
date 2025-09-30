-- =====================================================
-- AI智能体系统数据库初始化脚本
-- 创建日期: 2024-09-25
-- 描述: AI任务处理系统的核心数据表结构，支持项目管理、工作流执行和对话历史
-- =====================================================

-- -------------------------------------------
-- 1. AI项目表 - 项目基础信息和状态管理
-- -------------------------------------------
DROP TABLE IF EXISTS ai_project;
CREATE TABLE ai_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目唯一标识',
    name VARCHAR(255) NOT NULL COMMENT '项目名称',
    description TEXT COMMENT '项目描述',
    prompt TEXT NOT NULL COMMENT '用户输入的提示词',
    uploaded_files JSON COMMENT '上传文件objectName数组 - 存储MinIO中的文件引用',
    status INT DEFAULT 0 COMMENT '项目状态: 0-创建中, 1-执行中, 2-已完成, 3-失败',
    workflow_json TEXT COMMENT 'LLM分析生成的工作流定义JSON',
    current_step INT DEFAULT 0 COMMENT '当前执行步骤索引',
    total_steps INT DEFAULT 0 COMMENT '工作流总步骤数',
    result_files JSON COMMENT '最终结果文件objectName数组',
    member_id BIGINT NOT NULL COMMENT '所属用户ID，关联UMS用户系统',
    start_time DATETIME COMMENT '项目开始执行时间',
    end_time DATETIME COMMENT '项目完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 索引优化
    INDEX idx_member_id (member_id) COMMENT '用户项目列表查询优化',
    INDEX idx_status (status) COMMENT '状态筛选查询优化',
    INDEX idx_create_time (create_time) COMMENT '时间排序查询优化'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI项目主表 - 存储项目基础信息、工作流定义和执行状态';

-- -------------------------------------------
-- 2. AI工作流执行记录表 - 详细的执行过程追踪
-- -------------------------------------------
DROP TABLE IF EXISTS ai_workflow_execution;
CREATE TABLE ai_workflow_execution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '执行记录唯一标识',
    project_id BIGINT NOT NULL COMMENT '关联的项目ID',
    node_id VARCHAR(100) NOT NULL COMMENT '工作流节点唯一标识',
    tool_name VARCHAR(100) NOT NULL COMMENT 'AI工具名称 - 如image_enhance、video_generate等',
    step_index INT NOT NULL COMMENT '在工作流中的步骤序号',
    status INT DEFAULT 0 COMMENT '执行状态: 0-待执行, 1-执行中, 2-已完成, 3-失败',
    input_data TEXT COMMENT '工具输入参数JSON - 记录完整的输入配置',
    output_data TEXT COMMENT '工具输出结果JSON - 记录执行结果数据',
    output_files JSON COMMENT '输出文件objectName数组 - 工具生成的文件引用',
    original_files JSON COMMENT '第三方API返回的原始文件URL数组 - 临时链接备份',
    preview_required BOOLEAN DEFAULT FALSE COMMENT '是否需要前端预览 - 控制UI显示',
    error_message TEXT COMMENT '执行失败时的错误信息',
    start_time DATETIME COMMENT '步骤开始执行时间',
    end_time DATETIME COMMENT '步骤完成时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',

    -- 索引优化
    INDEX idx_project_id (project_id) COMMENT '项目执行记录查询优化',
    INDEX idx_status (status) COMMENT '状态筛选优化',
    INDEX idx_step_index (step_index) COMMENT '步骤排序优化',
    INDEX idx_tool_name (tool_name) COMMENT '工具统计查询优化'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI工作流执行记录表 - 追踪每个工具节点的详细执行过程';

-- -------------------------------------------
-- 3. AI项目对话消息表 - 完整的交互历史记录
-- -------------------------------------------
DROP TABLE IF EXISTS ai_project_message;
CREATE TABLE ai_project_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息唯一标识',
    project_id BIGINT NOT NULL COMMENT '关联的项目ID',
    message_type VARCHAR(50) NOT NULL COMMENT '消息类型: user-用户输入, system-系统消息, assistant-AI回复, tool_call-工具调用, tool_result-工具结果',
    sender VARCHAR(100) COMMENT '发送者标识 - user/system/assistant等',
    content TEXT COMMENT '消息文本内容',
    metadata JSON COMMENT '消息元数据 - 存储扩展信息如时间戳、配置参数等',
    files JSON COMMENT '消息关联文件objectName数组 - 输入或输出的文件引用',
    workflow_execution_id BIGINT COMMENT '关联的工作流执行记录ID - 可选，用于关联具体执行步骤',
    is_visible BOOLEAN DEFAULT TRUE COMMENT '是否在前端显示 - 控制消息可见性',
    message_order INT NOT NULL COMMENT '消息在项目中的顺序号 - 保证显示顺序正确',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '消息创建时间',

    -- 索引优化
    INDEX idx_project_order (project_id, message_order) COMMENT '项目对话历史查询优化',
    INDEX idx_visible (is_visible) COMMENT '可见消息筛选优化',
    INDEX idx_message_type (message_type) COMMENT '消息类型筛选优化'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI项目对话消息表 - 存储项目完整的交互历史，支持对话回放';

-- =====================================================
-- 数据表关系说明
-- =====================================================
/*
表间关系结构:
ai_project (项目表) [1:N]
    ├── ai_workflow_execution (工作流执行记录表) [1:N]
    └── ai_project_message (对话消息表) [1:N]
        └── ai_workflow_execution (可选关联) [N:1]

核心设计理念:
1. 简化架构: 去除中间层ai_task表，ai_project直接管理工作流
2. 文件统一管理: 所有文件通过objectName引用，存储在MinIO中
3. 完整追踪: 从项目创建到执行完成的全链路记录
4. 实时通信: 支持WebSocket状态推送和对话历史加载
5. 无外键约束: 移除外键约束以提高性能和灵活性，通过应用层保证数据一致性

文件存储策略:
- uploaded_files: 用户上传的原始文件
- output_files: 工具执行产生的中间文件
- result_files: 项目最终输出文件
- files (消息表): 消息关联的文件引用

状态流转:
用户提交 → ai_project(创建中) → LLM分析 → 生成workflow_json
→ ai_workflow_execution(逐步执行) → ai_project_message(记录对话)
→ ai_project(完成/失败)
*/

-- =====================================================
-- 初始化完成提示
-- =====================================================
SELECT '=====================================' as '初始化状态';
SELECT 'AI智能体系统数据库初始化完成!' as '状态信息';
SELECT '=====================================' as '';
SELECT 'ai_project - AI项目主表创建完成' as '表1';
SELECT 'ai_workflow_execution - 工作流执行记录表创建完成' as '表2';
SELECT 'ai_project_message - 项目对话消息表创建完成' as '表3';
SELECT '=====================================' as '';
SELECT '数据表关系: 项目 -> 工作流执行 + 对话消息' as '关系说明';
SELECT '文件管理: 统一通过objectName引用MinIO存储' as '文件策略';
SELECT '=====================================' as '';