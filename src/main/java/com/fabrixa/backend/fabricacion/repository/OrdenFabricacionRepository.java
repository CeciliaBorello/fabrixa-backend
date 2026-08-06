package com.fabrixa.backend.fabricacion.repository;

import com.fabrixa.backend.fabricacion.model.OrdenFabricacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdenFabricacionRepository extends JpaRepository<OrdenFabricacion, Long> {

    List<OrdenFabricacion> findByProductoIdOrderByFechaFinDesc(Long productoId);

    @Query("SELECT o FROM OrdenFabricacion o WHERE " +
            "((:soloCanceladas = true AND o.estado = 'CANCELADA') OR (:soloCanceladas = false AND o.estado <> 'CANCELADA')) AND " +
            "LOWER(o.producto.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<OrdenFabricacion> buscar(@Param("soloCanceladas") boolean soloCanceladas, @Param("busqueda") String busqueda, Pageable pageable);

    @Query("SELECT o FROM OrdenFabricacion o WHERE o.producto.id IN :ids AND o.estado = 'FINALIZADA' ORDER BY o.fechaFin DESC")
    List<OrdenFabricacion> findUltimasFinalizadasPorProductos(@Param("ids") List<Long> ids);
}