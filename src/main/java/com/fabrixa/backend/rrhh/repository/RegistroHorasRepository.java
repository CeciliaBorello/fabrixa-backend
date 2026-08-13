package com.fabrixa.backend.rrhh.repository;

import com.fabrixa.backend.rrhh.model.RegistroHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RegistroHorasRepository extends JpaRepository<RegistroHoras, Long> {

    List<RegistroHoras> findByEmpleadoIdAndLiquidadoFalseOrderByFechaAsc(Long empleadoId);

    @Query("SELECT r FROM RegistroHoras r WHERE r.liquidado = false ORDER BY r.empleado.nombre, r.fecha")
    List<RegistroHoras> findTodosNoLiquidados();

    List<RegistroHoras> findByEmpleadoIdAndFechaBetweenOrderByFechaAsc(Long empleadoId, LocalDate desde, LocalDate hasta);

    List<RegistroHoras> findByLiquidacionId(Long liquidacionId);
}