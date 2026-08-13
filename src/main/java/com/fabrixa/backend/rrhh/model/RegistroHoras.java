package com.fabrixa.backend.rrhh.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_horas")
@Getter
@Setter
public class RegistroHoras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private BigDecimal horas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigenRegistroHoras origen = OrigenRegistroHoras.MANUAL;

    @Column(nullable = false)
    private boolean liquidado = false;

    @Column(name = "liquidacion_id")
    private Long liquidacionId;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}