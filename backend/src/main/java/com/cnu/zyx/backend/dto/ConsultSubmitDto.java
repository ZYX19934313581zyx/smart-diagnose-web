package com.cnu.zyx.backend.dto;

import lombok.Data;

@Data
public class ConsultSubmitDto {
    private Long userId;
    private Long doctorId;
    private String title;
    private String symptom;
    private Long departmentId;
    //前端字段
    private String diseaseHistory;
    private String operationHistory;
    private String allergyHistory;

    private String imgUrl;
    private Integer isAnonymous;
    private Integer publishType;
    private Integer isPublic;
    private String aiSuggest;
    private Integer status;
}