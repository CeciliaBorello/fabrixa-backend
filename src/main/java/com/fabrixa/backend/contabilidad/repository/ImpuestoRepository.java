package com.fabrixa.backend.contabilidad.repository;

import com.fabrixa.backend.contabilidad.model.EstadoImpuesto;
import com.fabrixa.backend.contabilidad.model.Impuesto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ImpuestoRepository extends JpaRepository<Impuesto, Long> {

    @Query("SELECT i FROM Impuesto i WHERE (:estado IS NULL OR i.estado = :estado) AND " +
            "LOWER(i.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Impuesto> buscar(@Param("estado") EstadoImpuesto estado, @Param("busqueda") String busqueda, Pageable pageable);
}