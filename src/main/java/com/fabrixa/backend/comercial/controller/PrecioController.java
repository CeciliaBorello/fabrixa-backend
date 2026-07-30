package com.fabrixa.backend.comercial.controller;

import com.fabrixa.backend.comercial.dto.PrecioDTO.Request;
import com.fabrixa.backend.comercial.dto.PrecioDTO.Response;
import com.fabrixa.backend.comercial.service.PrecioService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@RestController
@RequestMapping("/api/productos/{productoId}/precios")
public class PrecioController {

    private final PrecioService service;

    public PrecioController(PrecioService service) {
        this.service = service;
    }

    @GetMapping
    public List<Response> historial(@PathVariable Long productoId) {
        return service.historial(productoId);
    }

    @PostMapping
    public Response registrar(@PathVariable Long productoId, @RequestBody Request request, Authentication auth) {
        return service.registrarPrecio(productoId, request, auth);
    }

    @GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.listarPaginado(PageRequest.of(page, size));
    }
}