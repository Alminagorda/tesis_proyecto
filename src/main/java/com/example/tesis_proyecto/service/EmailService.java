package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.model.NotificationPreference;
import com.example.tesis_proyecto.repository.NotificationPreferenceRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final NotificationPreferenceRepository preferenceRepository;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;




    @Async
    public void notificarAnomaliaDetectada(Detections detection) {
        String severity = detection.getSeverity().name(); // "critical", "high", etc.

        LocalDateTime desde = LocalDateTime.now().minusHours(1);
        List<NotificationPreference> destinatarios =
                preferenceRepository.findAllToNotifyBySeverity(severity, desde);

        if (destinatarios.isEmpty()) {
            log.info("Sin destinatarios para severidad: {}", severity);
            return;
        }

        for (NotificationPreference pref : destinatarios) {
            try {
                String email    = pref.getUser().getEmail();
                // ✅ usa fullName en lugar de username
                String nombre   = pref.getUser().getFullName() != null
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
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, fromName);
        helper.setTo(destinatario);
        helper.setSubject(asunto(detection));
        helper.setText(htmlBody(nombre, detection), true);

        mailSender.send(message);
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
        String color = switch (detection.getSeverity()) {
            case critical -> "#dc2626";
            case high     -> "#ea580c";
            case medium   -> "#ca8a04";
            case low      -> "#2563eb";
        };

        String timestamp = detection.getTimestamp() != null
                ? detection.getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : "N/A";

        String threatType   = detection.getThreatType()   != null ? detection.getThreatType()   : "ANOMALY";
        String sourceIp     = detection.getSourceIp()     != null ? detection.getSourceIp()     : "N/A";
        String status       = detection.getInvestigationStatus() != null
                ? detection.getInvestigationStatus() : "pending";
        float  error        = detection.getReconstructionError() != null
                ? detection.getReconstructionError() : 0f;
        float  confidence   = detection.getConfidence() != null
                ? detection.getConfidence() * 100 : 0f;

        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="font-family:Arial,sans-serif;background:#f4f4f4;padding:20px;margin:0;">
              <div style="max-width:600px;margin:auto;background:#fff;
                          border-radius:8px;overflow:hidden;
                          box-shadow:0 2px 8px rgba(0,0,0,0.1);">

                <div style="background:%s;padding:24px 32px;">
                  <h1 style="color:#fff;margin:0;font-size:20px;">
                    🔒 Alerta de Seguridad — %s
                  </h1>
                  <p style="color:rgba(255,255,255,0.85);margin:6px 0 0;font-size:13px;">
                    Sistema de Detección de Anomalías 
                  </p>
                </div>

                <div style="padding:32px;">
                  <p style="color:#374151;font-size:15px;margin-top:0;">
                    Hola <strong>%s</strong>,
                  </p>
                  <p style="color:#374151;font-size:15px;">
                    El sistema detectó una anomalía que requiere tu atención:
                  </p>

                  <table style="width:100%%;border-collapse:collapse;
                                font-size:14px;margin:20px 0;">
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;width:45%%;">Tipo de amenaza</td>
                      <td style="padding:10px 14px;color:#111827;font-weight:bold;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 14px;color:#6b7280;">Severidad</td>
                      <td style="padding:10px 14px;">
                        <span style="background:%s;color:#fff;padding:3px 10px;
                                     border-radius:4px;font-size:12px;font-weight:bold;">
                          %s
                        </span>
                      </td>
                    </tr>
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">IP de origen</td>
                      <td style="padding:10px 14px;color:#111827;font-family:monospace;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 14px;color:#6b7280;">Error de reconstrucción</td>
                      <td style="padding:10px 14px;color:#111827;">%.6f</td>
                    </tr>
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">Confianza del modelo</td>
                      <td style="padding:10px 14px;color:#111827;">%.2f%%</td>
                    </tr>
                    <tr>
                      <td style="padding:10px 14px;color:#6b7280;">Fecha y hora</td>
                      <td style="padding:10px 14px;color:#111827;">%s</td>
                    </tr>
                    <tr style="background:#f9fafb;">
                      <td style="padding:10px 14px;color:#6b7280;">Estado</td>
                      <td style="padding:10px 14px;color:#111827;">%s</td>
                    </tr>
                  </table>

                  <p style="color:#6b7280;font-size:13px;margin-top:24px;">
                    Ingresa al panel de seguridad para revisar y gestionar esta alerta.
                  </p>
                </div>

                <div style="background:#f9fafb;padding:16px 32px;
                            border-top:1px solid #e5e7eb;text-align:center;">
                  <p style="color:#9ca3af;font-size:12px;margin:0;">
                    Sistema de Detección de Anomalías —  · Notificación automática
                  </p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(
                color, detection.getSeverity().name().toUpperCase(),
                nombre,
                threatType,
                color, detection.getSeverity().name().toUpperCase(),
                sourceIp,
                error,
                confidence,
                timestamp,
                status
        );
    }


    public void sendOtpEmail(String toEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("🔐 Tu código de verificación de seguridad");

        String htmlContent = String.format("""
            <html>
                <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; padding: 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                        <h2 style="color: #333; text-align: center;">Código de Verificación</h2>
                        <p style="color: #666; font-size: 16px;">Hola,</p>
                        <p style="color: #666; font-size: 16px;">Se ha solicitado acceso a tu cuenta. Para continuar, ingresa el siguiente código:</p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <div style="font-size: 48px; font-weight: bold; color: #007bff; letter-spacing: 10px; font-family: monospace; background-color: #f0f8ff; padding: 20px; border-radius: 5px;">
                                %s
                            </div>
                        </div>
                        
                        <p style="color: #999; font-size: 14px; text-align: center;">Este código expira en <strong>10 minutos</strong></p>
                        
                        <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                        
                        <p style="color: #999; font-size: 12px;">
                            Si no iniciaste sesión, por favor ignora este correo.
                            <br>
                            Tu seguridad es importante para nosotros. Nunca compartimos tu código con nadie.
                        </p>
                    </div>
                </body>
            </html>
            """, otp);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}