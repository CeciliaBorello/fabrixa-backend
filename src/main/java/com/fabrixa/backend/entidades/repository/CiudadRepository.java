package com.fabrixa.backend.entidades.repository;

import com.fabrixa.backend.entidades.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CiudadRepository extends JpaRepository<Ciudad, String> {
    List<Ciudad> findByProvinciaIdOrderByNombre(String provinciaId);
}
