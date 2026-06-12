package com.mesalista.repository;

import com.mesalista.model.Solicitud;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    List<Solicitud> findByClienteOrderByFechaSolicitudDesc(Usuario cliente);
    List<Solicitud> findByProductoServicio_UsuarioOfertanteOrderByFechaSolicitudDesc(Usuario aliado);
    List<Solicitud> findByProductoServicio_UsuarioOfertanteAndEstadoSolicitudOrderByFechaSolicitudDesc(Usuario aliado, EstadoSolicitud estado);
    long countByProductoServicio_UsuarioOfertanteAndEstadoSolicitud(Usuario aliado, EstadoSolicitud estado);
}
