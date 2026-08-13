package com.fabrixa.backend.rrhh.model;

import com.fabrixa.backend.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidaciones_mensuales")
@Getter
@Setter
public class LiquidacionMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(nullable = false)
    private String periodo; // ej "08/2026"

    @Column(name = "total_horas", nullable = false)
    private BigDecimal totalHoras;

    @Column(name = "valor_hora_usado", nullable = false)
    private BigDecimal valorHoraUsado;

    @Column(name = "total_a_pagar", nullable = false)
    private BigDecimal totalAPagar;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoRemuneracion tipoRemuneracionUsado;

}