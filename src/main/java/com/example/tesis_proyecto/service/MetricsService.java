package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.dto.AttackMetricsResponse;
import com.example.tesis_proyecto.model.AttackSimulation;
import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.model.ModelMetrics;
import com.example.tesis_proyecto.repository.AttackSimulationRepository;
import com.example.tesis_proyecto.repository.DetectionRepository;
import com.example.tesis_proyecto.repository.MetricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MetricsService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MetricsRepository metricsRepository;
    @Autowired
    private AttackSimulationRepository attackSimulationRepository;

    @Autowired
    private DetectionRepository detectionRepository;
    // ----------------------------------------------------------
    // Total de alertas registradas
    // ----------------------------------------------------------
    public Long getTotalAlerts() {
        String sql = "SELECT COUNT(*) FROM alerts";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // ----------------------------------------------------------
    // Solo alertas críticas activas (no resueltas)
    // ----------------------------------------------------------
    public Long getCriticalAlerts() {
        String sql = """
            SELECT COUNT(*) FROM alerts
            WHERE severity = 'critical'
              AND status NOT IN ('resolved', 'false_positive')
            """;
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // ----------------------------------------------------------
    // Porcentaje de anomalías sobre total de detecciones
    // ----------------------------------------------------------
    public Double getAnomalyRate() {
        String sql = """
            SELECT 
                ROUND(
                    COUNT(*) FILTER (WHERE is_anomaly = true) * 100.0 
                    / NULLIF(COUNT(*), 0), 
                2)
            FROM detections
            WHERE timestamp >= NOW() - INTERVAL '24 hours'
            """;
        Double rate = jdbcTemplate.queryForObject(sql, Double.class);
        return rate != null ? rate : 0.0;
    }

    // ----------------------------------------------------------
    // Tasa de falsos positivos del modelo activo
    // ----------------------------------------------------------
    public Double getFalsePositiveRate() {
        String sql = """
            SELECT false_positive_rate 
            FROM model_metrics
            ORDER BY created_at DESC
            LIMIT 1
            """;
        try {
            Double fpr = jdbcTemplate.queryForObject(sql, Double.class);
            return fpr != null ? fpr : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    // ----------------------------------------------------------
    // Accuracy del modelo más reciente
    // ----------------------------------------------------------
    public Double getModelAccuracy() {
        return Double.valueOf(metricsRepository.findLatest()
                .map(ModelMetrics::getAccuracy)
                .orElse(0.0f));
    }

    // ----------------------------------------------------------
    // Métricas del día actual — para el card de daily
    // ----------------------------------------------------------
    public Map<String, Object> getDailyMetrics() {
        String sql = """
            SELECT
                total_predictions,
                total_anomalies_detected,
                total_alerts_generated,
                ROUND(avg_reconstruction_error::numeric, 4) AS avg_reconstruction_error,
                ROUND(anomaly_rate::numeric, 4)             AS anomaly_rate,
                ROUND(false_positive_rate::numeric, 4)      AS false_positive_rate,
                alerts_by_severity,
                alerts_by_type
            FROM daily_reports
            WHERE report_date = CURRENT_DATE
            """;
        try {
            return jdbcTemplate.queryForMap(sql);
        } catch (Exception e) {
            // Si no hay reporte de hoy todavía, genera uno vacío
            return Map.of(
                    "total_predictions",        0,
                    "total_anomalies_detected", 0,
                    "total_alerts_generated",   0,
                    "avg_reconstruction_error", 0.0,
                    "anomaly_rate",             0.0,
                    "false_positive_rate",      0.0,
                    "alerts_by_severity",       "{}",
                    "alerts_by_type",           "{}"
            );
        }
    }

    // ----------------------------------------------------------
    // Métricas completas del modelo activo
    // Incluye confusion matrix para mostrar en dashboard
    // ----------------------------------------------------------
    public Map<String, Object> getModelMetrics() {
        return metricsRepository.findLatest()
                .map(m -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("accuracy",           m.getAccuracy());
                    result.put("precision",          m.getPrecision());
                    result.put("recall",             m.getRecall());
                    result.put("f1Score",            m.getF1Score());
                    result.put("aucRoc",             m.getAucRoc());
                    result.put("falsePositiveRate",  m.getFalsePositiveRate());
                    result.put("falseNegativeRate",  m.getFalseNegativeRate());
                    result.put("truePositives",      m.getTruePositives());
                    result.put("trueNegatives",      m.getTrueNegatives());
                    result.put("falsePositives",     m.getFalsePositives());
                    result.put("falseNegatives",     m.getFalseNegatives());
                    result.put("detectionThreshold", m.getDetectionThreshold());
                    result.put("modelVersion",       m.getModelVersion());
                    result.put("normalSamples",      m.getNormalSamples());
                    result.put("anomalySamples",     m.getAnomalySamples());
                    result.put("testSamples",        m.getTestSamples());
                    result.put("createdAt",          m.getCreatedAt());
                    return result;
                })
                .orElse(Map.of("message", "No hay métricas disponibles aún"));
    }


    // Si attackType es null → devuelve métricas de TODOS los ataques
    // Si attackType tiene valor → filtra por ese tipo
    public List<AttackMetricsResponse> getMetrics(String attackType) {

        // 1. Obtener simulaciones (agrupadas por attackType)
        List<AttackSimulation> simulations = (attackType != null && !attackType.isBlank())
                ? attackSimulationRepository.findByAttackTypeOrderByCreatedAtDesc(attackType)
                : attackSimulationRepository.findAllByOrderByCreatedAtDesc();

        // 2. Agrupar simulaciones por attackType
        Map<String, List<AttackSimulation>> porTipo = simulations.stream()
                .collect(Collectors.groupingBy(AttackSimulation::getAttackType));

        // 3. Para cada tipo construir su respuesta
        return porTipo.entrySet().stream().map(entry -> {
            String tipo = entry.getKey();
            List<AttackSimulation> sims = entry.getValue();

            // Tomar la simulación más reciente para las métricas del modelo
            AttackSimulation ultima = sims.get(0);

            // Obtener detecciones reales de la tabla detections
            List<Detections> detecciones = detectionRepository
                    .findByThreatType(tipo, org.springframework.data.domain.Pageable.unpaged())
                    .getContent();

            // Contar anomalías y normales
            long anomalias = detecciones.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getIsAnomaly()))
                    .count();
            long normales = detecciones.size() - anomalias;

            // Desglose por severidad
            Map<AlertSeverity, Long> porSeveridad = detecciones.stream()
                    .filter(d -> d.getSeverity() != null)
                    .collect(Collectors.groupingBy(Detections::getSeverity, Collectors.counting()));

            Map<String, Long> porSeveridadStr = porSeveridad.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().name(),
                            Map.Entry::getValue
                    ));

            // Desglose por estado de investigación
            Map<String, Long> porEstado = detecciones.stream()
                    .filter(d -> d.getInvestigationStatus() != null)
                    .collect(Collectors.groupingBy(Detections::getInvestigationStatus, Collectors.counting()));

            // Promediar métricas de todas las simulaciones del tipo
            double avgPrecision = sims.stream()
                    .mapToDouble(s -> s.getPrecision() != null ? s.getPrecision() : 0.0)
                    .average().orElse(0.0);

            double avgRecall = sims.stream()
                    .mapToDouble(s -> s.getRecall() != null ? s.getRecall() : 0.0)
                    .average().orElse(0.0);

            double avgF1 = sims.stream()
                    .mapToDouble(s -> s.getF1Score() != null ? s.getF1Score() : 0.0)
                    .average().orElse(0.0);

            double avgDetectionRate = sims.stream()
                    .mapToDouble(s -> s.getDetectionRate() != null ? s.getDetectionRate() : 0.0)
                    .average().orElse(0.0);

            return AttackMetricsResponse.builder()
                    .attackType(tipo)
                    .totalDetections(detecciones.size())
                    .anomalies((int) anomalias)
                    .normalEvents((int) normales)
                    .precision(round(avgPrecision))
                    .recall(round(avgRecall))
                    .f1Score(round(avgF1))
                    .detectionRate(round(avgDetectionRate))
                    .avgReconstructionError(round(ultima.getAvgReconstructionError() != null
                            ? ultima.getAvgReconstructionError() : 0.0))
                    .minReconstructionError(round(ultima.getMinReconstructionError() != null
                            ? ultima.getMinReconstructionError() : 0.0))
                    .maxReconstructionError(round(ultima.getMaxReconstructionError() != null
                            ? ultima.getMaxReconstructionError() : 0.0))
                    .stdReconstructionError(round(ultima.getStdReconstructionError() != null
                            ? ultima.getStdReconstructionError() : 0.0))
                    .severityBreakdown(porSeveridadStr)
                    .investigationStatusBreakdown(porEstado)
                    .lastSimulation(ultima.getCreatedAt())
                    .totalSimulationsRun(sims.size())
                    .build();

        }).collect(Collectors.toList());
    }

    private double round(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
