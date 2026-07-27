package com.fabrixa.backend.fabricacion.controller;

import com.fabrixa.backend.fabricacion.dto.OrdenFabricacionDTO.*;
import com.fabrixa.backend.fabricacion.service.OrdenFabricacionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}