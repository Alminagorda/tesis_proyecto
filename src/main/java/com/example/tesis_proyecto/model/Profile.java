package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true,columnDefinition = "TEXT")
    private String email;

    @Column(name = "full_name",columnDefinition = "TEXT")
    private String fullName;
    @Column(name = "role",columnDefinition = "TEXT")
    private String role;
    @Column(name = "sample_id")
    private Integer sampleId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
