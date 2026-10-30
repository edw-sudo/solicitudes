package pe.edu.instituto.solicitudes.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.edu.instituto.solicitudes.entity.TipoSolicitud;

/** Datos del formulario "Nuevo tipo" / "Editar tipo de solicitud". */
public class TipoSolicitudForm {

    private Long idTipoSolicitud;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombre;

    @Size(max = 300, message = "Máximo 300 caracteres")
    private String descripcion;

    @NotNull(message = "Seleccione el área responsable")
    private Long idArea;

    public static TipoSolicitudForm desde(TipoSolicitud t) {
        TipoSolicitudForm f = new TipoSolicitudForm();
        f.idTipoSolicitud = t.getIdTipoSolicitud();
        f.nombre = t.getNombre();
        f.descripcion = t.getDescripcion();
        f.idArea = t.getArea() != null ? t.getArea().getIdArea() : null;
        return f;
    }

    public boolean esNuevo() {
        return idTipoSolicitud == null;
    }

    public Long getIdTipoSolicitud() { return idTipoSolicitud; }
    public void setIdTipoSolicitud(Long idTipoSolicitud) { this.idTipoSolicitud = idTipoSolicitud; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Long getIdArea() { return idArea; }
    public void setIdArea(Long idArea) { this.idArea = idArea; }
}
