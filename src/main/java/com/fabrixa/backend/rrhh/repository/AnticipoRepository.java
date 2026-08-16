package com.fabrixa.backend.rrhh.repository;

import com.fabrixa.backend.rrhh.model.Anticipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnticipoRepository extends JpaRepository<Anticipo, Long> {

    List<Anticipo> findByEmpleadoIdOrderByFechaDesc(Long empleadoId);

    List<Anticipo> findByEmpleadoIdAndLiquidadoFalseOrderByFechaAsc(Long empleadoId);

    List<Anticipo> findByLiquidacionId(Long liquidacionId);
}