package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Page<Producto> findByActivoTrueAndTipoIn(List<TipoProducto> tipos, Pageable pageable);

    // Vista "activos" (toggle apagado): productos base activos, tal cual antes.
    @Query("SELECT p FROM Producto p WHERE p.activo = :activo AND p.tipo IN :tipos AND p.productoBase IS NULL AND " +
            "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(p.codigoBarra) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Producto> buscar(@Param("activo") boolean activo, @Param("tipos") List<TipoProducto> tipos,
                          @Param("busqueda") String busqueda, Pageable pageable);

    // Vista "inactivos" (toggle prendido): productos base que están inactivos
    // ELLOS MISMOS, o que tienen alguna presentación inactiva aunque el base
    // siga activo -- así ningún inactivo, sea base o presentación, queda sin
    // forma de llegar a él desde la UI.
    @Query("SELECT DISTINCT p FROM Producto p WHERE p.tipo IN :tipos AND p.productoBase IS NULL AND " +
            "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(p.codigoBarra) LIKE LOWER(CONCAT('%', :busqueda, '%'))) AND " +
            "(p.activo = false OR EXISTS (SELECT 1 FROM Producto pres WHERE pres.productoBase = p AND pres.activo = false))")
    Page<Producto> buscarInactivos(@Param("tipos") List<TipoProducto> tipos,
                                   @Param("busqueda") String busqueda, Pageable pageable);

    List<Producto> findByProductoBaseIdAndActivoTrueOrderByPresentacion(Long productoBaseId);
    List<Producto> findByProductoBaseIdAndActivoFalseOrderByPresentacion(Long productoBaseId);

    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<Producto> findByProductoBaseIsNullOrderByNombre();
    List<Producto> findByProductoBaseIsNullAndActivoTrueOrderByNombre();

    @Query("SELECT DISTINCT p.productoBase.id FROM Producto p WHERE p.productoBase.id IN :ids")
    List<Long> findProductoBaseIdsConPresentaciones(@Param("ids") List<Long> ids);

    List<Producto> findByProductoBaseId(Long productoBaseId);
    long countByProductoBaseIdAndActivo(Long productoBaseId, boolean activo);
}