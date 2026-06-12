package com.mesalista.controller;

import com.mesalista.model.*;
import com.mesalista.model.enums.EstadoPedido;
import com.mesalista.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/cliente/pedidos")
public class ClientePedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoServicioRepository productoRepository;

    @Autowired
    private LocalGastronomicoRepository localRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String verMisPedidos(Model model, Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }
        Usuario cliente = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        if (cliente == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = pedidoRepository.findByClienteIdOrderByFechaCreacionDesc(cliente.getId());
        model.addAttribute("pedidos", pedidos);
        return "cliente/pedidos";
    }

    @PostMapping("/crear/{productoId}")
    public String crearPedido(@PathVariable Long productoId,
                              @RequestParam("cantidad") Integer cantidad,
                              @RequestParam(value = "observacion", required = false) String observacion,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para realizar un pedido.");
            return "redirect:/login";
        }

        Usuario cliente = usuarioRepository.findByEmail(authentication.getName()).orElse(null);
        if (cliente == null) {
            return "redirect:/login";
        }

        // Verificar que sea CLIENTE
        if (cliente.getRol() != com.mesalista.model.enums.Rol.CLIENTE) {
            redirectAttributes.addFlashAttribute("error", "Solo los clientes pueden realizar pedidos.");
            return "redirect:/";
        }

        ProductoServicio producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/";
        }

        // Verificar que el producto esté APROBADO
        if (producto.getEstadoValidacion() != com.mesalista.model.enums.EstadoValidacion.APROBADO) {
            redirectAttributes.addFlashAttribute("error", "Este producto no está disponible para pedidos.");
            return "redirect:/";
        }

        // The local should be associated to the product owner
        Usuario aliado = producto.getUsuarioOfertante();
        // Get the local gastronomico of this aliado
        LocalGastronomico local = localRepository.findByAliado(aliado).stream().findFirst().orElse(null);

        if (local == null) {
            redirectAttributes.addFlashAttribute("error", "Local no encontrado para este producto.");
            return "redirect:/";
        }

        if (cantidad == null || cantidad < 1) {
            redirectAttributes.addFlashAttribute("error", "La cantidad debe ser al menos 1.");
            return "redirect:/restaurante/" + local.getId();
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProducto(producto);
        pedido.setLocal(local);
        pedido.setAliadoPropietario(aliado);
        pedido.setCantidad(cantidad);
        pedido.setPrecioUnitario(producto.getPrecio());
        pedido.setTotal(producto.getPrecio().multiply(new BigDecimal(cantidad)));
        pedido.setObservacion(observacion);
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);

        pedidoRepository.save(pedido);

        redirectAttributes.addFlashAttribute("exito", "¡Pedido enviado correctamente! El local confirmará tu solicitud.");
        return "redirect:/cliente/pedidos";
    }
}
