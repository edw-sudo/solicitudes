package pe.edu.instituto.solicitudes.service;

import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.instituto.solicitudes.entity.Area;
import pe.edu.instituto.solicitudes.entity.Rol;
import pe.edu.instituto.solicitudes.entity.Usuario;
import pe.edu.instituto.solicitudes.repository.AreaRepository;
import pe.edu.instituto.solicitudes.repository.RolRepository;
import pe.edu.instituto.solicitudes.repository.UsuarioRepository;
import pe.edu.instituto.solicitudes.web.form.UsuarioForm;

/**
 * Administración de usuarios (RF-15): registrar, buscar, editar, dar de baja y reactivar.
 * Casos de uso CU-03 a CU-06.
 */
@Service
public class UsuarioService {

    private static final int LONGITUD_MINIMA_PASSWORD = 8;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoria;

    public UsuarioService(UsuarioRepository usuarioRepository, RolRepository rolRepository,
                          AreaRepository areaRepository, PasswordEncoder passwordEncoder,
                          AuditoriaService auditoria) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.areaRepository = areaRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoria = auditoria;
    }

    /** CU-04 Buscar usuarios. estado: "activo", "inactivo" o vacío (todos). */
    @Transactional(readOnly = true)
    public List<Usuario> buscar(String texto, String rol, String estado) {
        String t = texto == null ? "" : texto.trim();
        String r = (rol == null || rol.isBlank()) ? null : rol;
        Boolean activo = "activo".equals(estado) ? Boolean.TRUE
                : "inactivo".equals(estado) ? Boolean.FALSE : null;
        return usuarioRepository.buscar(t, r, activo);
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("El usuario no existe"));
    }

    /** CU-03 Registrar usuario. */
    @Transactional
    public Usuario crear(UsuarioForm form) {
        String password = form.getPassword() == null ? "" : form.getPassword();
        if (password.length() < LONGITUD_MINIMA_PASSWORD) {
            throw new ReglaNegocioException("password",
                    "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres");
        }
        Usuario u = new Usuario();
        copiarDatos(form, u, null);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.activar();
        Usuario guardado = usuarioRepository.save(u);
        auditoria.registrar(AccionAuditoria.CREAR, "USUARIO", guardado.getIdUsuario(),
                "Registro del usuario " + guardado.getUsername() + " (" + guardado.getRol().getNombre() + ")");
        return guardado;
    }

    /** CU-05 Editar usuario. La contraseña solo cambia si se escribe una nueva. */
    @Transactional
    public Usuario actualizar(Long id, UsuarioForm form, String usernameActual) {
        Usuario u = obtener(id);
        if (u.getUsername().equals(usernameActual)
                && !u.getRol().getIdRol().equals(form.getIdRol())) {
            throw new ReglaNegocioException("idRol", "No puede cambiar el rol de su propia cuenta");
        }
        copiarDatos(form, u, id);
        String password = form.getPassword();
        if (password != null && !password.isBlank()) {
            if (password.length() < LONGITUD_MINIMA_PASSWORD) {
                throw new ReglaNegocioException("password",
                        "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres");
            }
            u.setPasswordHash(passwordEncoder.encode(password));
        }
        Usuario guardado = usuarioRepository.save(u);
        boolean cambioPassword = password != null && !password.isBlank();
        auditoria.registrar(AccionAuditoria.EDITAR, "USUARIO", guardado.getIdUsuario(),
                "Edición del usuario " + guardado.getUsername() + " (" + guardado.getRol().getNombre() + ")"
                        + (cambioPassword ? "; cambio de contraseña" : ""));
        return guardado;
    }

    /** CU-06 Dar de baja usuario (baja lógica: activo = FALSE). */
    @Transactional
    public void darDeBaja(Long id, String usernameActual) {
        Usuario u = obtener(id);
        if (u.getUsername().equals(usernameActual)) {
            throw new ReglaNegocioException("No puede darse de baja a sí mismo");
        }
        u.desactivar();
        usuarioRepository.save(u);
        auditoria.registrar(AccionAuditoria.BAJA, "USUARIO", u.getIdUsuario(), "Baja del usuario " + u.getUsername());
    }

    @Transactional
    public void reactivar(Long id) {
        Usuario u = obtener(id);
        u.activar();
        usuarioRepository.save(u);
        auditoria.registrar(AccionAuditoria.REACTIVAR, "USUARIO", u.getIdUsuario(),
                "Reactivación del usuario " + u.getUsername());
    }

    // ------------------------------------------------------------------ reglas

    private void copiarDatos(UsuarioForm form, Usuario u, Long idActual) {
        String username = form.getUsername().trim();
        String documento = form.getNumeroDocumento().trim();
        String correo = form.getCorreo().trim().toLowerCase();
        String codigo = form.getCodigoEstudiante() == null ? "" : form.getCodigoEstudiante().trim();

        Rol rol = rolRepository.findById(form.getIdRol())
                .orElseThrow(() -> new ReglaNegocioException("idRol", "Seleccione un rol válido"));

        validarUnico(usuarioRepository.findByUsername(username), idActual, "username", "El username ya existe");
        validarUnico(usuarioRepository.findByNumeroDocumento(documento), idActual, "numeroDocumento",
                "El número de documento ya está registrado");
        validarUnico(usuarioRepository.findByCorreo(correo), idActual, "correo", "El correo ya está registrado");

        Area area = null;
        String codigoEstudiante = null;
        switch (rol.getNombre()) {
            case Roles.ESTUDIANTE -> {
                if (codigo.isEmpty()) {
                    throw new ReglaNegocioException("codigoEstudiante",
                            "El código de estudiante es obligatorio para el rol ESTUDIANTE");
                }
                validarUnico(usuarioRepository.findByCodigoEstudiante(codigo), idActual, "codigoEstudiante",
                        "El código de estudiante ya está registrado");
                codigoEstudiante = codigo;
            }
            case Roles.PERSONAL_ADMINISTRATIVO -> {
                if (form.getIdArea() == null) {
                    throw new ReglaNegocioException("idArea",
                            "El área es obligatoria para el rol PERSONAL_ADMINISTRATIVO");
                }
                area = areaRepository.findById(form.getIdArea())
                        .orElseThrow(() -> new ReglaNegocioException("idArea", "Seleccione un área válida"));
            }
            default -> {
                // ADMINISTRADOR: sin área ni código de estudiante
            }
        }

        u.setUsername(username);
        u.setNombres(form.getNombres().trim());
        u.setApellidos(form.getApellidos().trim());
        u.setNumeroDocumento(documento);
        u.setCorreo(correo);
        u.setCodigoEstudiante(codigoEstudiante);
        u.setRol(rol);
        u.setArea(area);
    }

    private void validarUnico(Optional<Usuario> existente, Long idActual, String campo, String mensaje) {
        if (existente.isPresent() && !existente.get().getIdUsuario().equals(idActual)) {
            throw new ReglaNegocioException(campo, mensaje);
        }
    }
}
