package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.model.TrainingRuns;
import com.example.tesis_proyecto.repository.TrainingRunRepository;
import com.example.tesis_proyecto.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class DataImportController {
    @Autowired
    private DataImportService dataImportService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private TrainingRunRepository trainingRunsRepository;

    


    public DataImportController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Un solo endpoint que importa todo
    @PostMapping("/all")
    public ResponseEntity<Map<String, Object>> importarTodo() {
        try {
            Map<String, Object> resultado =
                    dataImportService.importarTodo();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error",  e.getMessage(),
                    "status", "fallido"
            ));
        }
    }
    @PostMapping("/generate-alerts")
    public ResponseEntity<Map<String, Object>> generarAlertas() {
        try {
            // Obtiene el training_run_id que tienes
            String trainingRunId = jdbcTemplate.queryForObject(
                    "SELECT id FROM training_runs ORDER BY created_at DESC LIMIT 1",
                    String.class
            );

            UUID trainingUUID = UUID.fromString(trainingRunId);
            List<Map<String, Object>> alertas = List.of(
                    Map.of("type","Ransomware_Cifrado",   "severity","critical"),
                    Map.of("type","BruteForce",            "severity","high"),
                    Map.of("type","Phishing",              "severity","high"),
                    Map.of("type","VPN_Unauthorized",      "severity","critical"),
                    Map.of("type","DDoS",                  "severity","high"),
                    Map.of("type","PLC_Injection",         "severity","critical"),
                    Map.of("type","Exfiltracion",          "severity","critical"),
                    Map.of("type","Malware",               "severity","high"),
                    Map.of("type","PortScanning",          "severity","medium"),
                    Map.of("type","Ransomware_Reconocimiento", "severity","medium")
            );

            String[] ips = {
                    "45.33.32.156", "185.220.101.45",
                    "103.21.244.10", "196.207.40.15",
                    "177.54.144.20", "59.188.12.100"
            };
            String[] protocolos = {"TCP","HTTP","HTTPS","Modbus"};
            String[] estados    = {"new","reviewing","resolved"};
            Random random = new Random();
            // Agrega esto antes del for loop — coordenadas por IP
            Map<String, String[]> ipGeoData = new HashMap<>();
            ipGeoData.put("45.33.32.156",    new String[]{"55.7558", "37.6173",  "Rusia"});
            ipGeoData.put("185.220.101.45",  new String[]{"52.5200", "13.4050",  "Alemania"});
            ipGeoData.put("103.21.244.10",   new String[]{"39.9042", "116.4074", "China"});
            ipGeoData.put("196.207.40.15",   new String[]{"6.5244",  "3.3792",   "Nigeria"});
            ipGeoData.put("177.54.144.20",   new String[]{"-23.5505","-46.6333", "Brasil"});
            ipGeoData.put("59.188.12.100",   new String[]{"39.0392", "125.7625", "Corea del Norte"});

            int count = 0;
            for (Map<String, Object> alerta : alertas) {
                // Genera 3 alertas por tipo con fechas distintas
                for (int i = 0; i < 3; i++) {
                    String ip        = ips[random.nextInt(ips.length)];
                    String[] geoData = ipGeoData.get(ip);
                    String geoJson   = String.format(
                            "{\"latitude\": %s, \"longitude\": %s, \"country\": \"%s\", \"source_ip\": \"%s\"}",
                            geoData[0], geoData[1], geoData[2], ip
                    );
                    LocalDateTime fecha = LocalDateTime.now().minusHours(i * 2L);

                    jdbcTemplate.update("""
    INSERT INTO alerts (
        id, training_run_id, alert_type,
        severity, status, description,
        reconstruction_error, detection_confidence,
        source_ip, protocol, detected_at,
        affected_variables, top_indicators,
        affected_systems, geographic_info
    ) VALUES (
        uuid_generate_v4(), ?, ?,
        ?::alert_severity, ?::alert_status, ?,
        ?, ?, ?, ?, ?,
        '{}'::jsonb, '{}'::jsonb,
        '{}'::jsonb, ?::jsonb
    )
    """,
                            trainingUUID,
                            alerta.get("type"),
                            alerta.get("severity"),
                            estados[random.nextInt(estados.length)],
                            "Anomalía detectada: " + alerta.get("type"),
                            (float)(0.05 + random.nextDouble() * 0.5),
                            (float)(0.7  + random.nextDouble() * 0.3),
                            ip,
                            protocolos[random.nextInt(protocolos.length)],
                            fecha,
                            geoJson          // ← geographic_info con coordenadas reales
                    );
                    count++;
                }
            }

            return ResponseEntity.ok(Map.of(
                    "status",  "ok",
                    "alertas", count,
                    "mensaje", count + " alertas generadas"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/feature-statistics")
    public ResponseEntity<Map<String, Object>> importarFeatureStatistics() {
        try {
            // Toma el training run más reciente
            TrainingRuns trainingRun = trainingRunsRepository
                    .findLatest()
                    .orElseThrow(() -> new RuntimeException(
                            "No hay training run — ejecuta /api/import/all primero"));

            dataImportService.importarFeatureStatistics(
                    trainingRun.getId().toString());

            return ResponseEntity.ok(Map.of(
                    "exito",   true,
                    "mensaje", "feature_statistics importado correctamente",
                    "trainingRunId", trainingRun.getId().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "exito",   false,
                    "mensaje", "Error: " + e.getMessage()
            ));
        }
    }



}
