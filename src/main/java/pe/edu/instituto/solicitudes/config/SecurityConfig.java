package pe.edu.instituto.solicitudes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import pe.edu.instituto.solicitudes.service.AccionAuditoria;
import pe.edu.instituto.solicitudes.service.AuditoriaService;
import pe.edu.instituto.solicitudes.service.Roles;

/**
 * Seguridad del sistema (RF-01, RF-02, RF-17, RNF-03, RNF-04).
 * - Login propio en /login, contraseñas con BCrypt.
 * - Acceso por rol: /admin/** (ADMINISTRADOR), /administrativo/** (PERSONAL_ADMINISTRATIVO y ADMINISTRADOR),
 *   /estudiante/** (ESTUDIANTE).
 * - Registra en la auditoría los inicios de sesión correctos, fallidos y los cierres de sesión.
 */
@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuditoriaService auditoria) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/error").permitAll()
                .requestMatchers("/admin/**").hasRole(Roles.ADMINISTRADOR)
                .requestMatchers("/administrativo/**").hasAnyRole(Roles.PERSONAL_ADMINISTRATIVO, Roles.ADMINISTRADOR)
                .requestMatchers("/estudiante/**").hasRole(Roles.ESTUDIANTE)
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(loginCorrecto(auditoria))
                .failureHandler(loginFallido(auditoria))
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(cierreDeSesion(auditoria))
                .permitAll())
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"));
        return http.build();
    }

    /** Después del login: registra LOGIN_OK y lleva a cada usuario al panel de su rol. */
    private AuthenticationSuccessHandler loginCorrecto(AuditoriaService auditoria) {
        return (request, response, authentication) -> {
            auditoria.registrarAcceso(authentication.getName(), AccionAuditoria.LOGIN_OK,
                    "Inicio de sesión correcto", request);
            String rol = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.substring(5))
                    .findFirst()
                    .orElse("");
            response.sendRedirect(request.getContextPath() + Roles.panelDe(rol));
        };
    }

    /** Login rechazado: registra LOGIN_FALLIDO con el username intentado. */
    private AuthenticationFailureHandler loginFallido(AuditoriaService auditoria) {
        return (request, response, exception) -> {
            String detalle = exception instanceof DisabledException
                    ? "Intento de inicio de sesión con cuenta inactiva"
                    : "Intento de inicio de sesión fallido";
            auditoria.registrarAcceso(request.getParameter("username"), AccionAuditoria.LOGIN_FALLIDO, detalle, request);
            response.sendRedirect(request.getContextPath() + "/login?error");
        };
    }

    /** Cierre de sesión: registra LOGOUT y vuelve al login. */
    private LogoutSuccessHandler cierreDeSesion(AuditoriaService auditoria) {
        return (request, response, authentication) -> {
            if (authentication != null) {
                auditoria.registrarAcceso(authentication.getName(), AccionAuditoria.LOGOUT, "Cierre de sesión", request);
            }
            response.sendRedirect(request.getContextPath() + "/login?logout");
        };
    }
}
