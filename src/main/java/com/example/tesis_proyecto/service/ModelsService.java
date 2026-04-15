package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.FeatureImportanceResponse;
import com.example.tesis_proyecto.dto.FeatureStatisticsResponse;
import com.example.tesis_proyecto.dto.ValidationResultsResponse;
import com.example.tesis_proyecto.model.ConfusionMatrixData;
import com.example.tesis_proyecto.model.FeatureImportance;
import com.example.tesis_proyecto.model.FeatureStatistics;
import com.example.tesis_proyecto.repository.ConfusionMatrixRepository;
import com.example.tesis_proyecto.repository.FeatureImportanceRepository;
import com.example.tesis_proyecto.repository.FeatureStatisticsRepository;
import com.example.tesis_proyecto.repository.TrainingRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModelsService {
    private final FeatureImportanceRepository featureImportanceRepository;
    private final TrainingRunRepository trainingRunRepository;
    // Inyectar en el constructor (agregar al @RequiredArgsConstructor)
    private final FeatureStatisticsRepository featureStatisticsRepository;
    // Agregar al constructor
    private final ConfusionMatrixRepository confusionMatrixRepository;

    private Double safeDouble(Number value) {
        return value != null ? value.doubleValue() : 0.0;
    }
    public FeatureImportanceResponse getFeatureImportance() {
        UUID runId = trainingRunRepository.findLatest()
                .orElseThrow(() -> new RuntimeException("No hay training run disponible"))
                .getId();

        List<FeatureImportance> features = featureImportanceRepository
                .findTop10ByTrainingRunId(runId);

        List<FeatureImportanceResponse.FeatureDTO> dtos = features.stream()
                .map(f -> FeatureImportanceResponse.FeatureDTO.builder()
                        .rank(f.getImportanceRank())
                        .featureName(f.getFeatureName())
                        .meanShapValue(safeDouble(f.getMeanShapValue()))
                        .stdShapValue(safeDouble(f.getStdShapValue()))
                        .minShap(safeDouble(f.getMinShap()))
                        .maxShap(safeDouble(f.getMaxShap()))
                        .medianShap(safeDouble(f.getMedianShap()))
                        .correlationWithAnomaly(safeDouble(f.getCorrelationWithAnomaly()))
                        .meanValue(safeDouble(f.getMeanValue()))
                        .stdValue(safeDouble(f.getStdValue()))
                        .build())
                .toList();

        return FeatureImportanceResponse.builder()
                .trainingRunId(runId)
                .totalFeatures(dtos.size())
                .features(dtos)
                .build();
    }


    public FeatureStatisticsResponse getFeatureStatistics(String attackType) {
        UUID runId = trainingRunRepository.findLatest()
                .orElseThrow(() -> new RuntimeException("No hay training run disponible"))
                .getId();

        List<FeatureStatistics> stats = (attackType != null && !attackType.isBlank())
                ? featureStatisticsRepository
                .findByTrainingRunIdAndAttackTypeOrderByImportanceRankAsc(runId, attackType)
                : featureStatisticsRepository
                .findByTrainingRunIdOrderByImportanceRankAsc(runId);

        List<FeatureStatisticsResponse.FeatureStatDTO> dtos = stats.stream()
                .map(s -> FeatureStatisticsResponse.FeatureStatDTO.builder()
                        .featureName(s.getFeatureName())
                        .attackType(s.getAttackType())
                        .attackCategory(s.getAttackCategory())
                        .meanValue(Double.valueOf(s.getMeanValue()))
                        .medianValue(Double.valueOf(s.getMedianValue()))
                        .stdValue(Double.valueOf(s.getStdValue()))
                        .minValue(Double.valueOf(s.getMinValue()))
                        .maxValue(Double.valueOf(s.getMaxValue()))
                        .q25Value(Double.valueOf(s.getQ25Value()))
                        .q75Value(Double.valueOf(s.getQ75Value()))
                        .meanShapValue(Double.valueOf(s.getMeanShapValue()))
                        .importanceRank(s.getImportanceRank())
                        .sampleCount(s.getSampleCount())
                        .build())
                .toList();

        return FeatureStatisticsResponse.builder()
                .trainingRunId(runId)
                .attackTypeFilter(attackType)
                .totalFeatures(dtos.size())
                .features(dtos)
                .build();
    }


    public ValidationResultsResponse getValidationResults() {
        UUID runId = trainingRunRepository.findLatest()
                .orElseThrow(() -> new RuntimeException("No hay training run disponible"))
                .getId();

        List<ConfusionMatrixData> matrices = confusionMatrixRepository
                .findByTrainingRunIdOrderByAttackTypeAsc(runId);

        List<ValidationResultsResponse.ConfusionMatrixDTO> dtos = matrices.stream()
                .map(m -> ValidationResultsResponse.ConfusionMatrixDTO.builder()
                        .attackType(m.getAttackType())
                        .truePositives(m.getTruePositives())
                        .trueNegatives(m.getTrueNegatives())
                        .falsePositives(m.getFalsePositives())
                        .falseNegatives(m.getFalseNegatives())
                        .accuracy(Double.valueOf(m.getAccuracy()))
                        .precision(Double.valueOf(m.getPrecision()))
                        .recall(Double.valueOf(m.getRecall()))
                        .f1Score(Double.valueOf(m.getF1Score()))
                        .build())
                .toList();

        // Promedios generales
        double avgAccuracy  = dtos.stream().filter(d -> d.getAccuracy()  != null)
                .mapToDouble(ValidationResultsResponse.ConfusionMatrixDTO::getAccuracy).average().orElse(0);
        double avgF1        = dtos.stream().filter(d -> d.getF1Score()   != null)
                .mapToDouble(ValidationResultsResponse.ConfusionMatrixDTO::getF1Score).average().orElse(0);
        double avgPrecision = dtos.stream().filter(d -> d.getPrecision() != null)
                .mapToDouble(ValidationResultsResponse.ConfusionMatrixDTO::getPrecision).average().orElse(0);
        double avgRecall    = dtos.stream().filter(d -> d.getRecall()    != null)
                .mapToDouble(ValidationResultsResponse.ConfusionMatrixDTO::getRecall).average().orElse(0);

        return ValidationResultsResponse.builder()
                .trainingRunId(runId)
                .totalAttackTypes(dtos.size())
                .avgAccuracy(avgAccuracy)
                .avgF1Score(avgF1)
                .avgPrecision(avgPrecision)
                .avgRecall(avgRecall)
                .confusionMatrix(dtos)
                .build();
    }
}
