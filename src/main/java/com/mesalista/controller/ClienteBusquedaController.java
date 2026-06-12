package com.mesalista.controller;

import com.mesalista.model.ProductoServicio;
import com.mesalista.model.enums.CategoriaProducto;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.service.ProductoServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class ClienteBusquedaController {

    @Autowired
    private ProductoServicioService productoService;

    @GetMapping("/buscar")
    public String buscarProductos(Model model, @RequestParam(required = false) String categoria) {
        List<ProductoServicio> aprobados = productoService.listarPorEstado(EstadoValidacion.APROBADO);
        
        if (categoria != null && !categoria.isEmpty() && !categoria.equals("TODAS")) {
            try {
                CategoriaProducto catEnum = CategoriaProducto.valueOf(categoria.toUpperCase());
                aprobados = aprobados.stream()
                        .filter(p -> p.getCategoria() == catEnum)
                        .collect(Collectors.toList());
                model.addAttribute("categoriaSeleccionada", catEnum.name());
            } catch (IllegalArgumentException e) {
                model.addAttribute("categoriaSeleccionada", "TODAS");
            }
        } else {
            model.addAttribute("categoriaSeleccionada", "TODAS");
        }

        model.addAttribute("productos", aprobados);
        model.addAttribute("categorias", CategoriaProducto.values());
        return "cliente/buscar";
    }

    @GetMapping("/producto/{id}")
    public String verDetalleProducto(@PathVariable Long id, Model model) {
        ProductoServicio producto = productoService.listarPorEstado(EstadoValidacion.APROBADO).stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
                
        if (producto == null) {
            return "redirect:/cliente/buscar";
        }
        
        model.addAttribute("producto", producto);
        return "cliente/producto_detalle";
    }
}
