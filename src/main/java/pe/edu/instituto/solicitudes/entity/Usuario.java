package pe.edu.instituto.solicitudes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Cuenta de acceso y datos personales (tabla usuario). RF-01, RF-15 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "numero_documento", nullable = false, unique = true, length = 12)
    private String numeroDocumento;

    @Column(name = "correo", nullable = false, unique = true, length = 150)
    private String correo;

    /** Obligatorio solo para el rol ESTUDIANTE. */
    @Column(name = "codigo_estudiante", unique = true, length = 20)
    private String codigoEstudiante;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    /** Solo para el rol PERSONAL_ADMINISTRATIVO. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area")
    private Area area;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @PrePersist
    void alCrear() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        if (activo == null) activo = true;
    }

    @PreUpdate
    void alModificar() {
        fechaModificacion = LocalDateTime.now();
    }

    // ---------- Métodos del diagrama de clases ----------
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public boolean tieneRol(String nombreRol) {
        return rol != null && rol.getNombre().equalsIgnoreCase(nombreRol);
    }

    public void activar() { this.activo = true; }

    public void desactivar() { this.activo = false; }

    // ---------- Getters y setters ----------
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }
    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }
    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
