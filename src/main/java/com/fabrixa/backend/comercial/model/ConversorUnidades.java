package com.fabrixa.backend.comercial.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConversorUnidades {

    public static BigDecimal convertir(BigDecimal cantidad, UnidadMedida desde, UnidadMedida hacia) {
        if (desde == hacia) {
            return cantidad;
        }

        if (desde.getCategoria() != hacia.getCategoria()) {
            throw new IllegalArgumentException(
                    "No se puede convertir de " + desde + " a " + hacia +
                            " (son unidades de categorías distintas: " + desde.getCategoria() + " vs " + hacia.getCategoria() + ")");
        }

        // Paso a la unidad base de la categoría (gramos, ml o unidad) y de ahí a la unidad destino
        BigDecimal enBase = cantidad.multiply(desde.getFactorABaseCategoria());
        return enBase.divide(hacia.getFactorABaseCategoria(), 6, RoundingMode.HALF_UP);
    }
}