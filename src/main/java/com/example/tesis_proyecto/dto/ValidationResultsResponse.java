package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ValidationResultsResponse {

    private UUID trainingRunId;
    private int totalAttackTypes;
    private Double avgAccuracy;
    private Double avgF1Score;
    private Double avgPrecision;
    private Double avgRecall;
    private List<ConfusionMatrixDTO> confusionMatrix;

    @Data
    @Builder
    public static class ConfusionMatrixDTO {
        private String attackType;
        private Integer truePositives;
        private Integer trueNegatives;
        private Integer falsePositives;
        private Integer falseNegatives;
        private Double accuracy;
        private Double precision;
        private Double recall;
        private Double f1Score;
    }
}