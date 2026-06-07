-- ========================================================
-- 乡村驿站 / 末端配送系统 一键初始化脚本（含本地演示数据）
-- 生成说明：合并建库建表、必要增量表、默认管理员、北京高校驿站、商城/广告和本地演示数据。
-- 执行方式：mysql -uroot -p < sql/initial_with_data.sql
-- 注意：initial.sql 会 DROP 并重建核心业务表，请勿在生产已有数据环境直接执行。
-- ========================================================


-- ========================================================
-- BEGIN sql/initial.sql
-- ========================================================
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
-- ========================================================
-- END sql/initial.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/add1.0.sql
-- ========================================================
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
-- ========================================================
-- END sql/add1.0.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/migration_p1_p2.sql
-- ========================================================
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

-- ========================================================
-- END sql/migration_p1_p2.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/migration_p4.sql
-- ========================================================
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

-- ========================================================
-- END sql/migration_p4.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/add_default_admin.sql
-- ========================================================
-- ========================================================
-- 内置默认管理员账号
-- 用户名: admin  密码: 123456
-- 如果 initial.sql 已执行，可单独执行此文件
-- ========================================================
USE `XYJ`;

-- 插入默认超级管理员（如果不存在）
INSERT INTO `admins` (`username`, `password_hash`, `real_name`, `email`, `phone`, `role`, `status`)
SELECT 'admin',
       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
       '超级管理员',
       'admin@xyj.com',
       '13800000000',
       3,
       1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `admins` WHERE `username` = 'admin');

-- ========================================================
-- END sql/add_default_admin.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/add_bjtu_stations.sql
-- ========================================================
USE XYJ;

INSERT INTO stations(station_no, name, address, lat, lng, phone, opening_hours, status, created_at, updated_at)
VALUES
('ST-BJTU-SOUTH', '北京交通大学南门驿站', '北京市海淀区上园村3号北京交通大学南门', 39.949900, 116.342000, '010-51688001', '08:00-22:00', 1, NOW(), NOW()),
('ST-BUPT-XITUCHENG', '北京邮电大学西土城校区驿站', '北京市海淀区西土城路10号北京邮电大学西门', 39.960700, 116.358600, '010-62282002', '08:00-22:00', 1, NOW(), NOW()),
('ST-BNU-SOUTH', '北京师范大学南门驿站', '北京市海淀区新街口外大街19号北京师范大学南门', 39.961900, 116.366000, '010-58808003', '08:00-22:00', 1, NOW(), NOW()),
('ST-CUFE-XUEYUANNAN', '中央财经大学学院南路驿站', '北京市海淀区学院南路39号中央财经大学南门', 39.958800, 116.348600, '010-62288004', '08:00-22:00', 1, NOW(), NOW()),
('ST-MUC-EAST', '中央民族大学东门驿站', '北京市海淀区中关村南大街27号中央民族大学东门', 39.948700, 116.324000, '010-68932005', '08:00-22:00', 1, NOW(), NOW()),
('ST-BFSU-EAST', '北京外国语大学东门驿站', '北京市海淀区西三环北路2号北京外国语大学东门', 39.953200, 116.314800, '010-88816006', '08:00-22:00', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    address = VALUES(address),
    lat = VALUES(lat),
    lng = VALUES(lng),
    phone = VALUES(phone),
    opening_hours = VALUES(opening_hours),
    status = VALUES(status),
    updated_at = NOW(),
    deleted_at = NULL;

UPDATE admins
SET station_id = (SELECT id FROM stations WHERE station_no = 'ST-BJTU-SOUTH' LIMIT 1),
    updated_at = NOW()
WHERE station_id IS NULL;

UPDATE couriers
SET station_id = (SELECT id FROM stations WHERE station_no = 'ST-BJTU-SOUTH' LIMIT 1),
    updated_at = NOW()
WHERE station_id IS NULL;

-- ========================================================
-- END sql/add_bjtu_stations.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/add2.0.sql
-- ========================================================
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

-- ========================================================
-- END sql/add2.0.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/add3.0_splash_ads.sql
-- ========================================================
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
-- ========================================================
-- END sql/add3.0_splash_ads.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/add3.1_splash_ads_update.sql
-- ========================================================
USE `XYJ`;

-- 清空之前的模拟数据
DELETE FROM `splash_ads`;

-- 插入用户指定的开屏广告测试数据
INSERT INTO `splash_ads` (`ad_no`, `name`, `image_url`, `target_url`, `weight`, `status`) VALUES
('AD_SPLASH_004', '开屏广告测试', '/admin/advertisement/开屏广告测试图片.png', 'https://www.taobao.com/', 100, 1);

-- ========================================================
-- END sql/add3.1_splash_ads_update.sql
-- ========================================================

-- ========================================================
-- BEGIN sql/insert_test_data.sql
-- ========================================================
-- ========================================================
-- 管理系统测试数据
-- 先执行 initial.sql / add_bjtu_stations.sql / add2.0.sql 后再执行本文件
-- ========================================================
USE `XYJ`;
SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 驿站（确保有数据） ====================
INSERT INTO stations(station_no, name, address, lat, lng, phone, opening_hours, status)
SELECT 'ST-BJTU-SOUTH', '北京交通大学南门驿站', '北京市海淀区上园村3号', 39.949900, 116.342000, '010-51688001', '08:00-22:00', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_no='ST-BJTU-SOUTH');
INSERT INTO stations(station_no, name, address, lat, lng, phone, opening_hours, status)
SELECT 'ST-BUPT-XITUCHENG', '北京邮电大学西土城驿站', '北京市海淀区西土城路10号', 39.960700, 116.358600, '010-62282002', '08:00-22:00', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_no='ST-BUPT-XITUCHENG');
INSERT INTO stations(station_no, name, address, lat, lng, phone, opening_hours, status)
SELECT 'ST-BNU-SOUTH', '北京师范大学南门驿站', '北京市海淀区新街口外大街19号', 39.961900, 116.366000, '010-58808003', '08:30-21:30', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM stations WHERE station_no='ST-BNU-SOUTH');

