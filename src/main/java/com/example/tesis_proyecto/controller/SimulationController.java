package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.SimulationHistoryResponse;
import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.service.SimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/simulations")
@CrossOrigin(origins = "*")
public class SimulationController {
    @Autowired
    private SimulationService simulationService;

    @PostMapping("/phishing")
    public ResponseEntity<Map<String, Object>> runPhishingSimulation(@RequestBody Map<String, Object> request) {
        try {
            int targetCount = (Integer) request.getOrDefault("targetCount", 10);
            String attackType = (String) request.getOrDefault("attackType", "phishing");

            List<Detections> results = simulationService.simulatePhishingAttack(targetCount, attackType);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Simulación de phishing completada: " + results.size() + " detecciones");
            response.put("resultados", results);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("mensaje", "Error en simulación: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/unauthorized-access")
    public ResponseEntity<Map<String, Object>> runUnauthorizedAccessSimulation(@RequestBody Map<String, Object> request) {
        try {
            int targetCount = (Integer) request.getOrDefault("targetCount", 10);

            List<Detections> results = simulationService.simulateUnauthorizedAccess(targetCount);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Simulación de acceso no autorizado completada: " + results.size() + " detecciones");
            response.put("resultados", results);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("exito", false);
            error.put("mensaje", "Error en simulación: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/brute-force")
    public ResponseEntity<Map<String, Object>> runBruteForceSimulation(@RequestBody Map<String, Object> request) {
        try {
            int attempts = (Integer) request.getOrDefault("attempts", 50);
            List<Detections> results = simulationService.simulateBruteForceAttack(attempts);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Simulación de ataque de fuerza bruta completada");
            response.put("resultados", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("exito", false, "mensaje", e.getMessage()));
        }
    }

    @PostMapping("/malware")
    public ResponseEntity<Map<String, Object>> runMalwareSimulation(@RequestBody Map<String, Object> request) {
        try {
            String malwareType = (String) request.getOrDefault("malwareType", "trojan");
            List<Detections> results = simulationService.simulateMalwareDetection(malwareType);

            Map<String, Object> response = new HashMap<>();
            response.put("exito", true);
            response.put("mensaje", "Simulación de detección de malware completada");
            response.put("resultados", results);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("exito", false, "mensaje", e.getMessage()));
        }
    }
    @PostMapping("/ransomware")
    public ResponseEntity<Map<String, Object>> runRansomwareSimulation(
            @RequestBody Map<String, Object> request) {
        try {
            // "reconocimiento" o "cifrado"
            String fase = (String) request.getOrDefault("fase", "reconocimiento");

            List<Detections> results =
                    simulationService.simulateRansomwareAttack(fase);

            return ResponseEntity.ok(Map.of(
                    "exito",      true,
                    "fase",       fase,
                    "mensaje",    "Simulación ransomware fase '" + fase
                            + "' completada: " + results.size() + " detecciones",
                    "resultados", results,
                    "timestamp",  System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito",   false,
                    "mensaje", "Error en simulación: " + e.getMessage()
            ));
        }
    }
    /**
     * GET /api/simulations/history
     * GET /api/simulations/history?attackType=ransomware  ← opcional
     */
    @GetMapping("/history")
    public ResponseEntity<SimulationHistoryResponse> getSimulationHistory(
            @RequestParam(required = false) String attackType) {
        return ResponseEntity.ok(simulationService.getSimulationHistory(attackType));
    }
}
