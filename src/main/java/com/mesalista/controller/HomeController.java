package com.mesalista.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private com.mesalista.repository.LocalGastronomicoRepository localRepo;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "MesaLista - Descubre la gastronomía de Sucre");
        
        // Fetch up to 6 locales that are 'Restaurante'
        java.util.List<com.mesalista.model.LocalGastronomico> restaurantes = localRepo.findAll().stream()
                .filter(l -> l.getTipoLocal() != null && l.getTipoLocal().toLowerCase().contains("restaurante"))
                .limit(6)
                .collect(java.util.stream.Collectors.toList());
                
        model.addAttribute("locales", restaurantes);
        return "index";
    }
}
