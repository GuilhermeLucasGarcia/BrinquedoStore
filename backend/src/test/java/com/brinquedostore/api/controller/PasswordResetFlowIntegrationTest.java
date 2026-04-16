package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.CatalogoCategoriaMigrationInitializer;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.PerfilUsuario;
import com.brinquedostore.api.repository.IntegranteRepository;
import com.brinquedostore.api.service.PasswordResetEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:passwordreset;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class PasswordResetFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PasswordResetEmailService passwordResetEmailService;

    @MockBean
    private CatalogoCategoriaMigrationInitializer catalogoCategoriaMigrationInitializer;

    @BeforeEach
    void setUp() {
        integranteRepository.deleteAll();

        Integrante integrante = new Integrante();
        integrante.setNome("Maria Silva");
        integrante.setNomeUsuario("maria@exemplo.com");
        integrante.setEmail("maria@exemplo.com");
        integrante.setSenha(passwordEncoder.encode("Atual@123"));
        integrante.setPerfil(PerfilUsuario.CLIENTE);
        integranteRepository.save(integrante);
    }

    @Test
    void deveExecutarFluxoCompletoDeRedefinicao() throws Exception {
        mockMvc.perform(post("/esqueci-senha")
                        .with(csrf())
                        .param("email", "maria@exemplo.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        ArgumentCaptor<String> tokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(passwordResetEmailService).enviarEmailRedefinicao(eq("maria@exemplo.com"), tokenCaptor.capture());
        String token = tokenCaptor.getValue();

        mockMvc.perform(get("/redefinir-senha").param("token", token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/redefinir-senha")
                        .with(csrf())
                        .param("token", token)
                        .param("senha", "Nova@123")
                        .param("confirmarSenha", "Nova@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Integrante atualizado = integranteRepository.findByEmailIgnoreCase("maria@exemplo.com").orElseThrow();
        assertThat(passwordEncoder.matches("Nova@123", atualizado.getSenha())).isTrue();
        assertThat(atualizado.getResetTokenHash()).isNull();
        assertThat(atualizado.getSenhaAnterior1()).isNotNull();
    }
}
