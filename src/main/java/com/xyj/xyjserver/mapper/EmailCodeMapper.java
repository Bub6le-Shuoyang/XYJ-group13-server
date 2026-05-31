package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.EmailCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface EmailCodeMapper {

    @org.apache.ibatis.annotations.Insert("INSERT INTO email_codes(email, code, used, created_at, expire_time) " +
            "VALUES(#{email}, #{code}, 0, NOW(), #{expireTime})")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(EmailCode emailCode);

    @Select("SELECT * FROM email_codes WHERE email = #{email} AND used = 0 AND expire_time > NOW() ORDER BY created_at DESC LIMIT 1")
    EmailCode findValidCodeByEmail(@Param("email") String email);

    @Update("UPDATE email_codes SET used = 1 WHERE id = #{id}")
    int markAsUsed(@Param("id") Long id);
}