package com.cnu.zyx.backend.entity;

import lombok.Data;

@Data
public class PatientInfo {
    private Long id;
    private Long userId;
    private String nickname;
    private String phone;
    private Integer age;
    private String gender;
    private String allergy;
    private String diseaseHistory;
    private String operationHistory;
}