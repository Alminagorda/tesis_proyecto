package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Rol {
    @Id
    @Column(name = "name",columnDefinition = "TEXT")
    private String name;
    @Column(name = "description",columnDefinition = "TEXT")
    private String description;
}
