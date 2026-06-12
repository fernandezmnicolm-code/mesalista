package com.mesalista.repository;

import com.mesalista.model.LocalGastronomico;
import com.mesalista.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalGastronomicoRepository extends JpaRepository<LocalGastronomico, Long> {
    Optional<LocalGastronomico> findByAliado(Usuario aliado);

    @Query("SELECT l FROM LocalGastronomico l WHERE LOWER(l.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(l.tipoLocal) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<LocalGastronomico> searchPublico(@Param("keyword") String keyword);
}
