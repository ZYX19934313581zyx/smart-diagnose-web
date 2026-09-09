package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.AiChat;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface AiChatMapper {
    int insert(AiChat aiChat);
    List<AiChat> selectByUserId(Long userId);
}