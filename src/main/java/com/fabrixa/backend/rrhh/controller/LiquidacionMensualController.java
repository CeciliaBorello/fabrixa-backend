package com.fabrixa.backend.rrhh.controller;

import com.fabrixa.backend.rrhh.dto.LiquidacionMensualDTO.Request;
import com.fabrixa.backend.rrhh.dto.LiquidacionMensualDTO.Response;
import com.fabrixa.backend.rrhh.service.LiquidacionMensualService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/liquidaciones")
public class LiquidacionMensualController {

    private final LiquidacionMensualService service;

    public LiquidacionMensualController(LiquidacionMensualService service) {
        this.service = service;
    }

    @PostMapping
    public Response generar(@RequestBody Request request, Authentication auth) {
        return service.generar(request, auth);
    }

    @GetMapping("/pagina")
    public Page<Response> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busqueda) {
        return service.listarPaginado(busqueda, PageRequest.of(page, size));
    }
}