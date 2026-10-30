package pe.edu.instituto.solicitudes.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.instituto.solicitudes.entity.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);

    List<Rol> findByActivoTrueOrderByNombreAsc();
}
