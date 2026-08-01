package com.fabrixa.backend.entidades.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provincias")
@Getter
@Setter
public class Provincia {

    @Id
    private String id; // código Georef, ej "06" para Buenos Aires

    @Column(nullable = false)
    private String nombre;
}

