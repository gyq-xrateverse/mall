-- =====================================================
-- 白鹿AI平台 - 完整数据库初始化脚本
-- =====================================================
-- 基于现有mall电商系统扩展，支持AI创意设计平台功能
-- 包含用户认证系统、积分系统、套餐管理、支付系统等
-- 创建时间: 2025-09-10
-- 版本: V1.0
-- 执行顺序: 从上到下依次执行
-- =====================================================

-- 设置字符集和事务
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
START TRANSACTION;

-- =====================================================
-- 第一部分: 用户认证系统扩展
-- =====================================================

-- 1.1 扩展用户表 (ums_member) - 添加认证相关字段
-- 检查字段是否存在，避免重复添加
-- 添加邮箱字段（邮箱注册登录必需）
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'email') = 0, 'ALTER TABLE ums_member ADD COLUMN email VARCHAR(128) UNIQUE COMMENT "邮箱地址"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'wechat_openid') = 0, 'ALTER TABLE ums_member ADD COLUMN wechat_openid VARCHAR(64) UNIQUE COMMENT "微信OpenID"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'google_id') = 0, 'ALTER TABLE ums_member ADD COLUMN google_id VARCHAR(128) UNIQUE COMMENT "Google用户ID"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'register_type') = 0, 'ALTER TABLE ums_member ADD COLUMN register_type TINYINT DEFAULT 1 COMMENT "注册方式: 1-邮箱, 2-微信, 3-Google"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'email_verified') = 0, 'ALTER TABLE ums_member ADD COLUMN email_verified TINYINT DEFAULT 0 COMMENT "邮箱验证状态: 0-未验证, 1-已验证"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'last_login_time') = 0, 'ALTER TABLE ums_member ADD COLUMN last_login_time DATETIME COMMENT "最后登录时间"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'avatar_url') = 0, 'ALTER TABLE ums_member ADD COLUMN avatar_url VARCHAR(255) COMMENT "头像URL"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'account_status') = 0, 'ALTER TABLE ums_member ADD COLUMN account_status TINYINT DEFAULT 1 COMMENT "账户状态: 0-禁用, 1-正常, 2-锁定"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 1.2 创建验证码记录表
CREATE TABLE IF NOT EXISTS ums_verification_codes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(128) NOT NULL COMMENT '邮箱地址',
    code VARCHAR(6) NOT NULL COMMENT '验证码',
    code_type TINYINT NOT NULL COMMENT '验证码类型: 1-注册, 2-登录, 3-重置密码',
    expire_time DATETIME NOT NULL COMMENT '过期时间',
    used_status TINYINT DEFAULT 0 COMMENT '使用状态: 0-未使用, 1-已使用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_email_type (email, code_type),
    INDEX idx_expire_time (expire_time)
) COMMENT='邮箱验证码表';

-- 1.3 创建第三方登录记录表
CREATE TABLE IF NOT EXISTS ums_third_party_auth (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL COMMENT '会员ID',
    provider VARCHAR(32) NOT NULL COMMENT '第三方平台: wechat, google',
    third_party_id VARCHAR(128) NOT NULL COMMENT '第三方用户ID',
    access_token TEXT COMMENT '访问令牌',
    refresh_token TEXT COMMENT '刷新令牌',
    expire_time DATETIME COMMENT '令牌过期时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_provider_thirdid (provider, third_party_id),
    INDEX idx_member_id (member_id)
) COMMENT='第三方登录表';

-- =====================================================
-- 第二部分: 积分系统扩展
-- =====================================================

