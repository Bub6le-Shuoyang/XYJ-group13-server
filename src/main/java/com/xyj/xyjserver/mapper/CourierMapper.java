package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.Courier;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CourierMapper {

    @Select("SELECT * FROM couriers WHERE account = #{account} OR phone = #{account}")
    Courier findByAccount(@Param("account") String account);

    @Select("SELECT * FROM couriers WHERE id = #{id}")
    Courier findById(@Param("id") Long id);

    @Insert("INSERT INTO couriers(courier_no, account, password_hash, name, phone, station_id, status, created_at, updated_at) " +
            "VALUES(#{courierNo}, #{account}, #{passwordHash}, #{name}, #{phone}, #{stationId}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Courier courier);

    /**
     * 分页搜索配送员（关键词匹配 name / account / phone / courier_no）
     * stationId 为 null 时查全部站点（超管）
     */
    @Select("<script>" +
            "SELECT c.*, s.name AS station_name FROM couriers c " +
            "LEFT JOIN stations s ON c.station_id = s.id " +
            "WHERE 1=1" +
            "<if test='stationId != null'> AND c.station_id = #{stationId}</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (c.name LIKE CONCAT('%',#{keyword},'%')" +
            "    OR c.account LIKE CONCAT('%',#{keyword},'%')" +
            "    OR c.phone LIKE CONCAT('%',#{keyword},'%')" +
            "    OR c.courier_no LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            " ORDER BY c.created_at DESC LIMIT #{offset}, #{size}" +
            "</script>")
    List<Courier> searchByKeyword(@Param("keyword") String keyword,
                                              @Param("stationId") Long stationId,
                                              @Param("offset") long offset,
                                              @Param("size") long size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM couriers c WHERE 1=1" +
            "<if test='stationId != null'> AND c.station_id = #{stationId}</if>" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (c.name LIKE CONCAT('%',#{keyword},'%')" +
            "    OR c.account LIKE CONCAT('%',#{keyword},'%')" +
            "    OR c.phone LIKE CONCAT('%',#{keyword},'%')" +
            "    OR c.courier_no LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "</script>")
    long countByKeyword(@Param("keyword") String keyword, @Param("stationId") Long stationId);

    @Update("<script>" +
            "UPDATE couriers SET updated_at = NOW()" +
            "<if test='name != null'>, name = #{name}</if>" +
            "<if test='phone != null'>, phone = #{phone}</if>" +
            "<if test='stationId != null'>, station_id = #{stationId}</if>" +
            "<if test='status != null'>, status = #{status}</if>" +
            "<if test='levelName != null'>, level_name = #{levelName}</if>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(Courier courier);

    @Select("SELECT COUNT(*) FROM couriers WHERE account = #{account} AND id != #{excludeId}")
    long countByAccountExclude(@Param("account") String account, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM couriers WHERE phone = #{phone} AND id != #{excludeId}")
    long countByPhoneExclude(@Param("phone") String phone, @Param("excludeId") Long excludeId);

    /** 配送员业绩统计 */
    @Select("SELECT COALESCE(SUM(amount), 0) FROM courier_earnings WHERE courier_id = #{courierId} AND status = 'SETTLED'")
    BigDecimal sumTotalEarnings(@Param("courierId") Long courierId);

    @Select("SELECT COUNT(*) FROM delivery_tasks WHERE courier_id = #{courierId} AND status = 'COMPLETED'")
    int countCompletedTasks(@Param("courierId") Long courierId);

    @Select("SELECT COUNT(*) FROM delivery_tasks WHERE courier_id = #{courierId}")
    int countAllTasks(@Param("courierId") Long courierId);

    @Select("SELECT COUNT(*) FROM delivery_tasks WHERE courier_id = #{courierId} AND status = 'DELIVERING'")
    int countActiveTasks(@Param("courierId") Long courierId);
}
