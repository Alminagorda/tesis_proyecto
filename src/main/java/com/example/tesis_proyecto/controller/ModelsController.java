package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.FeatureImportanceResponse;
import com.example.tesis_proyecto.dto.FeatureStatisticsResponse;
import com.example.tesis_proyecto.dto.ValidationResultsResponse;
import com.example.tesis_proyecto.model.FeatureStatistics;
import com.example.tesis_proyecto.service.FeatureStatisticService;
import com.example.tesis_proyecto.service.ModelsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ModelsController {
    private final ModelsService modelsService;

    @Autowired
    private FeatureStatisticService featureStatisticService;
    /**
     * GET /api/models/feature-importance
     * Top 10 features por SHAP value del run más reciente
     */
    @GetMapping("/feature-importance")
    public ResponseEntity<FeatureImportanceResponse> getFeatureImportance() {
        return ResponseEntity.ok(modelsService.getFeatureImportance());
    }

    /**
     * GET /api/models/statistics
     * GET /api/models/statistics?attackType=ransomware  ← opcional
     */
    @GetMapping("/statistics")
    public ResponseEntity<FeatureStatisticsResponse> getFeatureStatistics(
            @RequestParam(required = false) String attackType) {
        return ResponseEntity.ok(modelsService.getFeatureStatistics(attackType));
    }
    /**
     * GET /api/models/validation-results
     * Matriz de confusión por attack type del run más reciente
     */
    @GetMapping("/validation-results")
    public ResponseEntity<ValidationResultsResponse> getValidationResults() {
        return ResponseEntity.ok(modelsService.getValidationResults());
    }

    // GET /api/feature-statistics
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(featureStatisticService.getAllStatistics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }

    // GET /api/feature-statistics/latest
    @GetMapping("/latest")
    public ResponseEntity<?> getLatest() {
        try {
            return ResponseEntity.ok(featureStatisticService.getLatestStatistics());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }

    // GET /api/feature-statistics/attack-type?attackType=Phishing
    @GetMapping("/attack-type")
    public ResponseEntity<?> getByAttackType(
            @RequestParam String attackType) {
        try {
            return ResponseEntity.ok(
                    featureStatisticService.getStatisticsByAttackType(attackType));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }

    // GET /api/feature-statistics/feature?featureName=hora
    @GetMapping("/feature")
    public ResponseEntity<?> getByFeatureName(
            @RequestParam String featureName) {
        try {
            return ResponseEntity.ok(
                    featureStatisticService.getStatisticsByFeatureName(featureName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }

    // GET /api/feature-statistics/training-run/{trainingRunId}
    @GetMapping("/training-run/{trainingRunId}")
    public ResponseEntity<?> getByTrainingRun(
            @PathVariable UUID trainingRunId) {
        try {
            return ResponseEntity.ok(
                    featureStatisticService.getStatisticsByTrainingRun(trainingRunId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }

    // GET /api/feature-statistics/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            FeatureStatistics fs = featureStatisticService.getStatisticById(id);
            if (fs == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(fs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }

    // DELETE /api/feature-statistics/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            featureStatisticService.deleteStatistic(id);
            return ResponseEntity.ok(Map.of(
                    "exito", true, "mensaje", "Eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito", false, "mensaje", e.getMessage()));
        }
    }


}
