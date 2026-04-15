package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "user_feedback")
public class UserFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_role",columnDefinition = "TEXT")
    private String userRole;

    @Column(name = "feature_evaluated",columnDefinition = "TEXT")
    private String featureEvaluated;

    private Integer rating;
    @Column(name = "comments",columnDefinition = "TEXT")
    private String comments;
    @Column(name = "is_useful")
    private Boolean isUseful;
    @Column(name = "suggestions",columnDefinition = "TEXT")
    private String suggestions;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
