package com.example.tesis_proyecto.model;

import com.example.tesis_proyecto.dto.AlertSeverity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "detections")
public class Detections {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime timestamp;
    @Column(name = "userId",columnDefinition = "TEXT")
    private String userId;
    @Column(name = "username",columnDefinition = "TEXT")
    private String username;
    @Column(name = "role",columnDefinition = "TEXT")
    private String role;
    @Column(name = "department",columnDefinition = "TEXT")
    private String department;

    @Column(name = "source_ip",columnDefinition = "TEXT")
    private String sourceIp;

    @Column(name = "destination_ip",columnDefinition = "TEXT")
    private String destinationIp;

    @Column(name = "destination_port")
    private Integer destinationPort;

    private String protocol;

    @Column(name = "connection_type",columnDefinition = "TEXT")
    private String connectionType;

    @Column(name = "threat_type",columnDefinition = "TEXT")
    private String threatType;

    @Column(name = "is_anomaly")
    private Boolean isAnomaly;

    @Column(name = "reconstruction_error")
    private Float reconstructionError;

    private Float confidence;

    @Column(name = "threshold_used")
    private Float thresholdUsed;

    @Column(name = "model_version",columnDefinition = "TEXT")
    private String modelVersion;

//    @Enumerated(EnumType.STRING)
//    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
//    @Column(name = "severity", columnDefinition = "alert_severity")
//    private AlertSeverity severity;
@Enumerated(EnumType.STRING)
@Column(name = "severity")
private AlertSeverity severity;


    @Column(name = "investigation_status",columnDefinition = "TEXT")
    private String investigationStatus;

    @Column(name = "session_duration_sec")
    private Integer sessionDurationSec;

    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts;

    @Column(name = "authentication_method",columnDefinition = "TEXT")
    private String authenticationMethod;

    @Column(name = "authentication_status",columnDefinition = "TEXT")
    private String authenticationStatus;

    @Column(name = "event_type",columnDefinition = "TEXT")
    private String eventType;

    @Column(name = "event_category",columnDefinition = "TEXT")
    private String eventCategory;

    @Column(name = "physical_location",columnDefinition = "TEXT")
    private String physicalLocation;

    @Column(name = "workstation_id",columnDefinition = "TEXT")
    private String workstationId;
    @Column(name = "os",columnDefinition = "TEXT")
    private String os;
    @Column(name = "application",columnDefinition = "TEXT")
    private String application;

    @Column(name = "day_of_week",columnDefinition = "TEXT")
    private String dayOfWeek;
    @Column(name = "shift",columnDefinition = "TEXT")
    private String shift;

    @Column(name = "is_weekend")
    private Boolean isWeekend;
    @Column(name = "notes",columnDefinition = "TEXT")
    private String notes;

    @Column(name = "notificado")
    private Boolean notificado = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
