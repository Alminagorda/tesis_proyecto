package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.model.Alert;
import com.example.tesis_proyecto.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    // 🔍 Obtener todas las alertas
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    // 🔍 Buscar por severidad
    public List<Alert> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverity(AlertSeverity.valueOf(severity.toUpperCase()));
    }

    // 🔍 Buscar por ID
    public Optional<Alert> getAlertById(UUID id) {
        return alertRepository.findById(id);
    }

    // 💬 Registrar feedback
    public void submitFeedback(UUID alertId, String feedbackText, Boolean isFalsePositive) {

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alert.setUserFeedback(feedbackText);

        // opcional: marcar estado automáticamente
        if (Boolean.TRUE.equals(isFalsePositive)) {
            alert.setStatus("false_positive");
        }

        alertRepository.save(alert);
    }

    // 🔄 Actualizar estado
    public Alert updateAlertStatus(UUID id, String newStatus) {

        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

        alert.setStatus(newStatus);

        // si se resuelve, guardar fecha
        if ("resolved".equalsIgnoreCase(newStatus)) {
            alert.setResolvedAt(LocalDateTime.now());
        }

        return alertRepository.save(alert);
    }
}
