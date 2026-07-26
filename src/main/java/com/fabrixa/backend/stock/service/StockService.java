package com.fabrixa.backend.stock.service;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.stock.dto.StockDTO.AjusteRequest;
import com.fabrixa.backend.stock.dto.StockDTO.MovimientoResponse;
import com.fabrixa.backend.stock.dto.StockDTO.StockActualResponse;
import com.fabrixa.backend.stock.model.StockActual;
import com.fabrixa.backend.stock.model.StockMovimiento;
import com.fabrixa.backend.stock.model.TipoMovimientoStock;
import com.fabrixa.backend.stock.repository.StockActualRepository;
import com.fabrixa.backend.stock.repository.StockMovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StockService {

    private final StockActualRepository stockActualRepository;
    private final StockMovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;

    public StockService(StockActualRepository stockActualRepository,
                        StockMovimientoRepository movimientoRepository,
                        ProductoRepository productoRepository) {
        this.stockActualRepository = stockActualRepository;
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Punto único de entrada para modificar stock. Cualquier módulo (Fabricación, Facturación, Granos)
     * llama a este método en vez de tocar StockActual directamente.
     *
     * @param cantidad siempre positiva, salvo para AJUSTE donde puede ser negativa (corrección manual)
     */
    @Transactional
    public MovimientoResponse registrarMovimiento(Long productoId, TipoMovimientoStock tipo,
                                                  BigDecimal cantidad, String referenciaTipo,
                                                  Long referenciaId, String motivo) {

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        StockActual stockActual = stockActualRepository.findById(productoId)
                .orElseGet(() -> {
                    StockActual nuevo = new StockActual();
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(BigDecimal.ZERO);
                    return nuevo;
                });

        BigDecimal delta = calcularDelta(tipo, cantidad);
        BigDecimal nuevaCantidad = stockActual.getCantidad().add(delta);

        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Stock insuficiente de " + producto.getNombre() +
                            " (disponible: " + stockActual.getCantidad() + ", se intentó descontar: " + cantidad.abs() + ")");
        }

        stockActual.setCantidad(nuevaCantidad);
        stockActualRepository.save(stockActual);

        StockMovimiento movimiento = new StockMovimiento();
        movimiento.setProducto(producto);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimiento.setReferenciaTipo(referenciaTipo);
        movimiento.setReferenciaId(referenciaId);
        movimiento.setMotivo(motivo);

        return aMovimientoResponse(movimientoRepository.save(movimiento));
    }

    public MovimientoResponse ajustar(AjusteRequest request) {
        return registrarMovimiento(
                request.productoId(),
                TipoMovimientoStock.AJUSTE,
                request.delta(), // acá sí puede venir negativo
                "AjusteManual",
                null,
                request.motivo()
        );
    }

    public List<StockActualResponse> listarStockActual() {
        return stockActualRepository.findAll().stream().map(this::aStockActualResponse).toList();
    }

    public List<MovimientoResponse> historialProducto(Long productoId) {
        return movimientoRepository.findByProductoIdOrderByFechaDesc(productoId).stream()
                .map(this::aMovimientoResponse)
                .toList();
    }

    private BigDecimal calcularDelta(TipoMovimientoStock tipo, BigDecimal cantidad) {
        if (tipo == TipoMovimientoStock.AJUSTE) {
            return cantidad; // el ajuste ya viene con signo desde quien lo pide
        }
        if (tipo.esIngreso()) {
            return cantidad.abs();
        }
        if (tipo.esEgreso()) {
            return cantidad.abs().negate();
        }
        throw new IllegalStateException("Tipo de movimiento no contemplado: " + tipo);
    }

    private StockActualResponse aStockActualResponse(StockActual s) {
        return new StockActualResponse(s.getProductoId(), s.getProducto().getNombre(), s.getCantidad());
    }

    private MovimientoResponse aMovimientoResponse(StockMovimiento m) {
        return new MovimientoResponse(
                m.getId(), m.getProducto().getId(), m.getProducto().getNombre(),
                m.getTipo(), m.getCantidad(), m.getFecha(), m.getReferenciaTipo(), m.getReferenciaId(), m.getMotivo()
        );
    }
}