package pe.edu.instituto.solicitudes.service;

/** Acciones que se registran en la tabla auditoria (deben coincidir con el CHECK de la tabla). */
public enum AccionAuditoria {
    LOGIN_OK,
    LOGIN_FALLIDO,
    LOGOUT,
    CREAR,
    EDITAR,
    BAJA,
    REACTIVAR
}
