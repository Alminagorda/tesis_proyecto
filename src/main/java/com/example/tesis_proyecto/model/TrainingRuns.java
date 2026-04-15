package com.example.tesis_proyecto.model;

import com.example.tesis_proyecto.dto.TrainingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity

@Table(name = "training_runs")
public class TrainingRuns {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "model_name",columnDefinition = "TEXT")
    private String modelName;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "training_status")
    private TrainingStatus status;

    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String architecture;

    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String hyperparameters;

    @Column(name = "dataset_info", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String datasetInfo;

    @Column(name = "training_started_at")
    private LocalDateTime trainingStartedAt;

    @Column(name = "training_ended_at")
    private LocalDateTime trainingEndedAt;

    @Column(name = "training_duration_seconds")
    private Float trainingDurationSeconds;

    @Column(name = "validation_results", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String validationResults;

    @Column(name = "final_metrics", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String finalMetrics;

    private Float threshold;

    @Column(name = "threshold_method",columnDefinition = "TEXT")
    private String thresholdMethod;

    @Column(name = "threshold_percentile")
    private Integer thresholdPercentile;

    @Column(name = "false_positive_rate")
    private Float falsePositiveRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
