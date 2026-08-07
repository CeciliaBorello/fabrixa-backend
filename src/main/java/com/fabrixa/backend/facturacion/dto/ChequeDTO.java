package com.fabrixa.backend.cheques.dto;

import com.fabrixa.backend.cheques.model.EstadoCheque;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ChequeDTO {
    public record Response(
            Long id, String numero, String banco, Long terceroId, String terceroNombre,
            BigDecimal monto, LocalDate fechaEmision, LocalDate fechaCobro,
            EstadoCheque estado, Long reciboIngresoId, Long reciboEgresoId
    ) {}
}