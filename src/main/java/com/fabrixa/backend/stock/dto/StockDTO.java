package com.fabrixa.backend.stock.dto;

import com.fabrixa.backend.stock.model.TipoMovimientoStock;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StockDTO {

    public record StockActualResponse(Long productoId, String productoNombre, BigDecimal cantidad) {}

    public record MovimientoResponse(
            Long id,
            Long productoId,
            String productoNombre,
            TipoMovimientoStock tipo,
            BigDecimal cantidad,
            LocalDateTime fecha,
            String referenciaTipo,
            Long referenciaId,
            String motivo
    ) {}

    public record AjusteRequest(Long productoId, BigDecimal delta, String motivo) {}
}