package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.model.AnomalyResult;
import com.example.tesis_proyecto.model.BehaviorProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BehaviorService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ----------------------------------------------------------
    // Construye el perfil normal del usuario desde su historial
    // ----------------------------------------------------------
    public BehaviorProfile buildProfile(String username) {
        BehaviorProfile profile = new BehaviorProfile();
        profile.setUsername(username);

        // Horas usuales de conexión
        List<Map<String, Object>> horas = jdbcTemplate.queryForList("""
            SELECT EXTRACT(HOUR FROM timestamp) as hora,
                   COUNT(*) as frecuencia
            FROM detections
            WHERE username = ? AND is_anomaly = false
            GROUP BY hora
            ORDER BY frecuencia DESC
            LIMIT 3
            """, username);

        List<Integer> horasUsuales = horas.stream()
                .map(h -> ((Number) h.get("hora")).intValue())
                .collect(Collectors.toList());
        profile.setUsualLoginHours(horasUsuales);

        // IPs usuales
        List<Map<String, Object>> ips = jdbcTemplate.queryForList("""
            SELECT source_ip, COUNT(*) as frecuencia
            FROM detections
            WHERE username = ? AND is_anomaly = false
            GROUP BY source_ip
            ORDER BY frecuencia DESC
            LIMIT 5
            """, username);

        List<String> ipsUsuales = ips.stream()
                .map(i -> String.valueOf(i.get("source_ip")))
                .collect(Collectors.toList());
        profile.setUsualIPs(ipsUsuales);

        // Duración promedio de sesión
        Double avgDuration = jdbcTemplate.queryForObject("""
            SELECT AVG(session_duration_sec)
            FROM detections
            WHERE username = ? AND is_anomaly = false
            """, Double.class, username);
        profile.setAverageSessionDuration(
                avgDuration != null ? avgDuration : 0.0);

        // Frecuencia de login (logins por día)
        Double loginFreq = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) * 1.0 /
                   NULLIF(COUNT(DISTINCT DATE(timestamp)), 0)
            FROM detections
            WHERE username = ?
            """, Double.class, username);
        profile.setLoginFrequency(
                loginFreq != null ? loginFreq.intValue() : 0);

        // Risk score calculado
        profile.setRiskScore(calcularRiskScore(username));

        return profile;
    }

    // ----------------------------------------------------------
    // Analiza si el comportamiento ACTUAL es anómalo
    // ----------------------------------------------------------
    public AnomalyResult analyzeCurrentBehavior(
            Map<String, Object> request) {

        String username  = (String) request.get("username");
        String sourceIp  = (String) request.get("sourceIp");
        int    horaActual = ((Number) request.get("hour")).intValue();
        int    duracion   = ((Number) request
                .getOrDefault("sessionDuration", 0)).intValue();

        AnomalyResult result = new AnomalyResult();
        List<String> razones = new ArrayList<>();
        double score = 0.0;

        BehaviorProfile profile = buildProfile(username);

        // 1. Verifica si la hora es inusual
        if (!profile.getUsualLoginHours().isEmpty() &&
                !profile.getUsualLoginHours().contains(horaActual)) {
            razones.add("Hora de acceso inusual: " + horaActual + ":00h");
            score += 0.30;
        }

        // 2. Verifica si la IP es nueva
        if (!profile.getUsualIPs().isEmpty() &&
                !profile.getUsualIPs().contains(sourceIp)) {
            razones.add("IP no reconocida: " + sourceIp);
            score += 0.35;
        }

        // 3. Verifica duración de sesión anómala
        double avgDuration = profile.getAverageSessionDuration();
        if (avgDuration > 0 && duracion > avgDuration * 3) {
            razones.add(String.format(
                    "Sesión inusualmente larga: %ds (promedio: %.0fs)",
                    duracion, avgDuration));
            score += 0.20;
        }

        // 4. Verifica frecuencia alta de logins hoy
        Integer loginsHoy = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM detections
            WHERE username = ?
              AND DATE(timestamp) = CURRENT_DATE
            """, Integer.class, username);

        if (loginsHoy != null &&
                loginsHoy > profile.getLoginFrequency() * 2) {
            razones.add("Frecuencia de acceso inusualmente alta hoy: "
                    + loginsHoy + " accesos");
            score += 0.15;
        }

        // Determina nivel de riesgo
        result.setAnomaly(score > 0.3);
        result.setConfidence(Math.min(score, 1.0));
        result.setReasons(razones);

        if      (score >= 0.7) result.setRiskLevel("critical");
        else if (score >= 0.5) result.setRiskLevel("high");
        else if (score >= 0.3) result.setRiskLevel("medium");
        else                   result.setRiskLevel("low");

        return result;
    }

    // ----------------------------------------------------------
    // Top 10 usuarios con comportamiento más sospechoso
    // ----------------------------------------------------------
    public List<Map<String, Object>> getTopSuspiciousUsers() {
        return jdbcTemplate.queryForList("""
            SELECT username,
                   COUNT(*) as total_anomalias,
                   ROUND(AVG(reconstruction_error)::numeric, 4)
                       as avg_error,
                   MAX(timestamp) as ultima_actividad,
                   COUNT(DISTINCT source_ip) as ips_distintas
            FROM detections
            WHERE is_anomaly = true
              AND timestamp >= NOW() - INTERVAL '7 days'
            GROUP BY username
            ORDER BY total_anomalias DESC
            LIMIT 10
            """);
    }

    // ----------------------------------------------------------
    // Risk score del usuario (0.0 a 1.0)
    // ----------------------------------------------------------
    private double calcularRiskScore(String username) {
        Integer totalAnomalias = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM detections
            WHERE username = ?
              AND is_anomaly = true
              AND timestamp >= NOW() - INTERVAL '30 days'
            """, Integer.class, username);

        Integer totalAccesos = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM detections
            WHERE username = ?
              AND timestamp >= NOW() - INTERVAL '30 days'
            """, Integer.class, username);

        if (totalAccesos == null || totalAccesos == 0) return 0.0;
        return Math.min(
                (double)(totalAnomalias != null ? totalAnomalias : 0)
                        / totalAccesos, 1.0);
    }
}
