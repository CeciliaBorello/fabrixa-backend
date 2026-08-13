package com.fabrixa.backend.contabilidad.dto;

import com.fabrixa.backend.contabilidad.model.TipoCuentaContable;

public class CuentaContableDTO {
    public record Request(String codigo, String nombre, TipoCuentaContable tipo, Long cuentaPadreId) {}
    public record Response(Long id, String codigo, String nombre, TipoCuentaContable tipo,
                           Long cuentaPadreId, String cuentaPadreNombre, boolean activo) {}
}