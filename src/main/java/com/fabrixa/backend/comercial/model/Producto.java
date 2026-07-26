package com.fabrixa.backend.comercial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo_barra")
    private String codigoBarra;

    private String rnpa;

    @Column(name = "valor_nutricional", columnDefinition = "TEXT")
    private String valorNutricional;

    @Column(name = "unidad_medida")
    private String unidadMedida;

    private String categoria;

    @Column(nullable = false)
    private boolean activo = true;
}