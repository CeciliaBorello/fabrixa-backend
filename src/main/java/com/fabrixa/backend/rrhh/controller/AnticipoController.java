package com.fabrixa.backend.rrhh.controller;

import com.fabrixa.backend.rrhh.dto.AnticipoDTO.Request;
import com.fabrixa.backend.rrhh.dto.AnticipoDTO.Response;
import com.fabrixa.backend.rrhh.service.AnticipoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anticipos")
public class AnticipoController {

    private final AnticipoService service;

    public AnticipoController(AnticipoService service) {
        this.service = service;
    }

    @PostMapping
    public Response crear(@RequestBody Request request, Authentication auth) {
        return service.crear(request, auth);
    }

    @GetMapping("/por-empleado/{empleadoId}")
    public List<Response> porEmpleado(@PathVariable Long empleadoId) {
        return service.porEmpleado(empleadoId);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}