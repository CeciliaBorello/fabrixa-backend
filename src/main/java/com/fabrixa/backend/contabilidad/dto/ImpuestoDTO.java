package com.fabrixa.backend.contabilidad.dto;

import com.fabrixa.backend.contabilidad.model.EstadoImpuesto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ImpuestoDTO {
    public record Request(String nombre, String periodo, BigDecimal monto, LocalDate fechaVencimiento) {}
    public record Response(Long id, String nombre, String periodo, BigDecimal monto,
                           LocalDate fechaVencimiento, LocalDate fechaPago, EstadoImpuesto estado) {}
}