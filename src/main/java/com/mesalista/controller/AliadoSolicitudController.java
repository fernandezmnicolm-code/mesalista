package com.mesalista.controller;

import com.mesalista.model.Solicitud;
import com.mesalista.service.SolicitudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aliado")
public class AliadoSolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping("/solicitudes")
    public String listarSolicitudes(Model model, Authentication auth) {
        List<Solicitud> solicitudes = solicitudService.listarPorAliado(auth.getName());
        model.addAttribute("solicitudes", solicitudes);
        return "aliado/solicitudes";
    }

    @GetMapping("/reservas")
    public String listarReservas(Model model, Authentication auth) {
        // Para simplificar, mostraremos la misma vista de solicitudes por ahora
        // ya que tanto reservas como pedidos entran como Solicitud.
        List<Solicitud> solicitudes = solicitudService.listarPorAliado(auth.getName());
        model.addAttribute("solicitudes", solicitudes);
        return "aliado/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            solicitudService.aceptarSolicitud(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Solicitud aceptada. Se generó el código de confirmación.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al aceptar: " + e.getMessage());
        }
        return "redirect:/aliado/solicitudes";
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            solicitudService.rechazarSolicitud(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Solicitud rechazada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al rechazar: " + e.getMessage());
        }
        return "redirect:/aliado/solicitudes";
    }
}
