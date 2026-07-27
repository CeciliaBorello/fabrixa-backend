package com.fabrixa.backend.comercial.service;

import com.fabrixa.backend.comercial.dto.ProductoDTO.Request;
import com.fabrixa.backend.comercial.dto.ProductoDTO.Response;
import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Response> listar() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public Response crear(Request request) {
        Producto producto = new Producto();
        aplicarDatos(producto, request);
        producto.setActivo(true);
        return aResponse(repository.save(producto));
    }

    public Response actualizar(Long id, Request request) {
        Producto producto = obtenerOFallar(id);
        aplicarDatos(producto, request);
        return aResponse(repository.save(producto));
    }

    public void desactivar(Long id) {
        Producto producto = obtenerOFallar(id);
        producto.setActivo(false);
        repository.save(producto);
    }

    public void reactivar(Long id) {
        Producto producto = obtenerOFallar(id);
        producto.setActivo(true);
        repository.save(producto);
    }


    private Producto obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    private void aplicarDatos(Producto producto, Request request) {
        producto.setNombre(request.nombre());
        producto.setTipo(request.tipo());
        producto.setCodigoBarra(request.codigoBarra());
        producto.setRnpa(request.rnpa());
        producto.setValorNutricional(request.valorNutricional());
        producto.setUnidadMedida(request.unidadMedida());
        producto.setCategoria(request.categoria());
    }

    private Response aResponse(Producto p) {
        return new Response(
                p.getId(), p.getNombre(), p.getTipo(), p.getCodigoBarra(), p.getRnpa(),
                p.getValorNutricional(), p.getUnidadMedida(), p.getCategoria(), p.isActivo()
        );
    }
}