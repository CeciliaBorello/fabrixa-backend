package com.fabrixa.backend.comercial.dto;

public class ProductoDTO {

    public record Request(
            String nombre,
            String codigoBarra,
            String rnpa,
            String valorNutricional,
            String unidadMedida,
            String categoria
    ) {}

    public record Response(
            Long id,
            String nombre,
            String codigoBarra,
            String rnpa,
            String valorNutricional,
            String unidadMedida,
            String categoria,
            boolean activo
    ) {}
}