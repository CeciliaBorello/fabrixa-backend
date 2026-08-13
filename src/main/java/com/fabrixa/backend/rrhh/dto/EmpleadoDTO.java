package com.fabrixa.backend.rrhh.dto;

import com.fabrixa.backend.rrhh.model.TipoRemuneracion;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EmpleadoDTO {

    public record Request(
            String nombre,
            String dni,
            TipoRemuneracion tipoRemuneracion,
            BigDecimal valorHora,
            BigDecimal sueldoFijo,
            String direccion,
            String telefono,
            String email,
            LocalDate fechaNacimiento,
            LocalDate fechaIngreso,
            String puesto,
            String contactoEmergenciaNombre,
            String contactoEmergenciaTelefono,
            String contactoEmergenciaVinculo,
            String obraSocial,
            String observaciones
    ) {}

    public record Response(
            Long id, String nombre, String dni,
            TipoRemuneracion tipoRemuneracion,
            BigDecimal valorHora,
            BigDecimal sueldoFijo,
            String direccion, String telefono, String email,
            LocalDate fechaNacimiento, LocalDate fechaIngreso, String puesto,
            String contactoEmergenciaNombre, String contactoEmergenciaTelefono, String contactoEmergenciaVinculo,
            String obraSocial, String observaciones, boolean activo
    ) {}
}