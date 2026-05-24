-- ========================================================
-- 乡村驿站 / 末端配送系统 数据库建表语句
-- MySQL 8.0+ | utf8mb4_unicode_ci
-- 自动生成时间: 2026-05-24
-- 说明：
--   1. 自动创建数据库（若不存在）
--   2. 每张表先 DROP IF EXISTS 再 CREATE，确保可重复执行
--   3. 开头关闭外键检查，避免表之间顺序依赖导致报错
-- ========================================================

SET NAMES utf8mb4;

-- 创建数据库（幂等：不存在才创建）
CREATE DATABASE IF NOT EXISTS `XYJ`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 切换数据库
USE `XYJ`;

-- 关闭外键检查，防止删表/建表时因外键顺序报错
SET FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table: admins
-- ----------------------------
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins` (
                          `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '管理员主键',
                          `username`        VARCHAR(64)   NOT NULL COMMENT '登录用户名',
                          `password_hash`   VARCHAR(256)  NOT NULL COMMENT '密码哈希值（BCrypt等加密存储）',
                          `real_name`       VARCHAR(64)   DEFAULT NULL COMMENT '真实姓名',
                          `avatar_url`      VARCHAR(512)  DEFAULT NULL COMMENT '头像URL路径',
                          `email`           VARCHAR(128)  DEFAULT NULL COMMENT '邮箱（可作为登录凭证）',
                          `phone`           VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
                          `role`            TINYINT       DEFAULT 1 COMMENT '后台权限：1=普通管理员，2=高级管理员，3=超级管理员',
                          `station_id`      BIGINT        DEFAULT NULL COMMENT '所属驿站ID，站点管理员必填',
                          `status`          TINYINT       DEFAULT 1 COMMENT '状态：0=禁用，1=正常',
                          `last_login_time` DATETIME      DEFAULT NULL COMMENT '最后登录时间',
                          `last_login_ip`   VARCHAR(45)   DEFAULT NULL COMMENT '最后登录IP（支持IPv6长度）',
                          `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `updated_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `uk_username` (`username`),
                          UNIQUE KEY `uk_email` (`email`),
                          KEY `idx_station_id` (`station_id`),
                          KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员账户表';


-- ----------------------------
-- Table: users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
                         `id`              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '用户主键',
                         `user_no`         VARCHAR(32)      DEFAULT NULL            COMMENT '用户编号（格式 U+时间戳，如 U20260406001）',
                         `phone`           VARCHAR(20)      DEFAULT NULL            COMMENT '手机号',
                         `email`           VARCHAR(128)     DEFAULT NULL            COMMENT '邮箱（唯一性校验在 Service 层做）',
                         `password_hash`   VARCHAR(256)     DEFAULT NULL            COMMENT '密码哈希值（可为空，支持验证码免密登录场景）',
                         `nickname`        VARCHAR(64)      DEFAULT NULL            COMMENT '昵称（注册时自动取 email @ 前缀部分）',
                         `avatar_url`      VARCHAR(512)     DEFAULT NULL            COMMENT '头像URL路径',
                         `signature`       VARCHAR(256)     DEFAULT NULL            COMMENT '个性签名',
                         `tags`            VARCHAR(256)     DEFAULT NULL            COMMENT '用户标签（逗号分隔存储）',
                         `gender`          TINYINT         DEFAULT 0               COMMENT '性别：0=未知，1=男，2=女',
                         `birthday`        DATE             DEFAULT NULL            COMMENT '出生日期',
                         `status`          TINYINT         DEFAULT 1               COMMENT '账户状态：0=禁用，1=正常，2=冻结',
                         `last_login_time` DATETIME         DEFAULT NULL            COMMENT '最后登录时间',
                         `last_login_ip`   VARCHAR(45)      DEFAULT NULL            COMMENT '最后登录IP',
                         `device_id`       VARCHAR(128)     DEFAULT NULL            COMMENT '设备标识（风控/多端登录判断用）',
                         `register_source` TINYINT          DEFAULT NULL            COMMENT '注册来源：1=手机验证码，2=第三方登录',
                         `register_ip`     VARCHAR(45)      DEFAULT NULL            COMMENT '注册时的IP地址',
                         `created_at`      DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `updated_at`      DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新新时间',
                         `deleted_at`      DATETIME          DEFAULT NULL            COMMENT '软删除时间（逻辑删除标记，非NULL表示已删除）',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_user_no` (`user_no`),
                         UNIQUE KEY `uk_email` (`email`),
                         KEY `idx_phone` (`phone`),
                         KEY `idx_status` (`status`),
                         KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户主表';


-- ----------------------------
-- Table: couriers
-- ----------------------------
DROP TABLE IF EXISTS `couriers`;
CREATE TABLE `couriers` (
                            `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '骑手主键',
                            `courier_no`      VARCHAR(32)   NOT NULL COMMENT '骑手编号，如 COURIER-013',
                            `account`         VARCHAR(64)   NOT NULL COMMENT '登录账号',
                            `password_hash`   VARCHAR(256)  NOT NULL COMMENT '密码哈希值',
                            `name`            VARCHAR(64)   NOT NULL COMMENT '骑手姓名',
                            `phone`           VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
                            `avatar_url`      VARCHAR(512)  DEFAULT NULL COMMENT '头像URL',
                            `station_id`      BIGINT        DEFAULT NULL COMMENT '服务驿站ID',
                            `level_name`      VARCHAR(64)   DEFAULT '普通配送员 Lv.1' COMMENT '骑手等级名称',
                            `level_progress`  DECIMAL(5,2)  DEFAULT 0 COMMENT '等级进度，范围 0-1',
                            `monthly_rank`    INT           DEFAULT 0 COMMENT '本月排名',
                            `status`          TINYINT       DEFAULT 1 COMMENT '状态：0=禁用，1=正常',
                            `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_courier_no` (`courier_no`),
                            UNIQUE KEY `uk_account` (`account`),
                            KEY `idx_station_id` (`station_id`),
                            KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='骑手账户表';


