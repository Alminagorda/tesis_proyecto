package com.example.tesis_proyecto.model;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.dto.AlertSeverityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;

    @Column(name = "alert_type", columnDefinition = "TEXT")
    private String alertType;

    @Column(name = "severity")
    @Type(AlertSeverityType.class)
    private AlertSeverity severity;

    @Column(name = "status")
    @org.hibernate.annotations.ColumnTransformer(write = "?::alert_status")
    private String status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "affected_variables", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String affectedVariables;

    @Column(name = "top_indicators", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String topIndicators;

    @Column(name = "affected_systems", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String affectedSystems;

    @Column(name = "geographic_info", columnDefinition = "jsonb")
    @org.hibernate.annotations.ColumnTransformer(write = "?::jsonb")
    private String geographicInfo;

    @Column(name = "reconstruction_error")
    private Float reconstructionError;

    @Column(name = "detection_confidence")
    private Float detectionConfidence;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "user_feedback",columnDefinition = "TEXT")
    private String userFeedback;

    @Column(name = "notes",columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sample_id")
    private Integer sampleId;

    @Column(name = "true_label")
    private Integer trueLabel;

    @Column(name = "predicted_label")
    private Integer predictedLabel;

    @Column(name = "source_ip",columnDefinition = "TEXT")
    private String sourceIp;

    @Column(name = "destination_ip",columnDefinition = "TEXT")
    private String destinationIp;

    @Column(name = "port_src",columnDefinition = "TEXT")
    private Integer portSrc;

    @Column(name = "port_dst")
    private Integer portDst;

    @Column(name = "protocol",columnDefinition = "TEXT")
    private String protocol;

}
