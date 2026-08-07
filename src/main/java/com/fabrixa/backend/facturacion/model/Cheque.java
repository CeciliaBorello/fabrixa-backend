package com.fabrixa.backend.cheques.model;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.facturacion.model.Comprobante;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cheques")
@Getter
@Setter
public class Cheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String banco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tercero_id", nullable = false)
    private ClienteProveedor tercero; // quién nos lo entregó originalmente

    @Column(nullable = false)
    private BigDecimal monto;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_cobro")
    private LocalDate fechaCobro; // fecha en que se puede cobrar/depositar

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCheque estado = EstadoCheque.EN_CARTERA;

    // el Recibo de Cobro en el que ingresó a cartera
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_ingreso_id")
    private Comprobante reciboIngreso;

    // el Recibo de Pago en el que se entregó a un proveedor (nullable hasta que se entregue)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_egreso_id")
    private Comprobante reciboEgreso;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}