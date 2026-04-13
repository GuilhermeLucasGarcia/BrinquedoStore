package com.brinquedostore.api.controller;

import com.brinquedostore.api.service.CarrinhoService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean autenticado = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        boolean funcionario = autenticado && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_FUNCIONARIO".equals(authority.getAuthority())
                        || "ROLE_ADMIN".equals(authority.getAuthority()));
        boolean cliente = autenticado && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CLIENTE".equals(authority.getAuthority()));

        model.addAttribute("usuarioAutenticado", autenticado);
        model.addAttribute("usuarioCliente", cliente);
        model.addAttribute("usuarioFuncionario", funcionario);
        model.addAttribute("showAdminMenu", funcionario);
    }
}
