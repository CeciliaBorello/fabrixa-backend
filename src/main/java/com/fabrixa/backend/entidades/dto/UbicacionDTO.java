package com.fabrixa.backend.entidades.dto;

public class UbicacionDTO {
    public record ProvinciaResponse(String id, String nombre) {}
    public record CiudadResponse(String id, String nombre, String provinciaId) {}
}