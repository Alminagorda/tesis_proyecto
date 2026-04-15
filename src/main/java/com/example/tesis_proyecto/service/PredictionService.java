package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.model.Alert;
import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.repository.AlertRepository;
import com.example.tesis_proyecto.repository.DetectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PredictionService {
    private final String FASTAPI_URL = "https://fastapi-ml-39cx.onrender.com";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DetectionRepository detectionsRepository;

    @Autowired
    private AlertRepository alertsRepository;

    @Autowired
    private EmailService emailService;

    // ----------------------------------------------------------
    // Predice un registro y lo guarda en PostgreSQL
    // ----------------------------------------------------------
    public Detections predict(Map<String, Object> features,
                              String username, String sourceIp) {
        // 1. Llama a FastAPI
        ResponseEntity<Map> response = restTemplate.postForEntity(
                FASTAPI_URL + "/predict", features, Map.class
        );
        Map<String, Object> result = response.getBody();

        // 2. Construye la detección
        Detections detection = new Detections();
        detection.setUsername(username);
        detection.setSourceIp(sourceIp);
        detection.setReconstructionError(
                (float) ((Number) result.get("reconstruction_error")).doubleValue());
        detection.setIsAnomaly(
                (Boolean) result.get("is_anomaly"));
        detection.setConfidence(
                (float) ((Number) result.get("confidence")).doubleValue());
        detection.setThresholdUsed(
                (float) ((Number) result.get("threshold")).doubleValue());
        // ✅ FIX: usa el severity que devuelve FastAPI
        float error = detection.getReconstructionError();
        if (error >= 0.75f)      detection.setSeverity(AlertSeverity.critical);
        else if (error >= 0.50f) detection.setSeverity(AlertSeverity.high);
        else if (error >= 0.30f) detection.setSeverity(AlertSeverity.medium);
        else                     detection.setSeverity(AlertSeverity.low);
        detection.setTimestamp(LocalDateTime.now());
        detection.setInvestigationStatus("pending");

        // 3. Guarda detección en PostgreSQL
        Detections saved = detectionsRepository.save(detection);

        // 4. Si es anomalía genera alerta automáticamente
        if (detection.getIsAnomaly()) {
            generarAlerta(saved, result);
            emailService.notificarAnomaliaDetectada(saved);
        }

        return saved;
    }

    // ----------------------------------------------------------
    // Simula ataque llamando a FastAPI
    // ----------------------------------------------------------
    public Map<String, Object> simularAtaque(String attackType, String fase) {
        try {
            // 1. Llama a FastAPI
            String url = FASTAPI_URL + "/simulate/" + attackType
                    + "?fase=" + fase;
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url, new HashMap<>(), Map.class
            );
            Map<String, Object> result = response.getBody();
            System.out.println(">>> FastAPI respondió: " + result);

            // 2. Guarda en detections
            Detections detection = new Detections();
            detection.setThreatType(attackType.toUpperCase());
            detection.setIsAnomaly(
                    (Boolean) result.get("is_anomaly"));
            detection.setReconstructionError(
                    (float) ((Number) result.get("reconstruction_error")).doubleValue());
            detection.setConfidence(
                    (float) ((Number) result.get("confidence")).doubleValue());
            detection.setThresholdUsed(
                    (float) ((Number) result.get("threshold")).doubleValue());
            // ✅ FIX: usa el severity de FastAPI en lugar de uno aleatorio
            float error = detection.getReconstructionError();
            if (error >= 0.75f)      detection.setSeverity(AlertSeverity.critical);
            else if (error >= 0.50f) detection.setSeverity(AlertSeverity.high);
            else if (error >= 0.30f) detection.setSeverity(AlertSeverity.medium);
            else                     detection.setSeverity(AlertSeverity.low);
            detection.setTimestamp(LocalDateTime.now());
            detection.setInvestigationStatus("pending");
            detection.setEventCategory("SIMULATION");
            detection.setNotes("Simulación: " + attackType + " fase " + fase);

            Detections saved = detectionsRepository.save(detection);
            System.out.println(">>> Guardado en BD con id: " + saved.getId());

            // 3. Si es anomalía genera alerta
            if (Boolean.TRUE.equals(detection.getIsAnomaly())) {
                generarAlerta(saved, result);
                System.out.println(">>> Alerta generada");
                emailService.notificarAnomaliaDetectada(saved);
            }

            // 4. Retorna resultado con el id guardado
            result.put("detection_id", saved.getId().toString());
            result.put("saved_to_db", true);
            return result;

        } catch (Exception e) {
            System.err.println(">>> ERROR: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ----------------------------------------------------------
    // Genera alerta cuando hay anomalía
    // ----------------------------------------------------------
    private void generarAlerta(Detections detection,
                               Map<String, Object> result) {

        Alert alert = new Alert();

        alert.setAlertType(detection.getThreatType() != null
                ? detection.getThreatType() : "ANOMALY_DETECTED");

        float error = Optional.ofNullable(alert.getReconstructionError())
                .orElse(0.0f);
        if (error >= 0.75f)      alert.setSeverity(AlertSeverity.critical);
        else if (error >= 0.50f) alert.setSeverity(AlertSeverity.high);
        else if (error >= 0.30f) alert.setSeverity(AlertSeverity.medium);
        else                     alert.setSeverity(AlertSeverity.low);
        alert.setStatus("new");

        alert.setDescription(String.format(
                "Anomalía detectada — error: %.6f (threshold: %.6f)",
                detection.getReconstructionError(),
                detection.getThresholdUsed()
        ));

        alert.setReconstructionError(detection.getReconstructionError());
        alert.setDetectionConfidence(detection.getConfidence());
        alert.setSourceIp(detection.getSourceIp());
        alert.setDetectedAt(LocalDateTime.now());

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> features =
                    (Map<String, Object>) result.get("features_used");

            if (features != null) {
                String json = mapper.writeValueAsString(features);
                alert.setAffectedSystems(json);
                alert.setAffectedVariables(json);
                alert.setTopIndicators(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        alertsRepository.save(alert);
    }

    public List<Detections> getRecentDetections(int limit) {
        return detectionsRepository
                .findAllByOrderByTimestampDesc(PageRequest.of(0, limit))
                .getContent();
    }

    public List<Detections> getRecentAnomalies(int limit) {
        return detectionsRepository
                .findByIsAnomalyTrueOrderByTimestampDesc(PageRequest.of(0, limit))
                .getContent();
    }
}
