package com.fabrixa.backend.rrhh.dto;

import com.fabrixa.backend.rrhh.model.TipoRemuneracion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LiquidacionMensualDTO {
    public record Request(
            Long empleadoId,
            String periodo,
            BigDecimal totalAPagar // nullable, solo requerido/usado si el empleado es SUELDO_FIJO
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