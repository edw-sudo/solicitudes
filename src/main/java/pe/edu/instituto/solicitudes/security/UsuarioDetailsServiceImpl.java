package pe.edu.instituto.solicitudes.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.instituto.solicitudes.entity.Usuario;
import pe.edu.instituto.solicitudes.repository.UsuarioRepository;

/**
 * Carga el usuario desde la tabla usuario para el inicio de sesión (CU-01).
 * El rol se entrega como ROLE_<nombre> y la cuenta queda deshabilitada si activo = FALSE.
 */
@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByUsername(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(u.getUsername())
                .password(u.getPasswordHash())
                .roles(u.getRol().getNombre())
                .disabled(!Boolean.TRUE.equals(u.getActivo()))
                .build();
    }
}
