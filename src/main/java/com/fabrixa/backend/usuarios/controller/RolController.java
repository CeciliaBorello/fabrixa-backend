package com.fabrixa.backend.usuarios.controller;

import com.fabrixa.backend.usuarios.dto.RolDTO.RolResponse;
import com.fabrixa.backend.usuarios.repository.RolRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolController {

    private final RolRepository rolRepository;

    public RolController(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @GetMapping
    public List<RolResponse> listar() {
        return rolRepository.findAll().stream()
                .map(r -> new RolResponse(r.getId(), r.getNombre()))
                .toList();
    }
}