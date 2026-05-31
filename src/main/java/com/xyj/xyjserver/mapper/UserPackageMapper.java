package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.vo.PackageVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.math.BigDecimal;

@Mapper
public interface UserPackageMapper {

    @Select("""
            SELECT id
            FROM stations
            WHERE station_no = 'ST-BJTU-SOUTH'
              AND status = 1
              AND deleted_at IS NULL
            LIMIT 1
            """)
    Long findDefaultStationId();

    @Insert("""
            INSERT INTO packages(
                package_no, pickup_code, name, sender_name, receiver_user_id, receiver_name,
                receiver_phone, address, weight, estimated_fee, reward_amount, status,
                station_id, lat, lng, created_at, updated_at
            )
            VALUES(
                #{packageNo}, #{pickupCode}, #{name}, #{senderName}, #{userId}, #{receiverName},
                #{receiverPhone}, #{address}, #{weight}, 0, #{rewardAmount}, 'PENDING_INBOUND',
                #{stationId}, #{lat}, #{lng}, NOW(), NOW()
            )
            """)
    int insertUserPackage(
            @Param("packageNo") String packageNo,
            @Param("pickupCode") String pickupCode,
            @Param("name") String name,
            @Param("senderName") String senderName,
            @Param("userId") Long userId,
            @Param("receiverName") String receiverName,
            @Param("receiverPhone") String receiverPhone,
            @Param("address") String address,
            @Param("weight") Double weight,
            @Param("rewardAmount") BigDecimal rewardAmount,
            @Param("stationId") Long stationId,
            @Param("lat") Double lat,
            @Param("lng") Double lng);

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
            WHERE p.receiver_user_id = #{userId}
            ORDER BY p.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<PackageVO> findUserPackages(
            @Param("userId") Long userId,
            @Param("offset") Long offset,
            @Param("size") Long size);

    @Select("SELECT COUNT(*) FROM packages WHERE receiver_user_id = #{userId}")
    Long countUserPackages(@Param("userId") Long userId);

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
            WHERE p.receiver_user_id = #{userId}
              AND p.package_no = #{packageNo}
            """)
    PackageVO findUserPackageByNo(@Param("userId") Long userId, @Param("packageNo") String packageNo);

    @Update("""
            UPDATE packages
            SET status = 'COMPLETED'
            WHERE receiver_user_id = #{userId}
              AND package_no = #{packageNo}
              AND status IN ('ASSIGNED', 'DELIVERING')
            """)
    int confirmReceipt(@Param("userId") Long userId, @Param("packageNo") String packageNo);

    @Select("""
            SELECT id
            FROM packages
            WHERE receiver_user_id = #{userId}
              AND package_no = #{packageNo}
            """)
    Long findOwnedPackageId(@Param("userId") Long userId, @Param("packageNo") String packageNo);

    @Insert("""
            INSERT INTO package_timelines(package_id, status, content, created_at)
            VALUES(#{packageId}, #{status}, #{content}, NOW())
            """)
    int insertTimeline(
            @Param("packageId") Long packageId,
            @Param("status") String status,
            @Param("content") String content);
}
