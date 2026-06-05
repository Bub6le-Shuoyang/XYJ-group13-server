package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.UserPointsAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserPointsAccountMapper {

    @Select("SELECT * FROM user_points_accounts WHERE user_id = #{userId}")
    UserPointsAccount findByUserId(@Param("userId") Long userId);

    @Update("UPDATE user_points_accounts SET points = points - #{points} WHERE user_id = #{userId} AND points >= #{points}")
    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);

    @Update("UPDATE user_points_accounts SET coupon_count = coupon_count + 1 WHERE user_id = #{userId}")
    int increaseCouponCount(@Param("userId") Long userId);
    
    @Insert("INSERT INTO user_points_accounts(user_id, points, coupon_count, balance, member_level, monthly_signed_count, updated_at) " +
            "VALUES(#{userId}, 0, 0, 0, '普通村民', 0, NOW())")
    int insertDefaultAccount(@Param("userId") Long userId);

    // ========== Admin methods ==========

    @Select("SELECT a.*, u.nickname, u.email, u.phone FROM user_points_accounts a " +
            "LEFT JOIN users u ON a.user_id = u.id " +
            "WHERE u.deleted_at IS NULL " +
            "ORDER BY a.updated_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Map<String, Object>> findAllWithUser(@Param("offset") long offset, @Param("size") long size);

    @Select("SELECT COUNT(*) FROM user_points_accounts")
    long countAll();

    @Update("UPDATE user_points_accounts SET points = points + #{points}, updated_at = NOW() WHERE user_id = #{userId}")
    int addPoints(@Param("userId") Long userId, @Param("points") int points);

    @Update("UPDATE user_points_accounts SET balance = balance + #{amount}, updated_at = NOW() WHERE user_id = #{userId}")
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
