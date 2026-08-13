package com.fabrixa.backend.contabilidad.controller;

import com.fabrixa.backend.contabilidad.dto.ImpuestoDTO.Request;
import com.fabrixa.backend.contabilidad.dto.ImpuestoDTO.Response;
import com.fabrixa.backend.contabilidad.model.EstadoImpuesto;
import com.fabrixa.backend.contabilidad.service.ImpuestoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/impuestos")
public class ImpuestoController {

    private final ImpuestoService service;

    public ImpuestoController(ImpuestoService service) {
        this.service = service;
    }

    @GetMapping("/pagina")
    public Page<Response> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaVencimiento") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) EstadoImpuesto estado,
            @RequestParam(defaultValue = "") String busqueda) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        if (sortBy.equals("fechaVencimiento")) {
            sort = sort.and(Sort.by(Sort.Direction.DESC, "fechaModificacion"));
        }

        return service.listarPaginado(estado, busqueda, PageRequest.of(page, size, sort));
    }

    @PostMapping
    public Response crear(@RequestBody Request request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public Response actualizar(@PathVariable Long id, @RequestBody Request request) {
        return service.actualizar(id, request);
    }

    @PutMapping("/{id}/pagar")
    public Response marcarPagado(@PathVariable Long id) {
        return service.marcarPagado(id);
    }
}