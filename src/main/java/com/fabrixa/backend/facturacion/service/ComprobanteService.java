package com.fabrixa.backend.facturacion.service;

import com.fabrixa.backend.cheques.model.Cheque;
import com.fabrixa.backend.cheques.repository.ChequeRepository;
import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.ClienteProveedorRepository;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.facturacion.dto.ComprobanteDTO.*;
import com.fabrixa.backend.facturacion.model.*;
import com.fabrixa.backend.facturacion.repository.ComprobanteRepository;
import com.fabrixa.backend.facturacion.repository.FormaPagoComprobanteRepository;
import com.fabrixa.backend.stock.model.TipoMovimientoStock;
import com.fabrixa.backend.stock.service.StockService;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ComprobanteService {

    private final ComprobanteRepository repository;
    private final FormaPagoComprobanteRepository formaPagoRepository;
    private final ClienteProveedorRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ChequeRepository chequeRepository;
    private final ChequeService chequeService;
    private final StockService stockService;

    public ComprobanteService(ComprobanteRepository repository,
                              FormaPagoComprobanteRepository formaPagoRepository,
                              ClienteProveedorRepository clienteRepository,
                              ProductoRepository productoRepository,
                              UsuarioRepository usuarioRepository,
                              ChequeRepository chequeRepository,
                              ChequeService chequeService,
                              StockService stockService) {
        this.repository = repository;
        this.formaPagoRepository = formaPagoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.chequeRepository = chequeRepository;
        this.chequeService = chequeService;
        this.stockService = stockService;
    }

    @Transactional
    public Response crear(Request request, Authentication auth) {
        ClienteProveedor cliente = clienteRepository.findById(request.clienteProveedorId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente/proveedor no encontrado"));
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Comprobante comprobante = new Comprobante();
        comprobante.setTipo(request.tipo());
        comprobante.setClienteProveedor(cliente);
        comprobante.setUsuario(usuario);
        comprobante.setFechaEmision(LocalDate.now());
        comprobante.setFechaVencimiento(request.fechaVencimiento());
        comprobante.setEstado(EstadoComprobante.EMITIDO);

        aplicarDireccionYOrigen(comprobante, request);

        switch (request.tipo()) {
            case FACTURA_A, FACTURA_B_REMITO -> crearConItemsVenta(comprobante, request);
            case FACTURA_COMPRA -> crearConItemsCompra(comprobante, request);
            case NOTA_CREDITO, NOTA_DEBITO -> crearNotaFinanciera(comprobante, request);
            case RECIBO_COBRO, PAGO_CONTADO -> crearReciboCobro(comprobante, request);
            case RECIBO_PAGO -> crearReciboPago(comprobante, request);
        }

        Comprobante guardado = repository.save(comprobante);

        if (request.remitoViaje() != null && (request.tipo() == TipoComprobante.FACTURA_A || request.tipo() == TipoComprobante.FACTURA_B_REMITO)) {
            RemitoViaje remito = new RemitoViaje();
            remito.setComprobante(guardado);
            remito.setNumero(request.remitoViaje().numero());
            remito.setTransportista(request.remitoViaje().transportista());
            remito.setChofer(request.remitoViaje().chofer());
            remito.setPatente(request.remitoViaje().patente());
            remito.setFecha(LocalDate.now());
            guardado.setRemitoViaje(remito);
        }

        return aResponse(repository.save(guardado));
    }

    private void aplicarDireccionYOrigen(Comprobante c, Request request) {
        switch (request.tipo()) {
            case FACTURA_A, FACTURA_B_REMITO, RECIBO_COBRO, PAGO_CONTADO -> {
                c.setDireccion(DireccionComprobante.VENTA);
                c.setOrigen(OrigenComprobante.GENERADO);
            }
            case FACTURA_COMPRA -> {
                c.setDireccion(DireccionComprobante.COMPRA);
                c.setOrigen(OrigenComprobante.RECIBIDO);
            }
            case RECIBO_PAGO -> {
                c.setDireccion(DireccionComprobante.COMPRA);
                c.setOrigen(OrigenComprobante.GENERADO);
            }
            case NOTA_CREDITO, NOTA_DEBITO -> {
                OrigenComprobante origen = request.origen() != null ? request.origen() : OrigenComprobante.GENERADO;
                c.setOrigen(origen);
                // generada por nosotros -> normalmente corrige una venta; recibida de un proveedor -> corrige una compra
                c.setDireccion(origen == OrigenComprobante.GENERADO ? DireccionComprobante.VENTA : DireccionComprobante.COMPRA);
            }
        }
    }

    private void crearConItemsVenta(Comprobante c, Request request) {
        BigDecimal total = agregarItems(c, request);
        c.setTotal(total);
        c.setEstadoCobro(EstadoCobro.PENDIENTE);

        // descuenta stock de cada producto vendido
        for (ItemComprobante item : c.getItems()) {
            stockService.registrarMovimiento(
                    item.getProducto().getId(), TipoMovimientoStock.EGRESO_VENTA, item.getCantidad(),
                    "Comprobante", c.getId(), "Venta por " + request.tipo()
            );
        }
    }

    private void crearConItemsCompra(Comprobante c, Request request) {
        BigDecimal total = agregarItems(c, request);
        c.setTotal(total);
        c.setEstadoPago(EstadoPago.RECIBIDO);

        for (ItemComprobante item : c.getItems()) {
            stockService.registrarMovimiento(
                    item.getProducto().getId(), TipoMovimientoStock.INGRESO_FACTURADO, item.getCantidad(),
                    "Comprobante", c.getId(), "Compra facturada"
            );
        }
    }

    private BigDecimal agregarItems(Comprobante c, Request request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El comprobante necesita al menos un ítem");
        }
        BigDecimal total = BigDecimal.ZERO;
        for (ItemRequest itemReq : request.items()) {
            Producto producto = productoRepository.findById(itemReq.productoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemReq.productoId()));

            ItemComprobante item = new ItemComprobante();
            item.setComprobante(c);
            item.setProducto(producto);
            item.setCantidad(itemReq.cantidad());
            item.setPrecioUnitario(itemReq.precioUnitario());
            BigDecimal subtotal = itemReq.cantidad().multiply(itemReq.precioUnitario());
            item.setSubtotal(subtotal);
            c.getItems().add(item);
            total = total.add(subtotal);
        }
        return total;
    }

    private void crearNotaFinanciera(Comprobante c, Request request) {
        if (request.formasPago() == null || request.formasPago().isEmpty()) {
            throw new IllegalArgumentException("La nota necesita un monto");
        }
        BigDecimal total = request.formasPago().stream().map(FormaPagoRequest::monto).reduce(BigDecimal.ZERO, BigDecimal::add);
        c.setTotal(total);

        if (request.comprobanteAfectadoId() != null) {
            Comprobante afectado = obtenerOFallar(request.comprobanteAfectadoId());
            c.setComprobanteAfectado(afectado);
        }
        // las NC/ND no generan movimiento de stock ni de formas de pago en esta primera versión —
        // son puramente financieras, se resuelven contablemente cuando exista el módulo de Contabilidad
    }

    @Transactional
    private void crearReciboCobro(Comprobante c, Request request) {
        Comprobante afectado = obtenerComprobanteAfectadoOFallar(request);
        c.setComprobanteAfectado(afectado);

        BigDecimal total = registrarFormasPago(c, request, true);
        c.setTotal(total);

        actualizarEstadoCobro(afectado);
    }

    @Transactional
    private void crearReciboPago(Comprobante c, Request request) {
        Comprobante afectado = obtenerComprobanteAfectadoOFallar(request);
        c.setComprobanteAfectado(afectado);

        BigDecimal total = registrarFormasPago(c, request, false);
        c.setTotal(total);

        actualizarEstadoPago(afectado);
    }

    private Comprobante obtenerComprobanteAfectadoOFallar(Request request) {
        if (request.comprobanteAfectadoId() == null) {
            throw new IllegalArgumentException("Hay que indicar a qué comprobante afecta este recibo");
        }
        return obtenerOFallar(request.comprobanteAfectadoId());
    }

    /**
     * Guarda cada forma de pago del recibo. Si es un cobro (esCobro=true) y la forma es CHEQUE sin chequeId,
     * crea un cheque nuevo en cartera. Si es un pago (esCobro=false) y la forma es CHEQUE, exige chequeId
     * (un cheque que ya está en cartera) y lo marca como entregado.
     */
    private BigDecimal registrarFormasPago(Comprobante recibo, Request request, boolean esCobro) {
        if (request.formasPago() == null || request.formasPago().isEmpty()) {
            throw new IllegalArgumentException("El recibo necesita al menos una forma de pago");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (FormaPagoRequest fpReq : request.formasPago()) {
            FormaPagoComprobante fp = new FormaPagoComprobante();
            fp.setComprobante(recibo);
            fp.setTipo(fpReq.tipo());
            fp.setMonto(fpReq.monto());

            if (fpReq.tipo() == TipoFormaPago.CHEQUE) {
                if (esCobro) {
                    Cheque cheque = chequeService.crear(
                            recibo.getClienteProveedor(), fpReq.monto(), fpReq.chequeNumero(),
                            fpReq.chequeBanco(), fpReq.chequeFechaCobro(), recibo
                    );
                    fp.setCheque(cheque);
                } else {
                    if (fpReq.chequeId() == null) {
                        throw new IllegalArgumentException("Para pagar con cheque hay que elegir uno de la cartera");
                    }
                    Cheque cheque = chequeService.entregar(fpReq.chequeId(), recibo);
                    fp.setCheque(cheque);
                }
            }

            recibo.getItems(); // no-op, solo para dejar claro que un recibo no tiene items de producto
            formaPagoRepository.save(fp);
            total = total.add(fpReq.monto());
        }

        return total;
    }

    private void actualizarEstadoCobro(Comprobante factura) {
        BigDecimal totalCobrado = sumaRecibosNoAnulados(factura.getId());
        if (totalCobrado.compareTo(factura.getTotal()) >= 0) {
            factura.setEstadoCobro(EstadoCobro.COBRADO);
        } else if (totalCobrado.compareTo(BigDecimal.ZERO) > 0) {
            factura.setEstadoCobro(EstadoCobro.PARCIAL);
        } else {
            factura.setEstadoCobro(EstadoCobro.PENDIENTE);
        }
        repository.save(factura);
    }

    private void actualizarEstadoPago(Comprobante factura) {
        BigDecimal totalPagado = sumaRecibosNoAnulados(factura.getId());
        if (totalPagado.compareTo(factura.getTotal()) >= 0) {
            factura.setEstadoPago(EstadoPago.PAGADO);
        } else if (totalPagado.compareTo(BigDecimal.ZERO) > 0) {
            factura.setEstadoPago(EstadoPago.PARCIAL);
        } else {
            factura.setEstadoPago(EstadoPago.RECIBIDO);
        }
        repository.save(factura);
    }

    private BigDecimal sumaRecibosNoAnulados(Long comprobanteAfectadoId) {
        return repository.findByComprobanteAfectadoIdAndEstado(comprobanteAfectadoId, EstadoComprobante.EMITIDO)
                .stream().map(Comprobante::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public Response anular(Long id) {
        Comprobante c = obtenerOFallar(id);
        if (c.getEstado() == EstadoComprobante.ANULADO) {
            throw new IllegalArgumentException("El comprobante ya está anulado");
        }
        c.setEstado(EstadoComprobante.ANULADO);
        repository.save(c);

        // si era un recibo que afectaba a otro comprobante, recalculamos el estado de cobro/pago sin él
        if (c.getComprobanteAfectado() != null) {
            if (c.getDireccion() == DireccionComprobante.VENTA) {
                actualizarEstadoCobro(c.getComprobanteAfectado());
            } else {
                actualizarEstadoPago(c.getComprobanteAfectado());
            }
        }

        return aResponse(c);
    }

    public Response asentar(Long id) {
        Comprobante c = obtenerOFallar(id);
        if (c.getTipo() != TipoComprobante.NOTA_CREDITO && c.getTipo() != TipoComprobante.NOTA_DEBITO) {
            throw new IllegalArgumentException("Solo se pueden asentar Notas de Crédito/Débito");
        }
        if (c.getEstado() != EstadoComprobante.EMITIDO) {
            throw new IllegalArgumentException("Solo se puede asentar un comprobante EMITIDO");
        }
        c.setEstado(EstadoComprobante.ASENTADA);
        return aResponse(repository.save(c));
    }

    public Page<Response> buscar(List<TipoComprobante> tipos, boolean soloAnulados, String busqueda, Pageable pageable) {
        return repository.buscar(tipos, soloAnulados, busqueda, pageable).map(this::aResponse);
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    private Comprobante obtenerOFallar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Comprobante no encontrado"));
    }

    private Response aResponse(Comprobante c) {
        List<ItemResponse> items = c.getItems().stream()
                .map(i -> new ItemResponse(i.getId(), i.getProducto().getId(), i.getProducto().getNombre(), i.getCantidad(), i.getPrecioUnitario(), i.getSubtotal()))
                .toList();

        List<FormaPagoResponse> formasPago = formaPagoRepository.findByComprobanteId(c.getId()).stream()
                .map(fp -> new FormaPagoResponse(fp.getId(), fp.getTipo(), fp.getMonto(),
                        fp.getCheque() != null ? fp.getCheque().getId() : null,
                        fp.getCheque() != null ? fp.getCheque().getNumero() : null))
                .toList();

        RemitoViajeResponse remito = c.getRemitoViaje() != null ? new RemitoViajeResponse(
                c.getRemitoViaje().getId(), c.getRemitoViaje().getNumero(), c.getRemitoViaje().getTransportista(),
                c.getRemitoViaje().getChofer(), c.getRemitoViaje().getPatente(), c.getRemitoViaje().getFecha()
        ) : null;

        return new Response(
                c.getId(), c.getTipo(), c.getDireccion(), c.getOrigen(), c.getNumero(), c.getPuntoVenta(),
                c.getClienteProveedor().getId(), c.getClienteProveedor().getRazonSocial(),
                c.getFechaEmision(), c.getFechaVencimiento(), c.getEstado(), c.getEstadoCobro(), c.getEstadoPago(),
                c.getTotal(), c.getUsuario().getNombre(),
                c.getComprobanteAfectado() != null ? c.getComprobanteAfectado().getId() : null,
                c.getFechaModificacion(), items, remito, formasPago
        );
    }

    public List<Response> pendientesPorCliente(Long clienteId, DireccionComprobante direccion) {
        return repository.findPendientesPorCliente(clienteId, direccion).stream()
                .map(this::aResponse)
                .toList();
    }
}