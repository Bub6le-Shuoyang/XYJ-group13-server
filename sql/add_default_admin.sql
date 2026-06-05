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
