package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FeatureImportanceResponse {

    private UUID trainingRunId;
    private int totalFeatures;
    private List<FeatureDTO> features;

    @Data
    @Builder
    public static class FeatureDTO {
        private Integer rank;
        private String featureName;
        private Double meanShapValue;
        private Double stdShapValue;
        private Double minShap;
        private Double maxShap;
        private Double medianShap;
        private Double correlationWithAnomaly;
        private Double meanValue;
        private Double stdValue;
    }
}