package com.brinquedostore.api.controller;

import com.brinquedostore.api.dto.ForgotPasswordForm;
import com.brinquedostore.api.dto.RegisterForm;
import com.brinquedostore.api.dto.ResetPasswordForm;
import com.brinquedostore.api.security.PasswordResetRateLimiter;
import com.brinquedostore.api.security.RegistrationRateLimiter;
import com.brinquedostore.api.service.IntegranteService;
import com.brinquedostore.api.service.PasswordResetEmailService;
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
    private final PasswordResetRateLimiter passwordResetRateLimiter;
    private final PasswordResetEmailService passwordResetEmailService;

    public AuthController(IntegranteService integranteService,
                          AuthenticationManager authenticationManager,
                          RegistrationRateLimiter registrationRateLimiter,
                          PasswordResetRateLimiter passwordResetRateLimiter,
                          PasswordResetEmailService passwordResetEmailService) {
        this.integranteService = integranteService;
        this.authenticationManager = authenticationManager;
        this.registrationRateLimiter = registrationRateLimiter;
        this.passwordResetRateLimiter = passwordResetRateLimiter;
        this.passwordResetEmailService = passwordResetEmailService;
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

    @GetMapping("/esqueci-senha")
    public String forgotPassword(Model model) {
        if (!model.containsAttribute("forgotPasswordForm")) {
            model.addAttribute("forgotPasswordForm", new ForgotPasswordForm());
        }
        return "auth/forgot-password";
    }

    @PostMapping("/esqueci-senha")
    public String submitForgotPassword(@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm forgotPasswordForm,
                                       BindingResult bindingResult,
                                       Model model,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }

        String ip = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        if (!passwordResetRateLimiter.tryAcquire("request:" + ip)) {
            logger.warn("Redefinicao bloqueada por rate limit. tipo=request, ip={}", ip);
            model.addAttribute("forgotPasswordError", "Muitas tentativas de recuperação. Aguarde alguns minutos e tente novamente.");
            return "auth/forgot-password";
        }

        try {
            String token = integranteService.criarTokenRedefinicaoSenha(forgotPasswordForm.getEmail());
            passwordResetEmailService.enviarEmailRedefinicao(forgotPasswordForm.getEmail().trim().toLowerCase(), token);
            logger.info("Solicitacao de redefinicao criada. email={}, ip={}", forgotPasswordForm.getEmail(), ip);
            redirectAttributes.addFlashAttribute("loginSuccess", "Enviamos um link de redefinição para o seu e-mail.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            logger.warn("Falha na solicitacao de redefinicao. email={}, ip={}, motivo={}",
                    forgotPasswordForm.getEmail(), ip, ex.getMessage());
            model.addAttribute("forgotPasswordError", "E-mail não encontrado.");
            return "auth/forgot-password";
        } catch (Exception ex) {
            logger.error("Erro ao enviar email de redefinicao. email={}, ip={}", forgotPasswordForm.getEmail(), ip, ex);
            model.addAttribute("forgotPasswordError", "Não foi possível enviar o e-mail de redefinição agora. Tente novamente em instantes.");
            return "auth/forgot-password";
        }
    }

    @GetMapping("/redefinir-senha")
    public String resetPasswordForm(@org.springframework.web.bind.annotation.RequestParam(value = "token", required = false) String token,
                                    Model model) {
        ResetPasswordForm form = new ResetPasswordForm();
        form.setToken(token);
        if (!model.containsAttribute("resetPasswordForm")) {
            model.addAttribute("resetPasswordForm", form);
        }

        try {
            integranteService.validarTokenRedefinicao(token);
            model.addAttribute("tokenValido", true);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("tokenValido", false);
            model.addAttribute("resetPasswordError", "Token expirado ou inválido.");
        }

        return "auth/reset-password";
    }

    @PostMapping("/redefinir-senha")
    public String submitResetPassword(@Valid @ModelAttribute("resetPasswordForm") ResetPasswordForm resetPasswordForm,
                                      BindingResult bindingResult,
                                      Model model,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tokenValido", true);
            return "auth/reset-password";
        }

        String ip = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        if (!passwordResetRateLimiter.tryAcquire("confirm:" + ip)) {
            logger.warn("Redefinicao bloqueada por rate limit. tipo=confirm, ip={}", ip);
            model.addAttribute("tokenValido", true);
            model.addAttribute("resetPasswordError", "Muitas tentativas de redefinição. Aguarde alguns minutos e tente novamente.");
            return "auth/reset-password";
        }

        try {
            integranteService.redefinirSenha(
                    resetPasswordForm.getToken(),
                    resetPasswordForm.getSenha(),
                    resetPasswordForm.getConfirmarSenha()
            );
            logger.info("Senha redefinida com sucesso. ip={}", ip);
            redirectAttributes.addFlashAttribute("loginSuccess", "Senha redefinida com sucesso. Faça login com sua nova senha.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            logger.warn("Falha ao redefinir senha. ip={}, motivo={}", ip, ex.getMessage());
            model.addAttribute("tokenValido", !ex.getMessage().toLowerCase().contains("token"));
            model.addAttribute("resetPasswordError", traduzirErroReset(ex.getMessage()));
            return "auth/reset-password";
        }
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

    private String traduzirErroReset(String message) {
        if (message == null) {
            return "Não foi possível redefinir a senha.";
        }
        if (message.contains("Token")) {
            return "Token expirado ou inválido.";
        }
        if (message.contains("últimas 3 senhas")) {
            return "A nova senha não pode repetir nenhuma das últimas 3 utilizadas.";
        }
        return message;
    }
}
