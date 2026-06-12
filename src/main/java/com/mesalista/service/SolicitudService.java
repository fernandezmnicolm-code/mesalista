package com.mesalista.service;

import com.mesalista.dto.SolicitudFormDTO;
import com.mesalista.model.Solicitud;
import com.mesalista.model.enums.EstadoSolicitud;

import java.util.List;

public interface SolicitudService {
    Solicitud crearSolicitud(Long productoId, SolicitudFormDTO formDTO, String emailCliente);
    List<Solicitud> listarPorCliente(String emailCliente);
    List<Solicitud> listarPorAliado(String emailAliado);
    Solicitud aceptarSolicitud(Long solicitudId, String emailAliado);
    Solicitud rechazarSolicitud(Long solicitudId, String emailAliado);
    Solicitud cancelarSolicitud(Long solicitudId, String emailCliente);
    long contarPendientesPorAliado(String emailAliado);
}
