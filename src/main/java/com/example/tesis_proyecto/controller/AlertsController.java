package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.model.Alert;
import com.example.tesis_proyecto.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertsController {
    @Autowired
    private AlertService alertService;

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/severity/{severity}")
    public List<Alert> getAlertsBySeverity(@RequestParam String severity) {
        return alertService.getAlertsBySeverity(String.valueOf(AlertSeverity.valueOf(severity.toUpperCase())));
    }
//    public ResponseEntity<List<Alert>> getAlertsBySeverity(@PathVariable String severity) {
//        List<Alert> alerts = alertService.getAlertsBySeverity(severity);
//        return ResponseEntity.ok(alerts);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable UUID id) {
        return alertService.getAlertById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(@RequestBody Map<String, Object> feedback) {
        try {
            UUID alertId = UUID.fromString((String) feedback.get("alertId"));
            String feedbackText = (String) feedback.get("feedback");
            Boolean isFalsePositive = (Boolean) feedback.get("isFalsePositive");

            alertService.submitFeedback(alertId, feedbackText, isFalsePositive);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Feedback registrado correctamente",
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Error al registrar feedback: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Alert> updateAlertStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        Alert updated = alertService.updateAlertStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }
}
