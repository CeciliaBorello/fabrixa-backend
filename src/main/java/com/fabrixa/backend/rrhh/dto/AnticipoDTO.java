package com.fabrixa.backend.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AnticipoDTO {
    public record Request(
            Long empleadoId,
            BigDecimal monto,
            LocalDate fecha,
            String motivo
    ) {}

    public record Response(
            Long id,
            Long empleadoId,
            String empleadoNombre,
            BigDecimal monto,
            LocalDate fecha,
            String motivo,
            boolean liquidado,
            Long liquidacionId,
            String usuarioNombre
    ) {}
}