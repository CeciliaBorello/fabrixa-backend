package com.fabrixa.backend.pedidos.repository;

import com.fabrixa.backend.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}