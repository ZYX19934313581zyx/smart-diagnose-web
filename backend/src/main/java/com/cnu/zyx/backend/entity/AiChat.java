package com.cnu.zyx.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChat {
    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private LocalDateTime createTime;
}