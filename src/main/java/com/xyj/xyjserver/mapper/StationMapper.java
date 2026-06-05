package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.Station;
import com.xyj.xyjserver.vo.StationVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StationMapper {

    @Select("""
            SELECT id
            FROM stations
            WHERE station_no = 'ST-BJTU-SOUTH'
              AND status = 1
              AND deleted_at IS NULL
            LIMIT 1
            """)
    Long findDefaultCampusStationId();

    @Select("""
            SELECT
                CAST(id AS CHAR) AS station_id,
                name,
                address,
                phone,
                opening_hours AS business_hours,
                lat,
                lng,
                CASE
                    WHEN #{lat} IS NULL OR #{lng} IS NULL THEN 0
                    ELSE ROUND(
                        6371 * 2 * ASIN(SQRT(
                            POWER(SIN((RADIANS(#{lat}) - RADIANS(lat)) / 2), 2)
                            + COS(RADIANS(#{lat})) * COS(RADIANS(lat))
                            * POWER(SIN((RADIANS(#{lng}) - RADIANS(lng)) / 2), 2)
                        )),
                        2
                    )
                END AS distance
            FROM stations
            WHERE status = 1
              AND deleted_at IS NULL
            HAVING #{lat} IS NULL OR #{lng} IS NULL OR distance <= 10
            ORDER BY
                CASE WHEN #{lat} IS NULL OR #{lng} IS NULL THEN id ELSE distance END ASC
            """)
    List<StationVO> findNearbyStations(@Param("lat") Double lat, @Param("lng") Double lng);

    @Select("""
            SELECT
                CAST(id AS CHAR) AS station_id,
                name,
                address,
                phone,
                opening_hours AS business_hours,
                lat,
                lng,
                0 AS distance
            FROM stations
            WHERE (CAST(id AS CHAR) = #{stationId} OR station_no = #{stationId})
              AND status = 1
              AND deleted_at IS NULL
            LIMIT 1
            """)
    StationVO findStationDetail(@Param("stationId") String stationId);

    // ========== Admin CRUD ==========

    @Select("<script>" +
            "SELECT * FROM stations WHERE deleted_at IS NULL" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (name LIKE CONCAT('%',#{keyword},'%')" +
            "    OR station_no LIKE CONCAT('%',#{keyword},'%')" +
            "    OR address LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
            "</script>")
    List<Station> searchByKeyword(@Param("keyword") String keyword,
                                  @Param("offset") long offset,
                                  @Param("size") long size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM stations WHERE deleted_at IS NULL" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (name LIKE CONCAT('%',#{keyword},'%')" +
            "    OR station_no LIKE CONCAT('%',#{keyword},'%')" +
            "    OR address LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "</script>")
    long countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM stations WHERE id = #{id} AND deleted_at IS NULL")
    Station findById(@Param("id") Long id);

    @Insert("INSERT INTO stations(station_no, name, address, lat, lng, phone, opening_hours, status, created_at, updated_at) " +
            "VALUES(#{stationNo}, #{name}, #{address}, #{lat}, #{lng}, #{phone}, #{openingHours}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Station station);

    @Update("<script>" +
            "UPDATE stations SET updated_at = NOW()" +
            "<if test='name != null'>, name = #{name}</if>" +
            "<if test='address != null'>, address = #{address}</if>" +
            "<if test='lat != null'>, lat = #{lat}</if>" +
            "<if test='lng != null'>, lng = #{lng}</if>" +
            "<if test='phone != null'>, phone = #{phone}</if>" +
            "<if test='openingHours != null'>, opening_hours = #{openingHours}</if>" +
            "<if test='status != null'>, status = #{status}</if>" +
            " WHERE id = #{id} AND deleted_at IS NULL" +
            "</script>")
    int update(Station station);

    @Update("UPDATE stations SET deleted_at = NOW(), status = 0 WHERE id = #{id} AND deleted_at IS NULL")
    int softDelete(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM stations WHERE station_no = #{stationNo} AND deleted_at IS NULL AND id != #{excludeId}")
    long countByStationNoExclude(@Param("stationNo") String stationNo, @Param("excludeId") Long excludeId);

    @Select("SELECT * FROM stations WHERE deleted_at IS NULL AND status = 1 ORDER BY name")
    List<Station> findAllActive();
}
