package com.cnu.zyx.backend.util;

import com.cnu.zyx.backend.config.AiQwenConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QwenOpenAiUtil {

    @Autowired
    private AiQwenConfig aiQwenConfig;

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    // 构建SSE流式请求
    public Request buildSseRequest(String systemContent, String userContent) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", aiQwenConfig.getModel());
        bodyMap.put("stream", true);

        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemContent);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userContent);

        bodyMap.put("messages", List.of(sysMsg, userMsg));
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(bodyMap);
        } catch (Exception e) {
            throw new RuntimeException("构建AI请求JSON失败", e);
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        // 修正顺序：MediaType在前，json字符串在后
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        return new Request.Builder()
                .url(aiQwenConfig.getBaseUrl() + "chat/completions")
                .header("Authorization", "Bearer " + aiQwenConfig.getApiKey())
                .post(body)
                .build();
    }

    // 解析SSE单条数据，提取文字内容
    public String parseSseContent(String data) {
        try {
            JsonNode node = objectMapper.readTree(data);
            JsonNode choices = node.path("choices");
            if (!choices.isArray() || choices.isEmpty()) {
                return null;
            }
            JsonNode delta = choices.get(0).path("delta");
            return delta.path("content").asText(null);
        } catch (IOException e) {
            return null;
        }
    }

    // 同步非流式调用（科室推荐接口用）
    public String syncChat(String systemContent, String userContent) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("model", aiQwenConfig.getModel());
        bodyMap.put("stream", false);

        Map<String, String> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemContent);

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userContent);

        bodyMap.put("messages", List.of(sysMsg, userMsg));
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(bodyMap);
        } catch (Exception e) {
            throw new RuntimeException("构建同步AI请求失败", e);
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
        // 修正顺序
        RequestBody body = RequestBody.create(mediaType, jsonBody);
        Request request = new Request.Builder()
                .url(aiQwenConfig.getBaseUrl() + "chat/completions")
                .header("Authorization", "Bearer " + aiQwenConfig.getApiKey())
                .post(body)
                .build();

        try (Response resp = okHttpClient.newCall(request).execute()) {
            String respStr = resp.body().string();
            JsonNode root = objectMapper.readTree(respStr);
            JsonNode choices = root.path("choices");
            return choices.get(0).path("message").path("content").asText();
        } catch (Exception e) {
            throw new RuntimeException("同步AI调用失败", e);
        }
    }
}