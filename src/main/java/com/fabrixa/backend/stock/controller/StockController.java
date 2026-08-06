package com.fabrixa.backend.stock.controller;

import com.fabrixa.backend.stock.dto.StockDTO;
import com.fabrixa.backend.stock.dto.StockDTO.AjusteRequest;
import com.fabrixa.backend.stock.dto.StockDTO.MovimientoResponse;
import com.fabrixa.backend.stock.dto.StockDTO.StockActualResponse;
import com.fabrixa.backend.stock.service.StockService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService service;

    public StockController(StockService service) {
        this.service = service;
    }

    @GetMapping
    public List<StockActualResponse> listar() {
        return service.listarStockActual();
    }

    @GetMapping("/{productoId}/movimientos")
    public List<MovimientoResponse> historial(@PathVariable Long productoId) {
        return service.historialProducto(productoId);
    }

    @PostMapping("/ajuste")
    public MovimientoResponse ajustar(@RequestBody AjusteRequest request) {
        return service.ajustar(request);
    }

    @GetMapping("/pagina")
    public Page<StockDTO.FilaResponse> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "") String busqueda,
            @RequestParam(defaultValue = "venta") String grupo) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return service.listarPaginado(grupo, activo, busqueda, PageRequest.of(page, size, sort));
    }

    @GetMapping("/presentaciones/{productoBaseId}")
    public List<StockDTO.FilaResponse> presentacionesConStock(@PathVariable Long productoBaseId) {
        return service.presentacionesConStock(productoBaseId);
    }
}