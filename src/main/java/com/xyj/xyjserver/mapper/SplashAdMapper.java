package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.SplashAd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SplashAdMapper {

    @Select("SELECT * FROM splash_ads WHERE status = 1")
    List<SplashAd> findAllActiveAds();
}