package com.fabrixa.backend.entidades.controller;

import com.fabrixa.backend.entidades.dto.UbicacionDTO.CiudadResponse;
import com.fabrixa.backend.entidades.dto.UbicacionDTO.ProvinciaResponse;
import com.fabrixa.backend.entidades.repository.CiudadRepository;
import com.fabrixa.backend.entidades.repository.ProvinciaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    private final ProvinciaRepository provinciaRepository;
    private final CiudadRepository ciudadRepository;

    public UbicacionController(ProvinciaRepository provinciaRepository, CiudadRepository ciudadRepository) {
        this.provinciaRepository = provinciaRepository;
        this.ciudadRepository = ciudadRepository;
    }

    @GetMapping("/provincias")
    public List<ProvinciaResponse> listarProvincias() {
        return provinciaRepository.findAll().stream()
                .map(p -> new ProvinciaResponse(p.getId(), p.getNombre()))
                .sorted((a, b) -> a.nombre().compareTo(b.nombre()))
                .toList();
    }

    @GetMapping("/ciudades")
    public List<CiudadResponse> listarCiudadesPorProvincia(@RequestParam String provinciaId) {
        return ciudadRepository.findByProvinciaIdOrderByNombre(provinciaId).stream()
                .map(c -> new CiudadResponse(c.getId(), c.getNombre(), c.getProvincia().getId()))
                .toList();
    }
}