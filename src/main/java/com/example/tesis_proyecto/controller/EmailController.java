package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.NotificationPreference;
import com.example.tesis_proyecto.service.NotificationPreferenceService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmailController {

    private final NotificationPreferenceService preferenceService;

    @Autowired
    private JavaMailSender mailSender;
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
            MimeMessage message =
                    mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("gala.alminagorda@gmail.com", "Sistema Antamina");
            helper.setTo("60659997@pronabec.edu.pe"); // te lo mandas a ti mismo
            helper.setSubject("🧪 Prueba de email — Sistema Antamina");
            helper.setText("<h2>El email funciona correctamente ✅</h2>", true);
            mailSender.send(message);
            return ResponseEntity.ok("Email enviado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}