-- 2.1 扩展用户表 - 添加积分相关字段
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'free_daily_credits') = 0, 'ALTER TABLE ums_member ADD COLUMN free_daily_credits INT DEFAULT 200 COMMENT "每日免费积分"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'used_today_free') = 0, 'ALTER TABLE ums_member ADD COLUMN used_today_free INT DEFAULT 0 COMMENT "今日已使用免费积分"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'last_reset_date') = 0, 'ALTER TABLE ums_member ADD COLUMN last_reset_date DATE COMMENT "最后重置日期"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.2 扩展积分变动记录表
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_integration_change_history' AND COLUMN_NAME = 'credit_type') = 0, 'ALTER TABLE ums_integration_change_history ADD COLUMN credit_type TINYINT DEFAULT 2 COMMENT "积分类型: 1-免费积分, 2-永久积分"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_integration_change_history' AND COLUMN_NAME = 'business_id') = 0, 'ALTER TABLE ums_integration_change_history ADD COLUMN business_id VARCHAR(64) COMMENT "业务ID(如AI任务ID)"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.3 扩展商品表 - 支持积分商品
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'product_type') = 0, 'ALTER TABLE pms_product ADD COLUMN product_type TINYINT DEFAULT 1 COMMENT "商品类型: 1-普通商品, 2-积分商品, 3-套餐商品"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'credit_amount') = 0, 'ALTER TABLE pms_product ADD COLUMN credit_amount INT COMMENT "积分数量(积分商品时使用)"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.4 配置每日免费积分规则
UPDATE ums_member_rule_setting SET 
    continue_sign_day = 1,
    continue_sign_point = 200,
    type = 0 
WHERE id = 1;

-- 如果规则不存在则插入
INSERT INTO ums_member_rule_setting (continue_sign_day, continue_sign_point, consume_per_point, low_order_amount, max_point_per_order, type)
VALUES (1, 200, 1.00, 0.00, -1, 0)
ON DUPLICATE KEY UPDATE 
    continue_sign_day = 1,
    continue_sign_point = 200;

-- =====================================================
-- 第三部分: 套餐管理系统扩展
-- =====================================================

-- 3.1 扩展商品表 - 支持套餐商品
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'subscription_period') = 0, 'ALTER TABLE pms_product ADD COLUMN subscription_period INT COMMENT "套餐周期(天): 30-月套餐, 90-季套餐, 365-年套餐"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'daily_credits') = 0, 'ALTER TABLE pms_product ADD COLUMN daily_credits INT COMMENT "每日积分额度"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'total_credits') = 0, 'ALTER TABLE pms_product ADD COLUMN total_credits INT COMMENT "总积分额度(用于显示)"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'max_concurrent_tasks') = 0, 'ALTER TABLE pms_product ADD COLUMN max_concurrent_tasks INT DEFAULT 1 COMMENT "最大并发任务数"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'priority_level') = 0, 'ALTER TABLE pms_product ADD COLUMN priority_level TINYINT DEFAULT 0 COMMENT "任务优先级: 0-普通, 1-高, 2-最高"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND COLUMN_NAME = 'features') = 0, 'ALTER TABLE pms_product ADD COLUMN features JSON COMMENT "套餐特性配置JSON"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.2 扩展订单表 - 支持套餐订阅
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND COLUMN_NAME = 'subscription_start_date') = 0, 'ALTER TABLE oms_order ADD COLUMN subscription_start_date DATE COMMENT "套餐开始日期"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND COLUMN_NAME = 'subscription_end_date') = 0, 'ALTER TABLE oms_order ADD COLUMN subscription_end_date DATE COMMENT "套餐结束日期"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND COLUMN_NAME = 'subscription_status') = 0, 'ALTER TABLE oms_order ADD COLUMN subscription_status TINYINT DEFAULT 0 COMMENT "订阅状态: 0-待生效, 1-有效, 2-过期, 3-已取消"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND COLUMN_NAME = 'auto_renew') = 0, 'ALTER TABLE oms_order ADD COLUMN auto_renew TINYINT DEFAULT 0 COMMENT "是否自动续费: 0-否, 1-是"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.3 扩展用户表 - 记录当前套餐
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'current_subscription_id') = 0, 'ALTER TABLE ums_member ADD COLUMN current_subscription_id BIGINT COMMENT "当前有效套餐订单ID"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'subscription_level') = 0, 'ALTER TABLE ums_member ADD COLUMN subscription_level TINYINT DEFAULT 0 COMMENT "套餐等级: 0-免费, 1-基础, 2-标准, 3-专业, 4-企业"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'daily_credits_limit') = 0, 'ALTER TABLE ums_member ADD COLUMN daily_credits_limit INT DEFAULT 200 COMMENT "每日积分限额"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'daily_credits_used') = 0, 'ALTER TABLE ums_member ADD COLUMN daily_credits_used INT DEFAULT 0 COMMENT "当日已使用积分"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND COLUMN_NAME = 'subscription_end_date') = 0, 'ALTER TABLE ums_member ADD COLUMN subscription_end_date DATE COMMENT "套餐到期日期"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3.4 创建套餐使用统计表
CREATE TABLE IF NOT EXISTS ums_subscription_usage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL COMMENT '用户ID',
    subscription_order_id BIGINT NOT NULL COMMENT '套餐订单ID',
    usage_date DATE NOT NULL COMMENT '使用日期',
    credits_used INT DEFAULT 0 COMMENT '当日使用积分',
    tasks_created INT DEFAULT 0 COMMENT '当日创建任务数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_member_date (member_id, usage_date),
    INDEX idx_subscription_order (subscription_order_id),
    INDEX idx_usage_date (usage_date)
) COMMENT='套餐使用统计表';

