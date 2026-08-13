package com.fabrixa.backend.contabilidad.service;

import com.fabrixa.backend.contabilidad.dto.CuentaContableDTO.Request;
import com.fabrixa.backend.contabilidad.dto.CuentaContableDTO.Response;
import com.fabrixa.backend.contabilidad.model.CuentaContable;
import com.fabrixa.backend.contabilidad.repository.CuentaContableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuentaContableService {

    private final CuentaContableRepository repository;

    public CuentaContableService(CuentaContableRepository repository) {
        this.repository = repository;
    }

    public List<Response> listar(boolean activo, String busqueda) {
        return repository.buscar(activo, busqueda).stream().map(this::aResponse).toList();
    }

    public Response crear(Request request) {
        if (repository.existsByCodigoIgnoreCase(request.codigo())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese código");
        }
        CuentaContable cuenta = new CuentaContable();
        aplicarDatos(cuenta, request);
        cuenta.setActivo(true);
        return aResponse(repository.save(cuenta));
    }

    public Response actualizar(Long id, Request request) {
        CuentaContable cuenta = obtenerOFallar(id);
        if (repository.existsByCodigoIgnoreCaseAndIdNot(request.codigo(), id)) {
            throw new IllegalArgumentException("Ya existe otra cuenta con ese código");
        }
        aplicarDatos(cuenta, request);
        return aResponse(repository.save(cuenta));
    }

    public void desactivar(Long id) {
        CuentaContable cuenta = obtenerOFallar(id);
        cuenta.setActivo(false);
        repository.save(cuenta);
    }

    public void reactivar(Long id) {
        CuentaContable cuenta = obtenerOFallar(id);
        cuenta.setActivo(true);
        repository.save(cuenta);
    }

    private void aplicarDatos(CuentaContable cuenta, Request request) {
        cuenta.setCodigo(request.codigo());
        cuenta.setNombre(request.nombre());
        cuenta.setTipo(request.tipo());
        if (request.cuentaPadreId() != null) {
            cuenta.setCuentaPadre(obtenerOFallar(request.cuentaPadreId()));
        } else {
            cuenta.setCuentaPadre(null);
        }
    }

    private CuentaContable obtenerOFallar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cuenta contable no encontrada"));
    }

    private Response aResponse(CuentaContable c) {
        return new Response(
                c.getId(), c.getCodigo(), c.getNombre(), c.getTipo(),
                c.getCuentaPadre() != null ? c.getCuentaPadre().getId() : null,
                c.getCuentaPadre() != null ? c.getCuentaPadre().getNombre() : null,
                c.isActivo()
        );
    }
}