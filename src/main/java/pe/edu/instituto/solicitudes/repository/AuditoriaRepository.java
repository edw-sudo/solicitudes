package pe.edu.instituto.solicitudes.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.instituto.solicitudes.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    /** Consultar auditoría (CU-09): por username, acción y rango de fechas, del más reciente al más antiguo. */
    @Query("""
           SELECT a FROM Auditoria a
           WHERE LOWER(a.username) LIKE LOWER(CONCAT('%', :usuario, '%'))
             AND (:accion IS NULL OR a.accion = :accion)
             AND a.fechaEvento >= :desde
             AND a.fechaEvento < :hasta
           ORDER BY a.fechaEvento DESC
           """)
    List<Auditoria> buscar(@Param("usuario") String usuario,
                           @Param("accion") String accion,
                           @Param("desde") LocalDateTime desde,
                           @Param("hasta") LocalDateTime hasta,
                           Pageable limite);
}
