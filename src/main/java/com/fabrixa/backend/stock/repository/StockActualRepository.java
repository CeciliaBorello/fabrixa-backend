package com.fabrixa.backend.stock.repository;

import com.fabrixa.backend.stock.model.StockActual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockActualRepository extends JpaRepository<StockActual, Long> {
}