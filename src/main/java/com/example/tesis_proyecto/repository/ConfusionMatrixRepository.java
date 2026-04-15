package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.ConfusionMatrixData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfusionMatrixRepository extends JpaRepository<ConfusionMatrixData, UUID> {

    List<ConfusionMatrixData> findByTrainingRunIdOrderByAttackTypeAsc(UUID trainingRunId);
}