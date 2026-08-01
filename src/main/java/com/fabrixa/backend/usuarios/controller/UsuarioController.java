package com.fabrixa.backend.usuarios.controller;

import com.fabrixa.backend.usuarios.dto.UsuarioDTO.UsuarioRequest;
import com.fabrixa.backend.usuarios.dto.UsuarioDTO.UsuarioResponse;
import com.fabrixa.backend.usuarios.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PostMapping
    public UsuarioResponse crear(@RequestBody UsuarioRequest request) {
        return usuarioService.crear(request);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        return usuarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public void desactivar(@PathVariable Long id) {
        usuarioService.desactivar(id);
    }

    @PutMapping("/{id}/reactivar")
    public void reactivar(@PathVariable Long id) {
        usuarioService.reactivar(id);
    }

    @GetMapping("/pagina")
    public Page<UsuarioResponse> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "fechaModificacion") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "") String busqueda) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        return usuarioService.buscar(activo, busqueda, PageRequest.of(page, size, sort));
    }
}