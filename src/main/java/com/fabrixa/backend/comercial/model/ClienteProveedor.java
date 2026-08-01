package com.fabrixa.backend.comercial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

import java.math.BigDecimal;

@Entity
@Table(name = "clientes_proveedores")
@Getter
@Setter
public class ClienteProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoClienteProveedor tipo;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(nullable = false, unique = true)
    private String cuit;

    @Column(name = "condicion_iva")
    private String condicionIva;

    private String direccion;
    private String telefono;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_precio_id")
    private ListaPrecio listaPrecio;

    @Column(name = "saldo_cuenta_corriente", nullable = false)
    private BigDecimal saldoCuentaCorriente = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean activo = true;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}