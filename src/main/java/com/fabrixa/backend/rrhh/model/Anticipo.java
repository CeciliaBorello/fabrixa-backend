package com.fabrixa.backend.rrhh.model;

import com.fabrixa.backend.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "anticipos")
@Getter
@Setter
public class Anticipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDate fecha;

    private String motivo;

    @Column(nullable = false)
    private boolean liquidado = false;

    @Column(name = "liquidacion_id")
    private Long liquidacionId; // se completa cuando se descuenta en una liquidación

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // quién registró el anticipo
}