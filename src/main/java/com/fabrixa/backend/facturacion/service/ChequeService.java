package com.fabrixa.backend.facturacion.service;

import com.fabrixa.backend.cheques.dto.ChequeDTO.Response;
import com.fabrixa.backend.cheques.model.Cheque;
import com.fabrixa.backend.cheques.model.EstadoCheque;
import com.fabrixa.backend.cheques.repository.ChequeRepository;
import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.facturacion.model.Comprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class ChequeService {

    private final ChequeRepository repository;

    public ChequeService(ChequeRepository repository) {
        this.repository = repository;
    }

    public Cheque crear(ClienteProveedor tercero, BigDecimal monto, String numero, String banco,
                        LocalDate fechaCobro, Comprobante reciboIngreso) {
        Cheque cheque = new Cheque();
        cheque.setTercero(tercero);
        cheque.setMonto(monto);
        cheque.setNumero(numero);
        cheque.setBanco(banco);
        cheque.setFechaEmision(LocalDate.now());
        cheque.setFechaCobro(fechaCobro);
        cheque.setEstado(EstadoCheque.EN_CARTERA);
        cheque.setReciboIngreso(reciboIngreso);
        return repository.save(cheque);
    }

    public Cheque entregar(Long chequeId, Comprobante reciboEgreso) {
        Cheque cheque = obtenerOFallar(chequeId);
        if (cheque.getEstado() != EstadoCheque.EN_CARTERA) {
            throw new IllegalArgumentException("El cheque no está en cartera, no se puede entregar");
        }
        cheque.setEstado(EstadoCheque.ENTREGADO);
        cheque.setReciboEgreso(reciboEgreso);
        return repository.save(cheque);
    }

    public Response cobrar(Long id) {
        Cheque cheque = obtenerOFallar(id);
        validarTransicionFinal(cheque.getEstado());
        cheque.setEstado(EstadoCheque.COBRADO);
        return aResponse(repository.save(cheque));
    }

    public Response rechazar(Long id) {
        Cheque cheque = obtenerOFallar(id);
        validarTransicionFinal(cheque.getEstado());
        cheque.setEstado(EstadoCheque.RECHAZADO);
        return aResponse(repository.save(cheque));
    }

    private void validarTransicionFinal(EstadoCheque estado) {
        if (estado == EstadoCheque.COBRADO || estado == EstadoCheque.RECHAZADO) {
            throw new IllegalArgumentException("El cheque ya está en un estado final (" + estado + ")");
        }
    }

    public Page<Response> listarPorEstado(EstadoCheque estado, String busqueda, Pageable pageable) {
        return repository.buscarPorEstado(estado, busqueda, pageable).map(this::aResponse);
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    private Cheque obtenerOFallar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cheque no encontrado"));
    }

    private Response aResponse(Cheque c) {
        return new Response(
                c.getId(), c.getNumero(), c.getBanco(), c.getTercero().getId(), c.getTercero().getRazonSocial(),
                c.getMonto(), c.getFechaEmision(), c.getFechaCobro(), c.getEstado(),
                c.getReciboIngreso() != null ? c.getReciboIngreso().getId() : null,
                c.getReciboEgreso() != null ? c.getReciboEgreso().getId() : null
        );
    }
}