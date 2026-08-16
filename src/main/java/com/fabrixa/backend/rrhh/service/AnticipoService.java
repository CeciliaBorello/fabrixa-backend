package com.fabrixa.backend.rrhh.service;

import com.fabrixa.backend.rrhh.dto.AnticipoDTO.Request;
import com.fabrixa.backend.rrhh.dto.AnticipoDTO.Response;
import com.fabrixa.backend.rrhh.model.Anticipo;
import com.fabrixa.backend.rrhh.model.Empleado;
import com.fabrixa.backend.rrhh.repository.AnticipoRepository;
import com.fabrixa.backend.usuarios.model.Usuario;
import com.fabrixa.backend.usuarios.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnticipoService {

    private final AnticipoRepository repository;
    private final EmpleadoService empleadoService;
    private final UsuarioRepository usuarioRepository;

    public AnticipoService(AnticipoRepository repository, EmpleadoService empleadoService,
                           UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.empleadoService = empleadoService;
        this.usuarioRepository = usuarioRepository;
    }

    public Response crear(Request request, Authentication auth) {
        Empleado empleado = empleadoService.obtenerOFallar(request.empleadoId());
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Anticipo anticipo = new Anticipo();
        anticipo.setEmpleado(empleado);
        anticipo.setMonto(request.monto());
        anticipo.setFecha(request.fecha());
        anticipo.setMotivo(request.motivo());
        anticipo.setLiquidado(false);
        anticipo.setUsuario(usuario);

        return aResponse(repository.save(anticipo));
    }

    public List<Response> porEmpleado(Long empleadoId) {
        return repository.findByEmpleadoIdOrderByFechaDesc(empleadoId).stream()
                .map(this::aResponse).toList();
    }

    public void eliminar(Long id) {
        Anticipo anticipo = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anticipo no encontrado"));
        if (anticipo.isLiquidado()) {
            throw new IllegalArgumentException("No se puede borrar un anticipo ya descontado en una liquidación");
        }
        repository.deleteById(id);
    }

    private Response aResponse(Anticipo a) {
        return new Response(a.getId(), a.getEmpleado().getId(), a.getEmpleado().getNombre(),
                a.getMonto(), a.getFecha(), a.getMotivo(), a.isLiquidado(), a.getLiquidacionId(),
                a.getUsuario().getNombre());
    }
}