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
        String nombreFinal = resolverNombre(request);

        if (repository.existsByNombreIgnoreCase(nombreFinal)) {
            throw new IllegalArgumentException("Ya existe un producto con ese nombre");
        }

        Producto producto = new Producto();
        aplicarDatos(producto, request, nombreFinal);
        producto.setActivo(true);
        return aResponse(repository.save(producto));
    }

    public Response actualizar(Long id, Request request) {
        Producto producto = obtenerOFallar(id);
        String nombreFinal = resolverNombre(request);

        if (repository.existsByNombreIgnoreCaseAndIdNot(nombreFinal, id)) {
            throw new IllegalArgumentException("Ya existe otro producto con ese nombre");
        }

        aplicarDatos(producto, request, nombreFinal);
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

    public List<Response> listarProductosBase() {
        return repository.findByProductoBaseIsNullAndActivoTrueOrderByNombre().stream()
                .map(this::aResponse)
                .toList();
    }

    public List<Response> listarPresentaciones(Long productoBaseId) {
        return repository.findByProductoBaseIdAndActivoTrueOrderByPresentacion(productoBaseId).stream()
                .map(this::aResponse)
                .toList();
    }

    private Producto obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
    }

    /**
     * Si el request trae un productoBaseId, el nombre se arma solo: "<nombre del base> <presentación>".
     * Si no, se usa el nombre que vino tal cual en el request (producto raíz).
     */
    private String resolverNombre(Request request) {
        if (request.productoBaseId() == null) {
            if (request.nombre() == null || request.nombre().isBlank()) {
                throw new IllegalArgumentException("El nombre es obligatorio");
            }
            return request.nombre().trim();
        }

        Producto base = repository.findById(request.productoBaseId())
                .orElseThrow(() -> new IllegalArgumentException("Producto base no encontrado"));

        String presentacion = request.presentacion() != null ? request.presentacion().trim() : "";
        return presentacion.isEmpty() ? base.getNombre() : base.getNombre() + " " + presentacion;
    }

    private void aplicarDatos(Producto producto, Request request, String nombreFinal) {
        producto.setNombre(nombreFinal);
        producto.setTipo(request.tipo());
        producto.setCodigoBarra(request.codigoBarra());
        producto.setRnpa(request.rnpa());
        producto.setValorNutricional(request.valorNutricional());
        producto.setUnidadMedida(request.unidadMedida());
        producto.setCategoria(request.categoria());
        producto.setPresentacion(request.presentacion());

        if (request.productoBaseId() != null) {
            Producto base = repository.findById(request.productoBaseId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto base no encontrado"));
            producto.setProductoBase(base);
        } else {
            producto.setProductoBase(null);
        }
    }

    private Response aResponse(Producto p) {
        return new Response(
                p.getId(), p.getNombre(), p.getTipo(), p.getCodigoBarra(), p.getRnpa(),
                p.getValorNutricional(), p.getUnidadMedida(), p.getCategoria(),
                p.getPrecioActual(), p.isActivo(),
                p.getProductoBase() != null ? p.getProductoBase().getId() : null,
                p.getProductoBase() != null ? p.getProductoBase().getNombre() : null,
                p.getPresentacion()
        );
    }
}