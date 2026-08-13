package com.fabrixa.backend.rrhh.dto;

import com.fabrixa.backend.rrhh.model.OrigenRegistroHoras;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroHorasDTO {
    public record Request(Long empleadoId, LocalDate fecha, BigDecimal horas) {}
    public record Response(Long id, Long empleadoId, String empleadoNombre, LocalDate fecha,
                           BigDecimal horas, OrigenRegistroHoras origen, boolean liquidado) {}
    public record NoLiquidadasPorEmpleado(Long empleadoId, String empleadoNombre, BigDecimal totalHoras, int cantidadDias) {}
}