package com.fabrixa.backend.stock.controller;

import com.fabrixa.backend.stock.dto.StockDTO.AjusteRequest;
import com.fabrixa.backend.stock.dto.StockDTO.MovimientoResponse;
import com.fabrixa.backend.stock.dto.StockDTO.StockActualResponse;
import com.fabrixa.backend.stock.service.StockService;
import org.springframework.web.bind.annotation.*;

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
}