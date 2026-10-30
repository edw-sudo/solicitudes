package pe.edu.instituto.solicitudes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.Immutable;

/**
 * Registro de auditoría de accesos y administración (tabla auditoria). RF-17.
 * Solo se inserta: la base de datos bloquea UPDATE y DELETE con un trigger.
 */
@Entity
@Immutable
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria")
    private Long idAuditoria;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    /** Usuario que realizó la acción; NULL si el login falló con un username inexistente. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /** Se guarda también como texto para conservar el nombre usado en el intento. */
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "accion", nullable = false, length = 30)
    private String accion;

    @Column(name = "entidad", length = 50)
    private String entidad;

    @Column(name = "id_entidad")
    private Long idEntidad;

    @Column(name = "detalle", columnDefinition = "TEXT")
    private String detalle;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    protected Auditoria() {
        // requerido por JPA
    }

    public Auditoria(Usuario usuario, String username, String accion, String entidad,
                     Long idEntidad, String detalle, String ipOrigen) {
        this.fechaEvento = LocalDateTime.now();
        this.usuario = usuario;
        this.username = username;
        this.accion = accion;
        this.entidad = entidad;
        this.idEntidad = idEntidad;
        this.detalle = detalle;
        this.ipOrigen = ipOrigen;
    }

    public Long getIdAuditoria() { return idAuditoria; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public Usuario getUsuario() { return usuario; }
    public String getUsername() { return username; }
    public String getAccion() { return accion; }
    public String getEntidad() { return entidad; }
    public Long getIdEntidad() { return idEntidad; }
    public String getDetalle() { return detalle; }
    public String getIpOrigen() { return ipOrigen; }
}
