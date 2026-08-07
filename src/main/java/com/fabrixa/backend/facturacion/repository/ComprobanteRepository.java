package com.fabrixa.backend.facturacion.repository;

import com.fabrixa.backend.facturacion.model.Comprobante;
import com.fabrixa.backend.facturacion.model.TipoComprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.fabrixa.backend.facturacion.model.DireccionComprobante;

import java.util.List;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    @Query("SELECT c FROM Comprobante c WHERE " +
            "(:tipos IS NULL OR c.tipo IN :tipos) AND " +
            "(:soloAnulados = true AND c.estado = 'ANULADO' OR :soloAnulados = false AND c.estado <> 'ANULADO') AND " +
            "LOWER(c.clienteProveedor.razonSocial) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    Page<Comprobante> buscar(@Param("tipos") List<TipoComprobante> tipos,
                             @Param("soloAnulados") boolean soloAnulados,
                             @Param("busqueda") String busqueda,
                             Pageable pageable);

    List<Comprobante> findByComprobanteAfectadoIdAndEstado(Long comprobanteAfectadoId, com.fabrixa.backend.facturacion.model.EstadoComprobante estado);

    @Query("SELECT c FROM Comprobante c WHERE c.clienteProveedor.id = :clienteId AND c.direccion = :direccion " +
            "AND c.estado = 'EMITIDO' AND c.tipo IN ('FACTURA_A', 'FACTURA_B_REMITO', 'FACTURA_COMPRA') AND " +
            "((:direccion = 'VENTA' AND c.estadoCobro <> 'COBRADO') OR (:direccion = 'COMPRA' AND c.estadoPago <> 'PAGADO')) " +
            "ORDER BY c.fechaEmision")
    List<Comprobante> findPendientesPorCliente(@Param("clienteId") Long clienteId, @Param("direccion") DireccionComprobante direccion);
}