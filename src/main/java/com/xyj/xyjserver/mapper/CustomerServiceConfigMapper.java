package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.CustomerServiceConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CustomerServiceConfigMapper {

    @Select("SELECT * FROM customer_service_configs WHERE status = 1 LIMIT 1")
    CustomerServiceConfig findActive();

    @Select("SELECT * FROM customer_service_configs ORDER BY id DESC")
    List<CustomerServiceConfig> findAll();

    @Select("SELECT * FROM customer_service_configs WHERE id = #{id}")
    CustomerServiceConfig findById(@Param("id") Long id);

    @Insert("INSERT INTO customer_service_configs(phone, online_time, wechat, status, created_at, updated_at) " +
            "VALUES(#{phone}, #{onlineTime}, #{wechat}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CustomerServiceConfig config);

    @Update("<script>" +
            "UPDATE customer_service_configs " +
            "<set>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "<if test='onlineTime != null'>online_time = #{onlineTime},</if>" +
            "<if test='wechat != null'>wechat = #{wechat},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "updated_at = NOW()," +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(CustomerServiceConfig config);
}
