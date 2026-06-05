package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.SplashAd;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SplashAdMapper {

    @Select("SELECT * FROM splash_ads WHERE status = 1")
    List<SplashAd> findAllActiveAds();

    @Select("SELECT * FROM splash_ads ORDER BY weight DESC, created_at DESC")
    List<SplashAd> findAll();

    @Select("SELECT * FROM splash_ads WHERE id = #{id}")
    SplashAd findById(@Param("id") Long id);

    @Insert("INSERT INTO splash_ads(ad_no, name, image_url, target_url, weight, status, created_at, updated_at) " +
            "VALUES(#{adNo}, #{name}, #{imageUrl}, #{targetUrl}, #{weight}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SplashAd ad);

    @Update("<script>" +
            "UPDATE splash_ads " +
            "<set>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='imageUrl != null'>image_url = #{imageUrl},</if>" +
            "<if test='targetUrl != null'>target_url = #{targetUrl},</if>" +
            "<if test='weight != null'>weight = #{weight},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "updated_at = NOW()," +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(SplashAd ad);

    @Delete("DELETE FROM splash_ads WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}