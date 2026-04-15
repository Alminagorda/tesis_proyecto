package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    @Autowired
    private ReportService reportService;

    // Reporte semanal PDF
    @GetMapping("/weekly")
    public ResponseEntity<byte[]> getWeeklyReport() throws Exception {
        byte[] pdf = reportService.generateWeeklyReport();
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=reporte_semanal.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // Reporte mensual PDF
    @GetMapping("/monthly")
    public ResponseEntity<byte[]> getMonthlyReport() throws Exception {
        byte[] pdf = reportService.generateMonthlyReport();
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=reporte_mensual.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // Detecciones CSV
    @GetMapping("/detections/csv")
    public ResponseEntity<byte[]> getDetectionsCsv() throws Exception {
        byte[] csv = reportService.generateDetectionsCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=detecciones.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    // Épocas de entrenamiento CSV
    @GetMapping("/training/csv")
    public ResponseEntity<byte[]> getTrainingCsv() throws Exception {
        byte[] csv = reportService.generateTrainingCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=entrenamiento.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    // Métricas JSON
    @GetMapping("/metrics/json")
    public ResponseEntity<byte[]> getMetricsJson() throws Exception {
        byte[] json = reportService.generateMetricsJson();
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=metricas_modelo.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }

}
