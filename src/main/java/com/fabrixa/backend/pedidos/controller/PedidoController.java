package com.fabrixa.backend.pedidos.controller;

import com.fabrixa.backend.pedidos.dto.PedidoDTO.Request;
import com.fabrixa.backend.pedidos.dto.PedidoDTO.Response;
import com.fabrixa.backend.pedidos.service.PedidoService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
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

    @PutMapping("/{id}/pendiente-entrega")
    public Response marcarPendienteEntrega(@PathVariable Long id) {
        return service.marcarPendienteEntrega(id);
    }

    @PutMapping("/{id}/entregado")
    public Response marcarEntregado(@PathVariable Long id) {
        return service.marcarEntregado(id);
    }

    @PutMapping("/{id}/cancelar")
    public Response cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaModificacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "false") boolean soloCancelados,
            @RequestParam(defaultValue = "") String busqueda) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return service.buscar(soloCancelados, busqueda, PageRequest.of(page, size, sort));
    }
}
