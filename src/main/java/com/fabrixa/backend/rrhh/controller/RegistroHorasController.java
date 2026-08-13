package com.fabrixa.backend.rrhh.controller;

import com.fabrixa.backend.rrhh.dto.RegistroHorasDTO.NoLiquidadasPorEmpleado;
import com.fabrixa.backend.rrhh.dto.RegistroHorasDTO.Request;
import com.fabrixa.backend.rrhh.dto.RegistroHorasDTO.Response;
import com.fabrixa.backend.rrhh.service.RegistroHorasService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registros-horas")
public class RegistroHorasController {

    private final RegistroHorasService service;

    public RegistroHorasController(RegistroHorasService service) {
        this.service = service;
    }

    @PostMapping
    public Response crear(@RequestBody Request request) {
        return service.crear(request);
    }

    @GetMapping("/por-empleado/{empleadoId}")
    public List<Response> porEmpleado(@PathVariable Long empleadoId) {
        return service.porEmpleado(empleadoId);
    }

    @GetMapping("/no-liquidadas")
    public List<NoLiquidadasPorEmpleado> noLiquidadas() {
        return service.noLiquidadasAgrupadas();
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}