package pe.edu.instituto.solicitudes.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.instituto.solicitudes.entity.Area;
import pe.edu.instituto.solicitudes.entity.TipoSolicitud;
import pe.edu.instituto.solicitudes.repository.AreaRepository;
import pe.edu.instituto.solicitudes.repository.TipoSolicitudRepository;
import pe.edu.instituto.solicitudes.web.form.TipoSolicitudForm;

/** Catálogo de tipos de solicitud (RF-03). Caso de uso CU-07. */
@Service
public class TipoSolicitudService {

    private final TipoSolicitudRepository tipoSolicitudRepository;
    private final AreaRepository areaRepository;
    private final AuditoriaService auditoria;

    public TipoSolicitudService(TipoSolicitudRepository tipoSolicitudRepository, AreaRepository areaRepository,
                                AuditoriaService auditoria) {
        this.tipoSolicitudRepository = tipoSolicitudRepository;
        this.areaRepository = areaRepository;
        this.auditoria = auditoria;
    }

    @Transactional(readOnly = true)
    public List<TipoSolicitud> listar() {
        return tipoSolicitudRepository.findAllByOrderByNombreAsc();
    }

    /** Tipos disponibles para nuevas solicitudes (lo usará el Sprint 3). */
    @Transactional(readOnly = true)
    public List<TipoSolicitud> listarActivos() {
        return tipoSolicitudRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public TipoSolicitud obtener(Long id) {
        return tipoSolicitudRepository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("El tipo de solicitud no existe"));
    }

    @Transactional
    public TipoSolicitud crear(TipoSolicitudForm form) {
        String nombre = form.getNombre().trim();
        if (tipoSolicitudRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ReglaNegocioException("nombre", "Ya existe un tipo de solicitud con ese nombre");
        }
        TipoSolicitud t = new TipoSolicitud();
        copiarDatos(form, t, nombre);
        t.activar();
        TipoSolicitud guardado = tipoSolicitudRepository.save(t);
        auditoria.registrar(AccionAuditoria.CREAR, "TIPO_SOLICITUD", guardado.getIdTipoSolicitud(),
                "Registro del tipo de solicitud «" + guardado.getNombre() + "»");
        return guardado;
    }

    @Transactional
    public TipoSolicitud actualizar(Long id, TipoSolicitudForm form) {
        TipoSolicitud t = obtener(id);
        String nombre = form.getNombre().trim();
        if (tipoSolicitudRepository.existsByNombreIgnoreCaseAndIdTipoSolicitudNot(nombre, id)) {
            throw new ReglaNegocioException("nombre", "Ya existe un tipo de solicitud con ese nombre");
        }
        copiarDatos(form, t, nombre);
        TipoSolicitud guardado = tipoSolicitudRepository.save(t);
        auditoria.registrar(AccionAuditoria.EDITAR, "TIPO_SOLICITUD", guardado.getIdTipoSolicitud(),
                "Edición del tipo de solicitud «" + guardado.getNombre() + "»");
        return guardado;
    }

    /** Baja lógica: el tipo deja de ofrecerse para nuevas solicitudes. */
    @Transactional
    public void darDeBaja(Long id) {
        TipoSolicitud t = obtener(id);
        t.desactivar();
        tipoSolicitudRepository.save(t);
        auditoria.registrar(AccionAuditoria.BAJA, "TIPO_SOLICITUD", t.getIdTipoSolicitud(),
                "Baja del tipo de solicitud «" + t.getNombre() + "»");
    }

    @Transactional
    public void reactivar(Long id) {
        TipoSolicitud t = obtener(id);
        t.activar();
        tipoSolicitudRepository.save(t);
        auditoria.registrar(AccionAuditoria.REACTIVAR, "TIPO_SOLICITUD", t.getIdTipoSolicitud(),
                "Reactivación del tipo de solicitud «" + t.getNombre() + "»");
    }

    private void copiarDatos(TipoSolicitudForm form, TipoSolicitud t, String nombre) {
        Area area = areaRepository.findById(form.getIdArea())
                .orElseThrow(() -> new ReglaNegocioException("idArea", "Seleccione un área válida"));
        String descripcion = form.getDescripcion() == null ? null : form.getDescripcion().trim();
        t.setNombre(nombre);
        t.setDescripcion(descripcion == null || descripcion.isEmpty() ? null : descripcion);
        t.setArea(area);
    }
}
