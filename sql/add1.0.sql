-- 切换数据库
USE `XYJ`;

-- ----------------------------
-- Table: email_codes
-- ----------------------------
DROP TABLE IF EXISTS `email_codes`;
CREATE TABLE `email_codes` (
                               `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                               `email`         VARCHAR(128) NOT NULL COMMENT '邮箱地址',
                               `code`          VARCHAR(10)  NOT NULL COMMENT '验证码',
                               `used`          TINYINT      DEFAULT 0 COMMENT '是否已使用：0=未使用，1=已使用',
                               `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `expire_time`   DATETIME     NOT NULL COMMENT '过期时间',
                               PRIMARY KEY (`id`),
                               KEY `idx_email_code` (`email`, `code`),
                               KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邮箱验证码记录表';