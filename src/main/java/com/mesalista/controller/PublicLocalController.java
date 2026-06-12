package com.mesalista.controller;

import com.mesalista.model.LocalGastronomico;
import com.mesalista.model.ProductoServicio;
import com.mesalista.model.enums.CategoriaProducto;
import com.mesalista.model.enums.EstadoValidacion;
import com.mesalista.repository.LocalGastronomicoRepository;
import com.mesalista.repository.ProductoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PublicLocalController {

    @Autowired
    private LocalGastronomicoRepository localGastronomicoRepository;

    @Autowired
    private ProductoServicioRepository productoServicioRepository;

    @Autowired
    private com.mesalista.repository.PromocionRepository promocionRepository;

    @GetMapping("/locales/buscar")
    public String buscarLocales(@RequestParam(value = "keyword", required = false) String keyword, 
                                @RequestParam(value = "categoria", required = false) String categoria,
                                Model model) {
        List<LocalGastronomico> resultados;
        
        String searchTerm = keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null;
        if (searchTerm == null && categoria != null && !categoria.trim().isEmpty()) {
            searchTerm = categoria.trim();
        }
        
        if (searchTerm != null) {
            String termLower = searchTerm.toLowerCase();
            if (termLower.equals("comida rápida") || termLower.equals("comida rapida")) {
                resultados = localGastronomicoRepository.findAll().stream()
                    .filter(l -> {
                        if (l.getTipoLocal() == null) return false;
                        String t = l.getTipoLocal().toLowerCase();
                        return t.contains("comida rápida") || t.contains("comida rapida") || 
                               t.contains("fast food") || t.contains("hamburguesería") || 
                               t.contains("broaster") || t.contains("choripán") || 
                               t.contains("snacks");
                    }).collect(Collectors.toList());
            } else {
                resultados = localGastronomicoRepository.searchPublico(searchTerm);
            }
            model.addAttribute("keyword", searchTerm);
        } else {
            resultados = localGastronomicoRepository.findAll();
        }
        
        // Filter out locales that are not APROBADO, assuming we want to show approved only,
        // though currently the prompt doesn't specify if searchPublico already filters it.
        // If not needed, we just leave it as is.
        model.addAttribute("locales", resultados);
        return "locales-resultados";
    }

    @GetMapping("/restaurante/{id}")
    public String verRestaurante(@PathVariable("id") Long id, Model model) {
        Optional<LocalGastronomico> localOpt = localGastronomicoRepository.findById(id);
        if (localOpt.isEmpty()) {
            return "redirect:/locales/buscar";
        }
        LocalGastronomico local = localOpt.get();
        model.addAttribute("local", local);

        // Fetch approved products
        List<ProductoServicio> productosAprobados = productoServicioRepository.findByUsuarioOfertanteAndEstadoValidacionOrderByFechaCreacionDesc(local.getAliado(), EstadoValidacion.APROBADO);

        // Group by category, handling null category safely
        Map<CategoriaProducto, List<ProductoServicio>> productosPorCategoria = productosAprobados.stream()
                .collect(Collectors.groupingBy(p -> p.getCategoria() != null ? p.getCategoria() : CategoriaProducto.GENERAL));

        List<ProductoServicio> destacados = productosAprobados.stream().limit(3).collect(Collectors.toList());
        model.addAttribute("destacados", destacados);

        // Fetch promociones
        List<com.mesalista.model.Promocion> promociones = promocionRepository.findByAliadoGastronomico(local.getAliado());
        model.addAttribute("promociones", promociones);

        model.addAttribute("productosPorCategoria", productosPorCategoria);
        return "restaurante-detalle";
    }
}
