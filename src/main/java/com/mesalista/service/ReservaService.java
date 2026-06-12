package com.mesalista.service;

import com.mesalista.dto.ReservaFormDTO;
import com.mesalista.model.Reserva;

import java.util.List;

public interface ReservaService {
    Reserva crearReserva(Long localId, ReservaFormDTO formDTO, String emailCliente);
    List<Reserva> listarPorCliente(String emailCliente);
    List<Reserva> listarPorAliado(String emailAliado);
    Reserva aceptarReserva(Long reservaId, String emailAliado);
    Reserva rechazarReserva(Long reservaId, String emailAliado);
    Reserva finalizarReserva(Long reservaId, String emailAliado);
    Reserva cancelarReserva(Long reservaId, String emailCliente);
}
