package com.cnu.zyx.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorInfo {
    private Long id;
    private Long userId;
    private String realName;
    private String department;
    private String hospital;
    private String title;
    private String auditStatus;
    private String intro;
    private String auditMsg;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 新增：资质附件OSS地址，多个文件英文逗号分隔
    private String certFile;
}