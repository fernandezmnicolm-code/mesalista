package com.mesalista.controller;

import com.mesalista.dto.ProductoServicioFormDTO;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.enums.CategoriaProducto;
import com.mesalista.service.ProductoServicioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aliado/productos")
public class ProductoController {

    @Autowired
    private ProductoServicioService productoService;

    @GetMapping
    public String listarMisProductos(Model model, Authentication auth) {
        List<ProductoServicio> productos = productoService.listarProductosPorAliado(auth.getName());
        model.addAttribute("productos", productos);
        return "producto/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("formDTO", new ProductoServicioFormDTO());
        model.addAttribute("categorias", CategoriaProducto.values());
        model.addAttribute("esEdicion", false);
        return "producto/formulario";
    }

    @PostMapping("/nuevo")
    public String guardarNuevoProducto(@Valid @ModelAttribute("formDTO") ProductoServicioFormDTO formDTO,
                                       BindingResult result, Model model,
                                       Authentication auth,
                                       RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", CategoriaProducto.values());
            model.addAttribute("esEdicion", false);
            return "producto/formulario";
        }
        try {
            productoService.registrarProducto(formDTO, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Producto registrado exitosamente. Quedará en revisión hasta ser aprobado.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
        }
        return "redirect:/aliado/productos";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, Authentication auth,
                                          RedirectAttributes redirectAttrs) {
        return productoService.buscarPorIdYAliado(id, auth.getName())
                .map(p -> {
                    ProductoServicioFormDTO dto = new ProductoServicioFormDTO();
                    dto.setId(p.getId());
                    dto.setTitulo(p.getTitulo());
                    dto.setDescripcion(p.getDescripcion());
                    dto.setPrecio(p.getPrecio());
                    dto.setCategoria(p.getCategoria());
                    dto.setImagenUrl(p.getImagenUrl());
                    model.addAttribute("formDTO", dto);
                    model.addAttribute("categorias", CategoriaProducto.values());
                    model.addAttribute("esEdicion", true);
                    model.addAttribute("estadoActual", p.getEstadoValidacion());
                    return "producto/formulario";
                })
                .orElseGet(() -> {
                    redirectAttrs.addFlashAttribute("error", "Producto no encontrado.");
                    return "redirect:/aliado/productos";
                });
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id,
                                     @Valid @ModelAttribute("formDTO") ProductoServicioFormDTO formDTO,
                                     BindingResult result, Model model,
                                     Authentication auth,
                                     RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", CategoriaProducto.values());
            model.addAttribute("esEdicion", true);
            return "producto/formulario";
        }
        try {
            productoService.editarProducto(id, formDTO, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Producto actualizado. Si cambiaste datos importantes, quedará en revisión nuevamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/aliado/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, Authentication auth,
                                   RedirectAttributes redirectAttrs) {
        try {
            productoService.eliminarProducto(id, auth.getName());
            redirectAttrs.addFlashAttribute("success", "Producto eliminado correctamente.");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo eliminar el producto.");
        }
        return "redirect:/aliado/productos";
    }
}
