package com.brinquedostore.api.controller;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CarrinhoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/sacola")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;
    private final BrinquedoService brinquedoService;

    public CarrinhoController(CarrinhoService carrinhoService, BrinquedoService brinquedoService) {
        this.carrinhoService = carrinhoService;
        this.brinquedoService = brinquedoService;
    }

    @GetMapping
    public String exibirSacola(Model model) {
        model.addAttribute("itens", carrinhoService.getItens());
        model.addAttribute("total", carrinhoService.getTotal());
        return "public/carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionar(@RequestParam Long id, @RequestParam Integer quantidade) {
        Brinquedo brinquedo = brinquedoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Brinquedo inválido: " + id));
        carrinhoService.adicionar(brinquedo, quantidade);
        return "redirect:/sacola";
    }

    @GetMapping("/remover/{id}")
    public String remover(@PathVariable Long id) {
        carrinhoService.remover(id);
        return "redirect:/sacola";
    }

    @PostMapping("/atualizar")
    public String atualizar(@RequestParam Long id, @RequestParam Integer quantidade) {
        carrinhoService.atualizarQuantidade(id, quantidade);
        return "redirect:/sacola";
    }
}
