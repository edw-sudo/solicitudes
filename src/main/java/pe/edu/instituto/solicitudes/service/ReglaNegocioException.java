package pe.edu.instituto.solicitudes.service;

/**
 * Error de una regla de negocio (dato duplicado, dato obligatorio según el rol, etc.).
 * Si "campo" no es null, el controlador muestra el mensaje junto a ese campo del formulario.
 */
public class ReglaNegocioException extends RuntimeException {

    private final String campo;

    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public ReglaNegocioException(String mensaje) {
        this(null, mensaje);
    }

    public String getCampo() {
        return campo;
    }
}
