package com.fabrixa.backend.comercial.controller;

import com.fabrixa.backend.comercial.dto.ProductoDTO.Request;
import com.fabrixa.backend.comercial.dto.ProductoDTO.Response;
import com.fabrixa.backend.comercial.model.TipoProducto;
import com.fabrixa.backend.comercial.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import org.springframework.data.domain.Sort;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
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
            @RequestParam(defaultValue = "") String busqueda,
            @RequestParam(required = false) String grupo) {

        List<TipoProducto> tipos;
        if ("terminados".equals(grupo)) tipos = List.of(TipoProducto.TERMINADO, TipoProducto.AMBOS);
        else if ("insumos".equals(grupo)) tipos = List.of(TipoProducto.INSUMO, TipoProducto.AMBOS);
        else tipos = List.of(TipoProducto.values());

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return service.buscar(activo, tipos, busqueda, PageRequest.of(page, size, sort));
    }
}