package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.service.GeolocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/geolocation")
@CrossOrigin(origins = "*")
public class GeolocationController {
    @Autowired
    private GeolocationService geolocationService;

    @GetMapping("/anomalies")
    public ResponseEntity<List<Map<String, Object>>> getAnomalouLocations(
            @RequestParam(defaultValue = "20") int limit) {
        List<Map<String, Object>> anomalies = geolocationService.getAnomalousLocations(limit);
        return ResponseEntity.ok(anomalies);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Map<String, Object>> getUserLocationProfile(@PathVariable String username) {
        Map<String, Object> profile = geolocationService.getUserLocationProfile(username);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/check-anomaly")
    public ResponseEntity<Map<String, Object>> checkLocationAnomaly(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            Double latitude = ((Number) request.get("latitude")).doubleValue();
            Double longitude = ((Number) request.get("longitude")).doubleValue();
            String city = (String) request.get("city");
            String country = (String) request.get("country");
            String sourceIp = (String) request.get("sourceIp");

            Map<String, Object> result = geolocationService.detectAnomalousLocation(
                    username, latitude, longitude, city, country, sourceIp
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "isAnomaly", false,
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGeolocationStats() {
        Map<String, Object> stats = geolocationService.getGeolocationStats();
        return ResponseEntity.ok(stats);
    }
}