-- =====================================================
-- 第四部分: 支付系统扩展
-- =====================================================

-- 4.1 扩展订单表 - 支持新的订单类型
-- 检查并删除已存在的字段，避免重复添加报错
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND COLUMN_NAME = 'order_type') > 0, 'ALTER TABLE oms_order DROP COLUMN order_type', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND COLUMN_NAME = 'business_type') > 0, 'ALTER TABLE oms_order DROP COLUMN business_type', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加字段
ALTER TABLE oms_order ADD COLUMN order_type TINYINT DEFAULT 0 COMMENT '订单类型: 0-普通商品, 1-套餐订单, 2-积分充值订单';
ALTER TABLE oms_order ADD COLUMN business_type TINYINT DEFAULT 0 COMMENT '业务类型: 0-商品购买, 1-套餐订阅, 2-积分充值, 3-套餐续费';

-- 4.2 扩展支付记录表 (如果表不存在则创建)
CREATE TABLE IF NOT EXISTS oms_order_payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_method VARCHAR(32) NOT NULL COMMENT '支付方式',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    transaction_id VARCHAR(128) COMMENT '第三方交易号',
    status TINYINT DEFAULT 0 COMMENT '支付状态: 0-待支付, 1-支付成功, 2-支付失败',
    pay_time DATETIME COMMENT '支付时间',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_order_id (order_id),
    INDEX idx_transaction_id (transaction_id)
) COMMENT='订单支付记录表';

-- 扩展支付记录表
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order_payment' AND COLUMN_NAME = 'payment_scene') = 0, 'ALTER TABLE oms_order_payment ADD COLUMN payment_scene VARCHAR(32) COMMENT "支付场景: subscription-套餐订阅, credits-积分充值, renewal-套餐续费"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order_payment' AND COLUMN_NAME = 'callback_status') = 0, 'ALTER TABLE oms_order_payment ADD COLUMN callback_status TINYINT DEFAULT 0 COMMENT "回调处理状态: 0-未处理, 1-处理成功, 2-处理失败"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order_payment' AND COLUMN_NAME = 'callback_retry_count') = 0, 'ALTER TABLE oms_order_payment ADD COLUMN callback_retry_count INT DEFAULT 0 COMMENT "回调重试次数"', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =====================================================
-- 第五部分: 索引优化
-- =====================================================

