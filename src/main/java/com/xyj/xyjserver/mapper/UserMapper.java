package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE (email = #{account} OR phone = #{account}) AND deleted_at IS NULL")
    User findByAccount(@Param("account") String account);

    @Select("SELECT * FROM users WHERE id = #{id} AND deleted_at IS NULL")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM users WHERE deleted_at IS NULL ORDER BY created_at DESC")
    List<User> findAll();

    @Insert("INSERT INTO users(user_no, email, password_hash, nickname, phone, status, created_at, updated_at) " +
            "VALUES(#{userNo}, #{email}, #{passwordHash}, #{nickname}, #{phone}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    /**
     * 分页搜索用户（关键词匹配 email / phone / nickname / user_no）
     */
    @Select("<script>" +
            "SELECT * FROM users WHERE deleted_at IS NULL" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (email LIKE CONCAT('%',#{keyword},'%')" +
            "    OR phone LIKE CONCAT('%',#{keyword},'%')" +
            "    OR nickname LIKE CONCAT('%',#{keyword},'%')" +
            "    OR user_no LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            " ORDER BY created_at DESC LIMIT #{offset}, #{size}" +
            "</script>")
    List<User> searchByKeyword(@Param("keyword") String keyword,
                               @Param("offset") long offset,
                               @Param("size") long size);

    /**
     * 统计匹配的用户数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM users WHERE deleted_at IS NULL" +
            "<if test='keyword != null and keyword != \"\"'>" +
            "  AND (email LIKE CONCAT('%',#{keyword},'%')" +
            "    OR phone LIKE CONCAT('%',#{keyword},'%')" +
            "    OR nickname LIKE CONCAT('%',#{keyword},'%')" +
            "    OR user_no LIKE CONCAT('%',#{keyword},'%'))" +
            "</if>" +
            "</script>")
    long countByKeyword(@Param("keyword") String keyword);

    /**
     * 更新用户基本信息
     */
    @Update("<script>" +
            "UPDATE users SET updated_at = NOW()" +
            "<if test='nickname != null'>, nickname = #{nickname}</if>" +
            "<if test='phone != null'>, phone = #{phone}</if>" +
            "<if test='email != null'>, email = #{email}</if>" +
            "<if test='status != null'>, status = #{status}</if>" +
            "<if test='avatarUrl != null'>, avatar_url = #{avatarUrl}</if>" +
            "<if test='signature != null'>, signature = #{signature}</if>" +
            "<if test='gender != null'>, gender = #{gender}</if>" +
            " WHERE id = #{id} AND deleted_at IS NULL" +
            "</script>")
    int update(User user);

    /**
     * 软删除
     */
    @Update("UPDATE users SET deleted_at = NOW(), status = 0 WHERE id = #{id} AND deleted_at IS NULL")
    int softDelete(@Param("id") Long id);

    /**
     * 检查 email 是否已被使用（排除自身）
     */
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email} AND deleted_at IS NULL AND id != #{excludeId}")
    long countByEmailExclude(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * 检查 phone 是否已被使用（排除自身）
     */
    @Select("SELECT COUNT(*) FROM users WHERE phone = #{phone} AND deleted_at IS NULL AND id != #{excludeId}")
    long countByPhoneExclude(@Param("phone") String phone, @Param("excludeId") Long excludeId);
}