package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.PageResult;
import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.service.AdminContentService;
import com.xyj.xyjserver.vo.NewsPostVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/content")
@Tag(name = "AdminContent 内容运营接口")
public class AdminContentController {

    @Autowired
    private AdminContentService adminContentService;

    @Operation(summary = "新闻列表（分页）")
    @GetMapping("/news")
    public Result<PageResult<NewsPostVO>> getNews(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size) {
        return Result.success(adminContentService.getNews(page, size));
    }

    @Operation(summary = "编辑新闻")
    @PutMapping("/news/{id}")
    public Result<Void> editNews(
            @Parameter(description = "新闻ID", example = "1") @PathVariable("id") Long id,
            @RequestBody Map<String, Object> body) {
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        String tag = (String) body.get("tag");
        Boolean isUrgent = body.containsKey("is_urgent") ? (Boolean) body.get("is_urgent") : null;
        adminContentService.editNews(id, title, content, tag, isUrgent);
        return Result.success();
    }

    @Operation(summary = "删除新闻")
    @DeleteMapping("/news/{id}")
    public Result<Void> deleteNews(
            @Parameter(description = "新闻ID", example = "1") @PathVariable("id") Long id) {
        adminContentService.deleteNews(id);
        return Result.success();
    }

    @Operation(summary = "评论列表（分页）")
    @GetMapping("/comments")
    public Result<PageResult<Map<String, Object>>> getComments(
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页条数", example = "10") @RequestParam(defaultValue = "10") Long size) {
        return Result.success(adminContentService.getComments(page, size));
    }

    @Operation(summary = "切换评论状态（审核/屏蔽）")
    @PutMapping("/comments/{id}/toggle")
    public Result<Void> toggleCommentStatus(
            @Parameter(description = "评论ID", example = "1") @PathVariable("id") Long id,
            @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        adminContentService.toggleCommentStatus(id, status);
        return Result.success();
    }
}
