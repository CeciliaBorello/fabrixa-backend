package com.fabrixa.backend.cuentascorrientes.repository;

import com.fabrixa.backend.cuentascorrientes.model.AjusteCuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AjusteCuentaCorrienteRepository extends JpaRepository<AjusteCuentaCorriente, Long> {

    List<AjusteCuentaCorriente> findByClienteProveedorIdOrderByFechaAsc(Long clienteProveedorId);

    @Query("SELECT COALESCE(SUM(a.monto), 0) FROM AjusteCuentaCorriente a WHERE a.clienteProveedor.id = :clienteId")
    BigDecimal sumaAjustes(@Param("clienteId") Long clienteId);
}