package com.example.tesis_proyecto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GeolocationService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // ============================================================
    // Obtiene las últimas detecciones anómalas con su ubicación
    // Para pintar los puntos rojos en el mapa
    // ============================================================
    public List<Map<String, Object>> getAnomalousLocations(int limit) {
        String sql = """
            SELECT 
                d.username,
                d.source_ip,
                d.threat_type,
                d.severity,
                d.reconstruction_error,
                d.confidence,
                d.timestamp,
                a.geographic_info
            FROM detections d
            LEFT JOIN alerts a ON d.source_ip = a.source_ip
            WHERE d.is_anomaly = true
              AND a.geographic_info IS NOT NULL
            ORDER BY d.timestamp DESC
            LIMIT ?
            """;

        return jdbcTemplate.queryForList(sql, limit);
    }

    // ============================================================
    // Perfil de ubicación de un usuario específico
    // ¿Desde dónde se conecta normalmente?
    // ============================================================
    public Map<String, Object> getUserLocationProfile(String username) {
        String sql = """
            SELECT 
                username,
                COUNT(*) as total_conexiones,
                COUNT(*) FILTER (WHERE is_anomaly = true) as conexiones_anomalas,
                MAX(timestamp) as ultima_conexion,
                source_ip as ultima_ip
            FROM detections
            WHERE username = ?
            GROUP BY username, source_ip
            ORDER BY ultima_conexion DESC
            LIMIT 1
            """;

        try {
            return jdbcTemplate.queryForMap(sql, username);
        } catch (Exception e) {
            return null;
        }
    }

    // ============================================================
    // Detecta si una ubicación es anómala para ese usuario
    // Compara con su historial de ubicaciones normales
    // ============================================================
    public Map<String, Object> detectAnomalousLocation(
            String username, Double latitude, Double longitude,
            String city, String country, String sourceIp) {

        // 1. Busca la ubicación habitual del usuario
        String sqlUbicacionNormal = """
            SELECT 
                (geographic_info->>'latitude')::FLOAT  as lat_normal,
                (geographic_info->>'longitude')::FLOAT as lon_normal,
                (geographic_info->>'country')          as pais_normal
            FROM alerts
            WHERE geographic_info->>'username' = ?
              AND status != 'false_positive'
            ORDER BY detected_at DESC
            LIMIT 5
            """;

        List<Map<String, Object>> historial =
                jdbcTemplate.queryForList(sqlUbicacionNormal, username);

        boolean isAnomaly = false;
        double distanciaKm = 0;
        String motivo = "Ubicación dentro del rango normal";

        if (!historial.isEmpty()) {
            // 2. Calcula distancia promedio desde ubicaciones normales
            double latNormal = (Double) historial.get(0).get("lat_normal");
            double lonNormal = (Double) historial.get(0).get("lon_normal");

            distanciaKm = calcularDistanciaKm(latitude, longitude, latNormal, lonNormal);

            // Si está a más de 500km de su ubicación habitual → anomalía
            if (distanciaKm > 500) {
                isAnomaly = true;
                motivo = String.format(
                        "Acceso desde %.0f km de ubicación habitual (%s)",
                        distanciaKm, country
                );
            }
        } else {
            // Usuario nuevo sin historial → ubicación fuera de Perú es sospechosa
            // Coordenadas aproximadas de Perú: lat -9 a -18, lon -68 a -81
            if (latitude < -18 || latitude > -3 || longitude < -81 || longitude > -68) {
                isAnomaly = true;
                motivo = "Primer acceso desde ubicación fuera de Perú: " + country;
            }
        }

        // 3. Si es anomalía, guarda en alerts con geographic_info
        if (isAnomaly) {
            guardarAlertaGeografica(username, latitude, longitude,
                    city, country, sourceIp, motivo, distanciaKm);
        }

        return Map.of(
                "isAnomaly",    isAnomaly,
                "distanciaKm",  Math.round(distanciaKm),
                "motivo",       motivo,
                "ciudad",       city != null ? city : "Desconocida",
                "pais",         country != null ? country : "Desconocido",
                "latitude",     latitude,
                "longitude",    longitude
        );
    }

    // ============================================================
    // Estadísticas generales para las métricas del mapa
    // Los 3 cards que aparecen debajo del mapa
    // ============================================================
    public Map<String, Object> getGeolocationStats() {
        String sql = """
            SELECT
                COUNT(*)                                            AS total_ataques_externos,
                (geographic_info->>'country')                       AS pais_frecuente,
                COUNT(geographic_info->>'country')                  AS frecuencia
            FROM alerts
            WHERE geographic_info IS NOT NULL
              AND (geographic_info->>'country') IS NOT NULL
            GROUP BY geographic_info->>'country'
            ORDER BY frecuencia DESC
            LIMIT 1
            """;

        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql);

            // Cuenta total de ataques externos
            Integer totalExternos = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*) FROM alerts 
                    WHERE geographic_info IS NOT NULL
                      AND severity IN ('high','critical')
                    """,
                    Integer.class
            );

            // Tipo de ataque más común
            String tipoComun = jdbcTemplate.queryForObject(
                    """
                    SELECT alert_type FROM alerts
                    WHERE geographic_info IS NOT NULL
                    GROUP BY alert_type
                    ORDER BY COUNT(*) DESC
                    LIMIT 1
                    """,
                    String.class
            );

            return Map.of(
                    "totalAtaquesExternos", totalExternos != null ? totalExternos : 0,
                    "paisMasFrecuente",     result.getOrDefault("pais_frecuente", "N/A"),
                    "tipoMasComun",         tipoComun != null ? tipoComun : "N/A"
            );

        } catch (Exception e) {
            return Map.of(
                    "totalAtaquesExternos", 0,
                    "paisMasFrecuente",     "N/A",
                    "tipoMasComun",         "N/A"
            );
        }
    }

    // ============================================================
    // Fórmula de Haversine — distancia real entre dos coordenadas
    // ============================================================
    private double calcularDistanciaKm(double lat1, double lon1,
                                       double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ============================================================
    // Guarda la alerta geográfica en la tabla alerts
    // ============================================================
    private void guardarAlertaGeografica(String username, Double lat, Double lon,
                                         String city, String country,
                                         String sourceIp, String motivo,
                                         double distanciaKm) {
        String sql = """
            INSERT INTO alerts (
                alert_type, severity, status, description,
                geographic_info, source_ip, detected_at
            ) VALUES (
                'VPN_Unauthorized', 'high', 'new', ?,
                ?::jsonb, ?, NOW()
            )
            """;

        String geoJson = String.format(
                """
                {
                    "username": "%s",
                    "latitude": %f,
                    "longitude": %f,
                    "city": "%s",
                    "country": "%s",
                    "distancia_km": %.0f
                }
                """,
                username, lat, lon,
                city != null ? city : "",
                country != null ? country : "",
                distanciaKm
        );

        jdbcTemplate.update(sql, motivo, geoJson, sourceIp);
    }
}
