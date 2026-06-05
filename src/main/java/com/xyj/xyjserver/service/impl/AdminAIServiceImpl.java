package com.xyj.xyjserver.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xyj.xyjserver.mapper.AnalyticsMapper;
import com.xyj.xyjserver.service.AdminAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@Service
public class AdminAIServiceImpl implements AdminAIService {

    private static final Logger log = LoggerFactory.getLogger(AdminAIServiceImpl.class);

    private static final String API_BASE_URL = "https://api.deepseek.com";
    private static final String API_KEY = "sk-516c987ae1f94bb4adb96314061d2d12";
    private static final String MODEL = "deepseek-chat";

    @Autowired
    private AnalyticsMapper analyticsMapper;

    // ==================== 公共接口 ====================

    @Override
    public Map<String, Object> getSystemDigest() {
        return analyticsMapper.getOverallStats();
    }

    @Override
    public void chatStream(String userMessage, SseEmitter emitter) {
        try {
            // 1. 构建对话消息列表
            JSONArray messages = JSONUtil.createArray();
            messages.add(buildSystemMessage());
            messages.add(JSONUtil.createObj().set("role", "user").set("content", userMessage));

            // 2. 第一次 API 调用（携带工具定义，让 AI 决定是否调用）
            JSONObject firstRequestBody = JSONUtil.createObj()
                    .set("model", MODEL)
                    .set("messages", messages)
                    .set("tools", buildToolDefinitions())
                    .set("stream", false)
                    .set("temperature", 0.7);

            String firstResponseStr = callDeepSeekAPI(firstRequestBody);
            JSONObject firstResponse = JSONUtil.parseObj(firstResponseStr);
            JSONObject choice = firstResponse.getJSONArray("choices").getJSONObject(0);
            JSONObject assistantMsg = choice.getJSONObject("message");

            String content;

            // 3. 判断 AI 是否决定调用工具
            if (assistantMsg.containsKey("tool_calls") && !assistantMsg.getJSONArray("tool_calls").isEmpty()) {
                JSONArray toolCalls = assistantMsg.getJSONArray("tool_calls");

                log.info("AI 决定调用 {} 个工具", toolCalls.size());

                // 3a. 将 AI 的工具调用消息加入对话历史
                messages.add(buildAssistantToolCallMessage(assistantMsg, toolCalls));

                // 3b. 逐个执行工具，将结果加入对话历史
                for (int i = 0; i < toolCalls.size(); i++) {
                    JSONObject toolCall = toolCalls.getJSONObject(i);
                    String funcName = toolCall.getJSONObject("function").getStr("name");
                    String argsStr = toolCall.getJSONObject("function").getStr("arguments");
                    String toolCallId = toolCall.getStr("id");

                    log.info("执行工具: {}, 参数: {}", funcName, argsStr);

                    String result = executeTool(funcName, argsStr);

                    messages.add(JSONUtil.createObj()
                            .set("role", "tool")
                            .set("tool_call_id", toolCallId)
                            .set("content", result));
                }

                // 3c. 第二次 API 调用（带上工具执行结果，让 AI 生成最终回复）
                JSONObject secondRequestBody = JSONUtil.createObj()
                        .set("model", MODEL)
                        .set("messages", messages)
                        .set("stream", false)
                        .set("temperature", 0.7);

                String secondResponseStr = callDeepSeekAPI(secondRequestBody);
                JSONObject secondResponse = JSONUtil.parseObj(secondResponseStr);
                content = secondResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getStr("content");

            } else {
                // AI 认为不需要调用工具，直接返回文本回复
                content = assistantMsg.getStr("content");
            }

            if (content == null || content.isEmpty()) {
                content = "抱歉，AI 助手暂时无法生成回复，请稍后重试。";
            }

            // 4. 模拟流式推送：将完整回复分块发送到 SSE
            simulateStream(content, emitter);

        } catch (Exception e) {
            log.error("AI 对话异常", e);
            try {
                emitter.send(SseEmitter.event().data("error: " + e.getMessage()));
            } catch (Exception ignored) {
            }
            emitter.complete();
        }
    }

    // ==================== 系统提示词 ====================

