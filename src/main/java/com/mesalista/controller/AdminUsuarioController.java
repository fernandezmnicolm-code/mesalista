package com.mesalista.controller;

import com.mesalista.model.Usuario;
import com.mesalista.model.enums.Rol;
import com.mesalista.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "admin/usuarios";
    }

    @PostMapping("/{id}/toggle-estado")
    public String toggleEstado(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        try {
            usuarioService.toggleEstadoUsuario(id);
            redirectAttrs.addFlashAttribute("success", "Estado del usuario actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar el usuario.");
        }
        return "redirect:/admin/usuarios";
    }
}
