package com.cnu.zyx.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PatientAdminVO {
    private Long id;
    private String nickname;
    private String gender;
    private Integer age;
    private String phone;
    private LocalDateTime createTime;
}