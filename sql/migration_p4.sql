CREATE TABLE IF NOT EXISTS `admin_notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知主键',
    `title` VARCHAR(128) NOT NULL COMMENT '通知标题',
    `content` VARCHAR(512) DEFAULT NULL COMMENT '通知内容',
    `type` VARCHAR(32) DEFAULT 'INFO' COMMENT '类型: NEW_PACKAGE/DELIVERY_COMPLETED/SYSTEM',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0=未读, 1=已读',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员通知表';
