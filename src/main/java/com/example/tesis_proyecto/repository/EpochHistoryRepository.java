package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.EpochHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EpochHistoryRepository extends JpaRepository<EpochHistory, UUID> {
    List<EpochHistory> findByTrainingRunIdOrderByEpochAsc(UUID trainingRunId);
}
