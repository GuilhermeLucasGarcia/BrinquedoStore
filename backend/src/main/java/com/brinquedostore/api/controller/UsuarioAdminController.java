package com.brinquedostore.api.controller;

import com.brinquedostore.api.dto.IntegranteAdminForm;
import com.brinquedostore.api.service.IntegranteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/administracao/usuarios")
public class UsuarioAdminController {

    private final IntegranteService integranteService;

    public UsuarioAdminController(IntegranteService integranteService) {
        this.integranteService = integranteService;
    }

    @GetMapping
    public String listar(@RequestParam(defaultValue = "") String nome, Model model) {
        model.addAttribute("usuarios", integranteService.buscarPorNome(nome));
        model.addAttribute("nomeFiltro", nome);
        return "admin/usuarios";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("usuarioForm")) {
            model.addAttribute("usuarioForm", integranteService.criarFormulario());
        }
        return "admin/formulario-usuario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarioForm", integranteService.buscarPorId(id)
                .map(integranteService::criarFormulario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + id)));
        return "admin/formulario-usuario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("usuarioForm") IntegranteAdminForm usuarioForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("erro", "Revise os campos obrigatórios antes de salvar.");
            return "admin/formulario-usuario";
        }

        try {
            boolean novoCadastro = usuarioForm.getId() == null;
            integranteService.salvar(usuarioForm);
            redirectAttributes.addFlashAttribute("mensagem",
                    novoCadastro ? "Usuário cadastrado com sucesso." : "Usuário atualizado com sucesso.");
            return "redirect:/administracao/usuarios";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("erro", ex.getMessage());
            return "admin/formulario-usuario";
        }
    }
}
