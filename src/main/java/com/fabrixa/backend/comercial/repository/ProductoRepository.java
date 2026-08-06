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

    @Query("SELECT p FROM Producto p WHERE p.activo = :activo AND p.tipo IN :tipos AND p.productoBase IS NULL AND " +
            "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(p.codigoBarra) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Producto> buscar(@Param("activo") boolean activo, @Param("tipos") List<TipoProducto> tipos,
                          @Param("busqueda") String busqueda, Pageable pageable);

    List<Producto> findByProductoBaseIdAndActivoTrueOrderByPresentacion(Long productoBaseId);

    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Producto> findByProductoBaseIsNullOrderByNombre(); // solo productos "raíz", para elegir como base
    List<Producto> findByProductoBaseIsNullAndActivoTrueOrderByNombre();

    @Query("SELECT DISTINCT p.productoBase.id FROM Producto p WHERE p.productoBase.id IN :ids")
    List<Long> findProductoBaseIdsConPresentaciones(@Param("ids") List<Long> ids);
}