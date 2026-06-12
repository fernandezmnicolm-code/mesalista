package com.mesalista.repository;

import com.mesalista.model.Pedido;
import com.mesalista.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);
    List<Pedido> findByAliadoPropietarioIdOrderByFechaCreacionDesc(Long aliadoId);
    List<Pedido> findByLocalIdOrderByFechaCreacionDesc(Long localId);
    List<Pedido> findByEstadoPedido(EstadoPedido estado);
}
