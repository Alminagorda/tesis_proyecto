package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "email_enabled")
    private Boolean emailEnabled = true;

    @Column(name = "notify_critical")
    private Boolean notifyCritical = true;

    @Column(name = "notify_high")
    private Boolean notifyHigh = true;

    @Column(name = "notify_medium")
    private Boolean notifyMedium = true;

    @Column(name = "notify_low")
    private Boolean notifyLow = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}