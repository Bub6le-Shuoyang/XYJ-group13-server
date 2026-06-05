package com.xyj.xyjserver.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AnalyticsMapper {

    @Select("SELECT DATE(created_at) as date, COUNT(*) as total " +
            "FROM packages " +
            "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> getPackageTrend(@Param("days") int days);

    @Select("SELECT DATE(created_at) as date, COUNT(*) as total " +
            "FROM users " +
            "WHERE deleted_at IS NULL AND created_at >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> getUserGrowth(@Param("days") int days);

    @Select("SELECT status, COUNT(*) as count FROM packages GROUP BY status")
    List<Map<String, Object>> getPackageStatusDistribution();

    @Select("SELECT c.id, c.name, c.courier_no, " +
            "COUNT(CASE WHEN t.status = 'COMPLETED' THEN 1 END) as completed, " +
            "COUNT(CASE WHEN t.status = 'DELIVERING' THEN 1 END) as active, " +
            "COALESCE(SUM(CASE WHEN e.status = 'SETTLED' THEN e.amount ELSE 0 END), 0) as total_earnings, " +
            "COUNT(t.id) as total_tasks " +
            "FROM couriers c " +
            "LEFT JOIN delivery_tasks t ON t.courier_id = c.id " +
            "LEFT JOIN courier_earnings e ON e.courier_id = c.id AND e.status = 'SETTLED' " +
            "GROUP BY c.id ORDER BY completed DESC LIMIT #{limit}")
    List<Map<String, Object>> getCourierEfficiency(@Param("limit") int limit);

    @Select("SELECT " +
            "(SELECT COUNT(*) FROM packages) as total_packages, " +
            "(SELECT COUNT(*) FROM users WHERE deleted_at IS NULL) as total_users, " +
            "(SELECT COUNT(*) FROM couriers WHERE status = 1) as active_couriers, " +
            "(SELECT COUNT(*) FROM stations WHERE status = 1 AND deleted_at IS NULL) as active_stations, " +
            "(SELECT COUNT(*) FROM packages WHERE status = 'PENDING_INBOUND') as pending_inbound, " +
            "(SELECT COUNT(*) FROM packages WHERE status = 'IN_STOCK') as in_stock, " +
            "(SELECT COUNT(*) FROM packages WHERE status = 'DELIVERING') as delivering, " +
            "(SELECT COUNT(*) FROM packages WHERE status = 'COMPLETED') as completed, " +
            "(SELECT COUNT(*) FROM packages WHERE DATE(created_at) = CURDATE()) as today_new")
    Map<String, Object> getOverallStats();

    @Select("SELECT " +
            "COALESCE((SELECT SUM(points) FROM user_points_accounts), 0) as total_points, " +
            "COALESCE((SELECT SUM(balance) FROM user_points_accounts), 0) as total_balance, " +
            "COALESCE((SELECT COUNT(*) FROM user_coupons), 0) as total_coupons, " +
            "COALESCE((SELECT COUNT(*) FROM mall_redeem_records), 0) as total_redeems, " +
            "COALESCE((SELECT COUNT(*) FROM wallet_transactions WHERE type = 'REWARD'), 0) as reward_count, " +
            "COALESCE((SELECT COUNT(*) FROM wallet_transactions WHERE type = 'CONSUME'), 0) as consume_count")
    Map<String, Object> getFinancialOverview();

    @Select("SELECT s.id, s.name, s.station_no, s.status, " +
            "COUNT(p.id) as package_count, " +
            "COUNT(CASE WHEN p.status = 'PENDING_INBOUND' THEN 1 END) as pending, " +
            "COUNT(CASE WHEN p.status = 'IN_STOCK' THEN 1 END) as in_stock, " +
            "COUNT(CASE WHEN p.status = 'DELIVERING' THEN 1 END) as delivering, " +
            "COUNT(CASE WHEN p.status = 'COMPLETED' THEN 1 END) as completed " +
            "FROM stations s " +
            "LEFT JOIN packages p ON p.station_id = s.id " +
            "WHERE s.deleted_at IS NULL " +
            "GROUP BY s.id, s.name, s.station_no, s.status " +
            "ORDER BY package_count DESC")
    List<Map<String, Object>> getStationPackageBreakdown();
}
