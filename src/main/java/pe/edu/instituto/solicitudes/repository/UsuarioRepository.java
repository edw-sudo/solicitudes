package pe.edu.instituto.solicitudes.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.instituto.solicitudes.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Usado por el inicio de sesión (Spring Security) y por las validaciones de unicidad. */
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCodigoEstudiante(String codigoEstudiante);

    /**
     * Buscar usuarios (CU-04): por username, nombres, apellidos o documento,
     * con filtros opcionales de rol y estado. "texto" nunca es null (usar "" para todos).
     */
    @Query("""
           SELECT u FROM Usuario u
           WHERE (LOWER(u.username) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(u.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR u.numeroDocumento LIKE CONCAT('%', :texto, '%'))
             AND (:rol IS NULL OR u.rol.nombre = :rol)
             AND (:activo IS NULL OR u.activo = :activo)
           ORDER BY u.apellidos, u.nombres
           """)
    List<Usuario> buscar(@Param("texto") String texto,
                         @Param("rol") String rol,
                         @Param("activo") Boolean activo);
}
