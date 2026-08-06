package com.fabrixa.backend.stock.dto;

import java.math.BigDecimal;

public class StockDTO {

    public record AjusteRequest(Long productoId, BigDecimal delta, String motivo) {}

    public record MovimientoResponse(
            Long id, Long productoId, String productoNombre,
            com.fabrixa.backend.stock.model.TipoMovimientoStock tipo,
            BigDecimal cantidad, java.time.LocalDateTime fecha,
            String referenciaTipo, Long referenciaId, String motivo
    ) {}

    public record StockActualResponse(Long productoId, String productoNombre, BigDecimal cantidad) {}

    // fila combinada: producto + su stock, para el listado paginado
    public record FilaResponse(
            Long productoId,
            String productoNombre,
            BigDecimal cantidad,
            String unidadMedida,
            boolean tienePresentaciones
    ) {}
}