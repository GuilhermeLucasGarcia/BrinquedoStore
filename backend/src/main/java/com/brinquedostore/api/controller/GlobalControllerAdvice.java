package com.brinquedostore.api.controller;

import com.brinquedostore.api.service.CarrinhoService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CarrinhoService carrinhoService;

    public GlobalControllerAdvice(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("quantidadeTotal", carrinhoService.getQuantidadeTotal());
    }
}
