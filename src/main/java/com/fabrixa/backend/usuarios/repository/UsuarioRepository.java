package com.fabrixa.backend.usuarios.repository;

import com.fabrixa.backend.usuarios.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.activo = :activo AND " +
            "(LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Usuario> buscar(@Param("activo") boolean activo, @Param("busqueda") String busqueda, Pageable pageable);
}