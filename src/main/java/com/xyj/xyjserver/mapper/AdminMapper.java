package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {

    @Select("SELECT * FROM admins WHERE username = #{username} OR email = #{username} OR phone = #{username}")
    Admin findByAccount(@Param("username") String username);

    @Select("SELECT * FROM admins WHERE id = #{id}")
    Admin findById(@Param("id") Long id);

    @Update("UPDATE admins SET password_hash = #{passwordHash} WHERE email = #{email}")
    int updatePasswordByEmail(@Param("email") String email, @Param("passwordHash") String passwordHash);

    @org.apache.ibatis.annotations.Insert("INSERT INTO admins(username, email, password_hash, role, status) VALUES(#{username}, #{email}, #{passwordHash}, #{role}, #{status})")
    int insert(Admin admin);

    @Update("<script>" +
            "UPDATE admins " +
            "<set>" +
            "<if test='realName != null'>real_name = #{realName},</if>" +
            "<if test='phone != null'>phone = #{phone},</if>" +
            "<if test='avatarUrl != null'>avatar_url = #{avatarUrl}</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateProfile(Admin admin);
}