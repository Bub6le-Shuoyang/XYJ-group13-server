package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.MallRedeemRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface MallRedeemRecordMapper {

    @Insert("INSERT INTO mall_redeem_records(record_no, user_id, item_id, item_name, points_cost, remain_points, status, created_at, updated_at) " +
            "VALUES(#{recordNo}, #{userId}, #{itemId}, #{itemName}, #{pointsCost}, #{remainPoints}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MallRedeemRecord record);

    @Select("SELECT COUNT(*) FROM mall_redeem_records WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM mall_redeem_records WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit} OFFSET #{offset}")
    List<MallRedeemRecord> findByUserId(@Param("userId") Long userId, @Param("offset") long offset, @Param("limit") long limit);

    // ========== Admin methods ==========

    @Select("SELECT r.*, u.nickname as user_nickname FROM mall_redeem_records r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "ORDER BY r.created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<Map<String, Object>> findAll(@Param("offset") long offset, @Param("size") long size);

    @Select("SELECT COUNT(*) FROM mall_redeem_records")
    long countAll();

    @Select("SELECT * FROM mall_redeem_records WHERE id = #{id}")
    MallRedeemRecord findById(@Param("id") Long id);

    @Update("UPDATE mall_redeem_records SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}