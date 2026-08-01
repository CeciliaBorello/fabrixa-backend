package com.fabrixa.backend.fabricacion.dto;

import com.fabrixa.backend.fabricacion.model.EstadoOrdenFabricacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdenFabricacionDTO {

    public record Request(Long productoId, Long formulaId, BigDecimal cantidadPlanificada) {}

    public record FinalizarRequest(BigDecimal cantidadProducida, String numeroLote, String fechaVencimiento) {}

    public record Response(
            Long id,
            Long productoId,
            String productoNombre,
            Long formulaId,
            BigDecimal cantidadPlanificada,
            BigDecimal cantidadProducida,
            EstadoOrdenFabricacion estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            LocalDateTime fechaModificacion,
            String usuarioNombre,
            BigDecimal costoTotalInsumos,
            BigDecimal costoUnitarioProducido
    ) {}
}