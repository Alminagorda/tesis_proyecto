package com.example.tesis_proyecto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "experiment_analysis")
public class ExperimentAnalysis {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "experiment_id", nullable = false,columnDefinition = "TEXT")
    private String experimentId;
    @Column(name = "actividad",columnDefinition = "TEXT")
    private String actividad;
    @Column(name = "fase",columnDefinition = "TEXT")
    private String fase;

    private LocalDateTime timestamp;

    @Column(name = "error_statistics", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String errorStatistics;

    @Column(name = "false_positives_analysis", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String falsePositivesAnalysis;

    @Column(name = "false_negatives_analysis", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String falseNegativesAnalysis;

    @Column(name = "threshold_sensitivity", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String thresholdSensitivity;
}
