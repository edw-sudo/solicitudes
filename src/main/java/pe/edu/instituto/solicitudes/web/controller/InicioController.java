package pe.edu.instituto.solicitudes.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.instituto.solicitudes.service.Roles;

/** Login, redirección inicial, paneles por rol y página de acceso denegado. */
@Controller
public class InicioController {

    @GetMapping("/login")
    public String login() {
        Authentication auth = UsuarioActualAdvice.autenticacion();
        if (auth != null) {
            return "redirect:" + Roles.panelDe(UsuarioActualAdvice.rolDe(auth));
        }
        return "login";
    }

    @GetMapping("/")
    public String inicio() {
        Authentication auth = UsuarioActualAdvice.autenticacion();
        return "redirect:" + (auth == null ? "/login" : Roles.panelDe(UsuarioActualAdvice.rolDe(auth)));
    }

    @GetMapping("/admin")
    public String panelAdministrador() {
        return "admin/panel";
    }

    @GetMapping("/administrativo")
    public String panelAdministrativo() {
        return "administrativo/panel";
    }

    @GetMapping("/estudiante")
    public String panelEstudiante() {
        return "estudiante/panel";
    }

    /** Cualquier método HTTP: Spring Security reenvía aquí también los POST denegados. */
    @RequestMapping("/403")
    public String accesoDenegado() {
        return "error/403";
    }
}
