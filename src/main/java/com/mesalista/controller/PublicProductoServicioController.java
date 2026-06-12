package com.mesalista.controller;

import com.mesalista.model.ProductoServicio;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.repository.ProductoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PublicProductoServicioController {

    @Autowired
    private ProductoServicioRepository productoServicioRepository;

    @GetMapping("/productos/buscar")
    public String buscarProductos(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<ProductoServicio> resultados;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            resultados = productoServicioRepository.searchPublico(keyword.trim(), EstadoValidacion.APROBADO);
            model.addAttribute("keyword", keyword);
        } else {
            resultados = productoServicioRepository.findByEstadoValidacionOrderByFechaActualizacionDesc(EstadoValidacion.APROBADO);
        }
        
        model.addAttribute("productos", resultados);
        return "productos-resultados";
    }
}
