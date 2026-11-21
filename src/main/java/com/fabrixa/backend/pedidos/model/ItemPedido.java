package com.fabrixa.backend.pedidos.model;

import com.fabrixa.backend.comercial.model.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "items_pedido")
@Getter
@Setter
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Admite decimales (p. ej. 2,5 kg). Hasta 3 decimales: ver V2__cantidad_pedido_decimal.sql
    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal cantidad;
}
