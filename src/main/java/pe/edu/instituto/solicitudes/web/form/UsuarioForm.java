package pe.edu.instituto.solicitudes.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import pe.edu.instituto.solicitudes.entity.Usuario;

/** Datos del formulario "Nuevo usuario" / "Editar usuario". */
public class UsuarioForm {

    private Long idUsuario;

    @NotBlank(message = "El username es obligatorio")
    @Pattern(regexp = "[A-Za-z0-9._-]{4,100}",
             message = "Use de 4 a 100 caracteres: letras, números, punto, guion o guion bajo")
    private String username;

    /** Obligatoria al registrar; opcional al editar (vacía = mantener la actual). */
    private String password;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "[0-9]{8,12}", message = "Ingrese de 8 a 12 dígitos")
    private String numeroDocumento;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo no válido")
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String correo;

    @Size(max = 20, message = "Máximo 20 caracteres")
    private String codigoEstudiante;

    @NotNull(message = "Seleccione un rol")
    private Long idRol;

    private Long idArea;

    public static UsuarioForm desde(Usuario u) {
        UsuarioForm f = new UsuarioForm();
        f.idUsuario = u.getIdUsuario();
        f.username = u.getUsername();
        f.nombres = u.getNombres();
        f.apellidos = u.getApellidos();
        f.numeroDocumento = u.getNumeroDocumento();
        f.correo = u.getCorreo();
        f.codigoEstudiante = u.getCodigoEstudiante();
        f.idRol = u.getRol() != null ? u.getRol().getIdRol() : null;
        f.idArea = u.getArea() != null ? u.getArea().getIdArea() : null;
        return f;
    }

    public boolean esNuevo() {
        return idUsuario == null;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
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
    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }
    public Long getIdArea() { return idArea; }
    public void setIdArea(Long idArea) { this.idArea = idArea; }
}
