package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.FeatureStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface FeatureStatisticsRepository extends JpaRepository<FeatureStatistics, UUID> {

    List<FeatureStatistics> findByTrainingRunIdOrderByImportanceRankAsc(UUID trainingRunId);

    List<FeatureStatistics> findByTrainingRunIdAndAttackTypeOrderByImportanceRankAsc(
            UUID trainingRunId, String attackType);


    /**
     * Obtener todas las estadísticas de features
     */
    List<FeatureStatistics> findAll();

    /**
     * Obtener estadísticas por training run
     */
    List<FeatureStatistics> findByTrainingRunId(UUID trainingRunId);

    /**
     * Obtener estadísticas por tipo de ataque
     */
    List<FeatureStatistics> findByAttackType(String attackType);

    /**
     * Obtener estadísticas por feature name
     */
    List<FeatureStatistics> findByFeatureName(String featureName);

    /**
     * Obtener estadísticas por training run y attack type
     */
    List<FeatureStatistics> findByTrainingRunIdAndAttackType(UUID trainingRunId, String attackType);

    /**
     * Obtener las últimas estadísticas por feature
     * (Útil para el último entrenamiento)
     */
    @Query(value = """
        SELECT DISTINCT ON (fs.feature_name) fs.*
        FROM feature_statistics fs
        ORDER BY fs.feature_name, fs.created_at DESC
        """, nativeQuery = true)
    List<FeatureStatistics> findLatestByFeature();

    /**
     * Obtener estadísticas del último training run
     */
    @Query(value = """
        SELECT fs.*
        FROM feature_statistics fs
        WHERE fs.training_run_id = (SELECT id FROM training_runs ORDER BY created_at DESC LIMIT 1)
        """, nativeQuery = true)
    List<FeatureStatistics> findLatestTrainingStatistics();
}
