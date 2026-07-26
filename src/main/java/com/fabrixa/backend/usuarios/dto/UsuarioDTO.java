package com.fabrixa.backend.usuarios.dto;

public class UsuarioDTO {

    public record UsuarioRequest(
            String nombre,
            String email,
            String password,   // solo se usa al crear o al cambiar contraseña
            Long rolId
    ) {}

    public record UsuarioResponse(
            Long id,
            String nombre,
            String email,
            String rol,
            boolean activo
    ) {}
}