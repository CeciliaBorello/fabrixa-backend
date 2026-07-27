package com.fabrixa.backend.comercial.model;

public enum UnidadMedida {
    KG(CategoriaUnidad.MASA, new java.math.BigDecimal("1000")),
    G(CategoriaUnidad.MASA, java.math.BigDecimal.ONE),
    L(CategoriaUnidad.VOLUMEN, new java.math.BigDecimal("1000")),
    ML(CategoriaUnidad.VOLUMEN, java.math.BigDecimal.ONE),
    UNIDAD(CategoriaUnidad.UNIDAD, java.math.BigDecimal.ONE);

    public enum CategoriaUnidad { MASA, VOLUMEN, UNIDAD }

    private final CategoriaUnidad categoria;
    private final java.math.BigDecimal factorABaseCategoria; // cuántas "unidades base" (g, ml o unidad) representa 1 de esta unidad

    UnidadMedida(CategoriaUnidad categoria, java.math.BigDecimal factorABaseCategoria) {
        this.categoria = categoria;
        this.factorABaseCategoria = factorABaseCategoria;
    }

    public CategoriaUnidad getCategoria() {
        return categoria;
    }

    public java.math.BigDecimal getFactorABaseCategoria() {
        return factorABaseCategoria;
    }
}