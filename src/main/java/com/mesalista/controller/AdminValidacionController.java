package com.mesalista.controller;

import com.mesalista.dto.RechazoDTO;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.service.ProductoServicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminValidacionController {

    @Autowired
    private ProductoServicioService productoService;



    @GetMapping("/validaciones")
    public String listarValidaciones(Model model,
                                     @RequestParam(defaultValue = "PENDIENTE") String estado) {
        EstadoValidacion estadoEnum;
        try {
            estadoEnum = EstadoValidacion.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            estadoEnum = EstadoValidacion.PENDIENTE;
        }

        List<ProductoServicio> productos = productoService.listarPorEstado(estadoEnum);
        model.addAttribute("productos", productos);
        model.addAttribute("estadoFiltro", estadoEnum);
        model.addAttribute("totalPendientes", productoService.contarPendientes());
        model.addAttribute("rechazoDTO", new RechazoDTO());
        return "admin/validaciones";
    }

    @PostMapping("/validaciones/{id}/aprobar")
    public String aprobar(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            productoService.aprobarProducto(id);
            redirectAttrs.addFlashAttribute("success", "✓ Producto aprobado exitosamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al aprobar el producto.");
        }
        return "redirect:/admin/validaciones";
    }

    @PostMapping("/validaciones/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @Valid @ModelAttribute("rechazoDTO") RechazoDTO rechazoDTO,
                           BindingResult result,
                           RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("error", "Debes especificar el motivo de rechazo.");
            return "redirect:/admin/validaciones";
        }
        try {
            productoService.rechazarProducto(id, rechazoDTO.getMotivoRechazo());
            redirectAttrs.addFlashAttribute("success", "Producto rechazado. Se notificó al aliado.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al rechazar el producto.");
        }
        return "redirect:/admin/validaciones";
    }
}
