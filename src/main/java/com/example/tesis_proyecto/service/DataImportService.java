package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.FeatureStatisticsDTO;
import com.example.tesis_proyecto.model.FeatureStatistics;
import com.example.tesis_proyecto.model.TrainingRuns;
import com.example.tesis_proyecto.repository.FeatureStatisticsRepository;
import com.example.tesis_proyecto.repository.TrainingRunRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

@Service
public class DataImportService {
        @Autowired
        private JdbcTemplate jdbcTemplate;

        @Autowired
        private ObjectMapper objectMapper;

    @Autowired
    private FeatureStatisticsRepository repository;

    @Autowired
    private TrainingRunRepository trainingRunsRepository;

    private Float toFloat(Object value) {
        if (value == null) return null;
        return ((Number) value).floatValue();
    }
        // ----------------------------------------------------------
        // Lee un JSON desde resources/data/
        // ----------------------------------------------------------
        private String leerJson(String nombreArchivo) throws Exception {
            ClassPathResource resource = new ClassPathResource(
                    "data/" + nombreArchivo);
            return new String(resource.getInputStream().readAllBytes());
        }

        // ----------------------------------------------------------
        // Importa training_run → retorna el UUID generado
        // ----------------------------------------------------------
        public String importarTrainingRun() throws Exception {
            String json = leerJson("training_run.json");
            Map<String, Object> data = objectMapper.readValue(
                    json, Map.class);

            String id = UUID.randomUUID().toString();
            jdbcTemplate.update("""
            INSERT INTO training_runs (
                id, model_name, status, architecture,
                hyperparameters, dataset_info, threshold,
                threshold_method, threshold_percentile
            ) VALUES (?::uuid, ?, 'completed', ?::jsonb,
                      ?::jsonb, ?::jsonb, ?, ?, ?)
            ON CONFLICT DO NOTHING
            """,
                    id,
                    data.get("model_name"),
                    objectMapper.writeValueAsString(data.get("architecture")),
                    objectMapper.writeValueAsString(data.get("hyperparameters")),
                    objectMapper.writeValueAsString(data.get("dataset_info")),
                    data.get("threshold"),
                    data.get("threshold_method"),
                    data.get("threshold_percentile")
            );

            System.out.println("✓ training_run importado: " + id);
            return id;
        }

        // ----------------------------------------------------------
        // Importa epoch_history
        // ----------------------------------------------------------
        public int importarEpochs(String trainingRunId) throws Exception {
            String json  = leerJson("epochs_history.json");
            List<Map<String, Object>> epochs = objectMapper.readValue(
                    json, List.class);

            int count = 0;
            for (Map<String, Object> epoch : epochs) {
                jdbcTemplate.update("""
                INSERT INTO epoch_history (
                    id, training_run_id, epoch,
                    loss, val_loss, learning_rate
                ) VALUES (uuid_generate_v4(), ?::uuid, ?, ?, ?, ?)
                """,
                        trainingRunId,
                        epoch.get("epoch"),
                        epoch.get("loss"),
                        epoch.get("val_loss"),
                        epoch.get("learning_rate")
                );
                count++;
            }
            System.out.println("✓ epochs importados: " + count);
            return count;
        }

        // ----------------------------------------------------------
        // Importa dataset_quality_metrics
        // ----------------------------------------------------------
        public void importarDatasetQuality(String trainingRunId)
                throws Exception {
            String json = leerJson("dataset_quality.json");
            Map<String, Object> data = objectMapper.readValue(
                    json, Map.class);

            jdbcTemplate.update("""
            INSERT INTO dataset_quality_metrics (
                id, training_run_id, total_records,
                total_features_final, total_features_original,
                missing_values_pct, duplicate_records_pct,
                normal_records, anomaly_records, balance_ratio,
                quality_score, is_approved
            ) VALUES (uuid_generate_v4(), ?::uuid, ?, ?, ?,
                      ?, ?, ?, ?, ?, ?, ?)
            """,
                    trainingRunId,
                    data.get("total_records"),
                    data.get("total_features_final"),
                    data.get("total_features_original"),
                    data.get("missing_values_pct"),
                    data.get("duplicate_records_pct"),
                    data.get("normal_records"),
                    data.get("anomaly_records"),
                    data.get("balance_ratio"),
                    data.get("quality_score"),
                    data.get("is_approved")
            );
            System.out.println("✓ dataset_quality importado");
        }

