package com.fabrixa.backend.fabricacion.controller;

import com.fabrixa.backend.fabricacion.dto.OrdenFabricacionDTO.*;
import com.fabrixa.backend.fabricacion.service.OrdenFabricacionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/ordenes-fabricacion")
public class OrdenFabricacionController {

    private final OrdenFabricacionService service;

    public OrdenFabricacionController(OrdenFabricacionService service) {
        this.service = service;
    }

    @GetMapping
    public List<Response> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Response buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Response crear(@RequestBody Request request, Authentication auth) {
        return service.crear(request, auth);
    }

    @PutMapping("/{id}/iniciar")
    public Response iniciarProduccion(@PathVariable Long id) {
        return service.iniciarProduccion(id);
    }

    @PutMapping("/{id}/finalizar")
    public Response finalizar(@PathVariable Long id, @RequestBody FinalizarRequest request) {
        return service.finalizar(id, request);
    }

    @PutMapping("/{id}/cancelar")
    public Response cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaModificacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "false") boolean soloCanceladas,
            @RequestParam(defaultValue = "") String busqueda) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return service.buscar(soloCanceladas, busqueda, PageRequest.of(page, size, sort));
    }

    @GetMapping("/por-producto/{productoId}")
    public List<Response> historialPorProducto(@PathVariable Long productoId) {
        return service.historialPorProducto(productoId);
    }

    @GetMapping("/ultimo-costo")
    public Map<Long, BigDecimal> ultimoCostoPorProductos(@RequestParam List<Long> ids) {
        return service.ultimoCostoPorProductos(ids);
    }
}