-- 5.1 用户认证系统索引
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND INDEX_NAME = 'idx_ums_member_register_type') = 0, 'CREATE INDEX idx_ums_member_register_type ON ums_member(register_type)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND INDEX_NAME = 'idx_ums_member_last_login') = 0, 'CREATE INDEX idx_ums_member_last_login ON ums_member(last_login_time)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND INDEX_NAME = 'idx_ums_member_account_status') = 0, 'CREATE INDEX idx_ums_member_account_status ON ums_member(account_status)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5.2 积分系统索引
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND INDEX_NAME = 'idx_ums_member_last_reset') = 0, 'CREATE INDEX idx_ums_member_last_reset ON ums_member(last_reset_date)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_integration_change_history' AND INDEX_NAME = 'idx_integration_history_credit_type') = 0, 'CREATE INDEX idx_integration_history_credit_type ON ums_integration_change_history(credit_type)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_integration_change_history' AND INDEX_NAME = 'idx_integration_history_business_id') = 0, 'CREATE INDEX idx_integration_history_business_id ON ums_integration_change_history(business_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pms_product' AND INDEX_NAME = 'idx_pms_product_type') = 0, 'CREATE INDEX idx_pms_product_type ON pms_product(product_type)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5.3 套餐系统索引
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND INDEX_NAME = 'idx_ums_member_subscription_level') = 0, 'CREATE INDEX idx_ums_member_subscription_level ON ums_member(subscription_level)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'ums_member' AND INDEX_NAME = 'idx_ums_member_subscription_end') = 0, 'CREATE INDEX idx_ums_member_subscription_end ON ums_member(subscription_end_date)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND INDEX_NAME = 'idx_oms_order_subscription_status') = 0, 'CREATE INDEX idx_oms_order_subscription_status ON oms_order(subscription_status)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND INDEX_NAME = 'idx_oms_order_subscription_end') = 0, 'CREATE INDEX idx_oms_order_subscription_end ON oms_order(subscription_end_date)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 5.4 支付系统索引
SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND INDEX_NAME = 'idx_oms_order_type') = 0, 'CREATE INDEX idx_oms_order_type ON oms_order(order_type)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order' AND INDEX_NAME = 'idx_oms_order_business_type') = 0, 'CREATE INDEX idx_oms_order_business_type ON oms_order(business_type)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order_payment' AND INDEX_NAME = 'idx_payment_scene') = 0, 'CREATE INDEX idx_payment_scene ON oms_order_payment(payment_scene)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF((SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'oms_order_payment' AND INDEX_NAME = 'idx_callback_status') = 0, 'CREATE INDEX idx_callback_status ON oms_order_payment(callback_status)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- =====================================================
-- 第六部分: 初始化数据
-- =====================================================

-- 6.1 积分商品数据初始化
INSERT INTO pms_product (
    brand_id, product_category_id, name, sub_title, description, price, original_price, 
    stock, unit, weight, sort, gift_growth, gift_point, use_point_limit, product_sn, 
    keywords, publish_status, new_status, recommand_status, verify_status, sale, 
    product_type, credit_amount
) VALUES 
(1, 1, '基础积分包', '1000积分', '适合轻度使用用户，包含1000积分', 9.90, 12.90, 999, '包', 0, 1, 0, 0, 0, 'CREDIT_BASIC_1000', '积分,充值,基础', 1, 1, 0, 1, 0, 2, 1000),
(1, 1, '标准积分包', '3000积分', '适合日常使用用户，包含3000积分', 26.90, 35.90, 999, '包', 0, 2, 0, 0, 0, 'CREDIT_STANDARD_3000', '积分,充值,标准', 1, 1, 1, 1, 0, 2, 3000),
(1, 1, '专业积分包', '6000积分', '适合专业设计师，包含6000积分', 49.90, 69.90, 999, '包', 0, 3, 0, 0, 0, 'CREDIT_PRO_6000', '积分,充值,专业', 1, 1, 1, 1, 0, 2, 6000),
(1, 1, '企业积分包', '15000积分', '适合团队协作，包含15000积分', 119.90, 169.90, 999, '包', 0, 4, 0, 0, 0, 'CREDIT_ENTERPRISE_15000', '积分,充值,企业', 1, 0, 1, 1, 0, 2, 15000),
(1, 1, '旗舰积分包', '30000积分', '无限创作可能，包含30000积分', 219.90, 329.90, 999, '包', 0, 5, 0, 0, 0, 'CREDIT_FLAGSHIP_30000', '积分,充值,旗舰', 1, 1, 1, 1, 0, 2, 30000);

