package com.fabrixa.backend.comercial.repository;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.model.TipoProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Page<Producto> findByActivoTrueAndTipoIn(List<TipoProducto> tipos, Pageable pageable);
}