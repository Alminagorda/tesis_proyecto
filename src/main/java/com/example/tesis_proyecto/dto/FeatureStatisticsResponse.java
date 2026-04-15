package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class FeatureStatisticsResponse {

    private UUID trainingRunId;
    private String attackTypeFilter;
    private int totalFeatures;
    private List<FeatureStatDTO> features;

    @Data
    @Builder
    public static class FeatureStatDTO {
        private String featureName;
        private String attackType;
        private String attackCategory;
        private Double meanValue;
        private Double medianValue;
        private Double stdValue;
        private Double minValue;
        private Double maxValue;
        private Double q25Value;
        private Double q75Value;
        private Double meanShapValue;
        private Integer importanceRank;
        private Integer sampleCount;
    }
}