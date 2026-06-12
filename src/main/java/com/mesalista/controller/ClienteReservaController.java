package com.mesalista.controller;

import com.mesalista.dto.ReservaFormDTO;
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
@RequestMapping("/cliente/reservas")
public class ClienteReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping
    public String misReservas(Model model, Authentication auth) {
        List<Reserva> reservas = reservaService.listarPorCliente(auth.getName());
        model.addAttribute("reservas", reservas);
        return "cliente/reservas_mesa";
    }

    @PostMapping("/nueva/{localId}")
    public String crearReserva(@PathVariable Long localId,
                               @ModelAttribute ReservaFormDTO formDTO,
                               Authentication auth,
                               RedirectAttributes redirectAttrs) {
        try {
            reservaService.crearReserva(localId, formDTO, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Reserva enviada exitosamente. El local te confirmará pronto.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al crear la reserva: " + e.getMessage());
        }
        return "redirect:/cliente/reservas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelarReserva(@PathVariable Long id, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            reservaService.cancelarReserva(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Reserva cancelada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo cancelar: " + e.getMessage());
        }
        return "redirect:/cliente/reservas";
    }
}
