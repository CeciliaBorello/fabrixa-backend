package com.fabrixa.backend.rrhh.service;

import com.fabrixa.backend.rrhh.dto.EmpleadoDTO.Request;
import com.fabrixa.backend.rrhh.dto.EmpleadoDTO.Response;
import com.fabrixa.backend.rrhh.model.Empleado;
import com.fabrixa.backend.rrhh.model.TipoRemuneracion;
import com.fabrixa.backend.rrhh.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public List<Response> listar(boolean activo, String busqueda) {
        return repository.buscar(activo, busqueda).stream().map(this::aResponse).toList();
    }

    public Response crear(Request request) {
        if (repository.existsByDni(request.dni())) {
            throw new IllegalArgumentException("Ya existe un empleado con ese DNI");
        }
        validarRemuneracion(request);
        Empleado empleado = new Empleado();
        aplicarDatos(empleado, request);
        empleado.setActivo(true);
        return aResponse(repository.save(empleado));
    }

    public Response actualizar(Long id, Request request) {
        Empleado empleado = obtenerOFallar(id);
        if (repository.existsByDniAndIdNot(request.dni(), id)) {
            throw new IllegalArgumentException("Ya existe otro empleado con ese DNI");
        }
        validarRemuneracion(request);
        aplicarDatos(empleado, request);
        return aResponse(repository.save(empleado));
    }

    private void validarRemuneracion(Request request) {
        if (request.tipoRemuneracion() == TipoRemuneracion.POR_HORA && request.valorHora() == null) {
            throw new IllegalArgumentException("Falta el valor hora para un empleado por hora");
        }
        if (request.tipoRemuneracion() == TipoRemuneracion.SUELDO_FIJO && request.sueldoFijo() == null) {
            throw new IllegalArgumentException("Falta el sueldo fijo para un empleado a sueldo fijo");
        }
    }

    private void aplicarDatos(Empleado e, Request request) {
        e.setNombre(request.nombre());
        e.setDni(request.dni());
        e.setTipoRemuneracion(request.tipoRemuneracion());
        // limpia el campo que no corresponde para no dejar basura de un cambio de tipo previo
        e.setValorHora(request.tipoRemuneracion() == TipoRemuneracion.POR_HORA ? request.valorHora() : null);
        e.setSueldoFijo(request.tipoRemuneracion() == TipoRemuneracion.SUELDO_FIJO ? request.sueldoFijo() : null);
        e.setDireccion(request.direccion());
        e.setTelefono(request.telefono());
        e.setEmail(request.email());
        e.setFechaNacimiento(request.fechaNacimiento());
        e.setFechaIngreso(request.fechaIngreso());
        e.setPuesto(request.puesto());
        e.setContactoEmergenciaNombre(request.contactoEmergenciaNombre());
        e.setContactoEmergenciaTelefono(request.contactoEmergenciaTelefono());
        e.setContactoEmergenciaVinculo(request.contactoEmergenciaVinculo());
        e.setObraSocial(request.obraSocial());
        e.setObservaciones(request.observaciones());
    }

    private Response aResponse(Empleado e) {
        return new Response(
                e.getId(), e.getNombre(), e.getDni(),
                e.getTipoRemuneracion(), e.getValorHora(), e.getSueldoFijo(),
                e.getDireccion(), e.getTelefono(), e.getEmail(),
                e.getFechaNacimiento(), e.getFechaIngreso(), e.getPuesto(),
                e.getContactoEmergenciaNombre(), e.getContactoEmergenciaTelefono(), e.getContactoEmergenciaVinculo(),
                e.getObraSocial(), e.getObservaciones(), e.isActivo()
        );
    }

    public void desactivar(Long id) {
        Empleado empleado = obtenerOFallar(id);
        empleado.setActivo(false);
        repository.save(empleado);
    }

    public void reactivar(Long id) {
        Empleado empleado = obtenerOFallar(id);
        empleado.setActivo(true);
        repository.save(empleado);
    }


    public Empleado obtenerOFallar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado"));
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

}