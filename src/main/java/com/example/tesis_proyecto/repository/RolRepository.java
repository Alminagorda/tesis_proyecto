package com.example.tesis_proyecto.repository;

import com.example.tesis_proyecto.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, String> {
}