package com.mesalista.service.impl;

import com.mesalista.dto.ReservaFormDTO;
import com.mesalista.model.LocalGastronomico;
import com.mesalista.model.Reserva;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoReserva;
import com.mesalista.repository.LocalGastronomicoRepository;
import com.mesalista.repository.ReservaRepository;
import com.mesalista.repository.UsuarioRepository;
import com.mesalista.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private LocalGastronomicoRepository localGastronomicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Reserva crearReserva(Long localId, ReservaFormDTO formDTO, String emailCliente) {
        if (formDTO.getFechaReserva() == null || formDTO.getFechaReserva().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de reserva no puede estar en el pasado ni ser nula");
        }
        if (formDTO.getCantidadPersonas() == null || formDTO.getCantidadPersonas() <= 0) {
            throw new IllegalArgumentException("La cantidad de personas debe ser mayor a cero");
        }

        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        LocalGastronomico local = localGastronomicoRepository.findById(localId)
                .orElseThrow(() -> new IllegalArgumentException("Local gastronómico no encontrado"));

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setLocal(local);
        reserva.setFechaReserva(formDTO.getFechaReserva());
        reserva.setHoraReserva(formDTO.getHoraReserva());
        reserva.setCantidadPersonas(formDTO.getCantidadPersonas());
        reserva.setMensaje(formDTO.getMensaje());
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reserva);
    }

    @Override
    public List<Reserva> listarPorCliente(String emailCliente) {
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return reservaRepository.findByClienteOrderByFechaCreacionDesc(cliente);
    }

    @Override
    public List<Reserva> listarPorAliado(String emailAliado) {
        Usuario aliado = usuarioRepository.findByEmail(emailAliado)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));
        return reservaRepository.findByLocal_AliadoOrderByFechaCreacionDesc(aliado);
    }

    @Override
    public Reserva aceptarReserva(Long reservaId, String emailAliado) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.getLocal().getAliado().getEmail().equals(emailAliado)) {
            throw new IllegalStateException("No tienes permiso para gestionar esta reserva");
        }

        reserva.setEstadoReserva(EstadoReserva.ACEPTADA);
        String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        reserva.setCodigoConfirmacion(codigo);

        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva rechazarReserva(Long reservaId, String emailAliado) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.getLocal().getAliado().getEmail().equals(emailAliado)) {
            throw new IllegalStateException("No tienes permiso para gestionar esta reserva");
        }

        reserva.setEstadoReserva(EstadoReserva.RECHAZADA);
        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva finalizarReserva(Long reservaId, String emailAliado) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.getLocal().getAliado().getEmail().equals(emailAliado)) {
            throw new IllegalStateException("No tienes permiso para gestionar esta reserva");
        }

        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva cancelarReserva(Long reservaId, String emailCliente) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        if (!reserva.getCliente().getEmail().equals(emailCliente)) {
            throw new IllegalStateException("No tienes permiso para cancelar esta reserva");
        }

        if (reserva.getEstadoReserva() == EstadoReserva.FINALIZADA || 
            reserva.getEstadoReserva() == EstadoReserva.RECHAZADA) {
            throw new IllegalStateException("Esta reserva no puede ser cancelada en su estado actual");
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }
}
