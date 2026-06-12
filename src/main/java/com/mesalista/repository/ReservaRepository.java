package com.mesalista.repository;

import com.mesalista.model.Reserva;
import com.mesalista.model.Usuario;
import com.mesalista.model.LocalGastronomico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteOrderByFechaCreacionDesc(Usuario cliente);
    List<Reserva> findByLocalOrderByFechaCreacionDesc(LocalGastronomico local);
    List<Reserva> findByLocal_AliadoOrderByFechaCreacionDesc(Usuario aliado);
}
