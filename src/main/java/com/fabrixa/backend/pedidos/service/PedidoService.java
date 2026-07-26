package com.fabrixa.backend.pedidos.service;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.ClienteProveedorRepository;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.pedidos.dto.PedidoDTO.*;
import com.fabrixa.backend.pedidos.model.EstadoPedido;
import com.fabrixa.backend.pedidos.model.ItemPedido;
import com.fabrixa.backend.pedidos.model.Pedido;
import com.fabrixa.backend.pedidos.repository.PedidoRepository;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteProveedorRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteProveedorRepository clienteRepository,
                         ProductoRepository productoRepository,
                         UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Response> listar() {
        return pedidoRepository.findAll().stream().map(this::aResponse).toList();
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public Response crear(Request request, Authentication auth) {
        ClienteProveedor cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("El pedido necesita al menos un ítem");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.NUEVO);
        pedido.setFechaPedido(LocalDateTime.now());

        for (ItemRequest itemReq : request.items()) {
            Producto producto = productoRepository.findById(itemReq.productoId())
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + itemReq.productoId()));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProducto(producto);
            item.setCantidad(itemReq.cantidad());
            pedido.getItems().add(item);
        }

        return aResponse(pedidoRepository.save(pedido));
    }

    public Response marcarPendienteEntrega(Long id) {
        Pedido pedido = obtenerOFallar(id);
        validarTransicion(pedido.getEstado(), EstadoPedido.PENDIENTE_ENTREGA);
        pedido.setEstado(EstadoPedido.PENDIENTE_ENTREGA);
        return aResponse(pedidoRepository.save(pedido));
    }

    public Response marcarEntregado(Long id) {
        Pedido pedido = obtenerOFallar(id);
        validarTransicion(pedido.getEstado(), EstadoPedido.ENTREGADO);
        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedido.setFechaEntrega(LocalDateTime.now());
        return aResponse(pedidoRepository.save(pedido));
    }

    public Response cancelar(Long id) {
        Pedido pedido = obtenerOFallar(id);
        validarTransicion(pedido.getEstado(), EstadoPedido.CANCELADO);
        pedido.setEstado(EstadoPedido.CANCELADO);
        return aResponse(pedidoRepository.save(pedido));
    }

    private void validarTransicion(EstadoPedido actual, EstadoPedido nuevo) {
        boolean valido = switch (actual) {
            case NUEVO -> nuevo == EstadoPedido.PENDIENTE_ENTREGA || nuevo == EstadoPedido.CANCELADO;
            case PENDIENTE_ENTREGA -> nuevo == EstadoPedido.ENTREGADO || nuevo == EstadoPedido.CANCELADO;
            case ENTREGADO, CANCELADO -> false; // estados finales, no se puede salir de ahí
        };

        if (!valido) {
            throw new IllegalArgumentException(
                    "No se puede pasar de " + actual + " a " + nuevo);
        }
    }

    private Pedido obtenerOFallar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
    }

    private Response aResponse(Pedido p) {
        List<ItemResponse> items = p.getItems().stream()
                .map(i -> new ItemResponse(i.getId(), i.getProducto().getId(), i.getProducto().getNombre(), i.getCantidad()))
                .toList();

        return new Response(
                p.getId(),
                p.getCliente().getId(),
                p.getCliente().getRazonSocial(),
                p.getUsuario().getNombre(),
                p.getEstado(),
                p.getFechaPedido(),
                p.getFechaEntrega(),
                items
        );
    }
}