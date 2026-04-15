package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "model_versions")
public class ModelVersions {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "version_number", nullable = false,columnDefinition = "TEXT")
    private String versionNumber;

    @Column(name = "model_name",columnDefinition = "TEXT")
    private String modelName;

    @Column(name = "model_type",columnDefinition = "TEXT")
    private String modelType;
    @Column(name = "framework",columnDefinition = "TEXT")
    private String framework;
    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String architecture;

    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String hyperparameters;

    @Column(name = "features_count")
    private Integer featuresCount;

    @Column(name = "training_samples")
    private Integer trainingSamples;

    @Column(name = "validation_samples")
    private Integer validationSamples;

    @Column(name = "threshold_value")
    private Float thresholdValue;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "deployment_status",columnDefinition = "TEXT")
    private String deploymentStatus;

    @Column(name = "training_dataset",columnDefinition = "TEXT")
    private String trainingDataset;
    @Column(name = "notes",columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
