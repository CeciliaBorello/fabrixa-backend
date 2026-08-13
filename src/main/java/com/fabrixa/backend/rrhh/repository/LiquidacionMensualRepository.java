package com.fabrixa.backend.rrhh.repository;

import com.fabrixa.backend.rrhh.model.LiquidacionMensual;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LiquidacionMensualRepository extends JpaRepository<LiquidacionMensual, Long> {

    @Query("SELECT l FROM LiquidacionMensual l WHERE " +
            "LOWER(l.empleado.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<LiquidacionMensual> buscar(@Param("busqueda") String busqueda, Pageable pageable);
}