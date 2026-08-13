package com.fabrixa.backend.rrhh.repository;

import com.fabrixa.backend.rrhh.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query("SELECT e FROM Empleado e WHERE e.activo = :activo AND " +
            "(LOWER(e.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(e.dni) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
            "ORDER BY e.nombre")
    List<Empleado> buscar(@Param("activo") boolean activo, @Param("busqueda") String busqueda);

    boolean existsByDniAndIdNot(String dni, Long id);
    boolean existsByDni(String dni);
}