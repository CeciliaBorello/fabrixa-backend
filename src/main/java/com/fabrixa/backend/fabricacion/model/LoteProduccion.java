package com.fabrixa.backend.fabricacion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lotes_produccion")
@Getter
@Setter
public class LoteProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_fabricacion_id", nullable = false)
    private OrdenFabricacion ordenFabricacion;

    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    @Column(nullable = false)
    private BigDecimal cantidad;

    @Column(name = "fecha_produccion", nullable = false)
    private LocalDateTime fechaProduccion = LocalDateTime.now();

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
}