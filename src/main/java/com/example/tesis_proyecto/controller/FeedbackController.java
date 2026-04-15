package com.example.tesis_proyecto.controller;

import com.example.tesis_proyecto.dto.FeedbackRequest;
import com.example.tesis_proyecto.dto.FeedbackResponse;
import com.example.tesis_proyecto.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * POST /api/feedback
     * Guarda feedback del analista en BD
     */
    @PostMapping("/feedback")
    public ResponseEntity<FeedbackResponse> saveFeedback(
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(feedbackService.saveFeedback(request));
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

}