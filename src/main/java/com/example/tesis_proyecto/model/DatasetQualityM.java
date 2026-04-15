package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "dataset_quality_metrics")
public class DatasetQualityM {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "total_features_final")
    private Integer totalFeaturesFinal;

    @Column(name = "total_features_original")
    private Integer totalFeaturesOriginal;

    @Column(name = "features_removed")
    private Integer featuresRemoved;

    @Column(name = "missing_values_pct")
    private Float missingValuesPct;

    @Column(name = "duplicate_records_pct")
    private Float duplicateRecordsPct;

    @Column(name = "normal_records")
    private Integer normalRecords;

    @Column(name = "anomaly_records")
    private Integer anomalyRecords;

    @Column(name = "balance_ratio")
    private Float balanceRatio;

    @Column(name = "low_variance_features", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String lowVarianceFeatures;

    @Column(name = "avg_correlation_between_features")
    private Float avgCorrelationBetweenFeatures;

    @Column(name = "high_correlation_pairs")
    private Integer highCorrelationPairs;

    @Column(name = "top_10_important_features", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String top10ImportantFeatures;

    @Column(name = "quality_score")
    private Float qualityScore;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
