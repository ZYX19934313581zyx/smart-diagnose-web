package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.AiChat;
import java.util.List;

public interface AiChatService {
    int saveChat(AiChat aiChat);
    List<AiChat> getUserHistory(Long userId);
}