package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.dto.CommentDTO;
import com.xyj.xyjserver.dto.NewsPostDTO;
import com.xyj.xyjserver.service.ContentService;
import com.xyj.xyjserver.vo.CommentVO;
import com.xyj.xyjserver.vo.NewsPostVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/content")
@Tag(name = "Content 接口")
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 获取乡镇资讯
     */
    @Operation(summary = "获取乡镇资讯")
    @GetMapping("/news")
    public Result<PageResult<NewsPostVO>> getNews(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "20") Long size) {
        return Result.success(contentService.getNews(page, size));
    }

    /**
     * 发布乡镇资讯
     */
    @Operation(summary = "发布乡镇资讯")
    @PostMapping("/news")
    public Result<NewsPostVO> publishNews(
            HttpServletRequest request,
            @Validated @RequestBody NewsPostDTO postDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(contentService.publishNews(userId, postDTO));
    }

    /**
     * 点赞资讯
     */
    @Operation(summary = "点赞资讯")
    @PostMapping("/news/{news_id}/like")
    public Result<Boolean> likeNews(
            HttpServletRequest request,
            @PathVariable("news_id") Long newsId) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(contentService.likeNews(userId, newsId));
    }

    /**
     * 评论资讯
     */
    @Operation(summary = "评论资讯")
    @PostMapping("/news/{news_id}/comments")
    public Result<CommentVO> commentNews(
            HttpServletRequest request,
            @PathVariable("news_id") Long newsId,
            @Validated @RequestBody CommentDTO commentDTO) {
        Long userId = (Long) request.getAttribute(AuthInterceptor.USER_ID_ATTR);
        return Result.success(contentService.commentNews(userId, newsId, commentDTO));
    }
}