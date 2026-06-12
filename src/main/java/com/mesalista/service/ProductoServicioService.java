package com.mesalista.service;

import com.mesalista.dto.ProductoServicioFormDTO;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.enums.EstadoValidacion;

import java.util.List;
import java.util.Optional;

public interface ProductoServicioService {

    // Para el aliado
    ProductoServicio registrarProducto(ProductoServicioFormDTO formDTO, String emailAliado);
    ProductoServicio editarProducto(Long id, ProductoServicioFormDTO formDTO, String emailAliado);
    void eliminarProducto(Long id, String emailAliado);
    List<ProductoServicio> listarProductosPorAliado(String emailAliado);
    Optional<ProductoServicio> buscarPorIdYAliado(Long id, String emailAliado);

    // Para el admin
    List<ProductoServicio> listarPorEstado(EstadoValidacion estado);
    void aprobarProducto(Long id);
    void rechazarProducto(Long id, String motivoRechazo);
    long contarPendientes();

    // Para el cliente
    List<ProductoServicio> listarProductosAprobados();
}
