package pe.edu.instituto.solicitudes.web.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Pone en todas las vistas el username y el rol del usuario conectado (barra de navegación). */
@ControllerAdvice
public class UsuarioActualAdvice {

    @ModelAttribute("usuarioActual")
    public String usuarioActual() {
        Authentication auth = autenticacion();
        return auth == null ? null : auth.getName();
    }

    @ModelAttribute("rolActual")
    public String rolActual() {
        return rolDe(autenticacion());
    }

    static Authentication autenticacion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return auth;
    }

    static String rolDe(Authentication auth) {
        if (auth == null) return null;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst()
                .orElse(null);
    }
}
