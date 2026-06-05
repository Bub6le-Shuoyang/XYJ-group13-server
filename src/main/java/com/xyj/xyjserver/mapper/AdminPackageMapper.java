package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.vo.PackageVO;
import com.xyj.xyjserver.vo.StationStatisticsVO;
import com.xyj.xyjserver.vo.TaskVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdminPackageMapper {

    @Select("SELECT role FROM admins WHERE id = #{adminId}")
    Integer findAdminRole(@Param("adminId") Long adminId);

    @Select("SELECT station_id FROM admins WHERE id = #{adminId}")
    Long findAdminStationId(@Param("adminId") Long adminId);

    @Select("""
            SELECT
                p.package_no AS package_id,
                t.task_no AS task_id,
                p.name,
                p.receiver_name,
                p.receiver_phone,
                p.address,
                p.weight,
                p.estimated_fee,
                p.status,
                p.pickup_code,
                p.sender_name,
                p.reward_amount,
                c.name AS courier_name,
                CAST(p.station_id AS CHAR) AS station_id,
                s.name AS station_name,
                p.lat,
                p.lng
            FROM packages p
            LEFT JOIN delivery_tasks t ON t.package_id = p.id
            LEFT JOIN couriers c ON p.courier_id = c.id
            LEFT JOIN stations s ON p.station_id = s.id
            WHERE (#{stationId} IS NULL OR p.station_id = #{stationId})
              AND (#{status} IS NULL OR #{status} = '' OR p.status = #{status})
            ORDER BY p.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<PackageVO> findStationPackages(
            @Param("stationId") Long stationId,
            @Param("status") String status,
            @Param("offset") Long offset,
            @Param("size") Long size);

    @Select("""
            SELECT COUNT(*)
            FROM packages
            WHERE (#{stationId} IS NULL OR station_id = #{stationId})
              AND (#{status} IS NULL OR #{status} = '' OR status = #{status})
            """)
    Long countStationPackages(@Param("stationId") Long stationId, @Param("status") String status);

    @Update("""
            UPDATE packages
            SET status = 'IN_STOCK'
            WHERE package_no = #{packageNo}
              AND (#{stationId} IS NULL OR station_id = #{stationId})
              AND status = 'PENDING_INBOUND'
            """)
    int inboundPackage(@Param("stationId") Long stationId, @Param("packageNo") String packageNo);

    @Update("""
            UPDATE packages
            SET status = 'TASK_PUBLISHED'
            WHERE package_no = #{packageNo}
              AND (#{stationId} IS NULL OR station_id = #{stationId})
              AND status = 'PENDING_INBOUND'
            """)
    int approvePackage(@Param("stationId") Long stationId, @Param("packageNo") String packageNo);

    @Update("""
            UPDATE packages
            SET status = 'TASK_PUBLISHED'
            WHERE package_no = #{packageNo}
              AND (#{stationId} IS NULL OR station_id = #{stationId})
              AND status IN ('IN_STOCK', 'TASK_PUBLISHED')
            """)
    int outboundPackage(@Param("stationId") Long stationId, @Param("packageNo") String packageNo);

    @Select("""
            SELECT id
            FROM packages
            WHERE package_no = #{packageNo}
              AND (#{stationId} IS NULL OR station_id = #{stationId})
            """)
    Long findStationPackageId(@Param("stationId") Long stationId, @Param("packageNo") String packageNo);

    @Insert("""
            INSERT INTO delivery_tasks(task_no, package_id, station_id, pickup_address, deliver_address, reward_amount, status, created_at, updated_at)
            SELECT #{taskNo}, p.id, p.station_id, COALESCE(s.address, '驿站自提点'), p.address, #{rewardAmount}, 'AVAILABLE', NOW(), NOW()
            FROM packages p
            LEFT JOIN stations s ON p.station_id = s.id
            WHERE p.package_no = #{packageNo}
              AND (#{stationId} IS NULL OR p.station_id = #{stationId})
              AND NOT EXISTS (SELECT 1 FROM delivery_tasks t WHERE t.package_id = p.id)
            """)
    int insertTask(
            @Param("stationId") Long stationId,
            @Param("packageNo") String packageNo,
            @Param("taskNo") String taskNo,
            @Param("rewardAmount") BigDecimal rewardAmount);

    @Select("""
            SELECT
                t.task_no AS task_id,
                p.package_no AS package_id,
                p.name AS package_name,
                t.pickup_address,
                t.deliver_address,
                t.reward_amount,
                t.status,
                CONCAT('****', RIGHT(p.pickup_code, 2)) AS pickup_code_masked,
                t.created_at,
                t.completed_at
            FROM delivery_tasks t
            JOIN packages p ON t.package_id = p.id
            WHERE p.package_no = #{packageNo}
              AND (#{stationId} IS NULL OR p.station_id = #{stationId})
            """)
    TaskVO findTaskByPackageNo(@Param("stationId") Long stationId, @Param("packageNo") String packageNo);

    @Insert("""
            INSERT INTO package_timelines(package_id, status, content, created_at)
            VALUES(#{packageId}, #{status}, #{content}, NOW())
            """)
    int insertTimeline(
            @Param("packageId") Long packageId,
            @Param("status") String status,
            @Param("content") String content);

    @Select("""
            SELECT
                SUM(CASE WHEN status = 'PENDING_INBOUND' THEN 1 ELSE 0 END) AS pending_inbound_count,
                SUM(CASE WHEN status = 'IN_STOCK' THEN 1 ELSE 0 END) AS in_stock_count,
                SUM(CASE WHEN status IN ('ASSIGNED', 'DELIVERING') THEN 1 ELSE 0 END) AS delivering_count,
                SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed_count,
                SUM(CASE WHEN DATE(created_at) = CURDATE() THEN 1 ELSE 0 END) AS today_inbound,
                SUM(CASE WHEN status IN ('TASK_PUBLISHED', 'ASSIGNED', 'DELIVERING', 'COMPLETED')
                          AND DATE(updated_at) = CURDATE() THEN 1 ELSE 0 END) AS today_outbound
            FROM packages
            WHERE (#{stationId} IS NULL OR station_id = #{stationId})
            """)
    StationStatisticsVO getStationStatistics(@Param("stationId") Long stationId);

    @Select("""
            SELECT package_no, name, receiver_name, receiver_phone, address, status, created_at
            FROM packages
            ORDER BY created_at DESC
            """)
    List<Map<String, Object>> findAllPackagesForExport();
}
