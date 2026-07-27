package com.fabrixa.backend.comercial.dto;

import com.fabrixa.backend.comercial.model.TipoProducto;
import com.fabrixa.backend.comercial.model.UnidadMedida;

public class ProductoDTO {

    public record Request(
            String nombre,
            TipoProducto tipo,
            String codigoBarra,
            String rnpa,
            String valorNutricional,
            UnidadMedida unidadMedida,
            String categoria
    ) {}

    public record Response(
            Long id,
            String nombre,
            TipoProducto tipo,
            String codigoBarra,
            String rnpa,
            String valorNutricional,
            UnidadMedida unidadMedida,
            String categoria,
            boolean activo
    ) {}
}