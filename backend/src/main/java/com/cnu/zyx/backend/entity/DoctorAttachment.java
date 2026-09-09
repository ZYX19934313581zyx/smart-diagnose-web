package com.cnu.zyx.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DoctorAttachment {
    private Long id;
    private Long doctorId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createTime;
}