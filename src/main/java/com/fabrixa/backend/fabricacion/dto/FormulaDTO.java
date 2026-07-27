package com.fabrixa.backend.fabricacion.dto;

import com.fabrixa.backend.comercial.model.UnidadMedida;

import java.math.BigDecimal;
import java.util.List;

public class FormulaDTO {

    public record InsumoRequest(Long insumoProductoId, BigDecimal cantidadNecesaria, UnidadMedida unidadMedida) {}

    public record Request(Long productoTerminadoId, String nombre, List<InsumoRequest> insumos) {}

    public record InsumoResponse(Long id, Long insumoProductoId, String insumoNombre,
                                 BigDecimal cantidadNecesaria, UnidadMedida unidadMedida) {}

    public record Response(Long id, Long productoTerminadoId, String productoTerminadoNombre,
                           String nombre, Integer version, boolean activo, List<InsumoResponse> insumos) {}
}