package com.fabrixa.backend.cuentascorrientes.controller;

import com.fabrixa.backend.cuentascorrientes.dto.CuentaCorrienteDTO.*;
import com.fabrixa.backend.cuentascorrientes.service.CuentaCorrienteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cuentas-corrientes")
public class CuentaCorrienteController {

    private final CuentaCorrienteService service;

    public CuentaCorrienteController(CuentaCorrienteService service) {
        this.service = service;
    }

    @GetMapping("/pagina")
    public Page<FilaResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busqueda) {
        return service.listarPaginado(busqueda, PageRequest.of(page, size));
    }

    @GetMapping("/{clienteId}/saldo")
    public BigDecimal saldo(@PathVariable Long clienteId) {
        return service.calcularSaldo(clienteId);
    }

    @GetMapping("/{clienteId}/movimientos")
    public List<MovimientoResponse> movimientos(@PathVariable Long clienteId) {
        return service.movimientos(clienteId);
    }

    @PostMapping("/ajuste")
    public void crearAjuste(@RequestBody AjusteRequest request, Authentication auth) {
        service.crearAjuste(request, auth);
    }

    @GetMapping("/saldos")
    public Map<Long, BigDecimal> saldos(@RequestParam List<Long> ids) {
        return service.saldosDe(ids);
    }
}