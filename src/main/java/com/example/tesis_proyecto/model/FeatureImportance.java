package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "feature_importance")
public class FeatureImportance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;

    @Column(name = "feature_name", nullable = false,columnDefinition = "TEXT")
    private String featureName;

    @Column(name = "mean_shap_value")
    private Float meanShapValue;

    @Column(name = "std_shap_value")
    private Float stdShapValue;

    @Column(name = "importance_rank")
    private Integer importanceRank;

    @Column(name = "min_shap")
    private Float minShap;

    @Column(name = "max_shap")
    private Float maxShap;

    @Column(name = "median_shap")
    private Float medianShap;

    @Column(name = "correlation_with_anomaly")
    private Float correlationWithAnomaly;

    @Column(name = "mean_value")
    private Float meanValue;

    @Column(name = "std_value")
    private Float stdValue;

    @Column(name = "min_value")
    private Float minValue;

    @Column(name = "max_value")
    private Float maxValue;

    @Column(name = "calculation_method",columnDefinition = "TEXT")
    private String calculationMethod;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
