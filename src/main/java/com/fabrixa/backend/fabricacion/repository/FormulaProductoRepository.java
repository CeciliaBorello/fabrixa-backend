package com.fabrixa.backend.fabricacion.repository;

import com.fabrixa.backend.fabricacion.model.FormulaProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormulaProductoRepository extends JpaRepository<FormulaProducto, Long> {
    List<FormulaProducto> findByProductoTerminadoId(Long productoTerminadoId);
}