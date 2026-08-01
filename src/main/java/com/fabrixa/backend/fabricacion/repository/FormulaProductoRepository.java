package com.fabrixa.backend.fabricacion.repository;

import com.fabrixa.backend.fabricacion.model.FormulaProducto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FormulaProductoRepository extends JpaRepository<FormulaProducto, Long> {

    List<FormulaProducto> findByProductoTerminadoId(Long productoTerminadoId);

    @Query("SELECT f FROM FormulaProducto f WHERE f.activo = :activo AND " +
            "(LOWER(f.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(f.productoTerminado.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<FormulaProducto> buscar(@Param("activo") boolean activo, @Param("busqueda") String busqueda, Pageable pageable);
}