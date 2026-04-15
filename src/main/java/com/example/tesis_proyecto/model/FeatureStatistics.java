package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "feature_statistics")
public class FeatureStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;

    @Column(name = "attack_type",columnDefinition = "TEXT")
    private String attackType;

    @Column(name = "attack_category",columnDefinition = "TEXT")
    private String attackCategory;

    @Column(name = "feature_name", nullable = false,columnDefinition = "TEXT")
    private String featureName;

    @Column(name = "mean_value")
    private Float meanValue;

    @Column(name = "median_value")
    private Float medianValue;

    @Column(name = "std_value")
    private Float stdValue;

    @Column(name = "min_value")
    private Float minValue;

    @Column(name = "max_value")
    private Float maxValue;

    @Column(name = "q25_value")
    private Float q25Value;

    @Column(name = "q75_value")
    private Float q75Value;

    @Column(name = "mean_shap_value")
    private Float meanShapValue;

    @Column(name = "importance_rank")
    private Integer importanceRank;

    @Column(name = "sample_count")
    private Integer sampleCount;

    @Column(name = "distribution_stats", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String distributionStats;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
