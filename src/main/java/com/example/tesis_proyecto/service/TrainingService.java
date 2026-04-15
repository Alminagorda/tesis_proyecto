package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.TrainingHistoryResponse;
import com.example.tesis_proyecto.model.EpochHistory;
import com.example.tesis_proyecto.model.TrainingRuns;
import com.example.tesis_proyecto.repository.EpochHistoryRepository;
import com.example.tesis_proyecto.repository.TrainingRunRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrainingService {
    private final TrainingRunRepository trainingRunRepository;
    private final EpochHistoryRepository epochHistoryRepository;


    private Double safeDouble(Number value) {
        return value != null ? value.doubleValue() : 0.0;
    }
//    public TrainingHistoryResponse getTrainingHistory() {
//        // Obtiene el training_run más reciente
//        TrainingRuns run = trainingRunRepository.findLatest()
//                .orElseThrow(() -> new RuntimeException("No hay runs de entrenamiento disponibles"));
//
//        // Obtiene todas las épocas de ese run, ordenadas
//        List<EpochHistory> epochs = epochHistoryRepository
//                .findByTrainingRunIdOrderByEpochAsc(run.getId());
//
//        // Mapea épocas a DTO
//        List<TrainingHistoryResponse.EpochDTO> epochDTOs = epochs.stream()
//                .map(e -> TrainingHistoryResponse.EpochDTO.builder()
//                        .epoch(e.getEpoch())
//                        .loss(Double.valueOf(e.getLoss()))
//                        .valLoss(Double.valueOf(e.getValLoss()))
//                        .learningRate(Double.valueOf(e.getLearningRate()))
//                        .build())
//                .toList();
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        Map<String, Object> metrics;
//        try {
//            metrics = mapper.readValue(run.getFinalMetrics(), Map.class);
//        } catch (Exception e) {
//            metrics = new HashMap<>();
//        }
//        return TrainingHistoryResponse.builder()
//                .trainingRunId(run.getId())
//                .modelName(run.getModelName())
//                .status(run.getStatus() != null ? run.getStatus().name() : null)
//                .threshold(Double.valueOf(run.getThreshold()))
//                .falsePositiveRate(Double.valueOf(run.getFalsePositiveRate()))
//                .finalMetrics(metrics)
//                .trainingStartedAt(run.getTrainingStartedAt())
//                .trainingEndedAt(run.getTrainingEndedAt())
//                .trainingDurationSeconds(Double.valueOf(run.getTrainingDurationSeconds()))
//                .epochs(epochDTOs)
//                .build();
//    }
public TrainingHistoryResponse getTrainingHistory() {

    TrainingRuns run = trainingRunRepository.findLatest()
            .orElseThrow(() -> new RuntimeException("No hay runs de entrenamiento disponibles"));

    List<EpochHistory> epochs = epochHistoryRepository
            .findByTrainingRunIdOrderByEpochAsc(run.getId());

    List<TrainingHistoryResponse.EpochDTO> epochDTOs = epochs.stream()
            .map(e -> TrainingHistoryResponse.EpochDTO.builder()
                    .epoch(e.getEpoch())
                    .loss(safeDouble(e.getLoss()))
                    .valLoss(safeDouble(e.getValLoss()))
                    .learningRate(safeDouble(e.getLearningRate()))
                    .build())
            .toList();

    ObjectMapper mapper = new ObjectMapper();

    Map<String, Object> metrics;
    try {
        metrics = mapper.readValue(run.getFinalMetrics(), Map.class);
    } catch (Exception e) {
        metrics = new HashMap<>();
    }

    return TrainingHistoryResponse.builder()
            .trainingRunId(run.getId())
            .modelName(run.getModelName())
            .status(run.getStatus() != null ? run.getStatus().name() : null)
            .threshold(safeDouble(run.getThreshold()))
            .falsePositiveRate(safeDouble(run.getFalsePositiveRate()))
            .finalMetrics(metrics)
            .trainingStartedAt(run.getTrainingStartedAt())
            .trainingEndedAt(run.getTrainingEndedAt())
            .trainingDurationSeconds(safeDouble(run.getTrainingDurationSeconds()))
            .epochs(epochDTOs)
            .build();
}

}
