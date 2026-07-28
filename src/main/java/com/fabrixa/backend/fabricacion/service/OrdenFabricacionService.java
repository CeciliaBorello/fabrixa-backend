package com.fabrixa.backend.fabricacion.service;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.fabricacion.dto.OrdenFabricacionDTO.*;
import com.fabrixa.backend.fabricacion.model.EstadoOrdenFabricacion;
import com.fabrixa.backend.fabricacion.model.FormulaProducto;
import com.fabrixa.backend.fabricacion.model.LoteProduccion;
import com.fabrixa.backend.fabricacion.model.OrdenFabricacion;
import com.fabrixa.backend.fabricacion.repository.FormulaProductoRepository;
import com.fabrixa.backend.fabricacion.repository.OrdenFabricacionRepository;
import com.fabrixa.backend.stock.model.TipoMovimientoStock;
import com.fabrixa.backend.stock.service.StockService;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fabrixa.backend.comercial.model.ConversorUnidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdenFabricacionService {

    private final OrdenFabricacionRepository repository;
    private final FormulaProductoRepository formulaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final StockService stockService;

    public OrdenFabricacionService(OrdenFabricacionRepository repository,
                                   FormulaProductoRepository formulaRepository,
                                   ProductoRepository productoRepository,
                                   UsuarioRepository usuarioRepository,
                                   StockService stockService) {
        this.repository = repository;
        this.formulaRepository = formulaRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
        this.stockService = stockService;
    }

    public List<Response> listar() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public Response crear(Request request, Authentication auth) {
        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        FormulaProducto formula = formulaRepository.findById(request.formulaId())
                .orElseThrow(() -> new IllegalArgumentException("Fórmula no encontrada"));

        if (!formula.getProductoTerminado().getId().equals(producto.getId())) {
            throw new IllegalArgumentException("La fórmula no corresponde a ese producto");
        }

        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        OrdenFabricacion orden = new OrdenFabricacion();
        orden.setProducto(producto);
        orden.setFormula(formula);
        orden.setCantidadPlanificada(request.cantidadPlanificada());
        orden.setEstado(EstadoOrdenFabricacion.PLANIFICADA);
        orden.setUsuario(usuario);

        return aResponse(repository.save(orden));
    }

    /**
     * Pasa la orden a EN_PROCESO y descuenta del stock todos los insumos de la fórmula,
     * multiplicados por la cantidad planificada. Si no hay stock suficiente de algún insumo,
     * el StockService tira error y ninguna otra parte de la orden se modifica (es una única transacción).
     */
    @Transactional
    public Response iniciarProduccion(Long id) {
        OrdenFabricacion orden = obtenerOFallar(id);

        if (orden.getEstado() != EstadoOrdenFabricacion.PLANIFICADA) {
            throw new IllegalArgumentException("Solo se puede iniciar producción de una orden PLANIFICADA");
        }

        java.math.BigDecimal costoAcumulado = java.math.BigDecimal.ZERO;

        for (var insumoFormula : orden.getFormula().getInsumos()) {
            var cantidadEnUnidadReceta = insumoFormula.getCantidadNecesaria().multiply(orden.getCantidadPlanificada());
            var cantidadADescontar = ConversorUnidades.convertir(
                    cantidadEnUnidadReceta,
                    insumoFormula.getUnidadMedida(),
                    insumoFormula.getInsumo().getUnidadMedida()
            );

            stockService.registrarMovimiento(
                    insumoFormula.getInsumo().getId(),
                    TipoMovimientoStock.EGRESO_FABRICACION_INSUMO,
                    cantidadADescontar,
                    "OrdenFabricacion",
                    orden.getId(),
                    "Consumo de insumo para orden #" + orden.getId()
            );

            var precioInsumo = insumoFormula.getInsumo().getPrecioActual();
            if (precioInsumo != null) {
                costoAcumulado = costoAcumulado.add(cantidadADescontar.multiply(precioInsumo));
            }
            // si el insumo no tiene precio cargado, simplemente no suma al costo — se puede
            // completar el precio después y no bloqueamos la producción por eso
        }

        orden.setEstado(EstadoOrdenFabricacion.EN_PROCESO);
        orden.setFechaInicio(LocalDateTime.now());
        orden.setCostoTotalInsumos(costoAcumulado);

        return aResponse(repository.save(orden));
    }

    @Transactional
    public Response finalizar(Long id, FinalizarRequest request) {
        OrdenFabricacion orden = obtenerOFallar(id);

        if (orden.getEstado() != EstadoOrdenFabricacion.EN_PROCESO) {
            throw new IllegalArgumentException("Solo se puede finalizar una orden EN_PROCESO");
        }

        orden.setCantidadProducida(request.cantidadProducida());
        orden.setEstado(EstadoOrdenFabricacion.FINALIZADA);
        orden.setFechaFin(LocalDateTime.now());

        if (orden.getCostoTotalInsumos() != null && request.cantidadProducida().compareTo(java.math.BigDecimal.ZERO) > 0) {
            orden.setCostoUnitarioProducido(
                    orden.getCostoTotalInsumos().divide(request.cantidadProducida(), 4, java.math.RoundingMode.HALF_UP)
            );
        }

        LoteProduccion lote = new LoteProduccion();
        lote.setOrdenFabricacion(orden);
        lote.setNumeroLote(request.numeroLote());
        lote.setCantidad(request.cantidadProducida());
        lote.setFechaProduccion(LocalDateTime.now());
        if (request.fechaVencimiento() != null && !request.fechaVencimiento().isBlank()) {
            lote.setFechaVencimiento(LocalDate.parse(request.fechaVencimiento()));
        }
        orden.getLotes().add(lote);

        stockService.registrarMovimiento(
                orden.getProducto().getId(),
                TipoMovimientoStock.INGRESO_PRODUCCION,
                request.cantidadProducida(),
                "OrdenFabricacion",
                orden.getId(),
                "Producción de orden #" + orden.getId() + " — lote " + request.numeroLote()
        );

        return aResponse(repository.save(orden));
    }

    public Response cancelar(Long id) {
        OrdenFabricacion orden = obtenerOFallar(id);

        if (orden.getEstado() != EstadoOrdenFabricacion.PLANIFICADA) {
            throw new IllegalArgumentException(
                    "Solo se puede cancelar una orden PLANIFICADA (todavía no consumió insumos). " +
                            "Si ya está en proceso, hay que revertir el stock manualmente con un ajuste.");
        }

        orden.setEstado(EstadoOrdenFabricacion.CANCELADA);
        return aResponse(repository.save(orden));
    }

    private OrdenFabricacion obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de fabricación no encontrada"));
    }

    private Response aResponse(OrdenFabricacion o) {
        return new Response(
                o.getId(), o.getProducto().getId(), o.getProducto().getNombre(),
                o.getFormula().getId(), o.getCantidadPlanificada(), o.getCantidadProducida(),
                o.getEstado(), o.getFechaInicio(), o.getFechaFin(), o.getUsuario().getNombre(),
                o.getCostoTotalInsumos(), o.getCostoUnitarioProducido()
        );
    }
}