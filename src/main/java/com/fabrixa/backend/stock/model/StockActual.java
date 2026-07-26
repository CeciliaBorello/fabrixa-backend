package com.fabrixa.backend.stock.model;

import com.fabrixa.backend.comercial.model.Producto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_actual")
@Getter
@Setter
public class StockActual {

    @Id
    @Column(name = "producto_id")
    private Long productoId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private BigDecimal cantidad = BigDecimal.ZERO;
}