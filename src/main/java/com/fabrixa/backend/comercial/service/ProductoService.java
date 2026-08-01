package com.fabrixa.backend.comercial.service;

import com.fabrixa.backend.comercial.dto.ProductoDTO.Request;
import com.fabrixa.backend.comercial.dto.ProductoDTO.Response;
import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.model.TipoProducto;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    public Page<Response> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(this::aResponse);
    }

    public Page<Response> listarPaginadoPorTipos(List<TipoProducto> tipos, Pageable pageable) {
        return repository.findByActivoTrueAndTipoIn(tipos, pageable).map(this::aResponse);
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public Page<Response> buscar(boolean activo, List<TipoProducto> tipos, String busqueda, Pageable pageable) {
        return repository.buscar(activo, tipos, busqueda, pageable).map(this::aResponse);
    }

    public Response crear(Request request) {
        if (repository.existsByNombreIgnoreCase(request.nombre())) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }
        Producto producto = new Producto();
        aplicarDatos(producto, request);
        producto.setActivo(true);
        return aResponse(repository.save(producto));
    }

    public Response actualizar(Long id, Request request) {
        Producto producto = obtenerOFallar(id);
        if (repository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre");
        }
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
                p.getValorNutricional(), p.getUnidadMedida(), p.getCategoria(),
                p.getPrecioActual(), p.isActivo(),
                p.getPresentacion()
        );
    }

    public List<Response> listarProductosBase() {
        return repository.findByProductoBaseIsNullAndActivoTrueOrderByNombre().stream()
                .map(this::aResponse)
                .toList();
    }
}