package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.AnomalyResult;
import com.example.tesis_proyecto.model.BehaviorProfile;
import com.example.tesis_proyecto.service.BehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/behavior")
@CrossOrigin(origins = "*")
public class BehaviorController {
    @Autowired
    private BehaviorService behaviorService;

    // Perfil de comportamiento de un usuario
    @GetMapping("/profile/{username}")
    public ResponseEntity<BehaviorProfile> getProfile(
            @PathVariable String username) {
        return ResponseEntity.ok(
                behaviorService.buildProfile(username));
    }

    // Analiza si una acción actual es anómala
    @PostMapping("/analyze")
    public ResponseEntity<AnomalyResult> analyze(
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(
                behaviorService.analyzeCurrentBehavior(request));
    }

    // Top usuarios con comportamiento sospechoso
    @GetMapping("/suspicious")
    public ResponseEntity<List<Map<String, Object>>> getSuspicious() {
        return ResponseEntity.ok(
                behaviorService.getTopSuspiciousUsers());
    }
}
