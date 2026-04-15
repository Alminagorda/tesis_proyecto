package com.example.tesis_proyecto.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MlResponse {
    private String attack_type;
    private String fase;
    private boolean is_anomaly;
    private double reconstruction_error;
    private double threshold;
    private double confidence;
}
