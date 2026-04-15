package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface NotificationPreferenceRepository
        extends JpaRepository<NotificationPreference, UUID> {

    Optional<NotificationPreference> findByUserId(UUID userId);

    @Query("""
    SELECT np FROM NotificationPreference np
    JOIN FETCH np.user u
    WHERE np.emailEnabled = true
    AND u.isActive = true
    AND u.lastLogin >= :desde
    AND (
        (:severity = 'critical' AND np.notifyCritical = true) OR
        (:severity = 'high'     AND np.notifyHigh     = true) OR
        (:severity = 'medium'   AND np.notifyMedium   = true) OR
        (:severity = 'low'      AND np.notifyLow      = true)
    )
""")
    List<NotificationPreference> findAllToNotifyBySeverity(
            @Param("severity") String severity,
            @Param("desde") LocalDateTime desde
    );
}
