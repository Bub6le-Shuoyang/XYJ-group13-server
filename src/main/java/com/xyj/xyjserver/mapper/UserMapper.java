package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE email = #{account} OR phone = #{account}")
    User findByAccount(@Param("account") String account);

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM users ORDER BY created_at DESC")
    List<User> findAll();

    @org.apache.ibatis.annotations.Insert("INSERT INTO users(user_no, email, password_hash, nickname, status, created_at, updated_at) " +
            "VALUES(#{userNo}, #{email}, #{passwordHash}, #{nickname}, #{status}, NOW(), NOW())")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}