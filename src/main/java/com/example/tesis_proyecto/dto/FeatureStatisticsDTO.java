package com.example.tesis_proyecto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FeatureStatisticsDTO {
    @JsonProperty("attack_type")
    private String attackType;

    @JsonProperty("attack_category")
    private String attackCategory;

    @JsonProperty("feature_name")
    private String featureName;

    @JsonProperty("mean_value")
    private Double meanValue;

    @JsonProperty("median_value")
    private Double medianValue;

    @JsonProperty("std_value")
    private Double stdValue;

    @JsonProperty("min_value")
    private Double minValue;

    @JsonProperty("max_value")
    private Double maxValue;

    @JsonProperty("q25_value")
    private Double q25Value;

    @JsonProperty("q75_value")
    private Double q75Value;

    @JsonProperty("sample_count")
    private Integer sampleCount;
}
