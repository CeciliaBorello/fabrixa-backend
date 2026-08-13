package com.fabrixa.backend.rrhh.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "empleados")
@Getter
@Setter
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String dni;

    // --- datos opcionales ---
    private String direccion;
    private String telefono;
    private String email;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    private String puesto; // ej. "Operario de producción", "Administrativo"

    @Column(name = "contacto_emergencia_nombre")
    private String contactoEmergenciaNombre;

    @Column(name = "contacto_emergencia_telefono")
    private String contactoEmergenciaTelefono;

    @Column(name = "contacto_emergencia_vinculo")
    private String contactoEmergenciaVinculo; // ej. "Esposa", "Padre", "Hermano"

    @Column(name = "obra_social")
    private String obraSocial;

    private String observaciones; // texto libre, para cualquier cosa que no entre en otro campo

    @Column(nullable = false)
    private boolean activo = true;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRemuneracion tipoRemuneracion = TipoRemuneracion.POR_HORA;

    private BigDecimal valorHora;

    private BigDecimal sueldoFijo;
}