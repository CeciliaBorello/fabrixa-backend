package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteProveedorRepository extends JpaRepository<ClienteProveedor, Long> {
    Optional<ClienteProveedor> findByCuit(String cuit);
}