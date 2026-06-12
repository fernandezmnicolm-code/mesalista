package com.mesalista.service.impl;

import com.mesalista.model.Promocion;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.repository.PromocionRepository;
import com.mesalista.repository.UsuarioRepository;
import com.mesalista.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromocionServiceImpl implements PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Promocion guardar(Promocion promocion, String emailAliado) {
        Usuario aliado = usuarioRepository.findByEmail(emailAliado)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));

        if (promocion.getId() != null) {
            Promocion existente = promocionRepository.findById(promocion.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));
            
            if (!existente.getAliadoGastronomico().getEmail().equals(emailAliado)) {
                throw new IllegalStateException("No puedes editar una promoción que no te pertenece");
            }
            // Si se edita, vuelve a pendiente
            promocion.setEstadoValidacion(EstadoValidacion.PENDIENTE);
        } else {
            promocion.setEstadoValidacion(EstadoValidacion.PENDIENTE);
        }

        promocion.setAliadoGastronomico(aliado);
        return promocionRepository.save(promocion);
    }

    @Override
    public Promocion buscarPorId(Long id) {
        return promocionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Promocion> listarPorAliado(String emailAliado) {
        Usuario aliado = usuarioRepository.findByEmail(emailAliado)
                .orElseThrow(() -> new IllegalArgumentException("Aliado no encontrado"));
        return promocionRepository.findByAliadoGastronomico(aliado);
    }

    @Override
    public List<Promocion> listarTodasAprobadasYVigentes() {
        LocalDate hoy = LocalDate.now();
        return promocionRepository.findByEstadoValidacion(EstadoValidacion.APROBADO).stream()
                .filter(p -> !hoy.isBefore(p.getFechaInicio()) && !hoy.isAfter(p.getFechaFin()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Promocion> listarPorEstado(EstadoValidacion estado) {
        return promocionRepository.findByEstadoValidacion(estado);
    }

    @Override
    public void eliminar(Long id, String emailAliado) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));

        if (!promocion.getAliadoGastronomico().getEmail().equals(emailAliado)) {
            throw new IllegalStateException("No puedes eliminar esta promoción");
        }
        promocionRepository.deleteById(id);
    }

    @Override
    public void aprobarPromocion(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));
        promocion.setEstadoValidacion(EstadoValidacion.APROBADO);
        promocionRepository.save(promocion);
    }

    @Override
    public void rechazarPromocion(Long id) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promoción no encontrada"));
        promocion.setEstadoValidacion(EstadoValidacion.RECHAZADO);
        promocionRepository.save(promocion);
    }
}
