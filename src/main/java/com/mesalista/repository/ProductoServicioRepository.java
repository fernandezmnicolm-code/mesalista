package com.mesalista.repository;

import com.mesalista.model.ProductoServicio;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoValidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface ProductoServicioRepository extends JpaRepository<ProductoServicio, Long> {

    // Para el panel del aliado: sus propios productos
    List<ProductoServicio> findByUsuarioOfertanteOrderByFechaCreacionDesc(Usuario usuario);

    // Para el perfil público del local: sus productos aprobados
    List<ProductoServicio> findByUsuarioOfertanteAndEstadoValidacionOrderByFechaCreacionDesc(Usuario usuario, EstadoValidacion estado);

    // Para el admin: filtrar por estado
    List<ProductoServicio> findByEstadoValidacionOrderByFechaCreacionDesc(EstadoValidacion estado);

    // Para el cliente: solo productos aprobados
    List<ProductoServicio> findByEstadoValidacionOrderByFechaActualizacionDesc(EstadoValidacion estado);

    // Contar pendientes (para badges en navbar del admin)
    long countByEstadoValidacion(EstadoValidacion estado);

    @Query("SELECT p FROM ProductoServicio p LEFT JOIN LocalGastronomico l ON p.usuarioOfertante.id = l.aliado.id " +
           "WHERE p.estadoValidacion = :estado AND " +
           "(LOWER(p.titulo) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
           "OR LOWER(l.nombre) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
           "OR LOWER(CAST(p.categoria AS string)) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    List<ProductoServicio> searchPublico(@Param("keyword") String keyword, @Param("estado") EstadoValidacion estado);
}
