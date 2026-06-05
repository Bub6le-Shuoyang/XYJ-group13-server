package com.xyj.xyjserver.controller;

import com.xyj.xyjserver.common.api.Result;
import com.xyj.xyjserver.common.api.ResultCode;
import com.xyj.xyjserver.common.exception.BusinessException;
import com.xyj.xyjserver.common.interceptor.AuthInterceptor;
import com.xyj.xyjserver.service.AdminAIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/ai")
@Tag(name = "AdminAI 智能AI中台接口")
public class AdminAIController {

    @Autowired
    private AdminAIService adminAIService;

    @Operation(summary = "获取系统数据摘要")
    @GetMapping("/digest")
    public Result<Map<String, Object>> getDigest(HttpServletRequest request) {
        requireRole(request);
        return Result.success(adminAIService.getSystemDigest());
    }

    @Operation(summary = "AI对话（SSE流式返回）")
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(HttpServletRequest request, @RequestBody Map<String, String> body) {
        requireRole(request);
        String message = body.getOrDefault("message", "");

        SseEmitter emitter = new SseEmitter(120000L); // 2 分钟超时
        emitter.onCompletion(() -> {});
        emitter.onTimeout(() -> {});
        emitter.onError(e -> {});

        // 在独立线程中执行 AI 调用，避免阻塞主线程
        new Thread(() -> {
            try {
                adminAIService.chatStream(message, emitter);
            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event().data("ERROR: " + e.getMessage()));
                } catch (Exception ignored) {
                }
                emitter.complete();
            }
        }).start();

        return emitter;
    }

    /**
     * 校验当前请求是否为管理员角色
     */
    private void requireRole(HttpServletRequest request) {
        String role = (String) request.getAttribute(AuthInterceptor.USER_ROLE_ATTR);
        if (!"ADMIN".equals(role)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "当前角色无权访问管理员接口");
        }
    }
}
