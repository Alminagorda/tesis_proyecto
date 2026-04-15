package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.repository.DetectionRepository;
import com.example.tesis_proyecto.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnomalyNotificationScheduler {

    private final DetectionRepository detectionRepository;
    private final EmailService emailService;

    // ✅ Corre cada 60 segundos automáticamente
    @Scheduled(fixedDelay = 60000)
    public void verificarAnomalias() {
        // Busca anomalías de los últimos 2 minutos que no hayan sido notificadas
        LocalDateTime desde = LocalDateTime.now().minusMinutes(2);

        List<Detections> anomalias = detectionRepository
                .findByIsAnomalyTrueAndNotificadoFalseAndTimestampAfter(desde);

        if (anomalias.isEmpty()) return;

        log.info("Scheduler: {} anomalías nuevas encontradas", anomalias.size());

        for (Detections detection : anomalias) {
            try {
                emailService.notificarAnomaliaDetectada(detection);
                // Marca como notificado para no mandar el mismo email dos veces
                detection.setNotificado(true);
                detectionRepository.save(detection);
            } catch (Exception e) {
                log.error("Error notificando detección {}: {}", detection.getId(), e.getMessage());
            }
        }
    }
}