package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.NotificationPreference;
import com.example.tesis_proyecto.service.EmailService;
import com.example.tesis_proyecto.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailController {

    private final NotificationPreferenceService preferenceService;
    private final EmailService emailService;

    @GetMapping("/preferences/{userId}")
    public ResponseEntity<NotificationPreference> getPreferences(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(preferenceService.getOrCreate(userId));
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<NotificationPreference> updatePreferences(
            @PathVariable UUID userId,
            @RequestBody Map<String, Boolean> body) {
        return ResponseEntity.ok(preferenceService.update(
                userId,
                body.get("emailEnabled"),
                body.get("notifyCritical"),
                body.get("notifyHigh"),
                body.get("notifyMedium"),
                body.get("notifyLow")
        ));
    }

    @PostMapping("/test")
    public ResponseEntity<String> testEmail() {
        try {
            emailService.sendOtpEmail("60659997@pronabec.edu.pe", "123456");
            return ResponseEntity.ok("Email enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}