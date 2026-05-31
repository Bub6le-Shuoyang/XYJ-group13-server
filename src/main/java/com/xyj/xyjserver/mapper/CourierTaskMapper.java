package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.vo.CourierProfileVO;
import com.xyj.xyjserver.vo.TaskVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CourierTaskMapper {

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
            WHERE t.status = 'AVAILABLE'
              AND t.courier_id IS NULL
            ORDER BY t.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<TaskVO> findAvailableTasks(@Param("offset") Long offset, @Param("size") Long size);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_tasks
            WHERE status = 'AVAILABLE'
              AND courier_id IS NULL
            """)
    Long countAvailableTasks();

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
            WHERE t.courier_id = #{courierId}
              AND (#{status} IS NULL OR #{status} = '' OR t.status = #{status})
            ORDER BY t.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<TaskVO> findCourierTasks(
            @Param("courierId") Long courierId,
            @Param("status") String status,
            @Param("offset") Long offset,
            @Param("size") Long size);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_tasks
            WHERE courier_id = #{courierId}
              AND (#{status} IS NULL OR #{status} = '' OR status = #{status})
            """)
    Long countCourierTasks(@Param("courierId") Long courierId, @Param("status") String status);

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
            WHERE t.task_no = #{taskNo}
            """)
    TaskVO findTaskByNo(@Param("taskNo") String taskNo);

    @Update("""
            UPDATE delivery_tasks
            SET courier_id = #{courierId},
                status = 'ASSIGNED',
                grabbed_at = NOW()
            WHERE task_no = #{taskNo}
              AND status = 'AVAILABLE'
              AND courier_id IS NULL
            """)
    int grabTask(@Param("courierId") Long courierId, @Param("taskNo") String taskNo);

    @Update("""
            UPDATE packages p
            JOIN delivery_tasks t ON t.package_id = p.id
            SET p.courier_id = #{courierId},
                p.status = 'ASSIGNED'
            WHERE t.task_no = #{taskNo}
            """)
    int assignPackage(@Param("courierId") Long courierId, @Param("taskNo") String taskNo);

    @Update("""
            UPDATE delivery_tasks
            SET status = 'DELIVERING'
            WHERE task_no = #{taskNo}
              AND courier_id = #{courierId}
              AND status = 'ASSIGNED'
            """)
    int pickupTask(@Param("courierId") Long courierId, @Param("taskNo") String taskNo);

    @Update("""
            UPDATE delivery_tasks
            SET deliver_image = #{deliverImage},
                remark = #{remark},
                status = 'DELIVERING'
            WHERE task_no = #{taskNo}
              AND courier_id = #{courierId}
              AND status IN ('ASSIGNED', 'DELIVERING')
            """)
    int deliverTask(
            @Param("courierId") Long courierId,
            @Param("taskNo") String taskNo,
            @Param("deliverImage") String deliverImage,
            @Param("remark") String remark);

    @Update("""
            UPDATE delivery_tasks t
            JOIN packages p ON t.package_id = p.id
            SET t.status = 'COMPLETED',
                t.completed_at = NOW(),
                p.status = 'COMPLETED'
            WHERE t.task_no = #{taskNo}
              AND t.courier_id = #{courierId}
              AND p.pickup_code = #{pickupCode}
              AND t.status IN ('ASSIGNED', 'DELIVERING')
            """)
    int verifyPickupCode(
            @Param("courierId") Long courierId,
            @Param("taskNo") String taskNo,
            @Param("pickupCode") String pickupCode);

    @Insert("""
            INSERT INTO courier_earnings(courier_id, task_id, amount, type, status, title, created_at, updated_at)
            SELECT #{courierId}, t.id, t.reward_amount, 'DELIVERY_REWARD', 'SETTLED', '配送任务奖励', NOW(), NOW()
            FROM delivery_tasks t
            WHERE t.task_no = #{taskNo}
              AND t.courier_id = #{courierId}
              AND NOT EXISTS (
                  SELECT 1 FROM courier_earnings e WHERE e.task_id = t.id AND e.courier_id = #{courierId}
              )
            """)
    int insertEarningIfAbsent(@Param("courierId") Long courierId, @Param("taskNo") String taskNo);

    @Select("""
            SELECT COALESCE(SUM(amount), 0)
            FROM courier_earnings
            WHERE courier_id = #{courierId}
              AND status = 'SETTLED'
            """)
    BigDecimal sumTotalEarnings(@Param("courierId") Long courierId);

    @Select("""
            SELECT COALESCE(SUM(amount), 0)
            FROM courier_earnings
            WHERE courier_id = #{courierId}
              AND status = 'SETTLED'
              AND DATE(created_at) = CURDATE()
            """)
    BigDecimal sumTodayEarnings(@Param("courierId") Long courierId);

    @Select("SELECT COUNT(*) FROM delivery_tasks WHERE courier_id = #{courierId} AND status = 'COMPLETED'")
    Integer countCompletedTasks(@Param("courierId") Long courierId);

    @Select("""
            SELECT COUNT(*)
            FROM delivery_tasks
            WHERE courier_id = #{courierId}
              AND DATE(completed_at) = CURDATE()
              AND status = 'COMPLETED'
            """)
    Integer countTodayTasks(@Param("courierId") Long courierId);

    @Select("""
            SELECT
                c.id,
                c.courier_no,
                c.name,
                c.phone,
                c.avatar_url,
                CASE c.status
                    WHEN 1 THEN 'WORKING'
                    WHEN 0 THEN 'DISABLED'
                    ELSE 'UNKNOWN'
                END AS status,
                CAST(c.station_id AS CHAR) AS station_id,
                s.name AS station_name,
                5.0 AS rating
            FROM couriers c
            LEFT JOIN stations s ON c.station_id = s.id
            WHERE c.id = #{courierId}
            """)
    CourierProfileVO findCourierProfile(@Param("courierId") Long courierId);
}
