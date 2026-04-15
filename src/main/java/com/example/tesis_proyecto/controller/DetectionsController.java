package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.service.DetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/detections")
@CrossOrigin(origins = "*")
public class DetectionsController {
    @Autowired
    private DetectionService detectionService;

    @GetMapping
    public ResponseEntity<List<Detections>> getDetections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<Detections> detections = detectionService.getAllDetections(PageRequest.of(page, size));
        return ResponseEntity.ok(detections.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Detections> getDetectionById(@PathVariable UUID id) {
        return detectionService.getDetectionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Detections> createDetection(@RequestBody Detections detection) {
        Detections saved = (Detections) detectionService.saveDetection(detection);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDetection(@PathVariable UUID id) {
        detectionService.deleteDetection(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filter/severity/{severity}")
    public ResponseEntity<List<Detections>> getDetectionsBySeverity(@PathVariable String severity) {
        List<Detections> detections = detectionService.getDetectionsBySeverity(severity);
        return ResponseEntity.ok(detections);
    }
    @GetMapping("/type")
    public ResponseEntity<List<Detections>> getByThreatType(
            @RequestParam String threatType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        Page<Detections> detections = detectionService.getDetectionsByThreatType(
                threatType,
                PageRequest.of(page, size, Sort.by("id").descending())
        );

        return ResponseEntity.ok(detections.getContent());
    }
}
