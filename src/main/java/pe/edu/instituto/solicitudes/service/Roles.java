package pe.edu.instituto.solicitudes.service;

/** Nombres de rol tal como están guardados en la tabla rol. */
public final class Roles {

    public static final String ESTUDIANTE = "ESTUDIANTE";
    public static final String PERSONAL_ADMINISTRATIVO = "PERSONAL_ADMINISTRATIVO";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";

    private Roles() {
    }

    /** Página de inicio de cada rol después del login. */
    public static String panelDe(String rol) {
        if (ADMINISTRADOR.equals(rol)) return "/admin";
        if (PERSONAL_ADMINISTRATIVO.equals(rol)) return "/administrativo";
        if (ESTUDIANTE.equals(rol)) return "/estudiante";
        return "/login";
    }
}
