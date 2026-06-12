package com.mesalista.service.impl;

import com.mesalista.dto.SolicitudFormDTO;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.Solicitud;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoSolicitud;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.repository.ProductoServicioRepository;
import com.mesalista.repository.SolicitudRepository;
import com.mesalista.repository.UsuarioRepository;
import com.mesalista.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ProductoServicioRepository productoServicioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Solicitud crearSolicitud(Long productoId, SolicitudFormDTO formDTO, String emailCliente) {
        if (formDTO.getFechaReserva() != null && formDTO.getFechaReserva().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede estar en el pasado");
        }
        if (formDTO.getCantidadPersonas() != null && formDTO.getCantidadPersonas() <= 0) {
            throw new IllegalArgumentException("La cantidad de personas debe ser mayor a cero");
        }

        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        ProductoServicio producto = productoServicioRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (producto.getEstadoValidacion() != EstadoValidacion.APROBADO) {
            throw new IllegalStateException("El producto no está disponible para solicitud");
        }

        Solicitud solicitud = new Solicitud();
        solicitud.setCliente(cliente);
        solicitud.setProductoServicio(producto);
        solicitud.setFechaReserva(formDTO.getFechaReserva());
        solicitud.setHoraReserva(formDTO.getHoraReserva());
        solicitud.setCantidadPersonas(formDTO.getCantidadPersonas());
        solicitud.setMensaje(formDTO.getMensaje());
        solicitud.setEstadoSolicitud(EstadoSolicitud.PENDIENTE);

        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<Solicitud> listarPorCliente(String emailCliente) {
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return solicitudRepository.findByClienteOrderByFechaSolicitudDesc(cliente);
    }

    @Override
    public List<Solicitud> listarPorAliado(String emailAliado) {
        Usuario aliado = usuarioRepository.findByEmail(emailAliado)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));
        return solicitudRepository.findByProductoServicio_UsuarioOfertanteOrderByFechaSolicitudDesc(aliado);
    }

    @Override
    public Solicitud aceptarSolicitud(Long solicitudId, String emailAliado) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.getProductoServicio().getUsuarioOfertante().getEmail().equals(emailAliado)) {
            throw new IllegalStateException("No tienes permiso para aceptar esta solicitud");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.ACEPTADA);
        
        // Generar un código corto (e.g. 6 caracteres hex)
        String codigo = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        solicitud.setCodigoConfirmacion("ML-" + codigo);

        return solicitudRepository.save(solicitud);
    }

    @Override
    public Solicitud rechazarSolicitud(Long solicitudId, String emailAliado) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.getProductoServicio().getUsuarioOfertante().getEmail().equals(emailAliado)) {
            throw new IllegalStateException("No tienes permiso para rechazar esta solicitud");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.RECHAZADA);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public Solicitud cancelarSolicitud(Long solicitudId, String emailCliente) {
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada"));

        if (!solicitud.getCliente().getEmail().equals(emailCliente)) {
            throw new IllegalStateException("No tienes permiso para cancelar esta solicitud");
        }
        
        if (solicitud.getEstadoSolicitud() == EstadoSolicitud.RECHAZADA || 
            solicitud.getEstadoSolicitud() == EstadoSolicitud.ACEPTADA) {
            throw new IllegalStateException("No se puede cancelar una solicitud que ya fue procesada");
        }

        solicitud.setEstadoSolicitud(EstadoSolicitud.CANCELADA);
        return solicitudRepository.save(solicitud);
    }

    @Override
    public long contarPendientesPorAliado(String emailAliado) {
        Usuario aliado = usuarioRepository.findByEmail(emailAliado).orElse(null);
        if (aliado == null) return 0;
        return solicitudRepository.countByProductoServicio_UsuarioOfertanteAndEstadoSolicitud(aliado, EstadoSolicitud.PENDIENTE);
    }
}
