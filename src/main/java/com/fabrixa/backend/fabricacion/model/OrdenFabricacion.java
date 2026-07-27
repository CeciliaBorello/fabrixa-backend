package com.fabrixa.backend.fabricacion.model;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordenes_fabricacion")
@Getter
@Setter
public class OrdenFabricacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formula_id", nullable = false)
    private FormulaProducto formula;

    @Column(name = "cantidad_planificada", nullable = false)
    private BigDecimal cantidadPlanificada;

    @Column(name = "cantidad_producida")
    private BigDecimal cantidadProducida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrdenFabricacion estado = EstadoOrdenFabricacion.PLANIFICADA;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "ordenFabricacion", cascade = CascadeType.ALL)
    private List<LoteProduccion> lotes = new ArrayList<>();
}