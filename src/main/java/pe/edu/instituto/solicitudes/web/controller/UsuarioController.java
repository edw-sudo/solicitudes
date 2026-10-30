package pe.edu.instituto.solicitudes.web.controller;

import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.instituto.solicitudes.repository.AreaRepository;
import pe.edu.instituto.solicitudes.repository.RolRepository;
import pe.edu.instituto.solicitudes.service.ReglaNegocioException;
import pe.edu.instituto.solicitudes.service.UsuarioService;
import pe.edu.instituto.solicitudes.web.form.UsuarioForm;

/** Administración de usuarios (CU-03 a CU-06). Solo ADMINISTRADOR (ver SecurityConfig). */
@Controller
@RequestMapping("/admin/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final AreaRepository areaRepository;

    public UsuarioController(UsuarioService usuarioService, RolRepository rolRepository, AreaRepository areaRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.areaRepository = areaRepository;
    }

    /** CU-04 Buscar usuarios. */
    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String texto,
                         @RequestParam(defaultValue = "") String rol,
                         @RequestParam(defaultValue = "") String estado,
                         Model model) {
        model.addAttribute("usuarios", usuarioService.buscar(texto, rol, estado));
        model.addAttribute("roles", rolRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("texto", texto);
        model.addAttribute("rol", rol);
        model.addAttribute("estado", estado);
        return "admin/usuarios/lista";
    }

    /** CU-03 Registrar usuario. */
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new UsuarioForm());
        cargarListas(model);
        return "admin/usuarios/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute("form") UsuarioForm form, BindingResult result,
                        Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                usuarioService.crear(form);
                flash.addFlashAttribute("ok", "Usuario " + form.getUsername().trim() + " registrado correctamente.");
                return "redirect:/admin/usuarios";
            } catch (ReglaNegocioException e) {
                registrarError(result, e);
            }
        }
        form.setPassword(null);
        cargarListas(model);
        return "admin/usuarios/form";
    }

    /** CU-05 Editar usuario. */
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("form", UsuarioForm.desde(usuarioService.obtener(id)));
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        }
        cargarListas(model);
        return "admin/usuarios/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") UsuarioForm form,
                             BindingResult result, Model model, Principal principal, RedirectAttributes flash) {
        form.setIdUsuario(id);
        if (!result.hasErrors()) {
            try {
                usuarioService.actualizar(id, form, principal.getName());
                flash.addFlashAttribute("ok", "Usuario " + form.getUsername().trim() + " actualizado correctamente.");
                return "redirect:/admin/usuarios";
            } catch (ReglaNegocioException e) {
                registrarError(result, e);
            }
        }
        form.setPassword(null);
        cargarListas(model);
        return "admin/usuarios/form";
    }

    /** CU-06 Dar de baja usuario. */
    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, Principal principal, RedirectAttributes flash) {
        try {
            usuarioService.darDeBaja(id, principal.getName());
            flash.addFlashAttribute("ok", "Usuario dado de baja. Ya no podrá iniciar sesión.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            usuarioService.reactivar(id);
            flash.addFlashAttribute("ok", "Usuario reactivado.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    private void cargarListas(Model model) {
        model.addAttribute("roles", rolRepository.findByActivoTrueOrderByNombreAsc());
        model.addAttribute("areas", areaRepository.findByActivoTrueOrderByNombreAsc());
    }

    private void registrarError(BindingResult result, ReglaNegocioException e) {
        if (e.getCampo() != null) {
            result.rejectValue(e.getCampo(), "regla", e.getMessage());
        } else {
            result.reject("regla", e.getMessage());
        }
    }
}
