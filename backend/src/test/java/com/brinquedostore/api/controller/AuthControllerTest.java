package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
import com.brinquedostore.api.dto.RegisterForm;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.security.RegistrationRateLimiter;
import com.brinquedostore.api.service.CarrinhoService;
import com.brinquedostore.api.service.IntegranteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarrinhoService carrinhoService;

    @MockBean
    private IntegranteService integranteService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private RegistrationRateLimiter registrationRateLimiter;

    @Test
    void deveRenderizarPaginaDeLoginCustomizada() throws Exception {
        when(carrinhoService.getQuantidadeTotal()).thenReturn(0);

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Criar Conta")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveRedirecionarUsuarioAutenticadoAoAcessarLogin() throws Exception {
        when(carrinhoService.getQuantidadeTotal()).thenReturn(0);

        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administracao/catalogos"));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteAutenticadoDeveSerRedirecionadoParaHomeAoAcessarLogin() throws Exception {
        when(carrinhoService.getQuantidadeTotal()).thenReturn(0);

        mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void deveCadastrarClienteERedirecionarParaDashboard() throws Exception {
        when(carrinhoService.getQuantidadeTotal()).thenReturn(0);
        when(registrationRateLimiter.tryAcquire(any())).thenReturn(true);

        Integrante integrante = new Integrante();
        integrante.setNome("Maria Silva");
        integrante.setEmail("maria@exemplo.com");
        integrante.setNomeUsuario("maria@exemplo.com");
        when(integranteService.registrarCliente(any(RegisterForm.class))).thenReturn(integrante);
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("maria@exemplo.com", "Senha@123", java.util.Collections.emptyList()));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nomeCompleto", "Maria Silva")
                        .param("email", "maria@exemplo.com")
                        .param("senha", "Senha@123")
                        .param("confirmarSenha", "Senha@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }

    @Test
    void deveExibirErroQuandoCadastroForBloqueadoPorRateLimit() throws Exception {
        when(carrinhoService.getQuantidadeTotal()).thenReturn(0);
        when(registrationRateLimiter.tryAcquire(any())).thenReturn(false);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("nomeCompleto", "Maria Silva")
                        .param("email", "maria@exemplo.com")
                        .param("senha", "Senha@123")
                        .param("confirmarSenha", "Senha@123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Muitas tentativas de cadastro")));
    }
}
