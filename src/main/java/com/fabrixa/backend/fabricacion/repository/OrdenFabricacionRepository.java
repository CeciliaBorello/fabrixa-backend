package com.fabrixa.backend.fabricacion.repository;

import com.fabrixa.backend.fabricacion.model.OrdenFabricacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenFabricacionRepository extends JpaRepository<OrdenFabricacion, Long> {
}