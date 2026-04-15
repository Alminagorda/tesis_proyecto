package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "attack_simulations")
public class AttackSimulation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;

    @Column(name = "attack_type", nullable = false,columnDefinition = "TEXT")
    private String attackType;

    @Column(name = "total_samples")
    private Integer totalSamples;

    @Column(name = "detected_correctly")
    private Integer detectedCorrectly;

    @Column(name = "false_negatives")
    private Integer falseNegatives;

    @Column(name = "false_positives")
    private Integer falsePositives;

    @Column(name = "detection_rate")
    private Double detectionRate;

    @Column(name = "precision")
    private Double precision;

    @Column(name = "recall")
    private Double recall;

    @Column(name = "f1_score")
    private Double f1Score;

    @Column(name = "avg_reconstruction_error")
    private Double avgReconstructionError;

    @Column(name = "min_reconstruction_error")
    private Double minReconstructionError;

    @Column(name = "max_reconstruction_error")
    private Double maxReconstructionError;

    @Column(name = "std_reconstruction_error")
    private Double stdReconstructionError;

    @Column(name = "top_features", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String topFeatures;

    @Column(name = "sample_record_ids", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String sampleRecordIds;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
