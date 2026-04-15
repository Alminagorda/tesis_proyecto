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
@Table(name = "model_metrics")
public class ModelMetrics {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "model_version",columnDefinition = "TEXT")
    private String modelVersion;

    @Column(name = "normal_samples")
    private Integer normalSamples;

    @Column(name = "anomaly_samples")
    private Integer anomalySamples;

    @Column(name = "test_samples")
    private Integer testSamples;

    private Float accuracy;
    private Float precision;
    private Float recall;

    @Column(name = "f1_score")
    private Float f1Score;

    @Column(name = "auc_roc")
    private Float aucRoc;

    @Column(name = "false_positive_rate")
    private Float falsePositiveRate;

    @Column(name = "false_negative_rate")
    private Float falseNegativeRate;

    @Column(name = "true_negatives")
    private Integer trueNegatives;

    @Column(name = "false_positives")
    private Integer falsePositives;

    @Column(name = "false_negatives")
    private Integer falseNegatives;

    @Column(name = "true_positives")
    private Integer truePositives;

    @Column(name = "detection_threshold")
    private Float detectionThreshold;
    @Column(name = "notes",columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
