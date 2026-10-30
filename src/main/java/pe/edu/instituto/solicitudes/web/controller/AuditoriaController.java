package pe.edu.instituto.solicitudes.web.controller;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pe.edu.instituto.solicitudes.service.AccionAuditoria;
import pe.edu.instituto.solicitudes.service.AuditoriaService;

/** Consultar auditoría (CU-09). Solo ADMINISTRADOR (ver SecurityConfig). */
@Controller
@RequestMapping("/admin/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String usuario,
                         @RequestParam(defaultValue = "") String accion,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                         Model model) {
        LocalDate fin = hasta != null ? hasta : LocalDate.now();
        LocalDate inicio = desde != null ? desde : fin.minusDays(30);
        model.addAttribute("registros", auditoriaService.buscar(usuario, accion, inicio, fin));
        model.addAttribute("acciones", AccionAuditoria.values());
        model.addAttribute("usuario", usuario);
        model.addAttribute("accion", accion);
        model.addAttribute("desde", inicio);
        model.addAttribute("hasta", fin);
        return "admin/auditoria/lista";
    }
}
