package com.example.tesis_proyecto.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FeedbackRequest {

    private String userRole;

    private String featureEvaluated;

    @Min(1) @Max(5)
    private Integer rating;

    private String comments;

    private Boolean isUseful;

    private String suggestions;
}
