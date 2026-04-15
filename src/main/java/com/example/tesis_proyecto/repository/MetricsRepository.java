package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.ModelMetrics;
import com.example.tesis_proyecto.model.ModelVersions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MetricsRepository extends JpaRepository<ModelVersions, UUID> {
    @Query("SELECT m FROM ModelMetrics m ORDER BY m.createdAt DESC LIMIT 1")
    Optional<ModelMetrics> findLatest();
}
