package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.HelpItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HelpItemMapper {

    @Select("SELECT * FROM help_items ORDER BY sort_order ASC, id DESC")
    List<HelpItem> findAll();

    @Select("SELECT * FROM help_items WHERE id = #{id}")
    HelpItem findById(@Param("id") Long id);

    @Insert("INSERT INTO help_items(help_no, title, content, sort_order, status, created_at) " +
            "VALUES(#{helpNo}, #{title}, #{content}, #{sortOrder}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(HelpItem item);

    @Update("<script>" +
            "UPDATE help_items " +
            "<set>" +
            "<if test='title != null'>title = #{title},</if>" +
            "<if test='content != null'>content = #{content},</if>" +
            "<if test='sortOrder != null'>sort_order = #{sortOrder},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int update(HelpItem item);

    @Delete("DELETE FROM help_items WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
