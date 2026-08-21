package com.fabrixa.backend.comercial.controller;

import com.fabrixa.backend.comercial.dto.ClienteProveedorDTO.Request;
import com.fabrixa.backend.comercial.dto.ClienteProveedorDTO.Response;
import com.fabrixa.backend.comercial.service.ClienteProveedorService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/clientes-proveedores")
public class ClienteProveedorController {

    private final ClienteProveedorService service;

    // Campos reales de la entidad ClienteProveedor por los que sí se puede
    // ordenar en el backend. "saldo" queda afuera a propósito: el saldo que
    // se ve en pantalla se calcula en vivo desde Cuentas Corrientes
    // (comprobantes + ajustes), no vive en esta entidad -- ordenar por
    // c.saldoCuentaCorriente daría un resultado desactualizado e incorrecto,
    // no simplemente "raro".
    private static final Set<String> CAMPOS_ORDENABLES = Set.of(
            "razonSocial", "cuit", "condicionIva", "fechaModificacion", "tipo", "activo"
    );

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

    @GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaModificacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "") String busqueda) {

        String campoOrden = CAMPOS_ORDENABLES.contains(sortBy) ? sortBy : "fechaModificacion";

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), campoOrden);
        return service.buscar(activo, busqueda, PageRequest.of(page, size, sort));
    }
}