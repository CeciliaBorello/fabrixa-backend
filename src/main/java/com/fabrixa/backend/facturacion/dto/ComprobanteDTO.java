package com.fabrixa.backend.facturacion.dto;

import com.fabrixa.backend.facturacion.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ComprobanteDTO {

    public record ItemRequest(Long productoId, BigDecimal cantidad, BigDecimal precioUnitario) {}

    public record RemitoViajeRequest(String numero, String transportista, String chofer, String patente) {}

    // para RECIBO_COBRO: si chequeId es null y tipo=CHEQUE, se crea un cheque nuevo con estos datos
    // para RECIBO_PAGO: chequeId obligatorio si tipo=CHEQUE (se entrega un cheque que ya está en cartera)
    public record FormaPagoRequest(
            TipoFormaPago tipo,
            BigDecimal monto,
            Long chequeId,
            String chequeNumero,
            String chequeBanco,
            LocalDate chequeFechaCobro
    ) {}

    public record Request(
            TipoComprobante tipo,
            OrigenComprobante origen,
            Long clienteProveedorId,
            LocalDate fechaVencimiento,
            List<ItemRequest> items,
            List<FormaPagoRequest> formasPago,
            Long comprobanteAfectadoId,
            RemitoViajeRequest remitoViaje
    ) {}

    public record ItemResponse(Long id, Long productoId, String productoNombre, BigDecimal cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {}

    public record RemitoViajeResponse(Long id, String numero, String transportista, String chofer, String patente, LocalDate fecha) {}

    public record FormaPagoResponse(Long id, TipoFormaPago tipo, BigDecimal monto, Long chequeId, String chequeNumero) {}

    public record Response(
            Long id,
            TipoComprobante tipo,
            DireccionComprobante direccion,
            OrigenComprobante origen,
            String numero,
            String puntoVenta,
            Long clienteProveedorId,
            String clienteProveedorNombre,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            EstadoComprobante estado,
            EstadoCobro estadoCobro,
            EstadoPago estadoPago,
            BigDecimal total,
            String usuarioNombre,
            Long comprobanteAfectadoId,
            LocalDateTime fechaModificacion,
            List<ItemResponse> items,
            RemitoViajeResponse remitoViaje,
            List<FormaPagoResponse> formasPago
    ) {}
}