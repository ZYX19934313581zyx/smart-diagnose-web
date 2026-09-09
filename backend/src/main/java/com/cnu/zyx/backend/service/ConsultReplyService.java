package com.cnu.zyx.backend.service;

import com.cnu.zyx.backend.entity.ConsultReply;
import java.util.List;

public interface ConsultReplyService {
    // 新增回复对话：传入登录用户ID，后端自动处理角色
    int add(ConsultReply consultReply, Long userId);

    // 获取某一条问诊的全部聊天记录
    List<ConsultReply> getChatList(Long consultId);

    // 获取当前用户未读消息总数
    int getUnReadCount(Long receiveId);

    // 获取存在未读消息的问诊id列表
    List<Long> getUnReadConsultList(Long receiveId);

    // 标记该问诊对话全部消息已读
    boolean markRead(Long consultId,Long receiveId);
}