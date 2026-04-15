package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.FeatureImportance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeatureImportanceRepository extends JpaRepository<FeatureImportance, UUID> {
    @Query("""
        SELECT f FROM FeatureImportance f
        WHERE f.trainingRun.id = :trainingRunId
        ORDER BY f.importanceRank ASC
        LIMIT 10
        """)
    List<FeatureImportance> findTop10ByTrainingRunId(UUID trainingRunId);
}
