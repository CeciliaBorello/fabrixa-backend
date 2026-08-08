package com.fabrixa.backend.facturacion.model;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comprobantes")
@Getter
@Setter
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComprobante tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DireccionComprobante direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrigenComprobante origen;

    private String numero;

    @Column(name = "punto_venta")
    private String puntoVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_proveedor_id", nullable = false)
    private ClienteProveedor clienteProveedor;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoComprobante estado = EstadoComprobante.BORRADOR;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cobro")
    private EstadoCobro estadoCobro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_pago")
    private EstadoPago estadoPago;

    @Column(nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // solo lo usan RECIBO_COBRO / RECIBO_PAGO / PAGO_CONTADO (a qué factura/remito afecta),
    // y opcionalmente NC/ND (referencia al comprobante original)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comprobante_afectado_id")
    private Comprobante comprobanteAfectado;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemComprobante> items = new ArrayList<>();

    @OneToOne(mappedBy = "comprobante", cascade = CascadeType.ALL, orphanRemoval = true)
    private RemitoViaje remitoViaje;

    @Column(nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO; // neto, sin IVA

    @Column(name = "iva_total", nullable = false)
    private BigDecimal ivaTotal = BigDecimal.ZERO;
    private String cae;

    @Column(name = "cae_vencimiento")
    private LocalDate caeVencimiento;

    private String moneda = "PES";

    private BigDecimal cotizacion = BigDecimal.ONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_arca", nullable = false)
    private EstadoArca estadoArca = EstadoArca.NO_GENERADO;

}