package com.fabrixa.backend.facturacion.controller;

import com.fabrixa.backend.cheques.dto.ChequeDTO.Response;
import com.fabrixa.backend.cheques.model.EstadoCheque;
import com.fabrixa.backend.facturacion.service.ChequeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cheques")
public class ChequeController {

    private final ChequeService service;

    public ChequeController(ChequeService service) {
        this.service = service;
    }

    @GetMapping("/pagina")
    public Page<Response> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "EN_CARTERA") EstadoCheque estado,
            @RequestParam(defaultValue = "") String busqueda) {
        return service.listarPorEstado(estado, busqueda, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Response buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}/cobrar")
    public Response cobrar(@PathVariable Long id) {
        return service.cobrar(id);
    }

    @PutMapping("/{id}/rechazar")
    public Response rechazar(@PathVariable Long id) {
        return service.rechazar(id);
    }
}