package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.model.FeatureStatistics;
import com.example.tesis_proyecto.repository.FeatureStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class FeatureStatisticService {
    @Autowired
    private FeatureStatisticsRepository featureStatisticRepository;

    /**
     * Obtener todas las estadísticas de features
     * @return Lista de todas las estadísticas
     */
    public List<FeatureStatistics> getAllStatistics() {
        return featureStatisticRepository.findAll();
    }

    /**
     * Obtener estadísticas por training run
     * @param trainingRunId ID del training run
     * @return Lista de estadísticas para ese training
     */
    public List<FeatureStatistics> getStatisticsByTrainingRun(UUID trainingRunId) {
        return featureStatisticRepository.findByTrainingRunId(trainingRunId);
    }

    /**
     * Obtener estadísticas por tipo de ataque
     * @param attackType Tipo de ataque (ej: "Ransomware", "DDoS", "Phishing")
     * @return Lista de estadísticas para ese tipo de ataque
     */
    public List<FeatureStatistics> getStatisticsByAttackType(String attackType) {
        return featureStatisticRepository.findByAttackType(attackType);
    }

    /**
     * Obtener estadísticas por feature name
     * @param featureName Nombre de la feature
     * @return Lista de estadísticas para esa feature
     */
    public List<FeatureStatistics> getStatisticsByFeatureName(String featureName) {
        return featureStatisticRepository.findByFeatureName(featureName);
    }

    /**
     * Obtener las últimas estadísticas (para gráficas)
     * Obtiene datos del último training run
     * @return Lista de estadísticas del último entrenamiento
     */
    public List<FeatureStatistics> getLatestStatistics() {
        return featureStatisticRepository.findLatestTrainingStatistics();
    }

    /**
     * Guardar una nueva estadística de feature
     * @param statistic La estadística a guardar
     * @return La estadística guardada
     */
    public FeatureStatistics saveStatistic(FeatureStatistics statistic) {
        return featureStatisticRepository.save(statistic);
    }

    /**
     * Obtener una estadística por ID
     * @param id ID de la estadística
     * @return La estadística encontrada, o null
     */
    public FeatureStatistics getStatisticById(UUID id) {
        return featureStatisticRepository.findById(id).orElse(null);
    }

    /**
     * Eliminar una estadística por ID
     * @param id ID de la estadística
     */
    public void deleteStatistic(UUID id) {
        featureStatisticRepository.deleteById(id);
    }
}
