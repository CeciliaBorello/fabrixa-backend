package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Page<Producto> findByActivoTrueAndTipoIn(List<TipoProducto> tipos, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = :activo AND p.tipo IN :tipos AND " +
            "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(p.codigoBarra) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Producto> buscar(@Param("activo") boolean activo, @Param("tipos") List<TipoProducto> tipos,
                          @Param("busqueda") String busqueda, Pageable pageable);
}