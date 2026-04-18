package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.OtpSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpSessionRepository extends CrudRepository<OtpSession, String> {
}

