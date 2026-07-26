package com.fabrixa.backend.pedidos.repository;

import com.fabrixa.backend.pedidos.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}