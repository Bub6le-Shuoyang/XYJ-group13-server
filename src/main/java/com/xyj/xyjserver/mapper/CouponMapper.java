package com.xyj.xyjserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface CouponMapper {

    @Insert("INSERT INTO user_coupons(coupon_no, user_id, name, amount, status, source, expire_time, created_at, updated_at) " +
            "VALUES(#{couponNo}, #{userId}, #{name}, #{amount}, 'AVAILABLE', #{source}, DATE_ADD(NOW(), INTERVAL 30 DAY), NOW(), NOW())")
    int issueCoupon(@Param("userId") Long userId,
                    @Param("couponNo") String couponNo,
                    @Param("name") String name,
                    @Param("amount") BigDecimal amount,
                    @Param("source") String source);

    @Select("SELECT c.*, u.nickname as user_nickname FROM user_coupons c " +
            "LEFT JOIN users u ON c.user_id = u.id " +
            "ORDER BY c.created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Map<String, Object>> findAll(@Param("offset") long offset, @Param("size") long size);

    @Select("SELECT COUNT(*) FROM user_coupons")
    long countAll();
}
