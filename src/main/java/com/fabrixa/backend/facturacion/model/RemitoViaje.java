package com.fabrixa.backend.facturacion.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "remitos_viaje")
@Getter
@Setter
public class RemitoViaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false, unique = true)
    private Comprobante comprobante;

    private String numero;
    private String transportista;
    private String chofer;
    private String patente;
    private LocalDate fecha;
}