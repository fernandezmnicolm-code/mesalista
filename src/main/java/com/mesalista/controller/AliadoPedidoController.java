package com.mesalista.controller;

import com.mesalista.model.Pedido;
import com.mesalista.model.Usuario;
import com.mesalista.model.enums.EstadoPedido;
import com.mesalista.repository.PedidoRepository;
import com.mesalista.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aliado/pedidos")
public class AliadoPedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String verPedidos(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Usuario aliado = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        if (aliado == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoRepository.findByAliadoPropietarioIdOrderByFechaCreacionDesc(aliado.getId());
        model.addAttribute("pedidos", pedidos);
        return "aliado/pedidos";
    }

    @PostMapping("/{id}/aceptar")
    public String aceptarPedido(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null) return "redirect:/login";
        Usuario aliado = usuarioRepository.findByEmail(authentication.getName()).orElse(null);

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null && pedido.getAliadoPropietario().getId().equals(aliado.getId())) {
            pedido.setEstadoPedido(EstadoPedido.ACEPTADO);
            pedidoRepository.save(pedido);
            redirectAttributes.addFlashAttribute("exito", "Pedido ACEPTADO exitosamente.");
        }
        return "redirect:/aliado/pedidos";
    }

    @PostMapping("/{id}/rechazar")
    public String rechazarPedido(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null) return "redirect:/login";
        Usuario aliado = usuarioRepository.findByEmail(authentication.getName()).orElse(null);

        Pedido pedido = pedidoRepository.findById(id).orElse(null);
        if (pedido != null && pedido.getAliadoPropietario().getId().equals(aliado.getId())) {
            pedido.setEstadoPedido(EstadoPedido.RECHAZADO);
            pedidoRepository.save(pedido);
            redirectAttributes.addFlashAttribute("error", "Pedido RECHAZADO.");
        }
        return "redirect:/aliado/pedidos";
    }
}