        // ----------------------------------------------------------
        // Importa model_metrics
        // ----------------------------------------------------------
        public void importarModelMetrics() throws Exception {
            String json = leerJson("model_metrics.json");
            Map<String, Object> data = objectMapper.readValue(
                    json, Map.class);

            jdbcTemplate.update("""
            INSERT INTO model_metrics (
                id, model_version, accuracy, precision,
                recall, f1_score, auc_roc,
                false_positive_rate, false_negative_rate,
                true_positives, true_negatives,
                false_positives, false_negatives,
                detection_threshold, normal_samples,
                anomaly_samples, test_samples
            ) VALUES (uuid_generate_v4(), ?, ?, ?, ?, ?, ?,
                      ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
                    data.get("model_version"),
                    data.get("accuracy"),
                    data.get("precision"),
                    data.get("recall"),
                    data.get("f1_score"),
                    data.get("auc_roc"),
                    data.get("false_positive_rate"),
                    data.get("false_negative_rate"),
                    data.get("true_positives"),
                    data.get("true_negatives"),
                    data.get("false_positives"),
                    data.get("false_negatives"),
                    data.get("detection_threshold"),
                    data.get("normal_samples"),
                    data.get("anomaly_samples"),
                    data.get("test_samples")
            );
            System.out.println("✓ model_metrics importado");
        }

        // ----------------------------------------------------------
        // Importa feature_importance
        // ----------------------------------------------------------
        public int importarFeatureImportance(String trainingRunId)
                throws Exception {
            String json = leerJson("feature_importance.json");
            List<Map<String, Object>> features = objectMapper.readValue(
                    json, List.class);

            int count = 0;
            for (Map<String, Object> feat : features) {
                jdbcTemplate.update("""
                INSERT INTO feature_importance (
                    id, training_run_id, feature_name,
                    mean_shap_value, importance_rank
                ) VALUES (uuid_generate_v4(), ?::uuid, ?, ?, ?)
                """,
                        trainingRunId,
                        feat.get("feature_name"),
                        feat.get("mean_shap_value"),
                        feat.get("importance_rank")
                );
                count++;
            }
            System.out.println("✓ feature_importance importado: " + count);
            return count;
        }

        // ----------------------------------------------------------
        // Importa confusion_matrix_data
        // ----------------------------------------------------------
        public void importarConfusionMatrix(String trainingRunId)
                throws Exception {
            String json = leerJson("confusion_matrix.json");
            Map<String, Object> data = objectMapper.readValue(
                    json, Map.class);

            jdbcTemplate.update("""
            INSERT INTO confusion_matrix_data (
                id, training_run_id, true_positives,
                true_negatives, false_positives, false_negatives,
                accuracy, precision, recall, f1_score
            ) VALUES (uuid_generate_v4(), ?::uuid, ?, ?, ?, ?,
                      ?, ?, ?, ?)
            """,
                    trainingRunId,
                    data.get("true_positives"),
                    data.get("true_negatives"),
                    data.get("false_positives"),
                    data.get("false_negatives"),
                    data.get("accuracy"),
                    data.get("precision"),
                    data.get("recall"),
                    data.get("f1_score")
            );
            System.out.println("✓ confusion_matrix importado");
        }

        // ----------------------------------------------------------
         //Importa attack_simulations
         //----------------------------------------------------------
//        public int importarAttackSimulations(String trainingRunId)
//                throws Exception {
//            String json = leerJson("attack_simulations.json");
//            List<Map<String, Object>> sims = objectMapper.readValue(
//                    json, List.class);
//
//            int count = 0;
//            for (Map<String, Object> sim : sims) {
//                jdbcTemplate.update("""
//                INSERT INTO attack_simulations (
//                    id, training_run_id, attack_type,
//                    total_samples, detected_correctly,
//                    false_negatives, false_positives,
//                    detection_rate, avg_reconstruction_error,
//                    min_reconstruction_error,
//                    max_reconstruction_error,
//                    std_reconstruction_error
//                ) VALUES (uuid_generate_v4(), ?::uuid, ?, ?, ?,
//                          ?, ?, ?, ?, ?, ?, ?)
//                """,
//                        trainingRunId,
//                        sim.get("attack_type"),
//                        sim.get("total_samples"),
//                        sim.get("detected_correctly"),
//                        sim.get("false_negatives"),
//                        sim.get("false_positives"),
//                        sim.get("detection_rate"),
//                        sim.get("avg_reconstruction_error"),
//                        sim.get("min_reconstruction_error"),
//                        sim.get("max_reconstruction_error"),
//                        sim.get("std_reconstruction_error")
//                );
//                count++;
//            }
////            System.out.println("✓ attack_simulations importado: " + count);
//            return count;
//        }
        public int importarAttackSimulations(String trainingRunId) throws Exception {

            String json = leerJson("attack_simulations.json");

            List<Map<String, Object>> sims = objectMapper.readValue(json, List.class);

            int count = 0;

            for (Map<String, Object> sim : sims) {

                int tp = ((Number) sim.get("detected_correctly")).intValue();
                int fn = ((Number) sim.get("false_negatives")).intValue();
                int fp = ((Number) sim.get("false_positives")).intValue();

                double precision = (tp + fp) > 0 ? (double) tp / (tp + fp) : 0.0;
                double recall = (tp + fn) > 0 ? (double) tp / (tp + fn) : 0.0;
                double f1 = (precision + recall) > 0
                        ? 2 * (precision * recall) / (precision + recall)
                        : 0.0;

                jdbcTemplate.update("""
            INSERT INTO attack_simulations (
                id, training_run_id, attack_type,
                total_samples, detected_correctly,
                false_negatives, false_positives,
                detection_rate, precision, recall, f1_score,
                avg_reconstruction_error,
                min_reconstruction_error,
                max_reconstruction_error,
                std_reconstruction_error
            ) VALUES (
                uuid_generate_v4(), ?::uuid, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            """,
                        trainingRunId,                                   // 1
                        sim.get("attack_type"),                          // 2
                        ((Number) sim.get("total_samples")).intValue(),  // 3
                        tp,                                              // 4
                        fn,                                              // 5
                        fp,                                              // 6
                        ((Number) sim.get("detection_rate")).doubleValue(), // 7
                        precision,                                       // 8
                        recall,                                          // 9
                        f1,                                              // 10
                        ((Number) sim.get("avg_reconstruction_error")).doubleValue(), // 11
                        ((Number) sim.get("min_reconstruction_error")).doubleValue(), // 12
                        ((Number) sim.get("max_reconstruction_error")).doubleValue(), // 13
                        ((Number) sim.get("std_reconstruction_error")).doubleValue()  // 14
                );

                count++;
            }

            System.out.println("✓ attack_simulations importado: " + count);
            return count;
        }

        // ----------------------------------------------------------
        // Importa model_versions
        // ----------------------------------------------------------
        public void importarModelVersions() throws Exception {
            String json = leerJson("model_versions.json");
            Map<String, Object> data = objectMapper.readValue(
                    json, Map.class);

            jdbcTemplate.update("""
            INSERT INTO model_versions (
                id, version_number, model_name, model_type,
                framework, features_count, threshold_value,
                is_active, deployment_status, training_dataset,
                architecture, hyperparameters
            ) VALUES (uuid_generate_v4(), ?, ?, ?, ?, ?, ?,
                      ?, ?, ?, ?::jsonb, ?::jsonb)
            """,
                    data.get("version_number"),
                    data.get("model_name"),
                    data.get("model_type"),
                    data.get("framework"),
                    data.get("features_count"),
                    data.get("threshold_value"),
                    data.get("is_active"),
                    data.get("deployment_status"),
                    data.get("training_dataset"),
                    objectMapper.writeValueAsString(data.get("architecture")),
                    objectMapper.writeValueAsString(data.get("hyperparameters"))
            );
            System.out.println("✓ model_versions importado");
        }

        // ----------------------------------------------------------
        // Importa experiment_summary
        // ----------------------------------------------------------
        public void importarExperimentSummary() throws Exception {
            String json = leerJson("experiment_summary.json");
            Map<String, Object> data = objectMapper.readValue(
                    json, Map.class);

            jdbcTemplate.update("""
            INSERT INTO experiment_summary (
                experiment_id, actividad, fase,
                optimal_threshold, auc_val, auc_test,
                val_precision, val_recall, val_f1,
                val_fpr, all_indicators_met
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
                    data.get("experiment_id"),
                    data.get("actividad"),
                    data.get("fase"),
                    data.get("optimal_threshold"),
                    data.get("auc_val"),
                    data.get("auc_test"),
                    data.get("val_precision"),
                    data.get("val_recall"),
                    data.get("val_f1"),
                    data.get("val_fpr"),
                    data.get("all_indicators_met")
            );
            System.out.println("✓ experiment_summary importado");
        }

        // ----------------------------------------------------------
        // IMPORTA TODO DE UNA VEZ
        // ----------------------------------------------------------
        @Transactional
        public Map<String, Object> importarTodo() throws Exception {
            Map<String, Object> resultado = new LinkedHashMap<>();

            // 1. training_run primero — genera el ID padre
            String trainingRunId = importarTrainingRun();
            resultado.put("training_run_id", trainingRunId);

            // 2. Todo lo que depende de training_run
            resultado.put("epochs",
                    importarEpochs(trainingRunId));
            importarDatasetQuality(trainingRunId);
            resultado.put("dataset_quality", "ok");
            importarConfusionMatrix(trainingRunId);
            resultado.put("confusion_matrix", "ok");
            resultado.put("feature_importance",
                    importarFeatureImportance(trainingRunId));
            resultado.put("attack_simulations",
                    importarAttackSimulations(trainingRunId));

            // 3. Tablas independientes
            importarModelMetrics();
            resultado.put("model_metrics", "ok");
            importarModelVersions();
            resultado.put("model_versions", "ok");
            importarExperimentSummary();
            resultado.put("experiment_summary", "ok");

            resultado.put("status", "completado");
            System.out.println("✓ Importación completa");
            return resultado;
        }

        //**************************

    public void importarFeatureStatistics(String trainingRunId) throws Exception {
        String json = leerJson("feature_statistics.json");
        List<Map<String, Object>> lista = objectMapper.readValue(json, List.class);

        for (Map<String, Object> row : lista) {
            jdbcTemplate.update("""
            INSERT INTO feature_statistics (
                id, training_run_id, attack_type, attack_category,
                feature_name, mean_value, median_value, std_value,
                min_value, max_value, q25_value, q75_value,
                mean_shap_value, importance_rank, sample_count
            ) VALUES (uuid_generate_v4(), ?::uuid, ?, ?,
                      ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """,
                    trainingRunId,
                    row.get("attack_type"),
                    row.get("attack_category"),
                    row.get("feature_name"),
                    toFloat(row.get("mean_value")),
                    toFloat(row.get("median_value")),
                    toFloat(row.get("std_value")),
                    toFloat(row.get("min_value")),
                    toFloat(row.get("max_value")),
                    toFloat(row.get("q25_value")),
                    toFloat(row.get("q75_value")),
                    toFloat(row.get("mean_shap_value")),   // null si no existe
                    row.get("importance_rank"),              // null si no existe
                    row.get("sample_count")
            );
        }
        System.out.println("✓ feature_statistics importado: " + lista.size() + " registros");
    }

}
