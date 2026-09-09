package com.cnu.zyx.backend.controller;

import com.cnu.zyx.backend.entity.AiChat;
import com.cnu.zyx.backend.service.AiChatService;
import com.cnu.zyx.backend.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aichat")
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    // 模拟AI对话，后期替换成真实大模型调用
    @PostMapping("/send")
    public Result<String> send(@RequestBody AiChat aiChat){
        // 模拟AI返回文本，后面对接第三方API直接改这里
        String mockAnswer = "【AI模拟回复】收到你的问题：" + aiChat.getQuestion() + "，正式版本将接入真实AI诊断模型";
        aiChat.setAnswer(mockAnswer);
        aiChatService.saveChat(aiChat);
        return Result.success(mockAnswer);
    }

    // 查询个人AI聊天历史记录
    @GetMapping("/history/{userId}")
    public Result<List<AiChat>> history(@PathVariable Long userId){
        List<AiChat> list = aiChatService.getUserHistory(userId);
        return Result.success(list);
    }
}