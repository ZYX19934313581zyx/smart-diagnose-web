package com.cnu.zyx.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultReply {
    private Long id;
    private Long consultId;
    private Long senderId;
    private Integer senderType;
    private String content;
    private Integer replyScope;
    private LocalDateTime createTime;
    private Integer isRead;

    // 前端聊天展示用扩展字段（数据库不存在，关联查询返回）
    private String nickname;
    private String realName;
    // 新增用户头像（来自sys_user表）
    private String avatar;
}