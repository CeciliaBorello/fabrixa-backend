package com.fabrixa.backend.rrhh.service;

import com.fabrixa.backend.rrhh.dto.LiquidacionMensualDTO.Request;
import com.fabrixa.backend.rrhh.dto.LiquidacionMensualDTO.Response;
import com.fabrixa.backend.rrhh.model.Empleado;
import com.fabrixa.backend.rrhh.model.LiquidacionMensual;
import com.fabrixa.backend.rrhh.model.RegistroHoras;
import com.fabrixa.backend.rrhh.model.TipoRemuneracion;
import com.fabrixa.backend.rrhh.repository.LiquidacionMensualRepository;
import com.fabrixa.backend.rrhh.repository.RegistroHorasRepository;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LiquidacionMensualService {

    private final LiquidacionMensualRepository repository;
    private final RegistroHorasRepository registroRepository;
    private final EmpleadoService empleadoService;
    private final UsuarioRepository usuarioRepository;

    public LiquidacionMensualService(LiquidacionMensualRepository repository,
                                     RegistroHorasRepository registroRepository,
                                     EmpleadoService empleadoService,
                                     UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.registroRepository = registroRepository;
        this.empleadoService = empleadoService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Response generar(Request request, Authentication auth) {
        Empleado empleado = empleadoService.obtenerOFallar(request.empleadoId());
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        LiquidacionMensual liquidacion = new LiquidacionMensual();
        liquidacion.setEmpleado(empleado);
        liquidacion.setPeriodo(request.periodo());
        liquidacion.setFechaGeneracion(LocalDateTime.now());
        liquidacion.setUsuario(usuario);

        List<RegistroHoras> pendientes = List.of();

        if (empleado.getTipoRemuneracion() == TipoRemuneracion.SUELDO_FIJO) {
            if (request.totalAPagar() == null) {
                throw new IllegalArgumentException("Falta el monto a liquidar para un empleado a sueldo fijo");
            }
            liquidacion.setTipoRemuneracionUsado(TipoRemuneracion.SUELDO_FIJO);
            liquidacion.setTotalAPagar(request.totalAPagar());
            liquidacion.setTotalHoras(null);
            liquidacion.setValorHoraUsado(null);
        } else {
            pendientes = registroRepository.findByEmpleadoIdAndLiquidadoFalseOrderByFechaAsc(empleado.getId());
            if (pendientes.isEmpty()) {
                throw new IllegalArgumentException("Este empleado no tiene horas pendientes de liquidar");
            }

            BigDecimal totalHoras = pendientes.stream()
                    .map(RegistroHoras::getHoras)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            liquidacion.setTipoRemuneracionUsado(TipoRemuneracion.POR_HORA);
            liquidacion.setTotalHoras(totalHoras);
            liquidacion.setValorHoraUsado(empleado.getValorHora());
            liquidacion.setTotalAPagar(totalHoras.multiply(empleado.getValorHora()));
        }

        LiquidacionMensual guardada = repository.save(liquidacion);

        for (RegistroHoras r : pendientes) {
            r.setLiquidado(true);
            r.setLiquidacionId(guardada.getId());
            registroRepository.save(r);
        }

        return aResponse(guardada);
    }

    public Page<Response> listarPaginado(String busqueda, Pageable pageable) {
        return repository.buscar(busqueda, pageable).map(this::aResponse);
    }

    private Response aResponse(LiquidacionMensual l) {
        return new Response(l.getId(), l.getEmpleado().getId(), l.getEmpleado().getNombre(), l.getPeriodo(),
                l.getTipoRemuneracionUsado(), l.getTotalHoras(), l.getValorHoraUsado(), l.getTotalAPagar(),
                l.getFechaGeneracion(), l.getUsuario().getNombre());
    }

}