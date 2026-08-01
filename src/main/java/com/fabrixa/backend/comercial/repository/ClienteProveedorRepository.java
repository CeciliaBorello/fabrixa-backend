package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.ClienteProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClienteProveedorRepository extends JpaRepository<ClienteProveedor, Long> {
    Optional<ClienteProveedor> findByCuit(String cuit);

    @Query("SELECT c FROM ClienteProveedor c WHERE c.activo = :activo AND " +
            "(LOWER(c.razonSocial) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(c.cuit) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<ClienteProveedor> buscar(@Param("activo") boolean activo, @Param("busqueda") String busqueda, Pageable pageable);
}