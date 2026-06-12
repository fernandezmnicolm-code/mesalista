package com.mesalista.service.impl;

import com.mesalista.dto.ProductoServicioFormDTO;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.repository.ProductoServicioRepository;
import com.mesalista.repository.UsuarioRepository;
import com.mesalista.service.ProductoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServicioServiceImpl implements ProductoServicioService {

    @Autowired
    private ProductoServicioRepository productoRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    public ProductoServicio registrarProducto(ProductoServicioFormDTO formDTO, String emailAliado) {
        if (formDTO.getTitulo() == null || formDTO.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (formDTO.getDescripcion() == null || formDTO.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (formDTO.getPrecio() == null || formDTO.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser un número positivo mayor a cero");
        }
        if (formDTO.getCategoria() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría");
        }

        Usuario aliado = usuarioRepo.findByEmail(emailAliado)
                .orElseThrow(() -> new RuntimeException("Aliado no encontrado"));

        ProductoServicio producto = new ProductoServicio();
        producto.setTitulo(formDTO.getTitulo());
        producto.setDescripcion(formDTO.getDescripcion());
        producto.setPrecio(formDTO.getPrecio());
        producto.setCategoria(formDTO.getCategoria());
        producto.setImagenUrl(formDTO.getImagenUrl());
        producto.setEstadoValidacion(EstadoValidacion.PENDIENTE);
        producto.setUsuarioOfertante(aliado);

        return productoRepo.save(producto);
    }

    @Override
    public ProductoServicio editarProducto(Long id, ProductoServicioFormDTO formDTO, String emailAliado) {
        if (formDTO.getTitulo() == null || formDTO.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (formDTO.getDescripcion() == null || formDTO.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (formDTO.getPrecio() == null || formDTO.getPrecio().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser un número positivo mayor a cero");
        }
        if (formDTO.getCategoria() == null) {
            throw new IllegalArgumentException("Debe seleccionar una categoría");
        }

        ProductoServicio producto = buscarPorIdYAliado(id, emailAliado)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado o sin permisos"));

        // Volver a PENDIENTE si se editan datos sensibles
        boolean cambioSensible = !producto.getTitulo().equals(formDTO.getTitulo())
                || !producto.getPrecio().equals(formDTO.getPrecio())
                || !producto.getCategoria().equals(formDTO.getCategoria());

        producto.setTitulo(formDTO.getTitulo());
        producto.setDescripcion(formDTO.getDescripcion());
        producto.setPrecio(formDTO.getPrecio());
        producto.setCategoria(formDTO.getCategoria());
        producto.setImagenUrl(formDTO.getImagenUrl());

        if (cambioSensible) {
            producto.setEstadoValidacion(EstadoValidacion.PENDIENTE);
            producto.setMotivoRechazo(null); // Limpiar motivo previo
        }

        return productoRepo.save(producto);
    }

    @Override
    public void eliminarProducto(Long id, String emailAliado) {
        ProductoServicio producto = buscarPorIdYAliado(id, emailAliado)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado o sin permisos"));
        productoRepo.delete(producto);
    }

    @Override
    public List<ProductoServicio> listarProductosPorAliado(String emailAliado) {
        Usuario aliado = usuarioRepo.findByEmail(emailAliado)
                .orElseThrow(() -> new RuntimeException("Aliado no encontrado"));
        return productoRepo.findByUsuarioOfertanteOrderByFechaCreacionDesc(aliado);
    }

    @Override
    public Optional<ProductoServicio> buscarPorIdYAliado(Long id, String emailAliado) {
        return productoRepo.findById(id)
                .filter(p -> p.getUsuarioOfertante().getEmail().equals(emailAliado));
    }

    @Override
    public List<ProductoServicio> listarPorEstado(EstadoValidacion estado) {
        return productoRepo.findByEstadoValidacionOrderByFechaCreacionDesc(estado);
    }

    @Override
    public void aprobarProducto(Long id) {
        ProductoServicio producto = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setEstadoValidacion(EstadoValidacion.APROBADO);
        producto.setMotivoRechazo(null);
        productoRepo.save(producto);
    }

    @Override
    public void rechazarProducto(Long id, String motivoRechazo) {
        ProductoServicio producto = productoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setEstadoValidacion(EstadoValidacion.RECHAZADO);
        producto.setMotivoRechazo(motivoRechazo);
        productoRepo.save(producto);
    }

    @Override
    public long contarPendientes() {
        return productoRepo.countByEstadoValidacion(EstadoValidacion.PENDIENTE);
    }

    @Override
    public List<ProductoServicio> listarProductosAprobados() {
        return productoRepo.findByEstadoValidacionOrderByFechaActualizacionDesc(EstadoValidacion.APROBADO);
    }
}
