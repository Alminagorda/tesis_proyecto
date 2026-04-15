package com.example.tesis_proyecto.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class AttackMetricsResponse {

    private String attackType;
    private Integer totalDetections;
    private Integer anomalies;
    private Integer normalEvents;

    // Métricas del modelo
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Double detectionRate;

    // Reconstruction error
    private Double avgReconstructionError;
    private Double minReconstructionError;
    private Double maxReconstructionError;
    private Double stdReconstructionError;

    // Desglose por severidad
    private Map<String, Long> severityBreakdown;

    // Desglose por estado de investigación
    private Map<String, Long> investigationStatusBreakdown;

    // Info de la última simulación
    private LocalDateTime lastSimulation;
    private Integer totalSimulationsRun;
}