-- ==================== 用户（10个） ====================
INSERT INTO users(user_no, phone, email, password_hash, nickname, gender, status, created_at) VALUES
('U20260501001', '13800138001', 'zhangsan@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '张三', 1, 1, '2026-05-01 10:00:00'),
('U20260501002', '13800138002', 'lisi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '李四', 2, 1, '2026-05-02 11:00:00'),
('U20260501003', '13800138003', 'wangwu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '王五', 1, 1, '2026-05-03 09:00:00'),
('U20260501004', '13800138004', 'zhaoliu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵六', 2, 1, '2026-05-04 14:00:00'),
('U20260501005', '13800138005', 'sunqi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '孙七', 1, 1, '2026-05-05 08:30:00'),
('U20260501006', '13800138006', 'zhouba@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '周八', 2, 1, '2026-05-06 16:00:00'),
('U20260501007', '13800138007', 'wujiu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '吴九', 1, 1, '2026-05-07 10:15:00'),
('U20260501008', '13800138008', 'zhengshi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '郑十', 2, 1, '2026-05-08 12:00:00'),
('U20260501009', '13800138009', 'xiaoming@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '小明', 1, 1, '2026-05-10 09:45:00'),
('U20260501010', '13800138010', 'xiaohong@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '小红', 2, 1, '2026-05-12 15:30:00')
ON DUPLICATE KEY UPDATE nickname=VALUES(nickname);

-- ==================== 骑手（5个） ====================
INSERT INTO couriers(courier_no, account, password_hash, name, phone, station_id, level_name, level_progress, monthly_rank, status) VALUES
('COURIER-001', 'courier01', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '刘骑手', '15800158001', (SELECT id FROM stations WHERE station_no='ST-BJTU-SOUTH' LIMIT 1), '金牌配送员 Lv.3', 0.75, 1, 1),
('COURIER-002', 'courier02', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '陈骑手', '15800158002', (SELECT id FROM stations WHERE station_no='ST-BJTU-SOUTH' LIMIT 1), '银牌配送员 Lv.2', 0.50, 2, 1),
('COURIER-003', 'courier03', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '杨骑手', '15800158003', (SELECT id FROM stations WHERE station_no='ST-BUPT-XITUCHENG' LIMIT 1), '普通配送员 Lv.1', 0.20, 3, 1),
('COURIER-004', 'courier04', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '黄骑手', '15800158004', (SELECT id FROM stations WHERE station_no='ST-BUPT-XITUCHENG' LIMIT 1), '金牌配送员 Lv.3', 0.80, 1, 1),
('COURIER-005', 'courier05', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '赵骑手', '15800158005', (SELECT id FROM stations WHERE station_no='ST-BNU-SOUTH' LIMIT 1), '普通配送员 Lv.1', 0.10, 5, 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- ==================== 包裹（20个，各种状态） ====================
-- 使用 station_id 引用实际驿站
SET @s1 = (SELECT id FROM stations WHERE station_no='ST-BJTU-SOUTH' LIMIT 1);
SET @s2 = (SELECT id FROM stations WHERE station_no='ST-BUPT-XITUCHENG' LIMIT 1);
SET @s3 = (SELECT id FROM stations WHERE station_no='ST-BNU-SOUTH' LIMIT 1);
SET @c1 = (SELECT id FROM couriers WHERE courier_no='COURIER-001' LIMIT 1);
SET @c2 = (SELECT id FROM couriers WHERE courier_no='COURIER-002' LIMIT 1);
SET @u1 = (SELECT id FROM users WHERE user_no='U20260501001' LIMIT 1);
SET @u2 = (SELECT id FROM users WHERE user_no='U20260501002' LIMIT 1);
SET @u3 = (SELECT id FROM users WHERE user_no='U20260501003' LIMIT 1);
SET @u4 = (SELECT id FROM users WHERE user_no='U20260501004' LIMIT 1);
SET @u5 = (SELECT id FROM users WHERE user_no='U20260501005' LIMIT 1);

-- 待入库（PENDING_INBOUND）- 4个
INSERT INTO packages(package_no, pickup_code, name, sender_name, receiver_user_id, receiver_name, receiver_phone, address, weight, status, station_id, created_at, updated_at) VALUES
('PKG-20260601-001', '880001', '手机壳快递', '京东物流', @u1, '张三', '13800138001', '北京交通大学9号楼305室', 0.5, 'PENDING_INBOUND', @s1, '2026-06-01 09:00:00', '2026-06-01 09:00:00'),
('PKG-20260601-002', '880002', '教科书包裹', '当当网', @u2, '李四', '13800138002', '北京交通大学西门家属院2栋', 3.2, 'PENDING_INBOUND', @s1, '2026-06-01 10:30:00', '2026-06-01 10:30:00'),
('PKG-20260602-001', '880003', '零食大礼包', '拼多多', @u3, '王五', '13800138003', '北京邮电大学学一公寓506', 2.1, 'PENDING_INBOUND', @s2, '2026-06-02 14:00:00', '2026-06-02 14:00:00'),
('PKG-20260603-001', '880004', '夏季衣物', '淘宝', @u4, '赵六', '13800138004', '北京师范大学东门教师楼', 1.8, 'PENDING_INBOUND', @s3, '2026-06-03 08:00:00', '2026-06-03 08:00:00')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 在库中（IN_STOCK）- 5个
INSERT INTO packages(package_no, pickup_code, name, sender_name, receiver_user_id, receiver_name, receiver_phone, address, weight, status, station_id, created_at, updated_at) VALUES
('PKG-20260528-001', '880011', '笔记本电脑', '顺丰速运', @u1, '张三', '13800138001', '北京交通大学9号楼305室', 4.5, 'IN_STOCK', @s1, '2026-05-28 11:00:00', '2026-05-29 09:00:00'),
('PKG-20260528-002', '880012', '保温杯', '中通快递', @u2, '李四', '13800138002', '北京交通大学西门家属院', 0.8, 'IN_STOCK', @s1, '2026-05-28 14:00:00', '2026-05-29 10:00:00'),
('PKG-20260529-001', '880013', '运动鞋', '圆通速递', @u3, '王五', '13800138003', '北京邮电大学学三公寓201', 1.5, 'IN_STOCK', @s2, '2026-05-29 08:00:00', '2026-05-30 09:00:00'),
('PKG-20260530-001', '880014', '化妆品套装', '韵达快递', @u5, '孙七', '13800138005', '北京师范大学主楼旁宿舍', 0.6, 'IN_STOCK', @s3, '2026-05-30 10:00:00', '2026-05-31 08:00:00'),
('PKG-20260530-002', '880015', '文具大礼包', '京东物流', @u4, '赵六', '13800138004', '北京师范大学东门教师楼', 1.2, 'IN_STOCK', @s3, '2026-05-30 15:00:00', '2026-05-31 09:00:00')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 已发布任务（TASK_PUBLISHED）- 3个
INSERT INTO packages(package_no, pickup_code, name, sender_name, receiver_user_id, receiver_name, receiver_phone, address, weight, reward_amount, status, station_id, created_at, updated_at) VALUES
('PKG-20260527-001', '880021', '耳机快递', '顺丰速运', @u1, '张三', '13800138001', '北京交通大学9号楼', 0.3, 5.00, 'TASK_PUBLISHED', @s1, '2026-05-27 09:00:00', '2026-05-28 10:00:00'),
('PKG-20260527-002', '880022', '充电宝', '申通快递', @u2, '李四', '13800138002', '北京交通大学西门', 0.4, 6.00, 'TASK_PUBLISHED', @s1, '2026-05-27 14:00:00', '2026-05-28 11:00:00'),
('PKG-20260526-001', '880023', '平板电脑', '京东物流', @u3, '王五', '13800138003', '北京邮电大学学一公寓', 1.8, 10.00, 'TASK_PUBLISHED', @s2, '2026-05-26 08:00:00', '2026-05-27 09:00:00')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 派送中（DELIVERING）- 3个
INSERT INTO packages(package_no, pickup_code, name, sender_name, receiver_user_id, receiver_name, receiver_phone, address, weight, reward_amount, status, station_id, courier_id, created_at, updated_at) VALUES
('PKG-20260525-001', '880031', '水果礼盒', '顺丰速运', @u4, '赵六', '13800138004', '北京师范大学教师楼3栋', 5.0, 8.00, 'DELIVERING', @s3, @c2, '2026-05-25 09:00:00', '2026-06-01 10:00:00'),
('PKG-20260525-002', '880032', '衣服包裹', '中通快递', @u5, '孙七', '13800138005', '北京师范大学宿舍7号楼', 2.0, 5.00, 'DELIVERING', @s3, @c2, '2026-05-25 11:00:00', '2026-06-01 14:00:00'),
('PKG-20260524-001', '880033', '数码配件', '圆通速递', @u1, '张三', '13800138001', '北京交通大学主楼旁', 0.5, 6.00, 'DELIVERING', @s1, @c1, '2026-05-24 10:00:00', '2026-06-02 08:00:00')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- 已完成（COMPLETED）- 5个
INSERT INTO packages(package_no, pickup_code, name, sender_name, receiver_user_id, receiver_name, receiver_phone, address, weight, reward_amount, status, station_id, courier_id, created_at, updated_at) VALUES
('PKG-20260520-001', '880041', '书籍快递', '当当网', @u1, '张三', '13800138001', '北京交通大学9号楼305室', 2.0, 5.00, 'COMPLETED', @s1, @c1, '2026-05-20 09:00:00', '2026-05-22 16:00:00'),
('PKG-20260520-002', '880042', '日用品包裹', '淘宝', @u2, '李四', '13800138002', '北京交通大学西门家属院', 3.0, 6.00, 'COMPLETED', @s1, @c1, '2026-05-20 14:00:00', '2026-05-23 11:00:00'),
('PKG-20260518-001', '880043', '零食快递', '拼多多', @u3, '王五', '13800138003', '北京邮电大学学一公寓', 1.5, 4.00, 'COMPLETED', @s2, @c2, '2026-05-18 08:00:00', '2026-05-20 10:00:00'),
('PKG-20260515-001', '880044', '夏季T恤', '京东物流', @u4, '赵六', '13800138004', '北京师范大学教师楼', 0.5, 5.00, 'COMPLETED', @s3, @c2, '2026-05-15 10:00:00', '2026-05-17 15:00:00'),
('PKG-20260510-001', '880045', '手机充电器', '顺丰速运', @u5, '孙七', '13800138005', '北京师范大学宿舍楼', 0.3, 8.00, 'COMPLETED', @s3, @c1, '2026-05-10 09:00:00', '2026-05-12 12:00:00')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- ==================== 配送任务（为已发布/派送中/已完成的包裹创建） ====================
INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260527-001', p.id, p.station_id, NULL, s.address, p.address, 5.00, 'AVAILABLE', NULL, NULL, '2026-05-28 10:00:00', '2026-05-28 10:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260527-001'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260527-001');

INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260527-002', p.id, p.station_id, NULL, s.address, p.address, 6.00, 'AVAILABLE', NULL, NULL, '2026-05-28 11:00:00', '2026-05-28 11:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260527-002'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260527-002');

INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260526-001', p.id, p.station_id, NULL, s.address, p.address, 10.00, 'AVAILABLE', NULL, NULL, '2026-05-27 09:00:00', '2026-05-27 09:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260526-001'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260526-001');

-- 派送中任务
INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260525-001', p.id, p.station_id, @c2, s.address, p.address, 8.00, 'DELIVERING', '2026-05-30 09:00:00', NULL, '2026-05-29 08:00:00', '2026-06-01 10:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260525-001'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260525-001');

INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260525-002', p.id, p.station_id, @c2, s.address, p.address, 5.00, 'DELIVERING', '2026-05-31 10:00:00', NULL, '2026-05-29 10:00:00', '2026-06-01 14:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260525-002'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260525-002');

INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260524-001', p.id, p.station_id, @c1, s.address, p.address, 6.00, 'DELIVERING', '2026-06-01 08:00:00', NULL, '2026-05-28 09:00:00', '2026-06-02 08:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260524-001'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260524-001');

-- 已完成任务
INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260520-001', p.id, p.station_id, @c1, s.address, p.address, 5.00, 'COMPLETED', '2026-05-21 09:00:00', '2026-05-22 16:00:00', '2026-05-20 10:00:00', '2026-05-22 16:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260520-001'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260520-001');

INSERT INTO delivery_tasks(task_no, package_id, station_id, courier_id, pickup_address, deliver_address, reward_amount, status, grabbed_at, completed_at, created_at, updated_at)
SELECT 'TASK-20260520-002', p.id, p.station_id, @c1, s.address, p.address, 6.00, 'COMPLETED', '2026-05-21 14:00:00', '2026-05-23 11:00:00', '2026-05-20 15:00:00', '2026-05-23 11:00:00'
FROM packages p JOIN stations s ON p.station_id=s.id WHERE p.package_no='PKG-20260520-002'
AND NOT EXISTS (SELECT 1 FROM delivery_tasks WHERE task_no='TASK-20260520-002');

-- ==================== 骑手收益流水 ====================
INSERT INTO courier_earnings(courier_id, task_id, amount, type, status, title, created_at)
SELECT @c1, t.id, 5.00, 'DELIVERY_REWARD', 'SETTLED', '配送奖励-书籍快递', '2026-05-22 16:00:00'
FROM delivery_tasks t WHERE t.task_no='TASK-20260520-001'
AND NOT EXISTS (SELECT 1 FROM courier_earnings WHERE task_id=t.id);

INSERT INTO courier_earnings(courier_id, task_id, amount, type, status, title, created_at)
SELECT @c1, t.id, 6.00, 'DELIVERY_REWARD', 'SETTLED', '配送奖励-日用品包裹', '2026-05-23 11:00:00'
FROM delivery_tasks t WHERE t.task_no='TASK-20260520-002'
AND NOT EXISTS (SELECT 1 FROM courier_earnings WHERE task_id=t.id);

-- ==================== 乡镇资讯（6条） ====================
INSERT INTO news_posts(post_no, title, content, tag, author_type, station_id, likes, is_urgent, created_at) VALUES
('NEWS-001', '驿站端午节放假通知', '各位同学好，驿站将于6月8日至6月10日端午节期间暂停营业，6月11日恢复正常。请大家提前取件，祝大家端午节快乐！', '公告通知', 'ADMIN', @s1, 42, 1, '2026-05-30 08:00:00'),
('NEWS-002', '6月促销活动：签收赢积分', '即日起，每成功签收一个包裹即可获得10积分，积分可在积分商城兑换精美礼品。活动持续至6月30日，快来参与吧！', '活动促销', 'ADMIN', @s1, 28, 0, '2026-05-28 10:00:00'),
('NEWS-003', '暴雨天气配送延迟提醒', '受近期暴雨天气影响，部分包裹配送可能会有1-2天延迟，请同学们耐心等待。如遇紧急情况请拨打驿站电话。', '天气预警', 'ADMIN', @s2, 55, 1, '2026-05-25 07:00:00'),
('NEWS-004', '新骑手招募中', '乡驿家正在招募校园配送骑手，时间灵活、收入可观。有意向的同学请联系驿站管理员报名，名额有限！', '招募信息', 'ADMIN', @s1, 36, 0, '2026-05-22 09:00:00'),
('NEWS-005', '积分商城上新啦', '积分商城新增多款实用商品：便携风扇、保温杯、环保袋等，快来用你的积分兑换心仪好礼吧！', '商城动态', 'ADMIN', @s2, 19, 0, '2026-05-18 14:00:00'),
('NEWS-006', '驿站服务升级公告', '为提升服务质量，驿站现已延长营业时间至22:00，同时新增自助取件柜，24小时随时取件。', '服务升级', 'ADMIN', @s3, 31, 0, '2026-05-15 11:00:00')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- ==================== 资讯评论 ====================
INSERT INTO news_comments(post_id, user_id, content, status, created_at) VALUES
((SELECT id FROM news_posts WHERE post_no='NEWS-001' LIMIT 1), @u1, '收到，提前去取件！', 1, '2026-05-30 09:00:00'),
((SELECT id FROM news_posts WHERE post_no='NEWS-001' LIMIT 1), @u2, '端午节快乐！', 1, '2026-05-30 10:00:00'),
((SELECT id FROM news_posts WHERE post_no='NEWS-002' LIMIT 1), @u3, '积分兑换太棒了', 1, '2026-05-28 11:00:00'),
((SELECT id FROM news_posts WHERE post_no='NEWS-003' LIMIT 1), @u4, '注意安全啊', 1, '2026-05-25 08:00:00'),
((SELECT id FROM news_posts WHERE post_no='NEWS-004' LIMIT 1), @u5, '想报名，怎么联系？', 1, '2026-05-22 10:00:00');

-- ==================== 用户积分账户 ====================
INSERT INTO user_points_accounts(user_id, points, coupon_count, balance, member_level, monthly_signed_count) VALUES
(@u1, 1200, 3, 50.00, '活跃村民', 5),
(@u2, 800, 2, 30.00, '普通村民', 3),
(@u3, 650, 1, 20.00, '普通村民', 2),
(@u4, 2000, 5, 100.00, '模范村民', 8),
(@u5, 300, 0, 10.00, '普通村民', 1)
ON DUPLICATE KEY UPDATE points=VALUES(points);

-- ==================== 用户地址 ====================
INSERT INTO user_addresses(user_id, name, phone, address, is_default) VALUES
(@u1, '张三', '13800138001', '北京交通大学9号楼305室', 1),
(@u1, '张三(家)', '13800138001', '北京市海淀区上园村3号家属院', 0),
(@u2, '李四', '13800138002', '北京交通大学西门家属院2栋', 1),
(@u3, '王五', '13800138003', '北京邮电大学学一公寓506', 1),
(@u4, '赵六', '13800138004', '北京师范大学东门教师楼', 1),
(@u5, '孙七', '13800138005', '北京师范大学主楼旁宿舍', 1);

-- ==================== 用户优惠券 ====================
INSERT INTO user_coupons(coupon_no, user_id, name, amount, status, source, expire_time) VALUES
('CPN-202606-001', @u1, '新用户满减券', 5.00, 'AVAILABLE', '活动', '2026-07-01 00:00:00'),
('CPN-202606-002', @u1, '配送立减券', 3.00, 'AVAILABLE', '兑换', '2026-06-30 00:00:00'),
('CPN-202606-003', @u1, '端午节专享券', 8.00, 'USED', '活动', '2026-06-10 00:00:00'),
('CPN-202606-004', @u2, '新用户满减券', 5.00, 'AVAILABLE', '活动', '2026-07-01 00:00:00'),
('CPN-202606-005', @u2, '配送立减券', 3.00, 'EXPIRED', '兑换', '2026-05-01 00:00:00'),
('CPN-202606-006', @u3, '配送立减券', 3.00, 'AVAILABLE', '兑换', '2026-06-30 00:00:00'),
('CPN-202606-007', @u4, 'VIP专属优惠券', 10.00, 'AVAILABLE', '补偿', '2026-08-01 00:00:00'),
('CPN-202606-008', @u4, '新用户满减券', 5.00, 'AVAILABLE', '活动', '2026-07-01 00:00:00');

-- ==================== 钱包流水 ====================
INSERT INTO wallet_transactions(user_id, type, amount, title, biz_type, created_at) VALUES
(@u1, 'REWARD', 10.00, '配送奖励返还', 'PACKAGE', '2026-05-22 16:00:00'),
(@u1, 'CONSUME', -5.00, '配送服务费', 'PACKAGE', '2026-05-23 11:00:00'),
(@u1, 'REFUND', 3.00, '延迟配送补偿', 'COMPLAIN', '2026-05-25 14:00:00'),
(@u2, 'REWARD', 8.00, '配送奖励返还', 'PACKAGE', '2026-05-23 11:00:00'),
(@u2, 'CONSUME', -6.00, '配送服务费', 'PACKAGE', '2026-05-24 09:00:00'),
(@u4, 'REWARD', 15.00, '大额配送奖励', 'PACKAGE', '2026-05-17 15:00:00'),
(@u4, 'REFUND', 5.00, '包裹损坏补偿', 'COMPLAIN', '2026-05-20 10:00:00');

-- ==================== 帮助中心 ====================
INSERT INTO help_items(help_no, title, content, sort_order, status) VALUES
('HELP-001', '如何提交包裹？', '打开APP首页，点击"寄件/收件"按钮，填写包裹信息（快递单号、收件人、地址等）后提交即可。管理员审核入库后，会发布配送任务。', 1, 1),
('HELP-002', '如何查看包裹状态？', '在"我的包裹"页面可以查看所有包裹的实时状态。状态包括：待入库、在库中、派送中、已完成等。', 2, 1),
('HELP-003', '取件码是什么？', '取件码是系统为您的包裹生成的6位数字验证码。骑手上门配送时，需要核验取件码才能完成签收，请在APP中查看您的取件码。', 3, 1),
('HELP-004', '如何获取积分？', '每成功签收一个包裹可获得10积分。积分可在积分商城兑换各种精美礼品和日用品。', 4, 1),
('HELP-005', '如何投诉或评价？', '包裹完成后，可在包裹详情页进行评分和评价。如遇问题，可点击"投诉"按钮提交投诉，管理员会尽快处理。', 5, 1)
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- ==================== 客服配置 ====================
INSERT INTO customer_service_configs(phone, online_time, wechat, status)
SELECT '010-51688001', '周一至周日 08:00-22:00', 'XYJ_Service', 1
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM customer_service_configs LIMIT 1);

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================================
-- END sql/insert_test_data.sql
-- ========================================================
