package com.fabrixa.backend.rrhh.service;

import com.fabrixa.backend.rrhh.dto.RegistroHorasDTO.NoLiquidadasPorEmpleado;
import com.fabrixa.backend.rrhh.dto.RegistroHorasDTO.Request;
import com.fabrixa.backend.rrhh.dto.RegistroHorasDTO.Response;
import com.fabrixa.backend.rrhh.model.Empleado;
import com.fabrixa.backend.rrhh.model.RegistroHoras;
import com.fabrixa.backend.rrhh.repository.RegistroHorasRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegistroHorasService {

    private final RegistroHorasRepository repository;
    private final EmpleadoService empleadoService;

    public RegistroHorasService(RegistroHorasRepository repository, EmpleadoService empleadoService) {
        this.repository = repository;
        this.empleadoService = empleadoService;
    }

    public Response crear(Request request) {
        Empleado empleado = empleadoService.obtenerOFallar(request.empleadoId());

        RegistroHoras registro = new RegistroHoras();
        registro.setEmpleado(empleado);
        registro.setFecha(request.fecha());
        registro.setHoras(request.horas());
        // origen queda MANUAL por default — cuando integremos el molinete, ese flujo
        // va a setear DISPOSITIVO acá en vez de este endpoint manual
        return aResponse(repository.save(registro));
    }

    /**
     * Sin rango: comportamiento original (todas las horas pendientes).
     * Se mantiene por compatibilidad con otros llamadores del service.
     */
    public List<Response> porEmpleado(Long empleadoId) {
        return porEmpleado(empleadoId, null, null);
    }

    /**
     * Con fechaDesde/fechaHasta: filtra las horas pendientes al rango elegido.
     * Si alguna de las dos es null, se ignora el filtro (comportamiento original).
     */
    public List<Response> porEmpleado(Long empleadoId, LocalDate fechaDesde, LocalDate fechaHasta) {
        List<RegistroHoras> registros;

        if (fechaDesde != null && fechaHasta != null) {
            if (fechaDesde.isAfter(fechaHasta)) {
                throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
            }
            registros = repository.findByEmpleadoIdAndLiquidadoFalseAndFechaBetweenOrderByFechaAsc(
                    empleadoId, fechaDesde, fechaHasta);
        } else {
            registros = repository.findByEmpleadoIdAndLiquidadoFalseOrderByFechaAsc(empleadoId);
        }

        return registros.stream().map(this::aResponse).toList();
    }

    public List<NoLiquidadasPorEmpleado> noLiquidadasAgrupadas() {
        List<RegistroHoras> todos = repository.findTodosNoLiquidados();
        Map<Long, NoLiquidadasPorEmpleado> agrupado = new LinkedHashMap<>();

        for (RegistroHoras r : todos) {
            Long empId = r.getEmpleado().getId();
            NoLiquidadasPorEmpleado actual = agrupado.get(empId);
            if (actual == null) {
                agrupado.put(empId, new NoLiquidadasPorEmpleado(empId, r.getEmpleado().getNombre(), r.getHoras(), 1));
            } else {
                agrupado.put(empId, new NoLiquidadasPorEmpleado(
                        empId, actual.empleadoNombre(), actual.totalHoras().add(r.getHoras()), actual.cantidadDias() + 1
                ));
            }
        }

        return agrupado.values().stream().toList();
    }

    public void eliminar(Long id) {
        RegistroHoras registro = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registro no encontrado"));
        if (registro.isLiquidado()) {
            throw new IllegalArgumentException("No se puede borrar un registro ya liquidado");
        }
        repository.deleteById(id);
    }

    private Response aResponse(RegistroHoras r) {
        return new Response(r.getId(), r.getEmpleado().getId(), r.getEmpleado().getNombre(),
                r.getFecha(), r.getHoras(), r.getOrigen(), r.isLiquidado());
    }
}