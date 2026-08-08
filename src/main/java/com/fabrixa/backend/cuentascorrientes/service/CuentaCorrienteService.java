package com.fabrixa.backend.cuentascorrientes.service;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.comercial.repository.ClienteProveedorRepository;
import com.fabrixa.backend.cuentascorrientes.dto.CuentaCorrienteDTO.*;
import com.fabrixa.backend.cuentascorrientes.model.AjusteCuentaCorriente;
import com.fabrixa.backend.cuentascorrientes.repository.AjusteCuentaCorrienteRepository;
import com.fabrixa.backend.facturacion.model.Comprobante;
import com.fabrixa.backend.facturacion.model.EstadoComprobante;
import com.fabrixa.backend.facturacion.model.OrigenComprobante;
import com.fabrixa.backend.facturacion.model.TipoComprobante;
import com.fabrixa.backend.facturacion.repository.ComprobanteRepository;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class CuentaCorrienteService {

    private static final Set<TipoComprobante> TIPOS_VENTA = Set.of(
            TipoComprobante.FACTURA_A, TipoComprobante.FACTURA_B_REMITO, TipoComprobante.FACTURA_C_REMITO
    );

    private final ComprobanteRepository comprobanteRepository;
    private final AjusteCuentaCorrienteRepository ajusteRepository;
    private final ClienteProveedorRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public CuentaCorrienteService(ComprobanteRepository comprobanteRepository,
                                  AjusteCuentaCorrienteRepository ajusteRepository,
                                  ClienteProveedorRepository clienteRepository,
                                  UsuarioRepository usuarioRepository) {
        this.comprobanteRepository = comprobanteRepository;
        this.ajusteRepository = ajusteRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** signo de un comprobante según su tipo, para el cálculo del saldo */
    private BigDecimal signoDe(Comprobante c) {
        return switch (c.getTipo()) {
            case FACTURA_A, FACTURA_B_REMITO, FACTURA_C_REMITO -> c.getTotal();
            case RECIBO_COBRO, PAGO_CONTADO -> c.getTotal().negate();
            case FACTURA_COMPRA -> c.getTotal().negate();
            case RECIBO_PAGO -> c.getTotal().negate().negate(); // = +total, lo dejamos explícito por claridad
            case NOTA_CREDITO -> c.getOrigen() == OrigenComprobante.GENERADO ? c.getTotal().negate() : c.getTotal();
            case NOTA_DEBITO -> c.getOrigen() == OrigenComprobante.GENERADO ? c.getTotal() : c.getTotal().negate();
        };
    }

    public BigDecimal calcularSaldo(Long clienteId) {
        List<Comprobante> comprobantes = comprobanteRepository.findByClienteProveedorIdAndEstadoNot(clienteId, EstadoComprobante.ANULADO);
        BigDecimal saldoComprobantes = comprobantes.stream().map(this::signoDe).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoAjustes = ajusteRepository.sumaAjustes(clienteId);
        return saldoComprobantes.add(saldoAjustes);
    }

    public List<MovimientoResponse> movimientos(Long clienteId) {
        List<Comprobante> comprobantes = comprobanteRepository.findByClienteProveedorIdAndEstadoNot(clienteId, EstadoComprobante.ANULADO);
        List<AjusteCuentaCorriente> ajustes = ajusteRepository.findByClienteProveedorIdOrderByFechaAsc(clienteId);

        record Linea(LocalDate fecha, java.time.LocalDateTime ordenPor, String concepto, String origen, Long comprobanteId, BigDecimal monto, String motivo) {}

        List<Linea> lineas = new ArrayList<>();

        for (Comprobante c : comprobantes) {
            lineas.add(new Linea(c.getFechaEmision(), c.getFechaCreacion(), etiquetaTipo(c), "COMPROBANTE", c.getId(), signoDe(c), null));
        }
        for (AjusteCuentaCorriente a : ajustes) {
            lineas.add(new Linea(a.getFecha(), a.getFechaModificacion(), "Ajuste manual", "AJUSTE", null, a.getMonto(), a.getMotivo()));
        }

        lineas.sort(Comparator.comparing(Linea::ordenPor));

        List<MovimientoResponse> resultado = new ArrayList<>();
        BigDecimal acumulado = BigDecimal.ZERO;
        for (Linea l : lineas) {
            acumulado = acumulado.add(l.monto());
            BigDecimal debe = l.monto().signum() > 0 ? l.monto() : BigDecimal.ZERO;
            BigDecimal haber = l.monto().signum() < 0 ? l.monto().abs() : BigDecimal.ZERO;
            resultado.add(new MovimientoResponse(l.fecha(), l.concepto(), l.origen(), l.comprobanteId(), debe, haber, acumulado, l.motivo()));
        }

        return resultado;
    }

    private String etiquetaTipo(Comprobante c) {
        String tipo = switch (c.getTipo()) {
            case FACTURA_A -> "Factura A";
            case FACTURA_B_REMITO -> "Factura B";
            case FACTURA_C_REMITO -> "Factura C";
            case FACTURA_COMPRA -> "Factura de Compra";
            case RECIBO_COBRO -> "Recibo de Cobro";
            case RECIBO_PAGO -> "Recibo de Pago";
            case PAGO_CONTADO -> "Pago Contado";
            case NOTA_CREDITO -> "Nota de Crédito";
            case NOTA_DEBITO -> "Nota de Débito";
        };
        return tipo + " #" + c.getId();
    }

    public Page<FilaResponse> listarPaginado(String busqueda, Pageable pageable) {
        Page<ClienteProveedor> pagina = clienteRepository.buscar(true, busqueda, pageable);
        return pagina.map(c -> new FilaResponse(c.getId(), c.getRazonSocial(), c.getTipo().name(), calcularSaldo(c.getId())));
    }

    public void crearAjuste(AjusteRequest request, Authentication auth) {
        ClienteProveedor cliente = clienteRepository.findById(request.clienteProveedorId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente/proveedor no encontrado"));
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        AjusteCuentaCorriente ajuste = new AjusteCuentaCorriente();
        ajuste.setClienteProveedor(cliente);
        ajuste.setFecha(LocalDate.now());
        ajuste.setMonto(request.monto());
        ajuste.setMotivo(request.motivo());
        ajuste.setUsuario(usuario);
        ajusteRepository.save(ajuste);
    }

    public Map<Long, BigDecimal> saldosDe(List<Long> ids) {
        Map<Long, BigDecimal> resultado = new LinkedHashMap<>();
        for (Long id : ids) {
            resultado.put(id, calcularSaldo(id));
        }
        return resultado;
    }

}