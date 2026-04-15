package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.AttackMetricsResponse;
import com.example.tesis_proyecto.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@CrossOrigin(origins = "*")
public class MetricsController {
    @Autowired
    private MetricsService metricsService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalAlerts", metricsService.getTotalAlerts());
        metrics.put("criticalAlerts", metricsService.getCriticalAlerts());
        metrics.put("anomalyRate", metricsService.getAnomalyRate());
        metrics.put("falsePositiveRate", metricsService.getFalsePositiveRate());
        metrics.put("modelAccuracy", metricsService.getModelAccuracy());
        metrics.put("uptime", 99.8);
        metrics.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/daily")
    public ResponseEntity<Map<String, Object>> getDailyMetrics() {
        return ResponseEntity.ok(metricsService.getDailyMetrics());
    }

    @GetMapping("/model")
    public ResponseEntity<Map<String, Object>> getModelMetrics() {
        return ResponseEntity.ok(metricsService.getModelMetrics());
    }

    @GetMapping("/metrics")
    public ResponseEntity<?> getMetrics(
            @RequestParam(required = false) String attackType) {
        try {
            List<AttackMetricsResponse> metrics = metricsService.getMetrics(attackType);

            if (metrics.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                        "exito", true,
                        "mensaje", "No hay simulaciones registradas aún",
                        "data", List.of()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "exito", true,
                    "total", metrics.size(),
                    "data", metrics
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false,
                    "mensaje", "Error al obtener métricas: " + e.getMessage()
            ));
        }
    }
}
