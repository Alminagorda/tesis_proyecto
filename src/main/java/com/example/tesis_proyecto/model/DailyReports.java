package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "daily_reports")
public class DailyReports {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "report_date", nullable = false, unique = true)
    private LocalDate reportDate;

    @Column(name = "total_predictions")
    private Integer totalPredictions;

    @Column(name = "total_anomalies_detected")
    private Integer totalAnomaliesDetected;

    @Column(name = "total_alerts_generated")
    private Integer totalAlertsGenerated;

    @Column(name = "avg_reconstruction_error")
    private Float avgReconstructionError;

    @Column(name = "max_reconstruction_error")
    private Float maxReconstructionError;

    @Column(name = "alerts_by_type", columnDefinition = "jsonb")
    private String alertsByType;

    @Column(name = "alerts_by_severity", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String alertsBySeverity;

    @Column(name = "anomaly_rate")
    private Float anomalyRate;

    @Column(name = "false_positive_rate")
    private Float falsePositiveRate;

    @Column(name = "critical_events", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String criticalEvents;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
