package com.fabrixa.backend.stock.repository;

import com.fabrixa.backend.stock.model.StockMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovimientoRepository extends JpaRepository<StockMovimiento, Long> {
    List<StockMovimiento> findByProductoIdOrderByFechaDesc(Long productoId);
}