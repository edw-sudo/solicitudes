package pe.edu.instituto.solicitudes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.instituto.solicitudes.entity.TipoSolicitud;

public interface TipoSolicitudRepository extends JpaRepository<TipoSolicitud, Long> {

    /** Tipos disponibles para nuevas solicitudes (se usará en el Sprint 3). */
    List<TipoSolicitud> findByActivoTrueOrderByNombreAsc();

    List<TipoSolicitud> findAllByOrderByNombreAsc();

    /** Validación de nombre único al registrar un tipo. */
    boolean existsByNombreIgnoreCase(String nombre);

    /** Validación de nombre único al editar (excluye el propio registro). */
    boolean existsByNombreIgnoreCaseAndIdTipoSolicitudNot(String nombre, Long idTipoSolicitud);
}
