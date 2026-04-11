package com.brinquedostore.api.controller;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CategoriaService;
import com.brinquedostore.api.service.IntegranteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PublicController {

    private final BrinquedoService brinquedoService;
    private final CategoriaService categoriaService;
    private final IntegranteService integranteService;

    public PublicController(BrinquedoService brinquedoService, CategoriaService categoriaService, IntegranteService integranteService) {
        this.brinquedoService = brinquedoService;
        this.categoriaService = categoriaService;
        this.integranteService = integranteService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Brinquedo> brinquedos = brinquedoService.listarTodos();
        model.addAttribute("brinquedos", brinquedos);
        return "public/index";
    }

    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("categorias", categoriaService.listarAtivas());
        return "public/catalogo";
    }
    
    @GetMapping("/catalogo/{categoria}")
    public String catalogoCategoria(@PathVariable String categoria, Model model) {
        categoriaService.buscarPorNome(categoria)
                .filter(Categoria::getAtivo)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo inválido: " + categoria));
        List<Brinquedo> brinquedos = brinquedoService.buscarPorCategoria(categoria);
        model.addAttribute("brinquedos", brinquedos);
        model.addAttribute("nomeCategoria", categoria);
        return "public/categoria";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        Brinquedo brinquedo = brinquedoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Brinquedo inválido: " + id));
        model.addAttribute("brinquedo", brinquedo);
        return "public/detalhes";
    }

    @GetMapping("/sobre")
    public String sobre(Model model) {
        model.addAttribute("equipe", integranteService.listarTodos());
        return "public/sobre";
    }
}
