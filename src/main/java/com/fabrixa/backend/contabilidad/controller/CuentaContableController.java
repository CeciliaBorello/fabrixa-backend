package com.fabrixa.backend.contabilidad.controller;

import com.fabrixa.backend.contabilidad.dto.CuentaContableDTO.Request;
import com.fabrixa.backend.contabilidad.dto.CuentaContableDTO.Response;
import com.fabrixa.backend.contabilidad.service.CuentaContableService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas-contables")
public class CuentaContableController {

    private final CuentaContableService service;

    public CuentaContableController(CuentaContableService service) {
        this.service = service;
    }

    @GetMapping
    public List<Response> listar(
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "") String busqueda) {
        return service.listar(activo, busqueda);
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