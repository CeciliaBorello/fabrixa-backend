package com.fabrixa.backend.contabilidad.repository;

import com.fabrixa.backend.contabilidad.model.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CuentaContableRepository extends JpaRepository<CuentaContable, Long> {

    @Query("SELECT c FROM CuentaContable c WHERE c.activo = :activo AND " +
            "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :busqueda, '%'))) " +
            "ORDER BY c.codigo")
    List<CuentaContable> buscar(@Param("activo") boolean activo, @Param("busqueda") String busqueda);

    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}