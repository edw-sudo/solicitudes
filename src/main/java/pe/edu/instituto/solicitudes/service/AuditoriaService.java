package pe.edu.instituto.solicitudes.service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pe.edu.instituto.solicitudes.entity.Auditoria;
import pe.edu.instituto.solicitudes.entity.Usuario;
import pe.edu.instituto.solicitudes.repository.AuditoriaRepository;
import pe.edu.instituto.solicitudes.repository.UsuarioRepository;

/** Registro y consulta de la auditoría de accesos y administración (RF-17, CU-09). */
@Service
public class AuditoriaService {

    private static final int MAXIMO_RESULTADOS = 500;

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository, UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Registra una acción de administración hecha por el usuario conectado.
     * Se ejecuta dentro de la transacción de la operación: si la operación falla, tampoco queda registro.
     */
    @Transactional
    public void registrar(AccionAuditoria accion, String entidad, Long idEntidad, String detalle) {
        String username = usernameActual();
        guardar(username, accion, entidad, idEntidad, detalle, ipActual());
    }

    /** Registra inicios de sesión (correctos o fallidos) y cierres de sesión. */
    @Transactional
    public void registrarAcceso(String username, AccionAuditoria accion, String detalle, HttpServletRequest request) {
        guardar(username, accion, null, null, detalle, request != null ? request.getRemoteAddr() : ipActual());
    }

    /** CU-09 Consultar auditoría. Fechas vacías: últimos 30 días. */
    @Transactional(readOnly = true)
    public List<Auditoria> buscar(String usuario, String accion, LocalDate desde, LocalDate hasta) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(30);
        String texto = usuario == null ? "" : usuario.trim();
        String acc = (accion == null || accion.isBlank()) ? null : accion;
        return auditoriaRepository.buscar(texto, acc, inicio.atStartOfDay(), fin.plusDays(1).atStartOfDay(),
                PageRequest.of(0, MAXIMO_RESULTADOS));
    }

    // ------------------------------------------------------------------ apoyo

    private void guardar(String username, AccionAuditoria accion, String entidad, Long idEntidad,
                         String detalle, String ip) {
        String nombre = (username == null || username.isBlank()) ? "(desconocido)" : username.trim();
        if (nombre.length() > 100) {
            nombre = nombre.substring(0, 100);
        }
        Usuario usuario = usuarioRepository.findByUsername(nombre).orElse(null);
        auditoriaRepository.save(new Auditoria(usuario, nombre, accion.name(), entidad, idEntidad, detalle, ip));
    }

    private String usernameActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth.getName();
    }

    private String ipActual() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servletAttrs) {
            return servletAttrs.getRequest().getRemoteAddr();
        }
        return null;
    }
}
