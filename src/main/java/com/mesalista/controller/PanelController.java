package com.mesalista.controller;

import com.mesalista.model.ProductoServicio;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.model.enums.Rol;
import com.mesalista.service.ProductoServicioService;
import com.mesalista.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PanelController {

    @Autowired
    private ProductoServicioService productoService;

    @GetMapping("/cliente/panel")
    public String panelCliente() {
        return "panel/cliente";
    }

    @GetMapping("/aliado/panel")
    public String panelAliado(Model model, Authentication auth) {
        List<ProductoServicio> todosLosProductos = productoService.listarProductosPorAliado(auth.getName());

        long aprobados  = todosLosProductos.stream().filter(p -> p.getEstadoValidacion() == EstadoValidacion.APROBADO).count();
        long pendientes = todosLosProductos.stream().filter(p -> p.getEstadoValidacion() == EstadoValidacion.PENDIENTE).count();

        // Mostrar solo los últimos 5 en el resumen
        List<ProductoServicio> recientes = todosLosProductos.stream().limit(5).toList();

        model.addAttribute("productos",       recientes);
        model.addAttribute("totalProductos",  (long) todosLosProductos.size());
        model.addAttribute("totalAprobados",  aprobados);
        model.addAttribute("totalPendientes", pendientes);

        return "panel/aliado";
    }

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/admin/dashboard")
    public String panelAdmin(Model model) {
        long pendientes = productoService.contarPendientes();
        List<ProductoServicio> productosPendientes = productoService.listarPorEstado(EstadoValidacion.PENDIENTE).stream().limit(5).toList();
        long aprobados = productoService.listarPorEstado(EstadoValidacion.APROBADO).size();
        
        List<Usuario> todosUsuarios = usuarioService.listarTodos();
        long totalUsuarios = todosUsuarios.size();
        long totalAliados = todosUsuarios.stream().filter(u -> u.getRol() == Rol.ALIADO_GASTRONOMICO).count();
        List<Usuario> ultimosUsuarios = todosUsuarios.stream()
                .sorted((u1, u2) -> u2.getId().compareTo(u1.getId()))
                .limit(5).toList();

        model.addAttribute("totalPendientes", pendientes);
        model.addAttribute("totalAprobados", aprobados);
        model.addAttribute("productosPendientes", productosPendientes);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalAliados", totalAliados);
        model.addAttribute("ultimosUsuarios", ultimosUsuarios);

        return "panel/admin";
    }
}