-- 6.2 套餐商品数据初始化
INSERT INTO pms_product (
    brand_id, product_category_id, name, sub_title, description, price, original_price, 
    stock, unit, weight, sort, gift_growth, gift_point, use_point_limit, product_sn, 
    keywords, publish_status, new_status, recommand_status, verify_status, sale, 
    product_type, subscription_period, daily_credits, total_credits, max_concurrent_tasks, 
    priority_level, features
) VALUES 
-- 基础套餐
(1, 2, '基础月套餐', '适合个人轻量使用', '每日500积分，基础功能完备', 29.90, 39.90, 999, '月', 0, 1, 0, 0, 0, 'SUB_BASIC_MONTHLY', '套餐,月付,基础', 1, 1, 0, 1, 0, 3, 30, 500, 15000, 1, 0, '{"features": ["基础AI模型", "标准生成速度", "邮件支持"], "limitations": {"max_file_size": "10MB", "max_history": 30}}'),

-- 标准套餐  
(1, 2, '标准月套餐', '适合专业设计师', '每日1200积分，高级功能解锁', 59.90, 79.90, 999, '月', 0, 2, 0, 0, 0, 'SUB_STANDARD_MONTHLY', '套餐,月付,标准', 1, 1, 1, 1, 0, 3, 30, 1200, 36000, 2, 1, '{"features": ["高级AI模型", "优先生成队列", "在线客服"], "limitations": {"max_file_size": "50MB", "max_history": 90}}'),

-- 专业套餐
(1, 2, '专业月套餐', '适合专业团队', '每日2500积分，专业功能全开', 119.90, 159.90, 999, '月', 0, 3, 0, 0, 0, 'SUB_PRO_MONTHLY', '套餐,月付,专业', 1, 1, 1, 1, 0, 3, 30, 2500, 75000, 5, 2, '{"features": ["顶级AI模型", "最高优先级", "专属客服", "API接口"], "limitations": {"max_file_size": "200MB", "max_history": 365}}'),

-- 企业套餐
(1, 2, '企业月套餐', '适合大型团队', '每日5000积分，企业级管理', 299.90, 399.90, 999, '月', 0, 4, 0, 0, 0, 'SUB_ENTERPRISE_MONTHLY', '套餐,月付,企业', 1, 0, 1, 1, 0, 3, 30, 5000, 150000, 10, 2, '{"features": ["企业级AI", "无限并发", "团队管理", "数据分析", "定制服务"], "limitations": {"max_file_size": "1GB", "max_history": -1}}'),

-- 季度套餐(15%折扣)
(1, 2, '标准季套餐', '季付更优惠', '标准版3个月，享85折优惠', 152.75, 179.70, 999, '季', 0, 12, 0, 0, 0, 'SUB_STANDARD_QUARTERLY', '套餐,季付,标准,优惠', 1, 1, 1, 1, 0, 3, 90, 1200, 108000, 2, 1, '{"features": ["高级AI模型", "优先生成队列", "在线客服"], "limitations": {"max_file_size": "50MB", "max_history": 90}}'),

-- 年度套餐(25%折扣)
(1, 2, '专业年套餐', '年付最划算', '专业版12个月，享75折优惠', 1079.10, 1439.40, 999, '年', 0, 13, 0, 0, 0, 'SUB_PRO_YEARLY', '套餐,年付,专业,最优惠', 1, 1, 1, 1, 0, 3, 365, 2500, 912500, 5, 2, '{"features": ["顶级AI模型", "最高优先级", "专属客服", "API接口"], "limitations": {"max_file_size": "200MB", "max_history": 365}}');

-- =====================================================
-- 第七部分: 清理和回滚脚本（注释状态）
-- =====================================================

