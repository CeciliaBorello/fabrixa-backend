package com.fabrixa.backend.cuentascorrientes.model;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.usuarios.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ajustes_cuenta_corriente")
@Getter
@Setter
public class AjusteCuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_proveedor_id", nullable = false)
    private ClienteProveedor clienteProveedor;

    @Column(nullable = false)
    private LocalDate fecha;

    // positivo = suma a favor nuestro (nos deben más); negativo = resta (les debemos más / le condonamos deuda)
    @Column(nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @UpdateTimestamp
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}