package com.fabrixa.backend.cuentascorrientes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuentaCorrienteDTO {

    public record FilaResponse(Long clienteProveedorId, String razonSocial, String tipo, BigDecimal saldo) {}

    public record MovimientoResponse(
            LocalDate fecha,
            String concepto,       // ej. "Factura A #12", "Ajuste manual"
            String origen,         // "COMPROBANTE" o "AJUSTE"
            Long comprobanteId,    // null si es un ajuste
            BigDecimal debe,       // suma a favor nuestro (positivo)
            BigDecimal haber,      // suma en contra (a favor de ellos)
            BigDecimal saldoAcumulado,
            String motivo          // solo en ajustes
    ) {}

    public record AjusteRequest(Long clienteProveedorId, BigDecimal monto, String motivo) {}
}