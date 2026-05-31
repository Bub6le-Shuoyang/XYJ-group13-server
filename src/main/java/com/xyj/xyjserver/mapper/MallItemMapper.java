package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.entity.MallItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
}