package com.brinquedostore.api.controller;

import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CategoriaService;
import com.brinquedostore.api.service.MarcaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/administracao")
public class AdminController {

    private final BrinquedoService brinquedoService;
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;

    public AdminController(BrinquedoService brinquedoService, 
                           CategoriaService categoriaService, 
                           MarcaService marcaService) {
        this.brinquedoService = brinquedoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Brinquedo> brinquedos = brinquedoService.listarTodos();
        model.addAttribute("brinquedos", brinquedos);
        return "admin/administracao";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("brinquedo", new Brinquedo());
        carregarDependencias(model);
        return "admin/formulario-brinquedo";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Brinquedo brinquedo) {
        brinquedoService.salvar(brinquedo);
        return "redirect:/administracao";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Brinquedo brinquedo = brinquedoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Brinquedo inválido: " + id));
        model.addAttribute("brinquedo", brinquedo);
        carregarDependencias(model);
        return "admin/formulario-brinquedo";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        brinquedoService.deletar(id);
        return "redirect:/administracao";
    }

    // --- Rotas de Marcas ---

    @GetMapping("/marcas")
    public String listarMarcas(Model model) {
        model.addAttribute("marcas", marcaService.listarTodas());
        return "admin/marcas";
    }

    @GetMapping("/marcas/novo")
    public String novaMarca(Model model) {
        model.addAttribute("marca", new Marca());
        return "admin/formulario-marca";
    }

    @PostMapping("/marcas/salvar")
    public String salvarMarca(@ModelAttribute Marca marca) {
        marcaService.salvar(marca);
        return "redirect:/administracao/marcas";
    }

    @GetMapping("/marcas/excluir/{id}")
    public String excluirMarca(@PathVariable Long id) {
        marcaService.deletar(id);
        return "redirect:/administracao/marcas";
    }

    private void carregarDependencias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("marcas", marcaService.listarTodas());
    }
}
