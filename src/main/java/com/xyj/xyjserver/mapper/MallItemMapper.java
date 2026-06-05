package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.MallItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MallItemMapper {

    @Select("SELECT COUNT(*) FROM mall_items WHERE status = 1")
    long countAvailableItems();

    @Select("SELECT * FROM mall_items WHERE status = 1 ORDER BY points ASC LIMIT #{limit} OFFSET #{offset}")
    List<MallItem> findAvailableItems(@Param("offset") long offset, @Param("limit") long limit);

    @Select("SELECT * FROM mall_items WHERE id = #{id} AND status = 1")
    MallItem findById(@Param("id") Long id);

    @Update("UPDATE mall_items SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decreaseStock(@Param("id") Long id);

    // ========== Admin methods ==========

    @Select("SELECT * FROM mall_items ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}")
    List<MallItem> findAll(@Param("offset") long offset, @Param("size") long size);

    @Select("SELECT COUNT(*) FROM mall_items")
    long countAll();

    @Select("SELECT * FROM mall_items WHERE id = #{id}")
    MallItem findByIdAny(@Param("id") Long id);

    @Select("<script>" +
            "SELECT * FROM mall_items " +
            "WHERE name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR item_no LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY created_at DESC LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<MallItem> searchByKeyword(@Param("keyword") String keyword, @Param("offset") long offset, @Param("size") long size);

    @Select("<script>" +
            "SELECT COUNT(*) FROM mall_items " +
            "WHERE name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR item_no LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%')" +
            "</script>")
    long countByKeyword(@Param("keyword") String keyword);

    @Insert("INSERT INTO mall_items(item_no, name, description, points, type, stock, status, created_at, updated_at) " +
            "VALUES(#{itemNo}, #{name}, #{description}, #{points}, #{type}, #{stock}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MallItem item);

    @Update("<script>" +
            "UPDATE mall_items " +
            "<set>" +
            "<if test='name != null'>name = #{name},</if>" +
            "<if test='description != null'>description = #{description},</if>" +
            "<if test='points != null'>points = #{points},</if>" +
            "<if test='type != null'>type = #{type},</if>" +
            "<if test='stock != null'>stock = #{stock},</if>" +
            "<if test='status != null'>status = #{status},</if>" +
            "updated_at = NOW()" +
            "</set>" +
            " WHERE id = #{id}" +
            "</script>")
    int update(MallItem item);

    @Update("UPDATE mall_items SET stock = stock + #{delta}, updated_at = NOW() WHERE id = #{id} AND stock + #{delta} >= 0")
    int adjustStock(@Param("id") Long id, @Param("delta") int delta);

    @Delete("DELETE FROM mall_items WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}