package com.fabrixa.backend.rrhh.dto;

import com.fabrixa.backend.rrhh.model.TipoRemuneracion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LiquidacionMensualDTO {
    public record Request(
            Long empleadoId,
            String periodo,          // etiqueta libre, ej: "08/2026 - 1ra quincena"
            LocalDate fechaDesde,    // nuevo — solo aplica si POR_HORA
            LocalDate fechaHasta,    // nuevo — solo aplica si POR_HORA
            BigDecimal totalAPagar   // solo aplica si SUELDO_FIJO
    ) {}

    public record Response(
            Long id,
            Long empleadoId,
            String empleadoNombre,
            String periodo,
            TipoRemuneracion tipoRemuneracionUsado,
            BigDecimal totalHoras,       // null si SUELDO_FIJO
            BigDecimal valorHoraUsado,   // null si SUELDO_FIJO
            BigDecimal totalAPagar,
            LocalDateTime fechaGeneracion,
            String usuarioNombre
    ) {}
}