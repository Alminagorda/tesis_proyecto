package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.model.NotificationPreference;
import com.example.tesis_proyecto.repository.NotificationPreferenceRepository;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class EmailService {

    private final Resend resend;
    private final NotificationPreferenceRepository preferenceRepository;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    public EmailService(@Value("${RESEND_API_KEY}") String apiKey,
                        NotificationPreferenceRepository preferenceRepository) {
        this.resend = new Resend(apiKey);
        this.preferenceRepository = preferenceRepository;
    }

    @Async
    public void notificarAnomaliaDetectada(Detections detection) {
        String severity = detection.getSeverity().name();
        LocalDateTime desde = LocalDateTime.now().minusHours(1);
        List<NotificationPreference> destinatarios =
                preferenceRepository.findAllToNotifyBySeverity(severity, desde);

        if (destinatarios.isEmpty()) {
            log.info("Sin destinatarios para severidad: {}", severity);
            return;
        }

        for (NotificationPreference pref : destinatarios) {
            try {
                String email = pref.getUser().getEmail();
                String nombre = pref.getUser().getFullName() != null
                        ? pref.getUser().getFullName() : email;
                enviarEmailAlerta(email, nombre, detection);
                log.info("Email enviado a {} — detección {}", email, detection.getId());
            } catch (Exception e) {
                log.error("Error al enviar email a {}: {}",
                        pref.getUser().getEmail(), e.getMessage());
            }
        }
    }

    private void enviarEmailAlerta(String destinatario, String nombre,
                                   Detections detection) throws Exception {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromName + " <" + fromEmail + ">")
                .to(destinatario)
                .subject(asunto(detection))
                .html(htmlBody(nombre, detection))
                .build();

        resend.emails().send(params);
    }

    public void sendOtpEmail(String toEmail, String otp) throws Exception {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromName + " <" + fromEmail + ">")
                .to(toEmail)
                .subject("🔐 Tu código de verificación de seguridad")
                .html(otpHtml(otp))
                .build();

        resend.emails().send(params);
    }


    private String asunto(Detections detection) {
        String emoji = switch (detection.getSeverity()) {
            case critical -> "🚨";
            case high     -> "⚠️";
            case medium   -> "🔔";
            case low      -> "ℹ️";
        };
        return emoji + " [" + detection.getSeverity().name().toUpperCase()
                + "] Anomalía detectada — "
                + (detection.getThreatType() != null ? detection.getThreatType() : "ANOMALY");
    }

    private String htmlBody(String nombre, Detections detection) {
        // ← mismo HTML que tenías antes, sin cambios
        String color = switch (detection.getSeverity()) {
            case critical -> "#dc2626";
            case high     -> "#ea580c";
            case medium   -> "#ca8a04";
            case low      -> "#2563eb";
        };

        String timestamp = detection.getTimestamp() != null
                ? detection.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : "N/A";

        String threatType = detection.getThreatType() != null ? detection.getThreatType() : "ANOMALY";
        String sourceIp   = detection.getSourceIp()   != null ? detection.getSourceIp()   : "N/A";
        String status     = detection.getInvestigationStatus() != null
                ? detection.getInvestigationStatus() : "pending";
        float error      = detection.getReconstructionError() != null
                ? detection.getReconstructionError() : 0f;
        float confidence = detection.getConfidence() != null
                ? detection.getConfidence() * 100 : 0f;

        return """
            <!DOCTYPE html><html><head><meta charset="UTF-8"></head>
            <body style="font-family:Arial,sans-serif;background:#f4f4f4;padding:20px;margin:0;">
              <div style="max-width:600px;margin:auto;background:#fff;border-radius:8px;
                          overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                <div style="background:%s;padding:24px 32px;">
                  <h1 style="color:#fff;margin:0;font-size:20px;">🔒 Alerta de Seguridad — %s</h1>
                </div>
                <div style="padding:32px;">
                  <p>Hola <strong>%s</strong>,</p>
                  <table style="width:100%%;border-collapse:collapse;font-size:14px;margin:20px 0;">
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">Tipo de amenaza</td>
                      <td style="padding:10px 14px;font-weight:bold;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 14px;color:#6b7280;">Severidad</td>
                      <td style="padding:10px 14px;">
                        <span style="background:%s;color:#fff;padding:3px 10px;border-radius:4px;">%s</span>
                      </td>
                    </tr>
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">IP de origen</td>
                      <td style="padding:10px 14px;font-family:monospace;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 14px;color:#6b7280;">Error de reconstrucción</td>
                      <td style="padding:10px 14px;">%.6f</td>
                    </tr>
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">Confianza</td>
                      <td style="padding:10px 14px;">%.2f%%</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 14px;color:#6b7280;">Fecha y hora</td>
                      <td style="padding:10px 14px;">%s</td>
                    </tr>
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">Estado</td>
                      <td style="padding:10px 14px;">%s</td>
                    </tr>
                  </table>
                </div>
              </div>
            </body></html>
            """.formatted(color, detection.getSeverity().name().toUpperCase(),
                nombre, threatType, color, detection.getSeverity().name().toUpperCase(),
                sourceIp, error, confidence, timestamp, status);
    }

    private String otpHtml(String otp) {
        return String.format("""
            <html>
            <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:20px;">
              <div style="max-width:600px;margin:0 auto;background:white;padding:30px;border-radius:8px;">
                <h2 style="color:#333;text-align:center;">Código de Verificación</h2>
                <p style="color:#666;">Ingresa el siguiente código para continuar:</p>
                <div style="text-align:center;margin:30px 0;">
                  <div style="font-size:48px;font-weight:bold;color:#007bff;
                              letter-spacing:10px;font-family:monospace;
                              background:#f0f8ff;padding:20px;border-radius:5px;">
                    %s
                  </div>
                </div>
                <p style="color:#999;text-align:center;">Expira en <strong>10 minutos</strong></p>
              </div>
            </body></html>
            """, otp);
    }
}