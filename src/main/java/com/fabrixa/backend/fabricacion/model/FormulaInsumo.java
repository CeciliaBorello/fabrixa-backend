package com.fabrixa.backend.fabricacion.model;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.model.UnidadMedida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "formula_insumos")
@Getter
@Setter
public class FormulaInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id", nullable = false)
    private FormulaProducto formula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "insumo_producto_id", nullable = false)
    private Producto insumo;

    @Column(name = "cantidad_necesaria", nullable = false)
    private BigDecimal cantidadNecesaria;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;
}