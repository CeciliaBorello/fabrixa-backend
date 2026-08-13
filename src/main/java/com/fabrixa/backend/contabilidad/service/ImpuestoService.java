package com.fabrixa.backend.contabilidad.service;

import com.fabrixa.backend.contabilidad.dto.ImpuestoDTO.Request;
import com.fabrixa.backend.contabilidad.dto.ImpuestoDTO.Response;
import com.fabrixa.backend.contabilidad.model.EstadoImpuesto;
import com.fabrixa.backend.contabilidad.model.Impuesto;
import com.fabrixa.backend.contabilidad.repository.ImpuestoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ImpuestoService {

    private final ImpuestoRepository repository;

    public ImpuestoService(ImpuestoRepository repository) {
        this.repository = repository;
    }

    public Page<Response> listarPaginado(EstadoImpuesto estado, String busqueda, Pageable pageable) {
        return repository.buscar(estado, busqueda, pageable).map(this::aResponseConVencimiento);
    }

    public Response crear(Request request) {
        Impuesto impuesto = new Impuesto();
        aplicarDatos(impuesto, request);
        impuesto.setEstado(EstadoImpuesto.PENDIENTE);
        return aResponseConVencimiento(repository.save(impuesto));
    }

    public Response actualizar(Long id, Request request) {
        Impuesto impuesto = obtenerOFallar(id);
        aplicarDatos(impuesto, request);
        return aResponseConVencimiento(repository.save(impuesto));
    }

    public Response marcarPagado(Long id) {
        Impuesto impuesto = obtenerOFallar(id);
        if (impuesto.getEstado() == EstadoImpuesto.PAGADO) {
            throw new IllegalArgumentException("Este impuesto ya está marcado como pagado");
        }
        impuesto.setEstado(EstadoImpuesto.PAGADO);
        impuesto.setFechaPago(LocalDate.now());
        return aResponseConVencimiento(repository.save(impuesto));
    }

    private void aplicarDatos(Impuesto impuesto, Request request) {
        impuesto.setNombre(request.nombre());
        impuesto.setPeriodo(request.periodo());
        impuesto.setMonto(request.monto());
        impuesto.setFechaVencimiento(request.fechaVencimiento());
    }

    private Impuesto obtenerOFallar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Impuesto no encontrado"));
    }

    /** el estado VENCIDO se calcula en el momento de leer, no se guarda — si venció y sigue pendiente, se muestra vencido */
    private Response aResponseConVencimiento(Impuesto i) {
        EstadoImpuesto estadoMostrado = i.getEstado();
        if (estadoMostrado == EstadoImpuesto.PENDIENTE && i.getFechaVencimiento().isBefore(LocalDate.now())) {
            estadoMostrado = EstadoImpuesto.VENCIDO;
        }
        return new Response(i.getId(), i.getNombre(), i.getPeriodo(), i.getMonto(),
                i.getFechaVencimiento(), i.getFechaPago(), estadoMostrado);
    }
}