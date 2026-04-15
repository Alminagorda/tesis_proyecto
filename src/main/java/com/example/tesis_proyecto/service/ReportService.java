package com.example.tesis_proyecto.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Service
public class ReportService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // ----------------------------------------------------------
    // REPORTE SEMANAL PDF
    // ----------------------------------------------------------
    public byte[] generateWeeklyReport() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, out);
        doc.open();

        // Título
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        Paragraph title = new Paragraph(
                "Reporte de Seguridad Semanal", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        doc.add(new Paragraph("Sistema de Detección de Anomalías — Mina Antamina",
                normalFont));
        doc.add(new Paragraph("Generado: " + LocalDateTime.now(), normalFont));
        doc.add(new Paragraph(" "));

        // Resumen de alertas de la semana
        doc.add(new Paragraph("Resumen de Alertas (últimos 7 días)", headerFont));
        doc.add(new Paragraph(" "));

        List<Map<String, Object>> alertas = jdbcTemplate.queryForList("""
            SELECT severity, COUNT(*) as total,
                   COUNT(*) FILTER (WHERE status = 'resolved') as resueltas
            FROM alerts
            WHERE detected_at >= NOW() - INTERVAL '7 days'
            GROUP BY severity
            ORDER BY total DESC
            """);

        PdfPTable tablaAlertas = new PdfPTable(3);
        tablaAlertas.setWidthPercentage(100);
        tablaAlertas.addCell(new PdfPCell(new Phrase("Severidad", headerFont)));
        tablaAlertas.addCell(new PdfPCell(new Phrase("Total",     headerFont)));
        tablaAlertas.addCell(new PdfPCell(new Phrase("Resueltas", headerFont)));

        for (Map<String, Object> row : alertas) {
            tablaAlertas.addCell(String.valueOf(row.get("severity")));
            tablaAlertas.addCell(String.valueOf(row.get("total")));
            tablaAlertas.addCell(String.valueOf(row.get("resueltas")));
        }
        doc.add(tablaAlertas);
        doc.add(new Paragraph(" "));

        // Detecciones de la semana
        doc.add(new Paragraph("Detecciones por Tipo de Ataque", headerFont));
        doc.add(new Paragraph(" "));

        List<Map<String, Object>> detecciones = jdbcTemplate.queryForList("""
            SELECT threat_type,
                   COUNT(*) as total,
                   ROUND(AVG(reconstruction_error)::numeric, 4) as avg_error
            FROM detections
            WHERE timestamp >= NOW() - INTERVAL '7 days'
              AND is_anomaly = true
            GROUP BY threat_type
            ORDER BY total DESC
            """);

        PdfPTable tablaDetecciones = new PdfPTable(3);
        tablaDetecciones.setWidthPercentage(100);
        tablaDetecciones.addCell(new PdfPCell(
                new Phrase("Tipo de Ataque",  headerFont)));
        tablaDetecciones.addCell(new PdfPCell(
                new Phrase("Total",           headerFont)));
        tablaDetecciones.addCell(new PdfPCell(
                new Phrase("Error Promedio",  headerFont)));

        for (Map<String, Object> row : detecciones) {
            tablaDetecciones.addCell(String.valueOf(row.get("threat_type")));
            tablaDetecciones.addCell(String.valueOf(row.get("total")));
            tablaDetecciones.addCell(String.valueOf(row.get("avg_error")));
        }
        doc.add(tablaDetecciones);
        doc.add(new Paragraph(" "));

        // Métricas del modelo
        doc.add(new Paragraph("Rendimiento del Modelo", headerFont));
        doc.add(new Paragraph(" "));

        Map<String, Object> metricas = jdbcTemplate.queryForMap("""
            SELECT accuracy, precision, recall, f1_score, auc_roc
            FROM model_metrics
            ORDER BY created_at DESC
            LIMIT 1
            """);

        PdfPTable tablaMetricas = new PdfPTable(2);
        tablaMetricas.setWidthPercentage(60);
        for (Map.Entry<String, Object> entry : metricas.entrySet()) {
            tablaMetricas.addCell(
                    new PdfPCell(new Phrase(entry.getKey(), headerFont)));
            tablaMetricas.addCell(String.valueOf(entry.getValue()));
        }
        doc.add(tablaMetricas);

        doc.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------
    // REPORTE MENSUAL PDF
    // ----------------------------------------------------------
    public byte[] generateMonthlyReport() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 40, 40, 60, 40);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Font titleFont  = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

        Paragraph title = new Paragraph(
                "Reporte de Seguridad Mensual", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(new Paragraph(
                "Sistema de Detección de Anomalías — Mina Antamina", normalFont));
        doc.add(new Paragraph("Generado: " + LocalDateTime.now(), normalFont));
        doc.add(new Paragraph(" "));

        // Tendencia mensual desde daily_reports
        doc.add(new Paragraph("Tendencia Mensual de Anomalías", headerFont));
        doc.add(new Paragraph(" "));

        List<Map<String, Object>> tendencia = jdbcTemplate.queryForList("""
            SELECT report_date,
                   total_predictions,
                   total_anomalies_detected,
                   ROUND(anomaly_rate::numeric, 4) as anomaly_rate
            FROM daily_reports
            WHERE report_date >= NOW() - INTERVAL '30 days'
            ORDER BY report_date ASC
            """);

        PdfPTable tablaTendencia = new PdfPTable(4);
        tablaTendencia.setWidthPercentage(100);
        tablaTendencia.addCell(new PdfPCell(new Phrase("Fecha",       headerFont)));
        tablaTendencia.addCell(new PdfPCell(new Phrase("Predicciones",headerFont)));
        tablaTendencia.addCell(new PdfPCell(new Phrase("Anomalías",   headerFont)));
        tablaTendencia.addCell(new PdfPCell(new Phrase("Tasa",        headerFont)));

        for (Map<String, Object> row : tendencia) {
            tablaTendencia.addCell(String.valueOf(row.get("report_date")));
            tablaTendencia.addCell(String.valueOf(row.get("total_predictions")));
            tablaTendencia.addCell(String.valueOf(row.get("total_anomalies_detected")));
            tablaTendencia.addCell(String.valueOf(row.get("anomaly_rate")));
        }
        doc.add(tablaTendencia);
        doc.add(new Paragraph(" "));

        // Simulaciones del mes
        doc.add(new Paragraph("Simulaciones de Ataques del Mes", headerFont));
        doc.add(new Paragraph(" "));

        List<Map<String, Object>> sims = jdbcTemplate.queryForList("""
            SELECT attack_type,
                   SUM(total_samples)      as total,
                   ROUND(AVG(detection_rate)::numeric, 4) as avg_detection,
                   ROUND(AVG(f1_score)::numeric, 4)       as avg_f1
            FROM attack_simulations
            WHERE created_at >= NOW() - INTERVAL '30 days'
            GROUP BY attack_type
            ORDER BY avg_detection DESC
            """);

        PdfPTable tablaSims = new PdfPTable(4);
        tablaSims.setWidthPercentage(100);
        tablaSims.addCell(new PdfPCell(new Phrase("Ataque",     headerFont)));
        tablaSims.addCell(new PdfPCell(new Phrase("Muestras",   headerFont)));
        tablaSims.addCell(new PdfPCell(new Phrase("Detección",  headerFont)));
        tablaSims.addCell(new PdfPCell(new Phrase("F1 Score",   headerFont)));

        for (Map<String, Object> row : sims) {
            tablaSims.addCell(String.valueOf(row.get("attack_type")));
            tablaSims.addCell(String.valueOf(row.get("total")));
            tablaSims.addCell(String.valueOf(row.get("avg_detection")));
            tablaSims.addCell(String.valueOf(row.get("avg_f1")));
        }
        doc.add(tablaSims);

        doc.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------
    // DETECCIONES CSV
    // ----------------------------------------------------------
    public byte[] generateDetectionsCsv() throws Exception {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
            SELECT timestamp, username, role, threat_type, severity,
                   is_anomaly, reconstruction_error, confidence,
                   source_ip, protocol, shift, investigation_status
            FROM detections
            ORDER BY timestamp DESC
            """);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out);
        CSVWriter csv = new CSVWriter(writer);

        // Cabecera
        csv.writeNext(new String[]{
                "timestamp","username","role","threat_type","severity",
                "is_anomaly","reconstruction_error","confidence",
                "source_ip","protocol","shift","investigation_status"
        });

        // Filas
        for (Map<String, Object> row : rows) {
            csv.writeNext(new String[]{
                    String.valueOf(row.get("timestamp")),
                    String.valueOf(row.get("username")),
                    String.valueOf(row.get("role")),
                    String.valueOf(row.get("threat_type")),
                    String.valueOf(row.get("severity")),
                    String.valueOf(row.get("is_anomaly")),
                    String.valueOf(row.get("reconstruction_error")),
                    String.valueOf(row.get("confidence")),
                    String.valueOf(row.get("source_ip")),
                    String.valueOf(row.get("protocol")),
                    String.valueOf(row.get("shift")),
                    String.valueOf(row.get("investigation_status"))
            });
        }
        csv.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------
    // ÉPOCAS DE ENTRENAMIENTO CSV
    // ----------------------------------------------------------
    public byte[] generateTrainingCsv() throws Exception {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("""
            SELECT e.epoch, e.loss, e.val_loss, e.learning_rate,
                   e.created_at, t.model_name, t.threshold
            FROM epoch_history e
            JOIN training_runs t ON e.training_run_id = t.id
            ORDER BY e.created_at DESC, e.epoch ASC
            """);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVWriter csv = new CSVWriter(new OutputStreamWriter(out));

        csv.writeNext(new String[]{
                "epoch","loss","val_loss","learning_rate",
                "created_at","model_name","threshold"
        });

        for (Map<String, Object> row : rows) {
            csv.writeNext(new String[]{
                    String.valueOf(row.get("epoch")),
                    String.valueOf(row.get("loss")),
                    String.valueOf(row.get("val_loss")),
                    String.valueOf(row.get("learning_rate")),
                    String.valueOf(row.get("created_at")),
                    String.valueOf(row.get("model_name")),
                    String.valueOf(row.get("threshold"))
            });
        }
        csv.close();
        return out.toByteArray();
    }

    // ----------------------------------------------------------
    // MÉTRICAS JSON
    // ----------------------------------------------------------
    public byte[] generateMetricsJson() throws Exception {
        Map<String, Object> metricas = jdbcTemplate.queryForMap("""
            SELECT model_version, accuracy, precision, recall,
                   f1_score, auc_roc, false_positive_rate,
                   false_negative_rate, true_positives, true_negatives,
                   false_positives, false_negatives,
                   detection_threshold, created_at
            FROM model_metrics
            ORDER BY created_at DESC
            LIMIT 1
            """);

        // Agrega metadata extra
        metricas.put("exported_at",   LocalDateTime.now().toString());
        metricas.put("system",        "Antamina Anomaly Detection");
        metricas.put("export_format", "JSON v1.0");

        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(metricas);
    }
}
