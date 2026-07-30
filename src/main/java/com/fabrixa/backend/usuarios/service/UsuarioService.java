package com.fabrixa.backend.usuarios.service;

import com.fabrixa.backend.usuarios.dto.UsuarioDTO.UsuarioRequest;
import com.fabrixa.backend.usuarios.dto.UsuarioDTO.UsuarioResponse;
import com.fabrixa.backend.usuarios.model.Rol;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.RolRepository;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(this::aResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setRol(rol);
        usuario.setActivo(true);

        return aResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = obtenerOFallar(id);

        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());

        if (request.rolId() != null) {
            Rol rol = rolRepository.findById(request.rolId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
            usuario.setRol(rol);
        }

        // La contraseña solo se cambia si mandaron una nueva
        if (request.password() != null && !request.password().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return aResponse(usuarioRepository.save(usuario));
    }

    public void desactivar(Long id) {
        Usuario usuario = obtenerOFallar(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public void reactivar(Long id) {
        Usuario usuario = obtenerOFallar(id);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    private Usuario obtenerOFallar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    private UsuarioResponse aResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol().getNombre(), u.isActivo());
    }

    public Page<UsuarioResponse> listarPaginado(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(this::aResponse);
    }
}