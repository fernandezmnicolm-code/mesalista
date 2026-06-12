package com.mesalista.repository;

import com.mesalista.model.Promocion;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoValidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    List<Promocion> findByAliadoGastronomico(Usuario aliado);
    List<Promocion> findByEstadoValidacion(EstadoValidacion estado);
}
