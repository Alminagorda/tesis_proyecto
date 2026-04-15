package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
//    List<Alert> findBySeverity(String severity);
List<Alert> findBySeverity(AlertSeverity severity);
}
