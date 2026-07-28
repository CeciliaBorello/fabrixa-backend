package com.fabrixa.backend.comercial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PrecioDTO {

    public record Request(BigDecimal precio, String motivo) {}

    public record Response(
            Long id,
            Long productoId,
            String productoNombre,
            BigDecimal precio,
            LocalDateTime fecha,
            String usuarioNombre,
            String motivo
    ) {}
}