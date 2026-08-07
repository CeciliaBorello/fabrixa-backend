package com.fabrixa.backend.cheques.repository;

import com.fabrixa.backend.cheques.model.Cheque;
import com.fabrixa.backend.cheques.model.EstadoCheque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {

    @Query("SELECT c FROM Cheque c WHERE c.estado = :estado AND " +
            "(LOWER(c.numero) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(c.banco) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Cheque> buscarPorEstado(@Param("estado") EstadoCheque estado, @Param("busqueda") String busqueda, Pageable pageable);
}