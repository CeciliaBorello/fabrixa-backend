package com.fabrixa.backend.facturacion.controller;

import com.fabrixa.backend.facturacion.dto.ComprobanteDTO.Request;
import com.fabrixa.backend.facturacion.dto.ComprobanteDTO.Response;
import com.fabrixa.backend.facturacion.model.DireccionComprobante;
import com.fabrixa.backend.facturacion.model.EstadoComprobante;
import com.fabrixa.backend.facturacion.model.TipoComprobante;
import com.fabrixa.backend.facturacion.service.ComprobanteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/comprobantes")
public class ComprobanteController {

    private final ComprobanteService service;

    public ComprobanteController(ComprobanteService service) {
        this.service = service;
    }

    @GetMapping("/pagina")
    public Page<Response> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaModificacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) List<TipoComprobante> tipos,
            @RequestParam(required = false) EstadoComprobante estado,
            @RequestParam(defaultValue = "") String busqueda) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return service.buscar(tipos, estado, busqueda, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{id}")
    public Response buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/pendientes")
    public List<Response> pendientesPorCliente(
            @RequestParam Long clienteProveedorId,
            @RequestParam DireccionComprobante direccion) {
        return service.pendientesPorCliente(clienteProveedorId, direccion);
    }

    @PostMapping
    public Response crear(@RequestBody Request request, Authentication auth) {
        return service.crear(request, auth);
    }

    @PutMapping("/{id}/anular")
    public Response anular(@PathVariable Long id) {
        return service.anular(id);
    }

    @PutMapping("/{id}/asentar")
    public Response asentar(@PathVariable Long id) {
        return service.asentar(id);
    }

    @PostMapping("/{id}/generar-arca")
    public Response generarArca(@PathVariable Long id) {
        return service.generarArca(id);
    }
}