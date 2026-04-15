package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.TrainingRuns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingRunRepository extends JpaRepository<TrainingRuns, UUID> {
    @Query("SELECT t FROM TrainingRuns t ORDER BY t.createdAt DESC LIMIT 1")
    Optional<TrainingRuns> findLatest();
    Optional<TrainingRuns> findTopByOrderByIdDesc();
}
