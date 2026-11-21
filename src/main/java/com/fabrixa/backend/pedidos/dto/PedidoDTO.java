package com.fabrixa.backend.pedidos.dto;

import com.fabrixa.backend.pedidos.model.EstadoPedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {

    public record ItemRequest(Long productoId, BigDecimal cantidad) {}

    public record Request(Long clienteId, List<ItemRequest> items) {}

    public record ItemResponse(Long id, Long productoId, String productoNombre, BigDecimal cantidad) {}

    public record Response(
            Long id,
            Long clienteId,
            String clienteNombre,
            String usuarioNombre,
            EstadoPedido estado,
            LocalDateTime fechaPedido,
            LocalDateTime fechaModificacion,
            LocalDateTime fechaEntrega,
            List<ItemResponse> items
    ) {}
}
