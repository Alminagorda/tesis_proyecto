package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.dto.FeedbackRequest;
import com.example.tesis_proyecto.dto.FeedbackResponse;
import com.example.tesis_proyecto.model.UserFeedback;
import com.example.tesis_proyecto.repository.UserFeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    private final UserFeedbackRepository userFeedbackRepository;

    public FeedbackService(UserFeedbackRepository userFeedbackRepository) {
        this.userFeedbackRepository = userFeedbackRepository;
    }

    public FeedbackResponse saveFeedback(FeedbackRequest request) {
        UserFeedback feedback = new UserFeedback();
        feedback.setUserRole(request.getUserRole());
        feedback.setFeatureEvaluated(request.getFeatureEvaluated());
        feedback.setRating(request.getRating());
        feedback.setComments(request.getComments());
        feedback.setIsUseful(request.getIsUseful());
        feedback.setSuggestions(request.getSuggestions());

        UserFeedback saved = userFeedbackRepository.save(feedback);

        return FeedbackResponse.builder()
                .id(saved.getId())
                .userRole(saved.getUserRole())
                .featureEvaluated(saved.getFeatureEvaluated())
                .rating(saved.getRating())
                .isUseful(saved.getIsUseful())
                .createdAt(saved.getCreatedAt())
                .message("Feedback guardado correctamente")
                .build();
    }

    public List<FeedbackResponse> getAllFeedback() {
        return userFeedbackRepository.findAll()
                .stream()
                .map(feedback -> FeedbackResponse.builder()
                        .id(feedback.getId())
                        .userRole(feedback.getUserRole())
                        .featureEvaluated(feedback.getFeatureEvaluated())
                        .rating(feedback.getRating())
                        .isUseful(feedback.getIsUseful())
                        .createdAt(feedback.getCreatedAt())
                        .message("OK")
                        .build()
                )
                .toList();
    }
}
