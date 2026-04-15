package com.example.tesis_proyecto.service;

import com.example.tesis_proyecto.model.NotificationPreference;
import com.example.tesis_proyecto.model.User;
import com.example.tesis_proyecto.repository.NotificationPreferenceRepository;
import com.example.tesis_proyecto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public NotificationPreference getOrCreate(UUID userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + userId));
                    NotificationPreference pref = new NotificationPreference();
                    pref.setUser(user);
                    return preferenceRepository.save(pref);
                });
    }

    public NotificationPreference update(UUID userId, Boolean emailEnabled,
                                         Boolean notifyCritical, Boolean notifyHigh,
                                         Boolean notifyMedium,  Boolean notifyLow) {
        NotificationPreference pref = getOrCreate(userId);
        if (emailEnabled   != null) pref.setEmailEnabled(emailEnabled);
        if (notifyCritical != null) pref.setNotifyCritical(notifyCritical);
        if (notifyHigh     != null) pref.setNotifyHigh(notifyHigh);
        if (notifyMedium   != null) pref.setNotifyMedium(notifyMedium);
        if (notifyLow      != null) pref.setNotifyLow(notifyLow);
        pref.setUpdatedAt(LocalDateTime.now());
        return preferenceRepository.save(pref);
    }
}