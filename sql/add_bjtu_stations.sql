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
