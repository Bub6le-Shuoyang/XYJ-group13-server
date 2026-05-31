package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.Courier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CourierMapper {

    @Select("SELECT * FROM couriers WHERE account = #{account} OR phone = #{account}")
    Courier findByAccount(@Param("account") String account);

    @Select("SELECT * FROM couriers WHERE id = #{id}")
    Courier findById(@Param("id") Long id);

    @org.apache.ibatis.annotations.Insert("INSERT INTO couriers(courier_no, account, password_hash, name, station_id, status, created_at, updated_at) " +
            "VALUES(#{courierNo}, #{account}, #{passwordHash}, #{name}, #{stationId}, #{status}, NOW(), NOW())")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Courier courier);
}
