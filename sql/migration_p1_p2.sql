-- ============================================================
-- 运营管理功能迁移脚本 (P1 + P2)
-- 执行前请确认已备份数据库
-- ============================================================

-- 1. admin_operation_logs - 操作日志表
CREATE TABLE IF NOT EXISTS `admin_operation_logs` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `admin_id`      BIGINT       NOT NULL COMMENT '操作管理员ID',
    `admin_name`    VARCHAR(64)  DEFAULT NULL COMMENT '管理员名称',
    `operation`     VARCHAR(64)  NOT NULL COMMENT '操作类型: APPROVE/INBOUND/OUTBOUND/DELETE_USER/PUBLISH_NEWS/EDIT_NEWS/DELETE_NEWS/HIDE_COMMENT/CREATE_COURIER/CREATE_STATION等',
    `target_type`   VARCHAR(32)  DEFAULT NULL COMMENT '目标类型: PACKAGE/USER/NEWS/COMMENT/COURIER/STATION',
    `target_id`     BIGINT       DEFAULT NULL COMMENT '目标ID',
    `detail`        VARCHAR(512) DEFAULT NULL COMMENT '操作详情',
    `ip`            VARCHAR(45)  DEFAULT NULL COMMENT '操作IP',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    KEY `idx_admin_id` (`admin_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_operation` (`operation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员操作日志表';

-- 2. splash_ads - 开屏广告表（initial.sql 中缺失）
CREATE TABLE IF NOT EXISTS `splash_ads` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '广告主键',
    `ad_no`         VARCHAR(32)  NOT NULL COMMENT '广告编号',
    `name`          VARCHAR(64)  NOT NULL COMMENT '广告名称',
    `image_url`     VARCHAR(512) NOT NULL COMMENT '图片URL',
    `target_url`    VARCHAR(512) DEFAULT NULL COMMENT '点击跳转URL',
    `weight`        INT          DEFAULT 10 COMMENT '权重（越大越常展示）',
    `status`        TINYINT      DEFAULT 1 COMMENT '状态：0=下线，1=上线',
    `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ad_no` (`ad_no`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开屏广告表';

-- 3. 为 news_posts 添加 view_count 字段（如果不存在）
-- MySQL 8.0 不支持 ADD COLUMN IF NOT EXISTS，用存储过程代替
DELIMITER //
DROP PROCEDURE IF EXISTS add_view_count_column//
CREATE PROCEDURE add_view_count_column()
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'news_posts' AND column_name = 'view_count') THEN
        ALTER TABLE `news_posts` ADD COLUMN `view_count` INT DEFAULT 0 COMMENT '浏览数' AFTER `likes`;
    END IF;
END//
DELIMITER ;
CALL add_view_count_column();
DROP PROCEDURE IF EXISTS add_view_count_column;
