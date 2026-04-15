package com.example.tesis_proyecto.model;

import lombok.Data;

import java.util.List;

@Data
public class AnomalyResult {
    private boolean      isAnomaly;
    private double       confidence;
    private List<String> reasons;
    private String       riskLevel;
}
