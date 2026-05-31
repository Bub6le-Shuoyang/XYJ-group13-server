USE `XYJ`;

-- ----------------------------
-- Table: splash_ads
-- ----------------------------
DROP TABLE IF EXISTS `splash_ads`;
CREATE TABLE `splash_ads` (
                              `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '广告主键',
                              `ad_no`       VARCHAR(32)  NOT NULL COMMENT '广告编号',
                              `name`        VARCHAR(64)  NOT NULL COMMENT '广告名称',
                              `image_url`   VARCHAR(512) NOT NULL COMMENT '广告图片链接',
                              `target_url`  VARCHAR(512) DEFAULT NULL COMMENT '点击跳转的H5或路由链接',
                              `weight`      INT          DEFAULT 1 COMMENT '权重，数字越大随机到的概率越高',
                              `status`      TINYINT      DEFAULT 1 COMMENT '状态：0=下线，1=上线',
                              `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_ad_no` (`ad_no`),
                              KEY `idx_status_weight` (`status`, `weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开屏广告表';

-- 插入一些初始开屏广告测试数据
INSERT INTO `splash_ads` (`ad_no`, `name`, `image_url`, `target_url`, `weight`, `status`) VALUES
('AD_SPLASH_001', '乡驿家助农大促', '/admin/picture/ad_splash_1.png', 'https://xiangyijia.com/promo1', 50, 1),
('AD_SPLASH_002', '寄件立减优惠', '/admin/picture/ad_splash_2.png', 'https://xiangyijia.com/promo2', 30, 1),
('AD_SPLASH_003', '积分商城上新', '/admin/picture/ad_splash_3.png', 'https://xiangyijia.com/mall', 20, 1);