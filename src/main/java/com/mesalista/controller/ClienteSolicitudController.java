package com.mesalista.controller;

import com.mesalista.dto.SolicitudFormDTO;
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
@RequestMapping("/cliente")
public class ClienteSolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Model model, Authentication auth) {
        List<Solicitud> solicitudes = solicitudService.listarPorCliente(auth.getName());
        model.addAttribute("solicitudes", solicitudes);
        return "cliente/solicitudes";
    }

    @PostMapping("/solicitar/{productoId}")
    public String solicitarProducto(@PathVariable Long productoId,
                                    @ModelAttribute SolicitudFormDTO formDTO,
                                    Authentication auth,
                                    RedirectAttributes redirectAttrs) {
        try {
            solicitudService.crearSolicitud(productoId, formDTO, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Solicitud enviada exitosamente. El aliado la revisará pronto.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al procesar la solicitud: " + e.getMessage());
        }
        return "redirect:/cliente/mis-solicitudes";
    }

    @PostMapping("/solicitud/{id}/cancelar")
    public String cancelarSolicitud(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            solicitudService.cancelarSolicitud(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Solicitud cancelada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cliente/mis-solicitudes";
    }
}
