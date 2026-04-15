package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FeedbackResponse {
    private UUID id;
    private String userRole;
    private String featureEvaluated;
    private Integer rating;
    private Boolean isUseful;
    private LocalDateTime createdAt;
    private String message;
}