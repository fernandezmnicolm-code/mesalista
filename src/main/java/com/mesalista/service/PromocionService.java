package com.mesalista.service;

import com.mesalista.model.Promocion;
import com.mesalista.model.enums.EstadoValidacion;

import java.util.List;

public interface PromocionService {
    Promocion guardar(Promocion promocion, String emailAliado);
    Promocion buscarPorId(Long id);
    List<Promocion> listarPorAliado(String emailAliado);
    List<Promocion> listarTodasAprobadasYVigentes();
    List<Promocion> listarPorEstado(EstadoValidacion estado);
    void eliminar(Long id, String emailAliado);
    void aprobarPromocion(Long id);
    void rechazarPromocion(Long id);
}
