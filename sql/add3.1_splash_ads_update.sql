USE `XYJ`;

-- 清空之前的模拟数据
DELETE FROM `splash_ads`;

-- 插入用户指定的开屏广告测试数据
INSERT INTO `splash_ads` (`ad_no`, `name`, `image_url`, `target_url`, `weight`, `status`) VALUES
('AD_SPLASH_004', '开屏广告测试', '/admin/advertisement/开屏广告测试图片.png', 'https://www.taobao.com/', 100, 1);
