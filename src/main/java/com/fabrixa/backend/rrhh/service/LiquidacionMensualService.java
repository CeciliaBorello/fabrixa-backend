package com.fabrixa.backend.rrhh.service;

import com.fabrixa.backend.rrhh.dto.LiquidacionMensualDTO.Request;
import com.fabrixa.backend.rrhh.dto.LiquidacionMensualDTO.Response;
import com.fabrixa.backend.rrhh.model.Anticipo;
import com.fabrixa.backend.rrhh.model.Empleado;
import com.fabrixa.backend.rrhh.model.LiquidacionMensual;
import com.fabrixa.backend.rrhh.model.RegistroHoras;
import com.fabrixa.backend.rrhh.model.TipoRemuneracion;
import com.fabrixa.backend.rrhh.repository.AnticipoRepository;
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
    private final AnticipoRepository anticipoRepository;
    private final EmpleadoService empleadoService;
    private final UsuarioRepository usuarioRepository;

    public LiquidacionMensualService(LiquidacionMensualRepository repository,
                                     RegistroHorasRepository registroRepository,
                                     AnticipoRepository anticipoRepository,
                                     EmpleadoService empleadoService,
                                     UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.registroRepository = registroRepository;
        this.anticipoRepository = anticipoRepository;
        this.empleadoService = empleadoService;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Response> listarPaginado(String busqueda, Pageable pageable) {
        return repository.buscar(busqueda, pageable).map(this::aResponse);
    }

    private Response aResponse(LiquidacionMensual l) {
        BigDecimal totalAnticipos = anticipoRepository.findByLiquidacionId(l.getId()).stream()
                .map(Anticipo::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Response(l.getId(), l.getEmpleado().getId(), l.getEmpleado().getNombre(), l.getPeriodo(),
                l.getTipoRemuneracionUsado(), l.getTotalHoras(), l.getValorHoraUsado(), totalAnticipos,
                l.getTotalAPagar(), l.getFechaGeneracion(), l.getUsuario().getNombre());
    }

    @Transactional
    public Response generar(Request request, Authentication auth) {
        Empleado empleado = empleadoService.obtenerOFallar(request.empleadoId());
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (request.totalAPagar() == null) {
            throw new IllegalArgumentException("Falta el monto final a liquidar");
        }

        LiquidacionMensual liquidacion = new LiquidacionMensual();
        liquidacion.setEmpleado(empleado);
        liquidacion.setPeriodo(request.periodo());
        liquidacion.setFechaGeneracion(LocalDateTime.now());
        liquidacion.setUsuario(usuario);
        liquidacion.setTotalAPagar(request.totalAPagar()); // monto final, ya editado si correspondía

        List<RegistroHoras> pendientesHoras = List.of();

        if (empleado.getTipoRemuneracion() == TipoRemuneracion.SUELDO_FIJO) {
            // A diferencia de POR_HORA, acá no hay rango de fechas que autolimite las
            // liquidaciones repetidas — el mismo período no puede liquidarse dos veces.
            if (repository.existsByEmpleadoIdAndPeriodo(empleado.getId(), request.periodo())) {
                throw new IllegalArgumentException(
                        "Ya existe una liquidación de sueldo fijo para " + empleado.getNombre() +
                                " en el período " + request.periodo());
            }
            liquidacion.setTipoRemuneracionUsado(TipoRemuneracion.SUELDO_FIJO);
            liquidacion.setTotalHoras(null);
            liquidacion.setValorHoraUsado(null);
        } else {
            if (request.fechaDesde() == null || request.fechaHasta() == null) {
                throw new IllegalArgumentException("Falta el rango de fechas para liquidar horas");
            }
            if (request.fechaDesde().isAfter(request.fechaHasta())) {
                throw new IllegalArgumentException("La fecha desde no puede ser posterior a la fecha hasta");
            }

            pendientesHoras = registroRepository.findByEmpleadoIdAndLiquidadoFalseAndFechaBetweenOrderByFechaAsc(
                    empleado.getId(), request.fechaDesde(), request.fechaHasta()
            );
            if (pendientesHoras.isEmpty()) {
                throw new IllegalArgumentException("No hay horas pendientes en el rango seleccionado");
            }

            BigDecimal totalHoras = pendientesHoras.stream()
                    .map(RegistroHoras::getHoras)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            liquidacion.setTipoRemuneracionUsado(TipoRemuneracion.POR_HORA);
            liquidacion.setTotalHoras(totalHoras);
            liquidacion.setValorHoraUsado(empleado.getValorHora());
            // totalAPagar NO se recalcula acá — viene del frontend (calculado - anticipos,
            // editable a mano) para no pisar un ajuste manual del usuario
        }

        List<Anticipo> anticiposPendientes =
                anticipoRepository.findByEmpleadoIdAndLiquidadoFalseOrderByFechaAsc(empleado.getId());

        LiquidacionMensual guardada = repository.save(liquidacion);

        for (RegistroHoras r : pendientesHoras) {
            r.setLiquidado(true);
            r.setLiquidacionId(guardada.getId());
            registroRepository.save(r);
        }

        for (Anticipo a : anticiposPendientes) {
            a.setLiquidado(true);
            a.setLiquidacionId(guardada.getId());
            anticipoRepository.save(a);
        }

        return aResponse(guardada);
    }
}