-- ----------------------------
-- Table: stations
-- ----------------------------
DROP TABLE IF EXISTS `stations`;
CREATE TABLE `stations` (
                            `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '驿站主键',
                            `station_no`     VARCHAR(32)   NOT NULL COMMENT '驿站编号，如 ST-001',
                            `name`           VARCHAR(64)   NOT NULL COMMENT '驿站名称',
                            `address`        VARCHAR(255)  NOT NULL COMMENT '驿站地址',
                            `lat`            DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
                            `lng`            DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
                            `phone`          VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
                            `opening_hours`  VARCHAR(64)   DEFAULT NULL COMMENT '营业时间',
                            `status`         TINYINT       DEFAULT 1 COMMENT '状态：0=停用，1=启用',
                            `created_at`     DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at`     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `deleted_at`     DATETIME      DEFAULT NULL COMMENT '软删除时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_station_no` (`station_no`),
                            KEY `idx_location` (`lat`, `lng`),
                            KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='驿站表';


-- ----------------------------
-- Table: packages
-- ----------------------------
DROP TABLE IF EXISTS `packages`;
CREATE TABLE `packages` (
                            `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '包裹主键',
                            `package_no`        VARCHAR(32)   NOT NULL COMMENT '包裹业务编号，如 PKG-001',
                            `pickup_code`       VARCHAR(16)   NOT NULL COMMENT '取件码，由骑手上门核验',
                            `name`              VARCHAR(128)  NOT NULL COMMENT '包裹名称',
                            `sender_name`       VARCHAR(64)   DEFAULT NULL COMMENT '来源方，如县医院/县城商超',
                            `receiver_user_id`  BIGINT        DEFAULT NULL COMMENT '收件用户ID，关联 users.id',
                            `receiver_name`     VARCHAR(64)   NOT NULL COMMENT '收件人',
                            `receiver_phone`    VARCHAR(20)   DEFAULT NULL COMMENT '收件电话',
                            `address`           VARCHAR(255)  NOT NULL COMMENT '配送地址',
                            `weight`            DECIMAL(10,2) DEFAULT 0 COMMENT '包裹重量',
                            `estimated_fee`     DECIMAL(10,2) DEFAULT 0 COMMENT '预估费用，收件业务可为0',
                            `reward_amount`     DECIMAL(10,2) DEFAULT 0 COMMENT '骑手配送奖励',
                            `status`            VARCHAR(32)   NOT NULL COMMENT 'PENDING_INBOUND/IN_STOCK/TASK_PUBLISHED/ASSIGNED/COMPLETED',
                            `station_id`        BIGINT        DEFAULT NULL COMMENT '当前驿站ID',
                            `courier_id`        BIGINT        DEFAULT NULL COMMENT '当前骑手ID',
                            `lat`               DECIMAL(10,6) DEFAULT NULL COMMENT '配送纬度',
                            `lng`               DECIMAL(10,6) DEFAULT NULL COMMENT '配送经度',
                            `created_at`        DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `updated_at`        DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_package_no` (`package_no`),
                            UNIQUE KEY `uk_pickup_code` (`pickup_code`),
                            KEY `idx_receiver_user_id` (`receiver_user_id`),
                            KEY `idx_station_id` (`station_id`),
                            KEY `idx_courier_id` (`courier_id`),
                            KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收件包裹表';


-- ----------------------------
-- Table: package_timelines
-- ----------------------------
DROP TABLE IF EXISTS `package_timelines`;
CREATE TABLE `package_timelines` (
                                     `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '流转记录主键',
                                     `package_id`  BIGINT       NOT NULL COMMENT '关联 packages.id',
                                     `status`      VARCHAR(32)  DEFAULT NULL COMMENT '记录产生时的包裹状态',
                                     `content`     VARCHAR(255) NOT NULL COMMENT '流转文案',
                                     `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (`id`),
                                     KEY `idx_package_id_created_at` (`package_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='包裹流转记录表';


-- ----------------------------
-- Table: delivery_tasks
-- ----------------------------
DROP TABLE IF EXISTS `delivery_tasks`;
CREATE TABLE `delivery_tasks` (
                                  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '任务主键',
                                  `task_no`         VARCHAR(32)   NOT NULL COMMENT '任务编号，如 TASK-001',
                                  `package_id`      BIGINT        NOT NULL COMMENT '关联 packages.id',
                                  `station_id`      BIGINT        NOT NULL COMMENT '发布任务的驿站ID',
                                  `courier_id`      BIGINT        DEFAULT NULL COMMENT '抢单骑手ID',
                                  `pickup_address`  VARCHAR(255)  NOT NULL COMMENT '取件地址，通常为驿站地址',
                                  `deliver_address` VARCHAR(255)  NOT NULL COMMENT '送达地址',
                                  `reward_amount`   DECIMAL(10,2) DEFAULT 0 COMMENT '配送奖励',
                                  `status`          VARCHAR(32)   NOT NULL COMMENT 'AVAILABLE/ASSIGNED/DELIVERING/COMPLETED',
                                  `deliver_image`   VARCHAR(512)  DEFAULT NULL COMMENT '送达凭证图片',
                                  `remark`          VARCHAR(255)  DEFAULT NULL COMMENT '备注',
                                  `grabbed_at`      DATETIME      DEFAULT NULL COMMENT '抢单时间',
                                  `completed_at`    DATETIME      DEFAULT NULL COMMENT '完成时间',
                                  `created_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updated_at`      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  UNIQUE KEY `uk_task_no` (`task_no`),
                                  UNIQUE KEY `uk_package_id` (`package_id`),
                                  KEY `idx_station_status` (`station_id`, `status`),
                                  KEY `idx_courier_status` (`courier_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配送任务表';


-- ----------------------------
-- Table: courier_earnings
-- ----------------------------
DROP TABLE IF EXISTS `courier_earnings`;
CREATE TABLE `courier_earnings` (
                                    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '收益流水主键',
                                    `courier_id`  BIGINT        NOT NULL COMMENT '关联 couriers.id',
                                    `task_id`     BIGINT        NOT NULL COMMENT '关联 delivery_tasks.id',
                                    `amount`      DECIMAL(10,2) NOT NULL COMMENT '收益金额',
                                    `type`        VARCHAR(32)   DEFAULT 'DELIVERY_REWARD' COMMENT '收益类型',
                                    `status`      VARCHAR(32)   DEFAULT 'SETTLED' COMMENT 'SETTLED/PENDING/CANCELLED',
                                    `title`       VARCHAR(128)  DEFAULT NULL COMMENT '流水标题',
                                    `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_courier_created_at` (`courier_id`, `created_at`),
                                    KEY `idx_task_id` (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='骑手收益流水表';


-- ----------------------------
-- Table: user_addresses
-- ----------------------------
DROP TABLE IF EXISTS `user_addresses`;
CREATE TABLE `user_addresses` (
                                  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '地址主键',
                                  `user_id`     BIGINT       NOT NULL COMMENT '关联 users.id',
                                  `name`        VARCHAR(64)  NOT NULL COMMENT '联系人',
                                  `phone`       VARCHAR(20)  NOT NULL COMMENT '联系电话',
                                  `address`     VARCHAR(255) NOT NULL COMMENT '详细地址',
                                  `lat`         DECIMAL(10,6) DEFAULT NULL COMMENT '纬度',
                                  `lng`         DECIMAL(10,6) DEFAULT NULL COMMENT '经度',
                                  `is_default`  TINYINT      DEFAULT 0 COMMENT '是否默认地址：0=否，1=是',
                                  `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (`id`),
                                  KEY `idx_user_id` (`user_id`),
                                  KEY `idx_default` (`user_id`, `is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户常用地址表';


-- ----------------------------
-- Table: user_points_accounts
-- ----------------------------
DROP TABLE IF EXISTS `user_points_accounts`;
CREATE TABLE `user_points_accounts` (
                                        `id`                    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '积分账户主键',
                                        `user_id`               BIGINT        NOT NULL COMMENT '关联 users.id',
                                        `points`                INT           DEFAULT 0 COMMENT '可用积分',
                                        `coupon_count`          INT           DEFAULT 0 COMMENT '可用优惠券数量',
                                        `balance`               DECIMAL(10,2) DEFAULT 0 COMMENT '零钱余额',
                                        `member_level`          VARCHAR(32)   DEFAULT '普通村民' COMMENT '会员等级',
                                        `monthly_signed_count`  INT           DEFAULT 0 COMMENT '本月收件签收次数',
                                        `updated_at`            DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分账户表';


-- ----------------------------
-- Table: user_coupons
-- ----------------------------
DROP TABLE IF EXISTS `user_coupons`;
CREATE TABLE `user_coupons` (
                                `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '优惠券主键',
                                `coupon_no`   VARCHAR(32)   NOT NULL COMMENT '优惠券编号',
                                `user_id`     BIGINT        NOT NULL COMMENT '关联 users.id',
                                `name`        VARCHAR(64)   NOT NULL COMMENT '优惠券名称',
                                `amount`      DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
                                `status`      VARCHAR(32)   DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE/USED/EXPIRED',
                                `source`      VARCHAR(32)   DEFAULT NULL COMMENT '来源：兑换/活动/补偿',
                                `expire_time` DATETIME      DEFAULT NULL COMMENT '过期时间',
                                `used_at`     DATETIME      DEFAULT NULL COMMENT '使用时间',
                                `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_coupon_no` (`coupon_no`),
                                KEY `idx_user_status` (`user_id`, `status`),
                                KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户优惠券表';


-- ----------------------------
-- Table: wallet_transactions
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transactions`;
CREATE TABLE `wallet_transactions` (
                                       `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '钱包流水主键',
                                       `user_id`     BIGINT        NOT NULL COMMENT '关联 users.id',
                                       `type`        VARCHAR(32)   NOT NULL COMMENT '流水类型：REFUND/REWARD/CONSUME/ADJUST',
                                       `amount`      DECIMAL(10,2) NOT NULL COMMENT '流水金额，收入为正，支出为负',
                                       `title`       VARCHAR(128)  NOT NULL COMMENT '流水标题',
                                       `biz_type`    VARCHAR(32)   DEFAULT NULL COMMENT '关联业务类型',
                                       `biz_id`      VARCHAR(64)   DEFAULT NULL COMMENT '关联业务ID',
                                       `created_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updated_at`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       KEY `idx_user_created_at` (`user_id`, `created_at`),
                                       KEY `idx_biz` (`biz_type`, `biz_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包流水表';


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
                              `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_item_no` (`item_no`),
                              KEY `idx_status_type` (`status`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商城权益表';


-- ----------------------------
-- Table: mall_redeem_records
-- ----------------------------
DROP TABLE IF EXISTS `mall_redeem_records`;
CREATE TABLE `mall_redeem_records` (
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


-- ----------------------------
-- Table: news_posts
-- ----------------------------
DROP TABLE IF EXISTS `news_posts`;
CREATE TABLE `news_posts` (
                              `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '资讯主键',
                              `post_no`     VARCHAR(32)  NOT NULL COMMENT '资讯编号，如 NEWS-001',
                              `title`       VARCHAR(128) NOT NULL COMMENT '标题',
                              `content`     TEXT         NOT NULL COMMENT '内容',
                              `tag`         VARCHAR(32)  DEFAULT NULL COMMENT '标签',
                              `author_id`   BIGINT       DEFAULT NULL COMMENT '发布人ID，可关联 users/admins',
                              `author_type` VARCHAR(16)  DEFAULT 'ADMIN' COMMENT '发布人类型：USER/ADMIN',
                              `station_id`  BIGINT       DEFAULT NULL COMMENT '关联驿站ID',
                              `likes`       INT          DEFAULT 0 COMMENT '点赞数',
                              `is_urgent`   TINYINT      DEFAULT 0 COMMENT '是否紧急通知',
                              `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_post_no` (`post_no`),
                              KEY `idx_station_id` (`station_id`),
                              KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='乡镇资讯表';


-- ----------------------------
-- Table: news_comments
-- ----------------------------
DROP TABLE IF EXISTS `news_comments`;
CREATE TABLE `news_comments` (
                                 `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评论主键',
                                 `post_id`     BIGINT       NOT NULL COMMENT '关联 news_posts.id',
                                 `user_id`     BIGINT       NOT NULL COMMENT '评论用户ID',
                                 `content`     VARCHAR(255) NOT NULL COMMENT '评论内容',
                                 `status`      TINYINT      DEFAULT 1 COMMENT '状态：0=隐藏，1=正常',
                                 `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `updated_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_post_created_at` (`post_id`, `created_at`),
                                 KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯评论表';


-- ----------------------------
-- Table: help_items
-- ----------------------------
DROP TABLE IF EXISTS `help_items`;
CREATE TABLE `help_items` (
                              `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '帮助条目主键',
                              `help_no`     VARCHAR(32)  NOT NULL COMMENT '帮助条目编号',
                              `title`       VARCHAR(128) NOT NULL COMMENT '标题',
                              `content`     TEXT         NOT NULL COMMENT '内容',
                              `sort_order`  INT          DEFAULT 0 COMMENT '排序值，越小越靠前',
                              `status`      TINYINT      DEFAULT 1 COMMENT '状态：0=下线，1=上线',
                              `created_at`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_help_no` (`help_no`),
                              KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帮助中心表';


-- ----------------------------
-- Table: customer_service_configs
-- ----------------------------
DROP TABLE IF EXISTS `customer_service_configs`;
CREATE TABLE `customer_service_configs` (
                                            `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置主键',
                                            `phone`         VARCHAR(20)  DEFAULT NULL COMMENT '客服电话',
                                            `online_time`   VARCHAR(64)  DEFAULT NULL COMMENT '在线时间',
                                            `wechat`        VARCHAR(64)  DEFAULT NULL COMMENT '客服微信',
                                            `status`        TINYINT      DEFAULT 1 COMMENT '状态：0=停用，1=启用',
                                            `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updated_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            PRIMARY KEY (`id`),
                                            KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服配置表';


-- ----------------------------
-- Table: upload_files
-- ----------------------------
DROP TABLE IF EXISTS `upload_files`;
CREATE TABLE `upload_files` (
                                `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件主键',
                                `file_no`       VARCHAR(32)  NOT NULL COMMENT '文件编号',
                                `url`           VARCHAR(512) NOT NULL COMMENT '文件访问路径',
                                `name`          VARCHAR(255) NOT NULL COMMENT '原始文件名',
                                `size`          BIGINT       DEFAULT 0 COMMENT '文件大小，单位字节',
                                `content_type`  VARCHAR(128) DEFAULT NULL COMMENT '文件MIME类型',
                                `scene`         VARCHAR(32)  DEFAULT NULL COMMENT '上传场景：AVATAR/COMPLAIN/DELIVER_PROOF',
                                `uploader_id`   BIGINT       DEFAULT NULL COMMENT '上传人ID',
                                `created_at`    DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `uk_file_no` (`file_no`),
                                KEY `idx_scene` (`scene`),
                                KEY `idx_uploader_id` (`uploader_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;