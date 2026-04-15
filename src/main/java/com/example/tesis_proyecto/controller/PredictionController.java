package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predictions")
@CrossOrigin(origins = "*")
public class PredictionController {
    @Autowired
    private PredictionService predictionService;

    // ----------------------------------------------------------
    // Predice un registro individual
    // ----------------------------------------------------------
    @PostMapping("/predict")
    public ResponseEntity<Detections> predict(
            @RequestBody Map<String, Object> request) {
        try {
            String username = (String) request
                    .getOrDefault("username", "unknown");
            String sourceIp = (String) request
                    .getOrDefault("sourceIp", "0.0.0.0");

            // Quita campos que no son features del modelo
            request.remove("username");
            request.remove("sourceIp");

            Detections result = predictionService
                    .predict(request, username, sourceIp);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ----------------------------------------------------------
    // Simula un ataque específico
    // ----------------------------------------------------------
    @PostMapping("/simulate/{attackType}")
    public ResponseEntity<Map<String, Object>> simulate(
            @PathVariable String attackType,
            @RequestBody(required = false) Map<String, Object> body) {
        try {
            String fase = body != null
                    ? (String) body.getOrDefault("fase", "reconocimiento")
                    : "reconocimiento";

            Map<String, Object> result = predictionService
                    .simularAtaque(attackType, fase);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    // ----------------------------------------------------------
    // Historial de predicciones recientes
    // ----------------------------------------------------------
    @GetMapping("/history")
    public ResponseEntity<List<Detections>> getHistory(
            @RequestParam(defaultValue = "20") int limit) {
        // Lo agregas en DetectionsRepository
        return ResponseEntity.ok(
                predictionService.getRecentDetections(limit));
    }

    // ----------------------------------------------------------
    // Solo anomalías detectadas
    // ----------------------------------------------------------
    @GetMapping("/anomalies")
    public ResponseEntity<List<Detections>> getAnomalies(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(
                predictionService.getRecentAnomalies(limit));
    }
}
