package com.cnu.zyx.backend.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;
    //新增字段
    private Integer status;
    private LocalDateTime createTime;

    // 用户头像OSS访问链接
    private String avatar;
}