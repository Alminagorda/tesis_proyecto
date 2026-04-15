package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.AlertSeverity;
import com.example.tesis_proyecto.dto.MlResponse;
import com.example.tesis_proyecto.dto.SimulationHistoryResponse;
import com.example.tesis_proyecto.model.AttackSimulation;
import com.example.tesis_proyecto.model.Detections;
import com.example.tesis_proyecto.model.TrainingRuns;
import com.example.tesis_proyecto.repository.AttackSimulationRepository;
import com.example.tesis_proyecto.repository.DetectionRepository;
import com.example.tesis_proyecto.repository.TrainingRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class SimulationService {
    @Autowired
    private DetectionRepository detectionsRepository;

    @Autowired
    private AttackSimulationRepository attackSimulationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private TrainingRunRepository trainingRunRepository;
    @Autowired
    private RestTemplate restTemplate;

    private final Random rnd = new Random();

    // ============================================================
    // DATOS DE SOPORTE — variedad realista
    // ============================================================

    private static final String[] USERNAMES = {
            "jperez", "mgarcia", "alopez", "crodriguez", "lmartinez",
            "rflores", "dchavez", "sramirez", "ktorres", "ncastillo"
    };
    private static final String[] ROLES = {
            "analyst", "developer", "admin", "manager", "support",
            "auditor", "ops_engineer", "data_scientist", "sysadmin", "intern"
    };
    private static final String[] DEPARTMENTS = {
            "IT", "Finance", "HR", "Operations", "Security",
            "DevOps", "Data", "Legal", "Marketing", "Infrastructure"
    };
    private static final String[] OS_LIST = {
            "Windows 10", "Windows 11", "Ubuntu 22.04", "macOS Ventura",
            "Debian 11", "CentOS 8", "RHEL 9", "Windows Server 2019"
    };
    private static final String[] APPLICATIONS = {
            "VPN Client", "RDP", "SSH Terminal", "Browser", "ERP System",
            "Mail Client", "Database Tool", "Remote Desktop", "FileZilla", "PuTTY"
    };
    private static final String[] WORKSTATION_IDS = {
            "WS-001", "WS-023", "WS-047", "WS-089", "WS-102",
            "WS-155", "WS-200", "WS-211", "WS-234", "WS-300"
    };
    private static final String[] MODEL_VERSIONS = {
            "autoencoder-v2.1", "autoencoder-v2.3", "autoencoder-v3.0"
    };

    // IPs externas con contexto real
    private static final String[][] IPS_EXTERNAS = {
            {"45.33.32.156",   "RU"}, {"185.220.101.45", "RU"},
            {"103.21.244.10",  "CN"}, {"196.207.40.15",  "NG"},
            {"177.54.144.20",  "BR"}, {"59.188.12.100",  "KP"},
            {"91.108.4.0",     "DE"}, {"5.188.206.14",   "UA"},
            {"194.165.16.10",  "IR"}, {"202.12.27.33",   "CN"}
    };

    // ============================================================
    // HELPERS
    // ============================================================

    private String rndUsername()      { return USERNAMES[rnd.nextInt(USERNAMES.length)]; }
    private String rndRole()          { return ROLES[rnd.nextInt(ROLES.length)]; }
    private String rndDept()          { return DEPARTMENTS[rnd.nextInt(DEPARTMENTS.length)]; }
    private String rndOs()            { return OS_LIST[rnd.nextInt(OS_LIST.length)]; }
    private String rndApp()          { return APPLICATIONS[rnd.nextInt(APPLICATIONS.length)]; }
    private String rndWorkstation()   { return WORKSTATION_IDS[rnd.nextInt(WORKSTATION_IDS.length)]; }
    private String rndModelVersion()  { return MODEL_VERSIONS[rnd.nextInt(MODEL_VERSIONS.length)]; }

    /** Genera una IP interna privada 192.168.x.x */
    private String ipInterna() {
        return "192.168." + rnd.nextInt(5) + "." + (rnd.nextInt(253) + 1);
    }

    /** Genera IP externa con país de origen */
    private String[] ipExternaConPais() {
        return IPS_EXTERNAS[rnd.nextInt(IPS_EXTERNAS.length)];
    }

    /** turno según hora */
    private String turno(int hora) {
        if (hora >= 6  && hora < 14) return "mañana";
        if (hora >= 14 && hora < 22) return "tarde";
        return "noche";
    }

    /** Día de la semana texto */
    private String diaSemana(LocalDateTime dt) {
        return dt.getDayOfWeek().name();
    }

    /** Es fin de semana */
    private boolean esFinDeSemana(LocalDateTime dt) {
        DayOfWeek d = dt.getDayOfWeek();
        return d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY;
    }

    /** Rango float inclusivo */
    private float rf(float min, float max) {
        return min + rnd.nextFloat() * (max - min);
    }

    /** Rango int inclusivo */
    private int ri(int min, int max) {
        return min + rnd.nextInt(max - min + 1);
    }

    // ============================================================
    // PHISHING
    // Hora madrugada, auth por password, bytes bajos, usuario real
    // ============================================================
    public List<Detections> simulatePhishingAttack(int targetCount, String attackType) {
        List<Detections> results = new ArrayList<>();

        for (int i = 0; i < targetCount; i++) {
            String url = "http://fastapi-ml-39cx.onrender.com/simulate/PHISHING?fase=reconocimiento";
            MlResponse mlResp = restTemplate.postForObject(url, null, MlResponse.class);
            // Hora nocturna: entre 1:00 y 5:00 AM
            int horaAtaque = ri(1, 5);
            LocalDateTime ts = LocalDateTime.now()
                    .withHour(horaAtaque)
                    .withMinute(rnd.nextInt(60))
                    .minusDays(rnd.nextInt(3));

            String username = rndUsername();
            String[] ipPais = ipExternaConPais();

            Detections d = new Detections();
            // Identidad
            d.setUserId(UUID.randomUUID().toString());
            d.setUsername(username);
            d.setRole(rndRole());
            d.setDepartment(rndDept());

            // Red
            d.setSourceIp(ipPais[0]);
            d.setDestinationIp(ipInterna());
            d.setDestinationPort(443);
            d.setProtocol("HTTPS");
            d.setConnectionType("WEB");

            // Amenaza
            d.setThreatType("Phishing");
            d.setIsAnomaly(true);
            d.setSeverity(AlertSeverity.valueOf(rnd.nextFloat() > 0.3f ? "high" : "critical"));

            // Auth
            d.setAuthenticationMethod(rnd.nextFloat() > 0.5f ? "password" : "sso");
            d.setAuthenticationStatus("success"); // robó credenciales
            d.setFailedLoginAttempts(ri(0, 2));

            // Sesión
            d.setSessionDurationSec(ri(60, 600));

            // Tiempo
            d.setTimestamp(ts);
            d.setShift(turno(horaAtaque));
            d.setIsWeekend(esFinDeSemana(ts));
            d.setDayOfWeek(diaSemana(ts));

            // Modelo
            d.setReconstructionError(rf(0.35f, 0.60f));
            d.setConfidence(rf(0.78f, 0.95f));
            d.setThresholdUsed(0.30F);
            d.setModelVersion(rndModelVersion());

            // Evento
            d.setEventType("LOGIN");
            d.setEventCategory("AUTH_ANOMALY");
            d.setInvestigationStatus("pending");

            // Dispositivo
            d.setWorkstationId(rndWorkstation());
            d.setOs(rndOs());
            d.setApplication(rnd.nextFloat() > 0.5f ? "Browser" : "Mail Client");

            // Ubicación
            d.setPhysicalLocation(ipPais[1]);

            // Notas contextuales variadas
            String[] notasPhishing = {
                    "Correo de phishing recibido desde dominio lookalike: paypa1.com",
                    "Clic en enlace malicioso en email corporativo falso",
                    "Credenciales capturadas via formulario HTML clonado",
                    "Acceso desde IP reportada en lista negra de phishing",
                    "Usuario reportó email sospechoso 30 min después del acceso"
            };
            d.setNotes(notasPhishing[rnd.nextInt(notasPhishing.length)]);
            // 🔗 2. SOBREESCRIBIR con los valores reales del modelo ML
            if (mlResp != null) {
                d.setThreatType(mlResp.getAttack_type());
                d.setIsAnomaly(mlResp.is_anomaly());
                d.setReconstructionError((float) mlResp.getReconstruction_error());
                d.setConfidence((float) mlResp.getConfidence());
                d.setThresholdUsed((float) mlResp.getThreshold());
            }

            results.add(detectionsRepository.save(d));
        }

        guardarResumenSimulacion("Phishing", targetCount, results);
        return results;
    }

    // ============================================================
    // ACCESO NO AUTORIZADO (VPN extranjera / GEO_ANOMALY)
    // ============================================================
    public List<Detections> simulateUnauthorizedAccess(int targetCount) {
        List<Detections> results = new ArrayList<>();

        for (int i = 0; i < targetCount; i++) {
            // 🔗 1. LLAMAR A FASTAPI
            String url = "http://fastapi-ml-39cx.onrender.com/simulate/UNAUTHORIZED_ACCESS?fase=reconocimiento";
            MlResponse mlResp = restTemplate.postForObject(url, null, MlResponse.class);
            String[] ipPais = ipExternaConPais();
            int hora = ri(0, 23);
            LocalDateTime ts = LocalDateTime.now()
                    .withHour(hora)
                    .withMinute(rnd.nextInt(60))
                    .minusHours(rnd.nextInt(48));

            String username = rndUsername();

            Detections d = new Detections();
            // Identidad
            d.setUserId(UUID.randomUUID().toString());
            d.setUsername(username);
            d.setRole(rndRole());
            d.setDepartment(rndDept());

            // Red
            d.setSourceIp(ipPais[0]);
            d.setDestinationIp("10.0." + ri(0, 5) + "." + ri(1, 50));
            d.setDestinationPort(rnd.nextFloat() > 0.5f ? 22 : 3389);  // SSH o RDP
            d.setProtocol(rnd.nextFloat() > 0.5f ? "TCP" : "UDP");
            d.setConnectionType("VPN_EXTERNAL");

            // Amenaza
            d.setThreatType("VPN_Unauthorized");
            d.setIsAnomaly(true);
            AlertSeverity[] values = AlertSeverity.values();
           d.setSeverity(values[rnd.nextInt(values.length)]);

            // Auth
            d.setAuthenticationMethod(rnd.nextFloat() > 0.6f ? "password" : "certificate");
            d.setAuthenticationStatus(rnd.nextFloat() > 0.3f ? "success" : "failed");
            d.setFailedLoginAttempts(ri(0, 3));

            // Sesión
            d.setSessionDurationSec(ri(30, 900));

            // Tiempo
            d.setTimestamp(ts);
            d.setShift(turno(hora));
            d.setIsWeekend(esFinDeSemana(ts));
            d.setDayOfWeek(diaSemana(ts));

            // Modelo
            d.setReconstructionError(rf(0.55f, 0.85f));
            d.setConfidence(rf(0.88f, 0.99f));
            d.setThresholdUsed(0.30F);
            d.setModelVersion(rndModelVersion());

            // Evento
            d.setEventType("REMOTE_ACCESS");
            d.setEventCategory("GEO_ANOMALY");
            d.setInvestigationStatus("pending");

            // Dispositivo
            d.setWorkstationId(rndWorkstation());
            d.setOs(rndOs());
            d.setApplication(rnd.nextFloat() > 0.5f ? "VPN Client" : "SSH Terminal");

            // Ubicación geográfica
            d.setPhysicalLocation(ipPais[1]);

            String[] notas = {
                    "Acceso desde país no registrado en política de seguridad",
                    "IP origen en lista negra de Tor exit nodes",
                    "Distancia geográfica: +" + ri(500, 12000) + " km del último acceso conocido",
                    "Proveedor de VPN comercial detectado en la IP de origen",
                    "Acceso concurrente detectado desde dos países distintos"
            };
            d.setNotes(notas[rnd.nextInt(notas.length)]);

            // 🔗 2. SOBREESCRIBIR con los valores reales del modelo ML
            if (mlResp != null) {
                d.setThreatType(mlResp.getAttack_type());
                d.setIsAnomaly(mlResp.is_anomaly());
                d.setReconstructionError((float) mlResp.getReconstruction_error());
                d.setConfidence((float) mlResp.getConfidence());
                d.setThresholdUsed((float) mlResp.getThreshold());
            }
            results.add(detectionsRepository.save(d));
        }

        guardarResumenSimulacion("VPN_Unauthorized", targetCount, results);
        return results;
    }

    // ============================================================
    // BRUTE FORCE
    // Muchos intentos fallidos, incremento progresivo
    // ============================================================
    public List<Detections> simulateBruteForceAttack(int attempts) {
        List<Detections> results = new ArrayList<>();
        String sourceIp = ipExternaConPais()[0]; // misma IP para todo el ataque
        String targetIp = ipInterna();
        int horaInicio = ri(0, 23);
        LocalDateTime baseTs = LocalDateTime.now().withHour(horaInicio).withMinute(0);

        String[] targetUsers = { rndUsername(), rndUsername(), rndUsername() };

        for (int i = 0; i < attempts; i++) {
            String url = "http://fastapi-ml-39cx.onrender.com/simulate/BRUTE_FORCE";
            MlResponse mlResp = restTemplate.postForObject(url, null, MlResponse.class);
            boolean detectado = i > attempts * 0.8;
            LocalDateTime ts = baseTs.plusSeconds((long) i * ri(1, 5));
            String username = targetUsers[i % targetUsers.length];

            Detections d = new Detections();
            // Identidad (usuario objetivo)
            d.setUserId(null); // no autenticado todavía
            d.setUsername(username);
            d.setRole("unknown");
            d.setDepartment("unknown");

            // Red
            d.setSourceIp(sourceIp);
            d.setDestinationIp(targetIp);
            d.setDestinationPort(rnd.nextFloat() > 0.5f ? 8080 : 443);
            d.setProtocol("HTTP");
            d.setConnectionType("WEB");

            // Amenaza
            d.setThreatType("BruteForce");
            d.setIsAnomaly(detectado);
            AlertSeverity[] values = AlertSeverity.values();
            d.setSeverity(values[rnd.nextInt(values.length)]);

            // Auth
            d.setAuthenticationMethod("password");
            d.setAuthenticationStatus("failed");
            d.setFailedLoginAttempts(i + 1);

            // Sesión
            d.setSessionDurationSec(ri(1, 4));

            // Tiempo
            d.setTimestamp(ts);
            d.setShift(turno(horaInicio));
            d.setIsWeekend(esFinDeSemana(ts));
            d.setDayOfWeek(diaSemana(ts));

            // Modelo — el error sube con cada intento
            float baseError = 0.15f + (i * 0.008f);
            d.setReconstructionError(Math.min(baseError + rf(0f, 0.05f), 0.95f));
            d.setConfidence(Math.min(0.45f + (i * 0.007f), 0.99f));
            d.setThresholdUsed(0.30F);
            d.setModelVersion(rndModelVersion());

            // Evento
            d.setEventType("LOGIN_ATTEMPT");
            d.setEventCategory("BRUTE_FORCE");
            d.setInvestigationStatus("pending");

            // Dispositivo
            d.setWorkstationId(null); // no identificado
            d.setOs("unknown");
            d.setApplication("custom_script");

            // Ubicación
            d.setPhysicalLocation("UNKNOWN");

            d.setNotes("Intento #" + (i + 1) + " — velocidad: " + ri(100, 500) + " req/min");
            // 🔗 2. SOBREESCRIBIR con los valores reales del modelo ML
            if (mlResp != null) {
                d.setThreatType(mlResp.getAttack_type());
                d.setIsAnomaly(mlResp.is_anomaly());
                d.setReconstructionError((float) mlResp.getReconstruction_error());
                d.setConfidence((float) mlResp.getConfidence());
                d.setThresholdUsed((float) mlResp.getThreshold());
            }

            results.add(detectionsRepository.save(d));
        }

        guardarResumenSimulacion("BruteForce", attempts, results);
        return results;
    }

    // ============================================================
    // MALWARE — conexión persistente, CPU alto, data exfiltration
    // ============================================================
    public List<Detections> simulateMalwareDetection(String malwareType) {
        List<Detections> results = new ArrayList<>();
        int samples = 15;

        String infectedIp = ipInterna();
        String c2Ip = ipExternaConPais()[0];
        String infectedUser = rndUsername();
        String infectedWs = rndWorkstation();

        for (int i = 0; i < samples; i++) {
            String url = "http://fastapi-ml-39cx.onrender.com/simulate/MALWARE";
            MlResponse mlResp = restTemplate.postForObject(url, null, MlResponse.class);
            int hora = ri(0, 23);
            LocalDateTime ts = LocalDateTime.now().minusMinutes((long) i * ri(3, 10));

            Detections d = new Detections();
            // Identidad — misma máquina infectada
            d.setUserId(UUID.randomUUID().toString());
            d.setUsername(infectedUser);
            d.setRole(rndRole());
            d.setDepartment(rndDept());

            // Red
            d.setSourceIp(infectedIp);
            d.setDestinationIp(c2Ip);
            d.setDestinationPort(rnd.nextFloat() > 0.5f ? 443 : 80);
            d.setProtocol(rnd.nextFloat() > 0.5f ? "TCP" : "HTTPS");
            d.setConnectionType("PERSISTENT");

            // Amenaza
            d.setThreatType("Malware_" + malwareType);
            d.setIsAnomaly(true);
            AlertSeverity[] values = AlertSeverity.values();
            d.setSeverity(values[rnd.nextInt(values.length)]);

            // Auth
            d.setAuthenticationMethod("token");
            d.setAuthenticationStatus("success");
            d.setFailedLoginAttempts(0);

            // Sesión larga (C2 beacon)
            d.setSessionDurationSec(ri(10000, 86400));

            // Tiempo
            d.setTimestamp(ts);
            d.setShift(turno(hora));
            d.setIsWeekend(esFinDeSemana(ts));
            d.setDayOfWeek(diaSemana(ts));

            // Modelo
            d.setReconstructionError(rf(0.45f, 0.75f));
            d.setConfidence(rf(0.85f, 0.98f));
            d.setThresholdUsed(0.30F);
            d.setModelVersion(rndModelVersion());

            // Evento
            d.setEventType("DATA_EXFILTRATION");
            d.setEventCategory("MALWARE");
            d.setInvestigationStatus("pending");

            // Dispositivo
            d.setWorkstationId(infectedWs);
            d.setOs(rndOs());
            d.setApplication("svchost.exe");  // proceso sospechoso

            // Ubicación
            d.setPhysicalLocation("PE"); // equipo en Perú infectado

            String[] notasMalware = {
                    "CPU: " + ri(80, 99) + "%, RAM: " + ri(75, 95) + "% — proceso svchost anómalo",
                    "Escrituras disco masivas: " + ri(500, 5000) + " MB/s — posible cifrado",
                    "Beacon C2 cada " + ri(30, 300) + " segundos hacia IP en lista negra",
                    "Exfiltración estimada: " + ri(100, 2000) + " MB datos sensibles",
                    "Conexión saliente en puerto no habitual hacia servidor externo"
            };
            d.setNotes(notasMalware[rnd.nextInt(notasMalware.length)]);
            // 🔗 2. SOBREESCRIBIR con los valores reales del modelo ML
            if (mlResp != null) {
                d.setThreatType(mlResp.getAttack_type());
                d.setIsAnomaly(mlResp.is_anomaly());
                d.setReconstructionError((float) mlResp.getReconstruction_error());
                d.setConfidence((float) mlResp.getConfidence());
                d.setThresholdUsed((float) mlResp.getThreshold());
            }

            results.add(detectionsRepository.save(d));
        }

        guardarResumenSimulacion("Malware_" + malwareType, samples, results);
        return results;
    }

    // ============================================================
    // RANSOMWARE — dos fases (reconocimiento y cifrado)
    // ============================================================
    public List<Detections> simulateRansomwareAttack(String fase) {
        List<Detections> results = new ArrayList<>();
        String infectedUser = rndUsername();
        String infectedWs = rndWorkstation();

        if ("reconocimiento".equals(fase)) {
            int samples = 20;
            for (int i = 0; i < samples; i++) {
                String url = "http://fastapi-ml-39cx.onrender.com/simulate/RANSOMWARE?fase=" + fase;
                MlResponse mlResp = restTemplate.postForObject(url, null, MlResponse.class);
                int hora = ri(2, 6); // madrugada sigilosa
                LocalDateTime ts = LocalDateTime.now().minusMinutes(samples - i);

                Detections d = new Detections();
                d.setUserId(UUID.randomUUID().toString());
                d.setUsername(infectedUser);
                d.setRole(rndRole());
                d.setDepartment(rndDept());

                d.setSourceIp(ipExternaConPais()[0]);
                d.setDestinationIp(ipInterna());
                d.setDestinationPort(ri(1, 65535));
                d.setProtocol("TCP");
                d.setConnectionType("PORT_SCAN");

                d.setThreatType("Ransomware_Reconocimiento");
                d.setIsAnomaly(true);
                AlertSeverity[] values = AlertSeverity.values();
                d.setSeverity(values[rnd.nextInt(values.length)]);

                d.setAuthenticationMethod("none");
                d.setAuthenticationStatus("failed");
                d.setFailedLoginAttempts(ri(0, 1));
                d.setSessionDurationSec(ri(1, 3));

                d.setTimestamp(ts);
                d.setShift(turno(hora));
                d.setIsWeekend(esFinDeSemana(ts));
                d.setDayOfWeek(diaSemana(ts));

                d.setReconstructionError(rf(0.25f, 0.40f));
                d.setConfidence(rf(0.65f, 0.80f));
                d.setThresholdUsed(0.30F);
                d.setModelVersion(rndModelVersion());

                d.setEventType("PORT_SCAN");
                d.setEventCategory("RANSOMWARE");
                d.setInvestigationStatus("pending");

                d.setWorkstationId(infectedWs);
                d.setOs(rndOs());
                d.setApplication("nmap / scanner");

                d.setPhysicalLocation("PE");
                d.setNotes("Escaneo de puertos: " + ri(100, 10000) + " puertos en " + ri(5, 60) + " seg");
                if (mlResp != null) {
                    d.setThreatType(mlResp.getAttack_type());
                    d.setIsAnomaly(mlResp.is_anomaly());
                    d.setReconstructionError((float) mlResp.getReconstruction_error());
                    d.setConfidence((float) mlResp.getConfidence());
                    d.setThresholdUsed((float) mlResp.getThreshold());
                }

                results.add(detectionsRepository.save(d));
            }

        } else { // cifrado
            int samples = 10;
            for (int i = 0; i < samples; i++) {
                String url = "http://fastapi-ml-39cx.onrender.com/simulate/RANSOMWARE?fase=" + fase;
                MlResponse mlResp = restTemplate.postForObject(url, null, MlResponse.class);
                int hora = ri(0, 4);
                LocalDateTime ts = LocalDateTime.now().minusSeconds((long) i * 30);
                String[] ipPais = ipExternaConPais();

                Detections d = new Detections();
                d.setUserId(UUID.randomUUID().toString());
                d.setUsername(infectedUser);
                d.setRole(rndRole());
                d.setDepartment(rndDept());

                d.setSourceIp(ipInterna());
                d.setDestinationIp(ipPais[0]); // C2 externo
                d.setDestinationPort(443);
                d.setProtocol("HTTPS");
                d.setConnectionType("C2_CHANNEL");

                d.setThreatType("Ransomware_Cifrado");
                d.setIsAnomaly(true);
                d.setSeverity(AlertSeverity.valueOf("critical"));

                d.setAuthenticationMethod("certificate");
                d.setAuthenticationStatus("success");
                d.setFailedLoginAttempts(0);
                d.setSessionDurationSec(ri(20000, 86400));

                d.setTimestamp(ts);
                d.setShift(turno(hora));
                d.setIsWeekend(esFinDeSemana(ts));
                d.setDayOfWeek(diaSemana(ts));

                d.setReconstructionError(rf(0.72f, 0.95f));
                d.setConfidence(rf(0.94f, 0.99f));
                d.setThresholdUsed(0.30F);
                d.setModelVersion(rndModelVersion());

                d.setEventType("DATA_ENCRYPTION");
                d.setEventCategory("RANSOMWARE");
                d.setInvestigationStatus("pending");

                d.setWorkstationId(infectedWs);
                d.setOs(rndOs());
                d.setApplication("explorer.exe / ransomware dropper");

                d.setPhysicalLocation("PE");
                d.setNotes("CPU ~" + ri(90, 99) + "%, escrituras disco: " + ri(1000, 9000)
                        + " MB/s, exfiltración previa estimada: " + ri(200, 5000) + " MB");
                if (mlResp != null) {
                    d.setThreatType(mlResp.getAttack_type());
                    d.setIsAnomaly(mlResp.is_anomaly());
                    d.setReconstructionError((float) mlResp.getReconstruction_error());
                    d.setConfidence((float) mlResp.getConfidence());
                    d.setThresholdUsed((float) mlResp.getThreshold());
                }

                results.add(detectionsRepository.save(d));
            }
        }

        guardarResumenSimulacion("Ransomware_" + fase, results.size(), results);
        return results;
    }


    // ============================================================
    // Guarda resumen en attack_simulations
    // ============================================================
    private void guardarResumenSimulacion(String attackType,
                                          int totalSamples,
                                          List<Detections> results) {
        long detectados = results.stream()
                .filter(Detections::getIsAnomaly).count();

        double avgError = results.stream()
                .mapToDouble(d -> d.getReconstructionError() != null
                        ? d.getReconstructionError() : 0.0)
                .average().orElse(0.0);

        double minError = results.stream()
                .mapToDouble(d -> d.getReconstructionError() != null
                        ? d.getReconstructionError() : 0.0)
                .min().orElse(0.0);

        double maxError = results.stream()
                .mapToDouble(d -> d.getReconstructionError() != null
                        ? d.getReconstructionError() : 0.0)
                .max().orElse(0.0);

        // Desviación estándar
        double stdError = 0.0;
        if (!results.isEmpty()) {
            double sum2 = results.stream()
                    .mapToDouble(d -> {
                        double e = d.getReconstructionError() != null ? d.getReconstructionError() : 0.0;
                        return Math.pow(e - avgError, 2);
                    }).sum();
            stdError = Math.sqrt(sum2 / results.size());
        }

        double precision = detectados > 0 ? (double) detectados / totalSamples : 0.0;
        double recall    = precision; // simplificado (sin FP externos en esta simulación)
        double f1        = (precision + recall) > 0
                ? 2 * precision * recall / (precision + recall) : 0.0;

        AttackSimulation sim = new AttackSimulation();
        TrainingRuns trainingRun = trainingRunRepository
                .findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("No existe TrainingRun"));

        sim.setTrainingRun(trainingRun);

        sim.setTrainingRun(trainingRun);
        sim.setAttackType(attackType);
        sim.setTotalSamples(totalSamples);
        sim.setDetectedCorrectly((int) detectados);
        sim.setFalseNegatives((int)(totalSamples - detectados));
        sim.setFalsePositives(0);
        sim.setDetectionRate((double) detectados / totalSamples);
        sim.setPrecision(precision);
        sim.setRecall(recall);
        sim.setF1Score(f1);
        sim.setAvgReconstructionError(avgError);
        sim.setMinReconstructionError(minError);
        sim.setMaxReconstructionError(maxError);
        sim.setStdReconstructionError(stdError);
        sim.setCreatedAt(LocalDateTime.now());
        attackSimulationRepository.save(sim);
    }
    public SimulationHistoryResponse getSimulationHistory(String attackType) {
        List<AttackSimulation> simulations = (attackType != null && !attackType.isBlank())
                ? attackSimulationRepository.findByAttackTypeOrderByCreatedAtDesc(attackType)
                : attackSimulationRepository.findAllByOrderByCreatedAtDesc();

        List<SimulationHistoryResponse.SimulationDTO> dtos = simulations.stream()
                .map(s -> SimulationHistoryResponse.SimulationDTO.builder()
                        .id(s.getId())
                        .trainingRunId(
                                s.getTrainingRun() != null
                                        ? s.getTrainingRun().getId()
                                        : null
                        )
                        .attackType(s.getAttackType())
                        .totalSamples(s.getTotalSamples())
                        .detectedCorrectly(s.getDetectedCorrectly())
                        .falseNegatives(s.getFalseNegatives())
                        .falsePositives(s.getFalsePositives())
                        .detectionRate(s.getDetectionRate())
                        .precision(s.getPrecision())
                        .recall(s.getRecall())
                        .f1Score(s.getF1Score())
                        .avgReconstructionError(s.getAvgReconstructionError())
                        .minReconstructionError(s.getMinReconstructionError())
                        .maxReconstructionError(s.getMaxReconstructionError())
                        .stdReconstructionError(s.getStdReconstructionError())
                        .createdAt(s.getCreatedAt())
                        .build())
                .toList();

        return SimulationHistoryResponse.builder()
                .totalSimulations(dtos.size())
                .simulations(dtos)
                .build();
    }
}
