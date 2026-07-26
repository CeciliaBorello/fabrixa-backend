package com.fabrixa.backend.comercial.controller;

import com.fabrixa.backend.comercial.dto.ClienteProveedorDTO.Request;
import com.fabrixa.backend.comercial.dto.ClienteProveedorDTO.Response;
import com.fabrixa.backend.comercial.service.ClienteProveedorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes-proveedores")
public class ClienteProveedorController {

    private final ClienteProveedorService service;

    public ClienteProveedorController(ClienteProveedorService service) {
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
    public Response crear(@RequestBody Request request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public Response actualizar(@PathVariable Long id, @RequestBody Request request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void desactivar(@PathVariable Long id) {
        service.desactivar(id);
    }

    @PutMapping("/{id}/reactivar")
    public void reactivar(@PathVariable Long id) {
        service.reactivar(id);
    }
}