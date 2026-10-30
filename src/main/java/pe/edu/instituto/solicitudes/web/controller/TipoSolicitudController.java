package pe.edu.instituto.solicitudes.web.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.instituto.solicitudes.repository.AreaRepository;
import pe.edu.instituto.solicitudes.service.ReglaNegocioException;
import pe.edu.instituto.solicitudes.service.TipoSolicitudService;
import pe.edu.instituto.solicitudes.web.form.TipoSolicitudForm;

/** Catálogo de tipos de solicitud (CU-07). Solo ADMINISTRADOR (ver SecurityConfig). */
@Controller
@RequestMapping("/admin/tipos")
public class TipoSolicitudController {

    private final TipoSolicitudService tipoSolicitudService;
    private final AreaRepository areaRepository;

    public TipoSolicitudController(TipoSolicitudService tipoSolicitudService, AreaRepository areaRepository) {
        this.tipoSolicitudService = tipoSolicitudService;
        this.areaRepository = areaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tipos", tipoSolicitudService.listar());
        return "admin/tipos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("form", new TipoSolicitudForm());
        model.addAttribute("areas", areaRepository.findByActivoTrueOrderByNombreAsc());
        return "admin/tipos/form";
    }

    @PostMapping("/nuevo")
    public String crear(@Valid @ModelAttribute("form") TipoSolicitudForm form, BindingResult result,
                        Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                tipoSolicitudService.crear(form);
                flash.addFlashAttribute("ok", "Tipo de solicitud registrado correctamente.");
                return "redirect:/admin/tipos";
            } catch (ReglaNegocioException e) {
                registrarError(result, e);
            }
        }
        model.addAttribute("areas", areaRepository.findByActivoTrueOrderByNombreAsc());
        return "admin/tipos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {
        try {
            model.addAttribute("form", TipoSolicitudForm.desde(tipoSolicitudService.obtener(id)));
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/tipos";
        }
        model.addAttribute("areas", areaRepository.findByActivoTrueOrderByNombreAsc());
        return "admin/tipos/form";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") TipoSolicitudForm form,
                             BindingResult result, Model model, RedirectAttributes flash) {
        form.setIdTipoSolicitud(id);
        if (!result.hasErrors()) {
            try {
                tipoSolicitudService.actualizar(id, form);
                flash.addFlashAttribute("ok", "Tipo de solicitud actualizado correctamente.");
                return "redirect:/admin/tipos";
            } catch (ReglaNegocioException e) {
                registrarError(result, e);
            }
        }
        model.addAttribute("areas", areaRepository.findByActivoTrueOrderByNombreAsc());
        return "admin/tipos/form";
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes flash) {
        try {
            tipoSolicitudService.darDeBaja(id);
            flash.addFlashAttribute("ok", "Tipo de solicitud dado de baja. Ya no se ofrecerá para nuevas solicitudes.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tipos";
    }

    @PostMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            tipoSolicitudService.reactivar(id);
            flash.addFlashAttribute("ok", "Tipo de solicitud reactivado.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/tipos";
    }

    private void registrarError(BindingResult result, ReglaNegocioException e) {
        if (e.getCampo() != null) {
            result.rejectValue(e.getCampo(), "regla", e.getMessage());
        } else {
            result.reject("regla", e.getMessage());
        }
    }
}
