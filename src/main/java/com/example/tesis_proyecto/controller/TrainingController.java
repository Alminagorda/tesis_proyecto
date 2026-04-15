package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.TrainingHistoryResponse;
import com.example.tesis_proyecto.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrainingController {
    private final TrainingService trainingService;

    /**
     * GET /api/training/history
     * Retorna curva de entrenamiento del run más reciente
     */
    @GetMapping("/history")
    public ResponseEntity<TrainingHistoryResponse> getTrainingHistory() {
        return ResponseEntity.ok(trainingService.getTrainingHistory());
    }
}
