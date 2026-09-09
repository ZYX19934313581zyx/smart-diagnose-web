package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.AiChat;
import com.cnu.zyx.backend.mapper.AiChatMapper;
import com.cnu.zyx.backend.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiChatMapper aiChatMapper;

    @Override
    public int saveChat(AiChat aiChat) {
        return aiChatMapper.insert(aiChat);
    }

    @Override
    public List<AiChat> getUserHistory(Long userId) {
        return aiChatMapper.selectByUserId(userId);
    }
}