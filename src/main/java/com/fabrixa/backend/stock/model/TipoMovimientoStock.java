package com.fabrixa.backend.stock.model;

public enum TipoMovimientoStock {
    INGRESO_FACTURADO,
    INGRESO_CARTA_PORTE,
    INGRESO_SIN_FACTURA,
    INGRESO_PRODUCCION,
    EGRESO_VENTA,
    EGRESO_FABRICACION_INSUMO,
    AJUSTE;

    public boolean esIngreso() {
        return this == INGRESO_FACTURADO || this == INGRESO_CARTA_PORTE
                || this == INGRESO_SIN_FACTURA || this == INGRESO_PRODUCCION;
    }

    public boolean esEgreso() {
        return this == EGRESO_VENTA || this == EGRESO_FABRICACION_INSUMO;
    }
}