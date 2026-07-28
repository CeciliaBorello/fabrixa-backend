package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.HistorialPrecioInsumo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialPrecioInsumoRepository extends JpaRepository<HistorialPrecioInsumo, Long> {
    List<HistorialPrecioInsumo> findByProductoIdOrderByFechaDesc(Long productoId);
}