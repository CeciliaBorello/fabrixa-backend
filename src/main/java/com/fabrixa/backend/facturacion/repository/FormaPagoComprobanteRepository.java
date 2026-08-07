package com.fabrixa.backend.facturacion.repository;

import com.fabrixa.backend.facturacion.model.FormaPagoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormaPagoComprobanteRepository extends JpaRepository<FormaPagoComprobante, Long> {
    List<FormaPagoComprobante> findByComprobanteId(Long comprobanteId);
}