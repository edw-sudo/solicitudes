package pe.edu.instituto.solicitudes.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.instituto.solicitudes.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByActivoTrueOrderByNombreAsc();
}
