package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}