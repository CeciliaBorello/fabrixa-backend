package com.fabrixa.backend.comercial.dto;

import com.fabrixa.backend.comercial.model.CondicionIva;
import com.fabrixa.backend.comercial.model.TipoClienteProveedor;

import java.math.BigDecimal;

public class ClienteProveedorDTO {

    public record Request(
            TipoClienteProveedor tipo,
            String razonSocial,
            String cuit,
            CondicionIva condicionIva,
            String direccion,
            String provinciaId,
            String ciudadId,
            String telefono,
            String email,
            Long listaPrecioId
    ) {}

    public record Response(
            Long id,
            TipoClienteProveedor tipo,
            String razonSocial,
            String cuit,
            CondicionIva condicionIva,
            String direccion,
            String provinciaId,
            String provinciaNombre,
            String ciudadId,
            String ciudadNombre,
            String telefono,
            String email,
            Long listaPrecioId,
            BigDecimal saldoCuentaCorriente,
            boolean activo
    ) {}
}