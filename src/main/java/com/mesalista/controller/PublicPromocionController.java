package com.mesalista.controller;

import com.mesalista.model.Promocion;
import com.mesalista.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PublicPromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping("/promociones")
    public String paginaPromociones(Model model) {
        List<Promocion> promociones = promocionService.listarTodasAprobadasYVigentes();
        model.addAttribute("promociones", promociones);
        return "public/promociones";
    }
}
