package com.fabrixa.backend.facturacion.model;

import com.fabrixa.backend.cheques.model.Cheque;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "formas_pago_comprobante")
@Getter
@Setter
public class FormaPagoComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_id", nullable = false)
    private Comprobante comprobante; // el Recibo/Pago Contado

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoFormaPago tipo;

    @Column(nullable = false)
    private BigDecimal monto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cheque_id")
    private Cheque cheque; // solo si tipo = CHEQUE
}