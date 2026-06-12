package com.mesalista.controller;

import com.mesalista.model.LocalGastronomico;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.repository.LocalGastronomicoRepository;
import com.mesalista.service.ProductoServicioService;
import com.mesalista.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class LocalGastronomicoController {

    @Autowired
    private LocalGastronomicoRepository localRepo;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoServicioService productoService;

    // --- PANEL DEL ALIADO (Para editar información de su local) ---
    @GetMapping("/aliado/local")
    public String editarLocalForm(Model model, Authentication auth) {
        Usuario aliado = usuarioService.buscarPorEmail(auth.getName());
        LocalGastronomico local = localRepo.findByAliado(aliado).orElse(new LocalGastronomico());
        model.addAttribute("local", local);
        return "aliado/local_formulario";
    }

    @PostMapping("/aliado/local")
    public String guardarLocal(@ModelAttribute LocalGastronomico formLocal, Authentication auth, RedirectAttributes redirectAttrs) {
        try {
            Usuario aliado = usuarioService.buscarPorEmail(auth.getName());
            LocalGastronomico local = localRepo.findByAliado(aliado).orElse(new LocalGastronomico());

            local.setAliado(aliado);
            local.setNombre(formLocal.getNombre());
            local.setTipoLocal(formLocal.getTipoLocal());
            local.setDescripcion(formLocal.getDescripcion());
            local.setDireccion(formLocal.getDireccion());
            local.setTelefono(formLocal.getTelefono());
            local.setHorarios(formLocal.getHorarios());
            local.setPortadaUrl(formLocal.getPortadaUrl());

            localRepo.save(local);
            redirectAttrs.addFlashAttribute("success", "Información del local guardada correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Ocurrió un error al guardar: " + e.getMessage());
        }
        return "redirect:/aliado/local";
    }

    // --- VISTA PÚBLICA (Landing page premium) ---
    @GetMapping("/local/{id}")
    public String verLocalPublico(@PathVariable Long id, Model model) {
        LocalGastronomico local = localRepo.findById(id).orElse(null);
        if (local == null) {
            return "redirect:/cliente/buscar";
        }

        // Obtener productos aprobados de este local
        List<ProductoServicio> productosDelLocal = productoService.listarPorEstado(EstadoValidacion.APROBADO)
                .stream()
                .filter(p -> p.getUsuarioOfertante().getId().equals(local.getAliado().getId()))
                .toList();

        model.addAttribute("local", local);
        model.addAttribute("productos", productosDelLocal);
        return "local/detalle";
    }
}
