package com.mesalista.controller;

import com.mesalista.model.Reserva;
import com.mesalista.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aliado/reservas_mesa")
public class AliadoReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public String listarReservas(Model model, Authentication auth) {
        List<Reserva> reservas = reservaService.listarPorAliado(auth.getName());
        model.addAttribute("reservas", reservas);
        return "aliado/reservas_mesa";
    }

    @PostMapping("/{id}/aceptar")
    public String aceptarReserva(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            reservaService.aceptarReserva(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Reserva de mesa aceptada. Código generado.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/aliado/reservas_mesa";
    }

    @PostMapping("/{id}/rechazar")
    public String rechazarReserva(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            reservaService.rechazarReserva(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Reserva rechazada.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/aliado/reservas_mesa";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizarReserva(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            reservaService.finalizarReserva(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Reserva marcada como finalizada.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/aliado/reservas_mesa";
    }
}