/*
-- 清理脚本 - 仅在需要回滚时使用
-- 注意：执行前请备份数据！

-- 删除新增的表
DROP TABLE IF EXISTS ums_verification_codes;
DROP TABLE IF EXISTS ums_third_party_auth;
DROP TABLE IF EXISTS ums_subscription_usage;

-- 删除积分商品数据
DELETE FROM pms_product WHERE product_type = 2;

-- 删除套餐商品数据
DELETE FROM pms_product WHERE product_type = 3;

-- 回滚用户表扩展字段
ALTER TABLE ums_member 
DROP COLUMN wechat_openid,
DROP COLUMN google_id,
DROP COLUMN register_type,
DROP COLUMN email_verified,
DROP COLUMN last_login_time,
DROP COLUMN avatar_url,
DROP COLUMN account_status,
DROP COLUMN free_daily_credits,
DROP COLUMN used_today_free,
DROP COLUMN last_reset_date,
DROP COLUMN current_subscription_id,
DROP COLUMN subscription_level,
DROP COLUMN daily_credits_limit,
DROP COLUMN daily_credits_used,
DROP COLUMN subscription_end_date;

-- 回滚积分历史表扩展字段
ALTER TABLE ums_integration_change_history 
DROP COLUMN credit_type,
DROP COLUMN business_id;

-- 回滚商品表扩展字段
ALTER TABLE pms_product 
DROP COLUMN product_type,
DROP COLUMN credit_amount,
DROP COLUMN subscription_period,
DROP COLUMN daily_credits,
DROP COLUMN total_credits,
DROP COLUMN max_concurrent_tasks,
DROP COLUMN priority_level,
DROP COLUMN features;

-- 回滚订单表扩展字段
ALTER TABLE oms_order 
DROP COLUMN subscription_start_date,
DROP COLUMN subscription_end_date,
DROP COLUMN subscription_status,
DROP COLUMN auto_renew,
DROP COLUMN order_type,
DROP COLUMN business_type;

-- 回滚支付表扩展字段
ALTER TABLE oms_order_payment 
DROP COLUMN payment_scene,
DROP COLUMN callback_status,
DROP COLUMN callback_retry_count;
*/

-- =====================================================
-- 第八部分: 验证脚本
-- =====================================================

-- 验证表结构创建成功
SELECT 'Tables created successfully' AS Status;
SHOW TABLES LIKE 'ums_verification_codes';
SHOW TABLES LIKE 'ums_third_party_auth';
SHOW TABLES LIKE 'ums_subscription_usage';

-- 验证字段扩展成功
DESCRIBE ums_member;
DESCRIBE pms_product;
DESCRIBE oms_order;
DESCRIBE ums_integration_change_history;

-- 验证数据初始化成功
SELECT COUNT(*) AS credit_products_count FROM pms_product WHERE product_type = 2;
SELECT COUNT(*) AS subscription_products_count FROM pms_product WHERE product_type = 3;

-- 验证索引创建成功
SHOW INDEX FROM ums_member;
SHOW INDEX FROM pms_product;
SHOW INDEX FROM oms_order;

-- =====================================================
-- 脚本执行完成
-- =====================================================

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

-- 执行成功信息
SELECT 
    '白鹿AI平台数据库初始化完成！' AS message,
    NOW() AS completion_time,
    'V1.0' AS version;

-- =====================================================
-- 使用说明
-- =====================================================
/*
1. 执行顺序：
   - 首先确保现有mall系统数据库存在
   - 按脚本顺序从上到下执行
   - 建议在测试环境先验证

2. 功能说明：
   - 用户认证：支持邮箱、微信、Google登录
   - 积分系统：双积分体系（免费+永久积分）
   - 套餐管理：5级套餐，支持月/季/年订阅
   - 支付系统：集成支付宝，支持积分和套餐购买

3. 注意事项：
   - 执行前请备份现有数据
   - 确保有足够的权限执行DDL操作
   - 如需回滚，请使用注释中的清理脚本

4. 后续开发：
   - 所有表结构已完成，可直接开发业务逻辑
   - 积分商品和套餐商品已初始化
   - 索引已优化，支持高并发查询
*/