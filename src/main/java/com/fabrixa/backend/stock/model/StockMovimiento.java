package com.fabrixa.backend.stock.model;

import com.fabrixa.backend.comercial.model.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movimientos")
@Getter
@Setter
public class StockMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimientoStock tipo;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "referencia_tipo")
    private String referenciaTipo; // ej: "OrdenFabricacion", "Comprobante"

    @Column(name = "referencia_id")
    private Long referenciaId;

    private String motivo; // usado sobre todo en AJUSTE, para dejar constancia del porqué
}