package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class TrainingHistoryResponse {
    private UUID trainingRunId;
    private String modelName;
    private String status;
    private Double threshold;
    private Double falsePositiveRate;
    private Map<String, Object> finalMetrics;
    private LocalDateTime trainingStartedAt;
    private LocalDateTime trainingEndedAt;
    private Double trainingDurationSeconds;
    private List<EpochDTO> epochs;

    @Data
    @Builder
    public static class EpochDTO {
        private Integer epoch;
        private Double loss;
        private Double valLoss;
        private Double learningRate;
    }
}
