package com.cnu.zyx.backend.service.impl;

import com.cnu.zyx.backend.entity.ConsultReply;
import com.cnu.zyx.backend.entity.User;
import com.cnu.zyx.backend.mapper.ConsultReplyMapper;
import com.cnu.zyx.backend.mapper.UserMapper;
import com.cnu.zyx.backend.service.ConsultReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ConsultReplyServiceImpl implements ConsultReplyService {

    @Autowired
    private ConsultReplyMapper consultReplyMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public int add(ConsultReply consultReply, Long userId) {
        // 强制固定发送人ID（以token为准，拒绝前端传入senderId）
        consultReply.setSenderId(userId);
        // 默认新消息为未读
        consultReply.setIsRead(0);
        // 根据userId查询用户角色，自动设置senderType
        User loginUser = userMapper.selectById(userId);
        if ("doctor".equals(loginUser.getRole())){
            consultReply.setSenderType(1);
        }else {
            // patient / admin 统一作为患者类型0
            consultReply.setSenderType(0);
        }
        return consultReplyMapper.insert(consultReply);
    }

    @Override
    public List<ConsultReply> getChatList(Long consultId) {
        return consultReplyMapper.selectByConsultId(consultId);
    }

    @Override
    public int getUnReadCount(Long receiveId) {
        Integer count = consultReplyMapper.selectUnReadCount(receiveId);
        return count == null ? 0 : count;
    }

    @Override
    public List<Long> getUnReadConsultList(Long receiveId) {
        return consultReplyMapper.selectUnReadConsultIdList(receiveId);
    }

    @Override
    public boolean markRead(Long consultId, Long receiveId) {
        int rows = consultReplyMapper.batchReadByConsult(consultId, receiveId);
        return rows > 0;
    }
}