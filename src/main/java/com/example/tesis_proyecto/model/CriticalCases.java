package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "critical_cases")
public class CriticalCases {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;

    @Column(name = "record_id")
    private Integer recordId;

    @Column(name = "case_type")
    private String caseType;

    @Column(name = "attack_type",columnDefinition = "TEXT")
    private String attackType;

    @Column(name = "key_features", columnDefinition = "jsonb")
    private String keyFeatures;

    @Column(name = "reconstruction_error")
    private Float reconstructionError;

    @Column(name = "prediction_score")
    private Float predictionScore;

    @Column(name = "was_detected")
    private Boolean wasDetected;

    @Column(name = "should_be_detected")
    private Boolean shouldBeDetected;

    @Column(name = "shap_top_features", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String shapTopFeatures;
    @Column(name = "notes",columnDefinition = "TEXT")
    private String notes;

    @Column(name = "impact_level",columnDefinition = "TEXT")
    private String impactLevel;

    @Column(name = "error_magnitude")
    private Float errorMagnitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
