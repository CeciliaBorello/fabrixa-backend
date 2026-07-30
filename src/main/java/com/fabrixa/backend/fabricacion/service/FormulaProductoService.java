package com.fabrixa.backend.fabricacion.service;

import com.fabrixa.backend.comercial.model.Producto;
import com.fabrixa.backend.comercial.repository.ProductoRepository;
import com.fabrixa.backend.fabricacion.dto.FormulaDTO.*;
import com.fabrixa.backend.fabricacion.model.FormulaInsumo;
import com.fabrixa.backend.fabricacion.model.FormulaProducto;
import com.fabrixa.backend.fabricacion.repository.FormulaProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class FormulaProductoService {

    private final FormulaProductoRepository repository;
    private final ProductoRepository productoRepository;

    public FormulaProductoService(FormulaProductoRepository repository, ProductoRepository productoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
    }

    public List<Response> listar() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    public Page<Response> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(this::aResponse);
    }

    public List<Response> listarPorProducto(Long productoTerminadoId) {
        return repository.findByProductoTerminadoId(productoTerminadoId).stream().map(this::aResponse).toList();
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public Response crear(Request request) {
        Producto productoTerminado = productoRepository.findById(request.productoTerminadoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto terminado no encontrado"));

        if (request.insumos() == null || request.insumos().isEmpty()) {
            throw new IllegalArgumentException("La fórmula necesita al menos un insumo");
        }

        // siguiente número de versión para este producto
        int siguienteVersion = repository.findByProductoTerminadoId(request.productoTerminadoId()).stream()
                .mapToInt(FormulaProducto::getVersion)
                .max()
                .orElse(0) + 1;

        FormulaProducto formula = new FormulaProducto();
        formula.setProductoTerminado(productoTerminado);
        formula.setNombre(request.nombre());
        formula.setVersion(siguienteVersion);
        formula.setActivo(true);

        for (InsumoRequest insumoReq : request.insumos()) {
            Producto insumoProducto = productoRepository.findById(insumoReq.insumoProductoId())
                    .orElseThrow(() -> new IllegalArgumentException("Insumo no encontrado: " + insumoReq.insumoProductoId()));

            FormulaInsumo insumo = new FormulaInsumo();
            insumo.setFormula(formula);
            insumo.setInsumo(insumoProducto);
            insumo.setCantidadNecesaria(insumoReq.cantidadNecesaria());
            insumo.setUnidadMedida(insumoReq.unidadMedida());
            formula.getInsumos().add(insumo);
        }

        return aResponse(repository.save(formula));
    }

    public void desactivar(Long id) {
        FormulaProducto formula = obtenerOFallar(id);
        formula.setActivo(false);
        repository.save(formula);
    }

    private FormulaProducto obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Fórmula no encontrada"));
    }

    private Response aResponse(FormulaProducto f) {
        List<InsumoResponse> insumos = f.getInsumos().stream()
                .map(i -> new InsumoResponse(i.getId(), i.getInsumo().getId(), i.getInsumo().getNombre(),
                        i.getCantidadNecesaria(), i.getUnidadMedida()))
                .toList();

        return new Response(f.getId(), f.getProductoTerminado().getId(), f.getProductoTerminado().getNombre(),
                f.getNombre(), f.getVersion(), f.isActivo(), insumos);
    }

    public void reactivar(Long id) {
        FormulaProducto formula = obtenerOFallar(id);
        formula.setActivo(true);
        repository.save(formula);
    }
}