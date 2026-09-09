package com.cnu.zyx.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consult {
    private Long id;
    private Long userId;
    private Long doctorId;
    private String title;
    private String symptom;
    private Long departmentId;
    private String pastMedical;
    private String operationHistory;
    private String allergyHistory;
    private String imgUrl;
    private Integer isAnonymous;
    private Integer publishType;
    private Integer isPublic;
    private String aiSuggest;
    private Integer status;
    private LocalDateTime createTime;
}