package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.repository.DetectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DetectionService {
    @Autowired
    private DetectionRepository detectionRepository;

    // 📄 Paginación
    public Page<Detections> getAllDetections(Pageable pageable) {
        return detectionRepository.findAll(pageable);
    }

    // 🔍 Buscar por ID
    public Optional<Detections> getDetectionById(UUID id) {
        return detectionRepository.findById(id);
    }

    // 💾 Guardar
    public DetectionRepository saveDetection(Detections detection) {
        return (DetectionRepository) detectionRepository.save(detection);
    }

    // ❌ Eliminar
    public void deleteDetection(UUID id) {
        if (!detectionRepository.existsById(id)) {
            throw new RuntimeException("Detection no encontrada");
        }
        detectionRepository.deleteById(id);
    }

    // 🔎 Filtrar por severidad
    public List<Detections> getDetectionsBySeverity(String severity) {
        return detectionRepository.findBySeverity(AlertSeverity.valueOf(severity.toUpperCase()));
    }

    // filtrar por tipo de deteccion
    public Page<Detections> getDetectionsByThreatType(String threatType, Pageable pageable) {
        return detectionRepository.findByThreatType(threatType, pageable);
    }
}
