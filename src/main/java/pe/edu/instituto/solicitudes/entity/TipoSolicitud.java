package pe.edu.instituto.solicitudes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Catálogo de trámites (tabla tipo_solicitud). RF-03 */
@Entity
@Table(name = "tipo_solicitud")
public class TipoSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_solicitud")
    private Long idTipoSolicitud;

    @Column(name = "nombre", nullable = false, unique = true, length = 120)
    private String nombre;

    @Column(name = "descripcion", length = 300)
    private String descripcion;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_area", nullable = false)
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
    public void activar() { this.activo = true; }

    public void desactivar() { this.activo = false; }

    // ---------- Getters y setters ----------
    public Long getIdTipoSolicitud() { return idTipoSolicitud; }
    public void setIdTipoSolicitud(Long idTipoSolicitud) { this.idTipoSolicitud = idTipoSolicitud; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
