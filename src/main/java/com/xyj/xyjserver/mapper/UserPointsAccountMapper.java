package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.UserPointsAccount;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserPointsAccountMapper {

    @Select("SELECT * FROM user_points_accounts WHERE user_id = #{userId}")
    UserPointsAccount findByUserId(@Param("userId") Long userId);

    @Update("UPDATE user_points_accounts SET points = points - #{points} WHERE user_id = #{userId} AND points >= #{points}")
    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);
    
    @Insert("INSERT INTO user_points_accounts(user_id, points, coupon_count, balance, member_level, monthly_signed_count, updated_at) " +
            "VALUES(#{userId}, 0, 0, 0, '普通村民', 0, NOW())")
    int insertDefaultAccount(@Param("userId") Long userId);
}