package com.mesalista.controller;

import com.mesalista.model.Promocion;
import com.mesalista.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aliado/promociones")
public class AliadoPromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping
    public String listarPromociones(Model model, Authentication auth) {
        List<Promocion> promociones = promocionService.listarPorAliado(auth.getName());
        model.addAttribute("promociones", promociones);
        return "aliado/promociones";
    }

    @GetMapping("/crear")
    public String formularioPromocion(Model model) {
        model.addAttribute("promocion", new Promocion());
        return "aliado/promocion_form";
    }

    @GetMapping("/editar/{id}")
    public String editarPromocion(@PathVariable Long id, Model model) {
        Promocion promocion = promocionService.buscarPorId(id);
        if (promocion == null) {
            return "redirect:/aliado/promociones";
        }
        model.addAttribute("promocion", promocion);
        return "aliado/promocion_form";
    }

    @PostMapping("/guardar")
    public String guardarPromocion(@ModelAttribute Promocion promocion, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            promocionService.guardar(promocion, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Promoción guardada exitosamente y enviada para validación.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/aliado/promociones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPromocion(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            promocionService.eliminar(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Promoción eliminada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/aliado/promociones";
    }
}
