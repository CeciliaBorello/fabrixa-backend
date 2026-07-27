package com.fabrixa.backend.fabricacion.model;

import com.fabrixa.backend.comercial.model.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formulas_producto")
@Getter
@Setter
public class FormulaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_terminado_id", nullable = false)
    private Producto productoTerminado;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "formula", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormulaInsumo> insumos = new ArrayList<>();
}