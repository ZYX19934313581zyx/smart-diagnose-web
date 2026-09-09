package com.cnu.zyx.backend.mapper;

import com.cnu.zyx.backend.entity.ConsultReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultReplyMapper {
    // 新增一条对话回复
    int insert(ConsultReply consultReply);

    // 根据问诊id查询全部对话记录，按时间升序
    List<ConsultReply> selectByConsultId(@Param("consultId") Long consultId);

    /**
     * 查询我的未读消息数量（receiveId = 当前登录用户 && isRead=0）
     */
    Integer selectUnReadCount(@Param("receiveId") Long receiveId);

    /**
     * 获取当前用户所有存在未读消息的问诊id列表
     */
    List<Long> selectUnReadConsultIdList(@Param("receiveId") Long receiveId);

    /**
     * 将某个问诊下发给我的全部消息批量改为已读
     */
    int batchReadByConsult(@Param("consultId") Long consultId, @Param("receiveId") Long receiveId);
}