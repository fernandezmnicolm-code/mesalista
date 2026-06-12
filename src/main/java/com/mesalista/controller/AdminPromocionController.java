package com.mesalista.controller;

import com.mesalista.model.Promocion;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/promociones")
public class AdminPromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping
    public String listarPromociones(Model model) {
        List<Promocion> pendientes = promocionService.listarPorEstado(EstadoValidacion.PENDIENTE);
        List<Promocion> aprobadas = promocionService.listarPorEstado(EstadoValidacion.APROBADO);
        List<Promocion> rechazadas = promocionService.listarPorEstado(EstadoValidacion.RECHAZADO);
        
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aprobadas", aprobadas);
        model.addAttribute("rechazadas", rechazadas);
        
        return "admin/promociones";
    }

    @PostMapping("/{id}/aprobar")
    public String aprobar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            promocionService.aprobarPromocion(id);
            redirectAttrs.addFlashAttribute("success", "Promoción aprobada.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promociones";
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            promocionService.rechazarPromocion(id);
            redirectAttrs.addFlashAttribute("success", "Promoción rechazada.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/promociones";
    }
}
