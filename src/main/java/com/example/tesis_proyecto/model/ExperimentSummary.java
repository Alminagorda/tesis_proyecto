package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "experiment_summary")
public class ExperimentSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "experiment_id", nullable = false,columnDefinition = "TEXT")
    private String experimentId;
    @Column(name = "actividad",columnDefinition = "TEXT")
    private String actividad;
    @Column(name = "fase",columnDefinition = "TEXT")
    private String fase;

    private LocalDateTime timestamp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "optimal_threshold")
    private Float optimalThreshold;

    @Column(name = "auc_val")
    private Float aucVal;

    @Column(name = "auc_test")
    private Float aucTest;

    @Column(name = "val_precision")
    private Float valPrecision;

    @Column(name = "val_recall")
    private Float valRecall;

    @Column(name = "val_f1")
    private Float valF1;

    @Column(name = "val_fpr")
    private Float valFpr;

    @Column(name = "all_indicators_met")
    private Boolean allIndicatorsMet;
}
