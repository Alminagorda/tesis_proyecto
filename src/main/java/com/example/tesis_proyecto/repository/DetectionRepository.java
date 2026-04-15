package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.model.Detections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DetectionRepository extends JpaRepository<Detections, UUID> {
    //List<Detections> findBySeverity(String severity);
    List<Detections> findBySeverity(AlertSeverity severity);
    Page<Detections> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<Detections> findByIsAnomalyTrueOrderByTimestampDesc(Pageable pageable);

    Page<Detections> findByThreatType(String threatType, Pageable pageable);
//
  long countByThreatType(String threatType);

    List<Detections> findByIsAnomalyTrueAndNotificadoFalseAndTimestampAfter(LocalDateTime timestamp);
}
