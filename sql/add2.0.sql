USE `XYJ`;

-- ----------------------------
-- Table: mall_items
-- ----------------------------
DROP TABLE IF EXISTS `mall_items`;
CREATE TABLE `mall_items` (
                              `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '权益主键',
                              `item_no`     VARCHAR(32)  NOT NULL COMMENT '权益编号',
                              `name`        VARCHAR(64)  NOT NULL COMMENT '权益名称',
                              `description` VARCHAR(255) DEFAULT NULL COMMENT '权益说明',
                              `points`      INT          NOT NULL COMMENT '兑换所需积分',
                              `type`        VARCHAR(16)  NOT NULL COMMENT 'coupon/goods',
                              `stock`       INT          DEFAULT 0 COMMENT '库存',
                              `status`      TINYINT      DEFAULT 1 COMMENT '0=下架，1=上架',
                              `image_url`   VARCHAR(512) DEFAULT NULL COMMENT '展示图片链接',
                              `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_item_no` (`item_no`),
                              KEY `idx_status_type` (`status`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商城权益表';

-- 插入 10 条商品数据
INSERT INTO `mall_items` (`item_no`, `name`, `description`, `points`, `type`, `stock`, `status`, `image_url`) VALUES
('ITEM_001', '小锅', '精致家用小锅，适合煮面热奶', 500, 'goods', 100, 1, '/admin/picture/小锅.png'),
('ITEM_002', '布袋', '环保帆布袋，结实耐用', 150, 'goods', 200, 1, '/admin/picture/布袋.png'),
('ITEM_003', '按摩锤', '家用舒适按摩锤，缓解疲劳', 200, 'goods', 150, 1, '/admin/picture/按摩锤.png'),
('ITEM_004', '牛奶', '营养纯牛奶一盒', 100, 'goods', 500, 1, '/admin/picture/牛奶.png'),
('ITEM_005', '白菜', '新鲜大白菜一棵', 50, 'goods', 300, 1, '/admin/picture/白菜.jpg'),
('ITEM_006', '豆腐', '鲜嫩手工豆腐一块', 60, 'goods', 200, 1, '/admin/picture/豆腐.png'),
('ITEM_007', '雨伞', '抗风防雨结实长柄伞', 400, 'goods', 100, 1, '/admin/picture/雨伞.png'),
('ITEM_008', '面包', '香甜软糯大面包', 80, 'goods', 250, 1, '/admin/picture/面包.png'),
('ITEM_009', '风扇', '便携式小风扇，夏日清凉', 800, 'goods', 50, 1, '/admin/picture/风扇.jpg'),
('ITEM_010', '鸡蛋', '新鲜农家土鸡蛋一盒', 120, 'goods', 400, 1, '/admin/picture/鸡蛋.jpg');

-- 确保兑换记录表存在
CREATE TABLE IF NOT EXISTS `mall_redeem_records` (
                                       `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '兑换记录主键',
                                       `record_no`      VARCHAR(32)  NOT NULL COMMENT '兑换记录编号',
                                       `user_id`        BIGINT       NOT NULL COMMENT '关联 users.id',
                                       `item_id`        BIGINT       NOT NULL COMMENT '关联 mall_items.id',
                                       `item_name`      VARCHAR(64)  NOT NULL COMMENT '兑换时的权益名称快照',
                                       `points_cost`    INT          NOT NULL COMMENT '消耗积分',
                                       `remain_points`  INT          NOT NULL COMMENT '兑换后剩余积分',
                                       `status`         VARCHAR(32)  DEFAULT 'SUCCESS' COMMENT 'SUCCESS/CANCELLED',
                                       `created_at`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updated_at`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_record_no` (`record_no`),
                                       KEY `idx_user_created_at` (`user_id`, `created_at`),
                                       KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分兑换记录表';
