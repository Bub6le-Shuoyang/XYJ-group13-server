package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.vo.StationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
