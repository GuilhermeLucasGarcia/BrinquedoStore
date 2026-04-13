package com.brinquedostore.api.controller;

import com.brinquedostore.api.dto.RegisterForm;
import com.brinquedostore.api.security.RegistrationRateLimiter;
import com.brinquedostore.api.service.IntegranteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final IntegranteService integranteService;
    private final AuthenticationManager authenticationManager;
    private final RegistrationRateLimiter registrationRateLimiter;

    public AuthController(IntegranteService integranteService,
                          AuthenticationManager authenticationManager,
                          RegistrationRateLimiter registrationRateLimiter) {
        this.integranteService = integranteService;
        this.authenticationManager = authenticationManager;
        this.registrationRateLimiter = registrationRateLimiter;
    }

    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            boolean funcionario = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_FUNCIONARIO".equals(authority.getAuthority())
                            || "ROLE_ADMIN".equals(authority.getAuthority()));
            return "redirect:" + (funcionario ? "/administracao/catalogos" : "/dashboard");
        }
        if (!model.containsAttribute("registerForm")) {
            model.addAttribute("registerForm", new RegisterForm());
        }
        if (!model.containsAttribute("authMode")) {
            model.addAttribute("authMode", "login");
        }
        return "auth/login";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm registerForm,
                           BindingResult bindingResult,
                           Model model,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        model.addAttribute("authMode", "register");

        if (bindingResult.hasErrors()) {
            model.addAttribute("registerError", "Revise os campos destacados e tente novamente.");
            return "auth/login";
        }

        String rateLimitKey = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        if (!registrationRateLimiter.tryAcquire(rateLimitKey)) {
            logger.warn("Cadastro bloqueado por rate limit. ip={}", rateLimitKey);
            model.addAttribute("registerError", "Muitas tentativas de cadastro. Aguarde alguns minutos e tente novamente.");
            return "auth/login";
        }

        try {
            integranteService.registrarCliente(registerForm);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerForm.getEmail(), registerForm.getSenha())
            );
            SecurityContext context = new SecurityContextImpl(authentication);
            SecurityContextHolder.setContext(context);
            HttpSession session = request.getSession(true);
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);

            redirectAttributes.addFlashAttribute("dashboardMessage", "Conta criada com sucesso. Bem-vindo(a)!");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("registerError", ex.getMessage());
            return "auth/login";
        }
    }
}
