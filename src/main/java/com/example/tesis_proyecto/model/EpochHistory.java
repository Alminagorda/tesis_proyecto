package com.example.tesis_proyecto.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "epoch_history")
public class EpochHistory {
    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "training_run_id")
    private TrainingRuns trainingRun;
    @Column(nullable = false)
    private Integer epoch;
    private Float loss;
    @Column(name = "val_loss")
    private Float valLoss;
    @Column(name = "learning_rate")
    private Float learningRate;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
