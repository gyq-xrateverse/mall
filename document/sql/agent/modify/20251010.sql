ALTER TABLE ai_project ADD COLUMN uuid VARCHAR(36) NOT NULL UNIQUE COMMENT '项目UUID,用于外部访问';
ALTER TABLE ai_project ADD INDEX idx_uuid (uuid);