    private JSONObject buildSystemMessage() {
        String systemPrompt = "你是\"乡驿家\"驿站末端配送管理系统的 AI 智能助手。\n\n" +
                "你的职责是帮助管理员分析系统运营数据、发现潜在问题、提供优化建议。\n\n" +
                "你可以使用以下工具来查询实时系统数据：\n" +
                "- get_overall_stats：获取系统整体概览（包裹总量、用户数、骑手数、站点数、各状态包裹数等）\n" +
                "- get_package_trend：获取近 N 天的每日包裹数量趋势\n" +
                "- get_user_growth：获取近 N 天的每日用户注册趋势\n" +
                "- get_package_status_distribution：获取各状态包裹的数量分布\n" +
                "- get_courier_efficiency：获取配送员效率排行榜（完成数、收入等）\n" +
                "- get_station_breakdown：获取各站点的包裹分布和运营明细\n" +
                "- get_financial_overview：获取财务总览（积分、余额、优惠券、兑换、钱包流水）\n\n" +
                "回答规则：\n" +
                "1. 根据用户的问题，主动调用合适的工具获取数据，不要凭猜测回答\n" +
                "2. 如果一个问题涉及多个维度，可以同时调用多个工具\n" +
                "3. 回答要基于真实数据，简洁专业，使用中文\n" +
                "4. 适当使用 Markdown 格式（标题、列表、表格、加粗等）让回答结构清晰\n" +
                "5. 在数据分析后给出你的专业见解和建议\n" +
                "6. 如果用户问的问题与系统管理无关，礼貌提醒并引导回系统相关话题";

        return JSONUtil.createObj().set("role", "system").set("content", systemPrompt);
    }

    // ==================== 工具定义 ====================

    private JSONArray buildToolDefinitions() {
        JSONArray tools = JSONUtil.createArray();

        // 1. 系统概览
        tools.add(buildTool("get_overall_stats",
                "获取系统整体运营概览数据，包括包裹总量、今日新增、各状态包裹数（待入库/在库/派送中/已完成）、注册用户数、活跃配送员数、活跃站点数"));

        // 2. 包裹趋势
        tools.add(buildToolWithParams("get_package_trend",
                "获取最近若干天的每日包裹数量趋势数据，可用于分析包裹量变化走势",
                new String[]{"days"}, new String[]{"integer"}, new String[]{"查询天数，默认7天，最大30天"},
                new String[]{}, false));

        // 3. 用户增长
        tools.add(buildToolWithParams("get_user_growth",
                "获取最近若干天的每日新注册用户数量趋势",
                new String[]{"days"}, new String[]{"integer"}, new String[]{"查询天数，默认7天，最大30天"},
                new String[]{}, false));

        // 4. 包裹状态分布
        tools.add(buildTool("get_package_status_distribution",
                "获取当前各状态包裹的数量分布（待入库 PENDING_INBOUND、在库 IN_STOCK、已发布 TASK_PUBLISHED、派送中 DELIVERING、已完成 COMPLETED）"));

        // 5. 配送员效率
        tools.add(buildToolWithParams("get_courier_efficiency",
                "获取配送员效率排行榜，包含每位配送员的已完成任务数、进行中任务数、总任务数、累计收入",
                new String[]{"limit"}, new String[]{"integer"}, new String[]{"返回前N名配送员，默认10"},
                new String[]{}, false));

        // 6. 站点明细
        tools.add(buildTool("get_station_breakdown",
                "获取各站点的运营明细，包括每个站点的包裹总数、待入库数、在库数、派送中数、已完成数"));

        // 7. 财务总览
        tools.add(buildTool("get_financial_overview",
                "获取系统财务总览数据，包括全平台积分总量、账户余额总量、优惠券发放量、兑换记录数、钱包奖励和消费笔数"));

        return tools;
    }

    // ==================== 工具执行 ====================

