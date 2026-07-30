package com.fabrixa.backend.comercial.service;

import com.fabrixa.backend.comercial.dto.PrecioDTO.Request;
import com.fabrixa.backend.comercial.dto.PrecioDTO.Response;
import com.fabrixa.backend.comercial.dto.ProductoDTO;
import com.fabrixa.backend.comercial.model.HistorialPrecioInsumo;
import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.HistorialPrecioInsumoRepository;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class PrecioService {

    private final HistorialPrecioInsumoRepository historialRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PrecioService(HistorialPrecioInsumoRepository historialRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.historialRepository = historialRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Response registrarPrecio(Long productoId, Request request, Authentication auth) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        HistorialPrecioInsumo registro = new HistorialPrecioInsumo();
        registro.setProducto(producto);
        registro.setPrecio(request.precio());
        registro.setUsuario(usuario);
        registro.setMotivo(request.motivo());

        historialRepository.save(registro);

        producto.setPrecioActual(request.precio());
        productoRepository.save(producto);

        return aResponse(registro);
    }

    public List<Response> historial(Long productoId) {
        return historialRepository.findByProductoIdOrderByFechaDesc(productoId).stream()
                .map(this::aResponse)
                .toList();
    }

    private Response aResponse(HistorialPrecioInsumo h) {
        return new Response(
                h.getId(), h.getProducto().getId(), h.getProducto().getNombre(),
                h.getPrecio(), h.getFecha(), h.getUsuario().getNombre(), h.getMotivo()
        );
    }

    public Page<Response> listarPaginado(Pageable pageable) {
        return historialRepository.findAll(pageable).map(this::aResponse);
    }
}