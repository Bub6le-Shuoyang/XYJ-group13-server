package com.xyj.xyjserver.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface AdminAIService {

    /**
     * 获取系统数据摘要（供 AI 上下文使用）
     */
    Map<String, Object> getSystemDigest();

    /**
     * 与 AI 进行流式对话（SSE）
     *
     * @param userMessage 用户输入消息
     * @param emitter     SSE 发射器
     */
    void chatStream(String userMessage, SseEmitter emitter);
}
