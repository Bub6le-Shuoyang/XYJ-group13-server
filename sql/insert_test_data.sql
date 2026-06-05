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
