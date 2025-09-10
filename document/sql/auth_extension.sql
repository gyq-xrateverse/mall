-- 认证系统数据库扩展脚本
-- 执行时间: 2025-09-10

-- 1. 扩展现有ums_member表，添加认证相关字段
ALTER TABLE ums_member ADD COLUMN email VARCHAR(128) UNIQUE COMMENT '邮箱地址';
ALTER TABLE ums_member ADD COLUMN wechat_openid VARCHAR(64) UNIQUE COMMENT '微信OpenID';
ALTER TABLE ums_member ADD COLUMN google_id VARCHAR(128) UNIQUE COMMENT 'Google用户ID';
ALTER TABLE ums_member ADD COLUMN register_type TINYINT DEFAULT 1 COMMENT '注册方式: 1-邮箱, 2-微信, 3-谷歌';
ALTER TABLE ums_member ADD COLUMN email_verified TINYINT DEFAULT 0 COMMENT '邮箱是否验证: 0-未验证, 1-已验证';
ALTER TABLE ums_member ADD COLUMN last_login_time DATETIME COMMENT '最后登录时间';
ALTER TABLE ums_member ADD COLUMN avatar_url VARCHAR(512) COMMENT '头像URL';
ALTER TABLE ums_member ADD COLUMN account_status TINYINT DEFAULT 1 COMMENT '账户状态: 1-正常, 2-冻结, 3-禁用';

-- 2. 创建验证码记录表
DROP TABLE IF EXISTS `ums_verification_codes`;
CREATE TABLE `ums_verification_codes` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `email` VARCHAR(128) NOT NULL COMMENT '邮箱地址',
    `code` VARCHAR(6) NOT NULL COMMENT '验证码',
    `code_type` TINYINT NOT NULL COMMENT '类型: 1-注册, 2-登录, 3-重置密码',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `used_status` TINYINT DEFAULT 0 COMMENT '是否已使用: 0-未使用, 1-已使用',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_email_type` (`email`, `code_type`),
    INDEX `idx_expire_time` (`expire_time`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '验证码记录表' ROW_FORMAT = DYNAMIC;

-- 3. 创建第三方登录记录表
DROP TABLE IF EXISTS `ums_third_party_auth`;
CREATE TABLE `ums_third_party_auth` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL COMMENT '用户ID',
    `provider` VARCHAR(32) NOT NULL COMMENT '第三方平台: wechat, google',
    `third_party_id` VARCHAR(128) NOT NULL COMMENT '第三方用户ID',
    `access_token` VARCHAR(512) COMMENT '访问令牌',
    `refresh_token` VARCHAR(512) COMMENT '刷新令牌',
    `expire_time` DATETIME COMMENT '令牌过期时间',
    `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_provider_third_id` (`provider`, `third_party_id`),
    INDEX `idx_member_id` (`member_id`)
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '第三方登录记录表' ROW_FORMAT = DYNAMIC;

-- 4. 添加邮箱索引到现有表
ALTER TABLE ums_member ADD INDEX `idx_email` (`email`);

-- 5. 数据库字符集优化 (如果需要)
-- ALTER TABLE ums_member CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE ums_verification_codes CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- ALTER TABLE ums_third_party_auth CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;