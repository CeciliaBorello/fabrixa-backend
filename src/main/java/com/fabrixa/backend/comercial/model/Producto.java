package com.fabrixa.backend.comercial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoProducto tipo = TipoProducto.TERMINADO;

    @Column(name = "codigo_barra")
    private String codigoBarra;

    private String rnpa;

    @Column(name = "valor_nutricional", columnDefinition = "TEXT")
    private String valorNutricional;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false)
    private UnidadMedida unidadMedida = UnidadMedida.KG;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private CategoriaProducto categoria;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "precio_actual")
    private java.math.BigDecimal precioActual;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_base_id")
    private Producto productoBase; // null si es el producto "raíz"; si tiene valor, es una presentación de otro

    @Column(name = "presentacion")
    private String presentacion; // ej: "200 g", "1 kg", "Pack x6"
}