    private String executeTool(String functionName, String argsStr) {
        try {
            JSONObject args = JSONUtil.parseObj(argsStr);
            switch (functionName) {
                case "get_overall_stats":
                    return JSONUtil.toJsonStr(analyticsMapper.getOverallStats());

                case "get_package_trend":
                    int days = args.getInt("days", 7);
                    if (days > 30) days = 30;
                    if (days < 1) days = 7;
                    return JSONUtil.toJsonStr(analyticsMapper.getPackageTrend(days));

                case "get_user_growth":
                    int gDays = args.getInt("days", 7);
                    if (gDays > 30) gDays = 30;
                    if (gDays < 1) gDays = 7;
                    return JSONUtil.toJsonStr(analyticsMapper.getUserGrowth(gDays));

                case "get_package_status_distribution":
                    return JSONUtil.toJsonStr(analyticsMapper.getPackageStatusDistribution());

                case "get_courier_efficiency":
                    int limit = args.getInt("limit", 10);
                    if (limit > 50) limit = 50;
                    if (limit < 1) limit = 10;
                    return JSONUtil.toJsonStr(analyticsMapper.getCourierEfficiency(limit));

                case "get_station_breakdown":
                    return JSONUtil.toJsonStr(analyticsMapper.getStationPackageBreakdown());

                case "get_financial_overview":
                    return JSONUtil.toJsonStr(analyticsMapper.getFinancialOverview());

                default:
                    log.warn("未知工具: {}", functionName);
                    return "{\"error\": \"未知工具: " + functionName + "\"}";
            }
        } catch (Exception e) {
            log.error("工具执行失败: {}", functionName, e);
            return "{\"error\": \"工具执行失败: " + e.getMessage() + "\"}";
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 调用 DeepSeek API
     */
    private String callDeepSeekAPI(JSONObject requestBody) {
        return HttpRequest.post(API_BASE_URL + "/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(60000)
                .execute()
                .body();
    }

    /**
     * 构建包含 tool_calls 的 assistant 消息（用于对话历史）
     */
    private JSONObject buildAssistantToolCallMessage(JSONObject originalMsg, JSONArray toolCalls) {
        JSONArray tcArray = JSONUtil.createArray();
        for (int i = 0; i < toolCalls.size(); i++) {
            JSONObject tc = toolCalls.getJSONObject(i);
            tcArray.add(JSONUtil.createObj()
                    .set("id", tc.getStr("id"))
                    .set("type", "function")
                    .set("function", JSONUtil.createObj()
                            .set("name", tc.getJSONObject("function").getStr("name"))
                            .set("arguments", tc.getJSONObject("function").getStr("arguments"))));
        }
        return JSONUtil.createObj()
                .set("role", "assistant")
                .set("content", originalMsg.getStr("content", ""))
                .set("tool_calls", tcArray);
    }

    /**
     * 模拟流式推送：按行分块通过 SSE 发送，对 Markdown 渲染更友好
     */
    private void simulateStream(String content, SseEmitter emitter) throws Exception {
        String[] lines = content.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            emitter.send(SseEmitter.event().data(lines[i]));
            Thread.sleep(30);
        }
        emitter.send(SseEmitter.event().data("[DONE]"));
        emitter.complete();
    }

    /**
     * 构建无参数的工具定义
     */
    private JSONObject buildTool(String name, String description) {
        return buildToolWithParams(name, description,
                new String[]{}, new String[]{}, new String[]{},
                new String[]{}, false);
    }

    /**
     * 构建带参数的工具定义（DeepSeek Function Calling 格式）
     */
    private JSONObject buildToolWithParams(String name, String description,
                                           String[] paramNames, String[] paramTypes, String[] paramDescs,
                                           String[] requiredParams, boolean hasRequired) {
        JSONObject properties = JSONUtil.createObj();
        JSONArray required = JSONUtil.createArray();

        for (int i = 0; i < paramNames.length; i++) {
            properties.set(paramNames[i], JSONUtil.createObj()
                    .set("type", paramTypes[i])
                    .set("description", paramDescs[i]));
        }
        if (hasRequired) {
            for (String r : requiredParams) {
                required.add(r);
            }
        }

        JSONObject parameters = JSONUtil.createObj()
                .set("type", "object")
                .set("properties", properties)
                .set("required", required);

        JSONObject function = JSONUtil.createObj()
                .set("name", name)
                .set("description", description)
                .set("parameters", parameters);

        return JSONUtil.createObj()
                .set("type", "function")
                .set("function", function);
    }
}
