package com.fabrixa.backend.fabricacion.controller;

import com.fabrixa.backend.fabricacion.dto.FormulaDTO.Request;
import com.fabrixa.backend.fabricacion.dto.FormulaDTO.Response;
import com.fabrixa.backend.fabricacion.service.FormulaProductoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/formulas")
public class FormulaProductoController {

    private final FormulaProductoService service;

    public FormulaProductoController(FormulaProductoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Response> listar(@RequestParam(required = false) Long productoId) {
        return productoId != null ? service.listarPorProducto(productoId) : service.listar();
    }

    @GetMapping("/{id}")
    public Response buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public Response crear(@RequestBody Request request) {
        return service.crear(request);
    }

    @DeleteMapping("/{id}")
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }

    @PutMapping("/{id}/reactivar")
    public void reactivar(@PathVariable Long id) {
        service.reactivar(id);
    }

    @GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaModificacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "") String busqueda) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return service.buscar(activo, busqueda, PageRequest.of(page, size, sort));
    }
}