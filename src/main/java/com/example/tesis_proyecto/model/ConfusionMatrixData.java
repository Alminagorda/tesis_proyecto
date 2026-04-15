package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "confusion_matrix_data")
public class ConfusionMatrixData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;
    @Column(name = "attack_type",columnDefinition = "TEXT")
    private String attackType;
    @Column(name = "true_positives")
    private Integer truePositives;

    @Column(name = "true_negatives")
    private Integer trueNegatives;

    @Column(name = "false_positives")
    private Integer falsePositives;
    @Column(name = "false_negatives")
    private Integer falseNegatives;

    private Float accuracy;
    private Float precision;
    private Float recall;
    @Column(name = "f1_score")
    private Float f1Score;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }


}
