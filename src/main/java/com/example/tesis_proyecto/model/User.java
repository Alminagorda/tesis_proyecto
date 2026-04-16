package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    @Column(nullable = false)
    private String passwordHash;

    @ManyToOne
    @JoinColumn(name = "role_name")
    private Rol role;

    private Boolean isActive = true;

    private LocalDateTime lastLogin;

    private Integer failedAttempts = 0;

    private LocalDateTime lockedUntil;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
