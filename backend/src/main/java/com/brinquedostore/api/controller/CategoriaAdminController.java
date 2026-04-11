package com.brinquedostore.api.controller;

import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.service.CategoriaService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/administracao/catalogos")
public class CategoriaAdminController {

    private final CategoriaService categoriaService;

    public CategoriaAdminController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String nome,
                         @RequestParam(required = false) Boolean ativo,
                         @RequestParam(defaultValue = "0") int pagina,
                         Model model) {
        Page<Categoria> categorias = categoriaService.listarComFiltros(nome, ativo, pagina, 8);
        model.addAttribute("catalogosPage", categorias);
        model.addAttribute("nomeFiltro", nome);
        model.addAttribute("statusFiltro", ativo);
        return "admin/catalogos";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("categoria")) {
            Categoria categoria = new Categoria();
            categoria.setAtivo(Boolean.TRUE);
            model.addAttribute("categoria", categoria);
        }
        return "admin/formulario-catalogo";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Catálogo inválido: " + id));
        model.addAttribute("categoria", categoria);
        return "admin/formulario-catalogo";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("categoria") Categoria categoria,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("erro", "Revise os campos obrigatórios antes de salvar.");
            return "admin/formulario-catalogo";
        }

        try {
            boolean novoCadastro = categoria.getId() == null;
            categoriaService.salvar(categoria);
            redirectAttributes.addFlashAttribute("mensagem",
                    novoCadastro ? "Catálogo criado com sucesso." : "Catálogo atualizado com sucesso.");
            return "redirect:/administracao/catalogos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "admin/formulario-catalogo";
        }
    }

    @PostMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagem", "Catálogo excluído com sucesso.");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/administracao/catalogos";
    }
}
