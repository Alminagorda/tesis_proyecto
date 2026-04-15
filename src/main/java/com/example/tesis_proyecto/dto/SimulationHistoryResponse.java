package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SimulationHistoryResponse {

    private int totalSimulations;
    private List<SimulationDTO> simulations;

    @Data
    @Builder
    public static class SimulationDTO {
        private UUID id;
        private UUID trainingRunId;
        private String attackType;
        private Integer totalSamples;
        private Integer detectedCorrectly;
        private Integer falseNegatives;
        private Integer falsePositives;
        private Double detectionRate;
        private Double precision;
        private Double recall;
        private Double f1Score;
        private Double avgReconstructionError;
        private Double minReconstructionError;
        private Double maxReconstructionError;
        private Double stdReconstructionError;
        private LocalDateTime createdAt;
    }
}
