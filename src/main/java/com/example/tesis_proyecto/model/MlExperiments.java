package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ml_experiments")
public class MlExperiments {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "experiment_id", nullable = false,columnDefinition = "TEXT")
    private String experimentId;


    private LocalDateTime timestamp;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "fase",columnDefinition = "TEXT")
    private String fase;
    @Column(name = "actividad",columnDefinition = "TEXT")
    private String actividad;

    @Column(name = "model_config", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String modelConfig;
    @Column(name = "dataset_info", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String datasetInfo;
    @Column(name = "experimentInfo", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String experimentInfo;
    @Column(name = "performance_metrics", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String performanceMetrics;
    @Column(name = "validation_metrics", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String validationMetrics;
    @Column(name = "test_metrics", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String testMetrics;
    @Column(name = "auc_scores", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String aucScores;
    @Column(name = "reconstruction_errors", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String reconstructionErrors;
    @Column(name = "confusion_matrix_val", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String confusionMatrixVal;
    @Column(name = "critical_cases", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String critical_cases;
    @Column(name = "optimal_threshold")
    private Float optimalThreshold;
    @Column(name = "threshold_strategy",columnDefinition = "TEXT")
    private String thresholdStrategy;
    @Column(name = "threshold_candidates", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String thresholdCandidates;
    @Column(name = "thesis_indicators", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String thesisIndicators;
    @Column(name = "thesis_validation", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String thesisValidation;
    @Column(name = "generated_files", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String generated_files;


}
