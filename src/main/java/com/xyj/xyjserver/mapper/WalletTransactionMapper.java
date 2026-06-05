package com.xyj.xyjserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface WalletTransactionMapper {

    @Insert("INSERT INTO wallet_transactions(user_id, type, amount, description, created_at) " +
            "VALUES(#{userId}, #{type}, #{amount}, #{description}, NOW())")
    int insert(@Param("userId") Long userId,
               @Param("type") String type,
               @Param("amount") BigDecimal amount,
               @Param("description") String description);

    @Select("<script>" +
            "SELECT w.*, u.nickname as user_nickname FROM wallet_transactions w " +
            "LEFT JOIN users u ON w.user_id = u.id " +
            "<where>" +
            "<if test='userId != null'> AND w.user_id = #{userId}</if>" +
            "<if test='type != null'> AND w.type = #{type}</if>" +
            "</where>" +
            " ORDER BY w.created_at DESC LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Map<String, Object>> findAll(@Param("offset") long offset,
                                      @Param("size") long size,
                                      @Param("userId") Long userId,
                                      @Param("type") String type);

    @Select("<script>" +
            "SELECT COUNT(*) FROM wallet_transactions " +
            "<where>" +
            "<if test='userId != null'> AND user_id = #{userId}</if>" +
            "<if test='type != null'> AND type = #{type}</if>" +
            "</where>" +
            "</script>")
    long countAll(@Param("userId") Long userId, @Param("type") String type);
}
