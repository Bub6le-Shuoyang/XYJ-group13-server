package com.xyj.xyjserver.mapper;

import com.xyj.xyjserver.vo.CommentVO;
import com.xyj.xyjserver.vo.NewsPostVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ContentMapper {

    @Select("""
            SELECT
                p.id,
                p.title,
                p.content,
                p.tag,
                p.is_urgent,
                p.created_at AS publish_time,
                p.likes,
                COUNT(c.id) AS comments_count,
                COALESCE(u.nickname, a.real_name, a.username, CONCAT('用户', p.author_id)) AS author_name,
                s.name AS station_name
            FROM news_posts p
            LEFT JOIN news_comments c ON c.post_id = p.id AND c.status = 1
            LEFT JOIN users u ON p.author_type = 'USER' AND p.author_id = u.id
            LEFT JOIN admins a ON p.author_type = 'ADMIN' AND p.author_id = a.id
            LEFT JOIN stations s ON p.station_id = s.id
            GROUP BY p.id
            ORDER BY p.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<NewsPostVO> findNews(@Param("offset") Long offset, @Param("size") Long size);

    @Select("SELECT COUNT(*) FROM news_posts")
    Long countNews();

    @Select("""
            SELECT
                p.id,
                p.title,
                p.content,
                p.tag,
                p.is_urgent,
                p.created_at AS publish_time,
                p.likes,
                COUNT(c.id) AS comments_count,
                COALESCE(u.nickname, a.real_name, a.username, CONCAT('用户', p.author_id)) AS author_name,
                s.name AS station_name
            FROM news_posts p
            LEFT JOIN news_comments c ON c.post_id = p.id AND c.status = 1
            LEFT JOIN users u ON p.author_type = 'USER' AND p.author_id = u.id
            LEFT JOIN admins a ON p.author_type = 'ADMIN' AND p.author_id = a.id
            LEFT JOIN stations s ON p.station_id = s.id
            WHERE p.post_no = #{postNo}
            GROUP BY p.id
            """)
    NewsPostVO findNewsByPostNo(@Param("postNo") String postNo);

    @Insert("""
            INSERT INTO news_posts(post_no, title, content, tag, author_id, author_type, station_id, is_urgent, created_at, updated_at)
            VALUES(#{postNo}, #{title}, #{content}, #{tag}, #{authorId}, #{authorType}, #{stationId}, #{isUrgent}, NOW(), NOW())
            """)
    int insertNews(
            @Param("postNo") String postNo,
            @Param("title") String title,
            @Param("content") String content,
            @Param("tag") String tag,
            @Param("authorId") Long authorId,
            @Param("authorType") String authorType,
            @Param("stationId") Long stationId,
            @Param("isUrgent") Boolean isUrgent);

    @Select("SELECT station_id FROM admins WHERE id = #{adminId}")
    Long findAdminStationId(@Param("adminId") Long adminId);

    @Select("SELECT id FROM stations ORDER BY id LIMIT 1")
    Long findDefaultStationId();

    @Update("UPDATE news_posts SET likes = likes + 1 WHERE id = #{newsId}")
    int increaseNewsLikes(@Param("newsId") Long newsId);

    @Insert("""
            INSERT INTO news_comments(post_id, user_id, content, status, created_at, updated_at)
            VALUES(#{newsId}, #{userId}, #{content}, 1, NOW(), NOW())
            """)
    int insertComment(@Param("newsId") Long newsId, @Param("userId") Long userId, @Param("content") String content);

    @Select("""
            SELECT
                c.id,
                c.content,
                COALESCE(u.nickname, CONCAT('用户', c.user_id)) AS author,
                c.created_at AS time
            FROM news_comments c
            LEFT JOIN users u ON c.user_id = u.id
            WHERE c.id = LAST_INSERT_ID()
            """)
    CommentVO findLastInsertedComment();

    // ===== Admin methods =====

    @Update("<script>" +
            "UPDATE news_posts " +
            "<set>" +
            "<if test='title != null'>title = #{title},</if>" +
            "<if test='content != null'>content = #{content},</if>" +
            "<if test='tag != null'>tag = #{tag},</if>" +
            "<if test='isUrgent != null'>is_urgent = #{isUrgent},</if>" +
            "updated_at = NOW()," +
            "</set>" +
            "WHERE id = #{id}" +
            "</script>")
    int updateNews(@Param("id") Long id,
                   @Param("title") String title,
                   @Param("content") String content,
                   @Param("tag") String tag,
                   @Param("isUrgent") Boolean isUrgent);

    @Delete("DELETE FROM news_posts WHERE id = #{id}")
    int deleteNews(@Param("id") Long id);

    @Select("""
            SELECT
                c.id,
                c.content,
                COALESCE(u.nickname, CONCAT('用户', c.user_id)) AS author,
                c.created_at AS time,
                c.status
            FROM news_comments c
            LEFT JOIN users u ON c.user_id = u.id
            WHERE c.post_id = #{postId}
            ORDER BY c.created_at DESC
            """)
    List<Map<String, Object>> findCommentsByPostId(@Param("postId") Long postId);

    @Select("""
            SELECT
                c.id,
                c.content,
                COALESCE(u.nickname, CONCAT('用户', c.user_id)) AS author,
                c.status,
                c.created_at AS time,
                p.title AS post_title
            FROM news_comments c
            LEFT JOIN users u ON c.user_id = u.id
            LEFT JOIN news_posts p ON c.post_id = p.id
            ORDER BY c.created_at DESC
            LIMIT #{offset}, #{size}
            """)
    List<Map<String, Object>> findAllComments(@Param("offset") long offset, @Param("size") long size);

    @Select("SELECT COUNT(*) FROM news_comments")
    Long countAllComments();

    @Update("UPDATE news_comments SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int toggleCommentStatus(@Param("id") Long id, @Param("status") Integer status);
}
