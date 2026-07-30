package com.fabrixa.backend.comercial.service;

import com.fabrixa.backend.comercial.dto.ClienteProveedorDTO.Request;
import com.fabrixa.backend.comercial.dto.ClienteProveedorDTO.Response;
import com.fabrixa.backend.comercial.model.ClienteProveedor;
import com.fabrixa.backend.comercial.model.ListaPrecio;
import com.fabrixa.backend.comercial.repository.ClienteProveedorRepository;
import com.fabrixa.backend.comercial.repository.ListaPrecioRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class ClienteProveedorService {

    private final ClienteProveedorRepository repository;
    private final ListaPrecioRepository listaPrecioRepository;

    public ClienteProveedorService(ClienteProveedorRepository repository,
                                   ListaPrecioRepository listaPrecioRepository) {
        this.repository = repository;
        this.listaPrecioRepository = listaPrecioRepository;
    }

    public List<Response> listar() {
        return repository.findAll().stream().map(this::aResponse).toList();
    }

    public Page<Response> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable).map(this::aResponse);
    }

    public Response buscarPorId(Long id) {
        return aResponse(obtenerOFallar(id));
    }

    public Response crear(Request request) {
        if (repository.findByCuit(request.cuit()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente/proveedor con ese CUIT");
        }

        ClienteProveedor entidad = new ClienteProveedor();
        aplicarDatos(entidad, request);
        entidad.setActivo(true);

        return aResponse(repository.save(entidad));
    }

    public Response actualizar(Long id, Request request) {
        ClienteProveedor entidad = obtenerOFallar(id);
        aplicarDatos(entidad, request);
        return aResponse(repository.save(entidad));
    }

    public void desactivar(Long id) {
        ClienteProveedor entidad = obtenerOFallar(id);
        entidad.setActivo(false);
        repository.save(entidad);
    }

    public void reactivar(Long id) {
        ClienteProveedor entidad = obtenerOFallar(id);
        entidad.setActivo(true);
        repository.save(entidad);
    }

    private void aplicarDatos(ClienteProveedor entidad, Request request) {
        entidad.setTipo(request.tipo());
        entidad.setRazonSocial(request.razonSocial());
        entidad.setCuit(request.cuit());
        entidad.setCondicionIva(request.condicionIva());
        entidad.setDireccion(request.direccion());
        entidad.setTelefono(request.telefono());
        entidad.setEmail(request.email());

        if (request.listaPrecioId() != null) {
            ListaPrecio lista = listaPrecioRepository.findById(request.listaPrecioId())
                    .orElseThrow(() -> new IllegalArgumentException("Lista de precio no encontrada"));
            entidad.setListaPrecio(lista);
        } else {
            entidad.setListaPrecio(null);
        }
    }

    private ClienteProveedor obtenerOFallar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente/proveedor no encontrado"));
    }

    private Response aResponse(ClienteProveedor c) {
        return new Response(
                c.getId(),
                c.getTipo(),
                c.getRazonSocial(),
                c.getCuit(),
                c.getCondicionIva(),
                c.getDireccion(),
                c.getTelefono(),
                c.getEmail(),
                c.getListaPrecio() != null ? c.getListaPrecio().getId() : null,
                c.getSaldoCuentaCorriente(),
                c.isActivo()
        );
    }
}