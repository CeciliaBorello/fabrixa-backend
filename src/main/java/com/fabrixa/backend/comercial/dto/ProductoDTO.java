package com.fabrixa.backend.comercial.dto;

import com.fabrixa.backend.comercial.model.CategoriaProducto;
import com.fabrixa.backend.comercial.model.TipoProducto;
import com.fabrixa.backend.comercial.model.UnidadMedida;

import java.math.BigDecimal;

public class ProductoDTO {

    public record Request(
            String nombre,
            TipoProducto tipo,
            String codigoBarra,
            String rnpa,
            String valorNutricional,
            UnidadMedida unidadMedida,
            CategoriaProducto categoria,
            Long productoBaseId,
            String presentacion
    ) {}

    public record Response(
            Long id,
            String nombre,
            TipoProducto tipo,
            String codigoBarra,
            String rnpa,
            String valorNutricional,
            UnidadMedida unidadMedida,
            CategoriaProducto categoria,
            BigDecimal precioActual,
            boolean activo,
            Long productoBaseId,
            String productoBaseNombre,
            String presentacion
    ) {}
}