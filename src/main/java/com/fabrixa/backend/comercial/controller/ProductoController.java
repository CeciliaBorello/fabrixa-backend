package com.fabrixa.backend.comercial.controller;

import com.fabrixa.backend.comercial.dto.ProductoDTO.Request;
import com.fabrixa.backend.comercial.dto.ProductoDTO.Response;
import com.fabrixa.backend.comercial.model.TipoProducto;
import com.fabrixa.backend.comercial.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


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

    /*@GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.listarPaginado(PageRequest.of(page, size));
    }*/

    @GetMapping("/pagina")
    public Page<Response> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String grupo) {

        List<TipoProducto> tipos;
        if ("terminados".equals(grupo)) {
            tipos = List.of(TipoProducto.TERMINADO, TipoProducto.AMBOS);
        } else if ("insumos".equals(grupo)) {
            tipos = List.of(TipoProducto.INSUMO, TipoProducto.AMBOS);
        } else {
            tipos = List.of(TipoProducto.values());
        }

        return service.listarPaginadoPorTipos(tipos, PageRequest.of(page, size));
    }
}