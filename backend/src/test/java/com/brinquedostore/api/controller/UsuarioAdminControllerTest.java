package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
import com.brinquedostore.api.dto.IntegranteAdminForm;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.PerfilUsuario;
import com.brinquedostore.api.service.CarrinhoService;
import com.brinquedostore.api.service.IntegranteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(UsuarioAdminController.class)
@Import(SecurityConfig.class)
class UsuarioAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IntegranteService integranteService;

    @MockBean
    private CarrinhoService carrinhoService;

    @Test
    void deveRedirecionarParaLoginQuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/administracao/usuarios"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarUsuarios() throws Exception {
        when(integranteService.buscarPorNome("")).thenReturn(Collections.singletonList(criarIntegrante()));

        mockMvc.perform(get("/administracao/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios"))
                .andExpect(model().attributeExists("usuarios"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAbrirTelaDeEdicao() throws Exception {
        when(integranteService.buscarPorId(eq(1L))).thenReturn(Optional.of(criarIntegrante()));
        when(integranteService.criarFormulario(any(Integrante.class))).thenReturn(criarFormulario());

        mockMvc.perform(get("/administracao/usuarios/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/formulario-usuario"))
                .andExpect(model().attributeExists("usuarioForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveSalvarUsuarioComSucesso() throws Exception {
        when(integranteService.salvar(any(IntegranteAdminForm.class))).thenReturn(criarIntegrante());

        mockMvc.perform(post("/administracao/usuarios/salvar")
                        .with(csrf())
                        .param("nome", "Ana Souza")
                        .param("nomeUsuario", "ana.souza")
                        .param("senha", "ana123")
                        .param("perfil", "FUNCIONARIO")
                        .param("imgUrl", "https://exemplo.com/ana.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administracao/usuarios"))
                .andExpect(flash().attributeExists("mensagem"));

        verify(integranteService).salvar(any(IntegranteAdminForm.class));
    }

    private Integrante criarIntegrante() {
        Integrante integrante = new Integrante();
        integrante.setId(1L);
        integrante.setNome("Ana Souza");
        integrante.setNomeUsuario("ana.souza");
        integrante.setImgUrl("https://exemplo.com/ana.jpg");
        integrante.setSenha("$2a$10$hash");
        integrante.setPerfil(PerfilUsuario.FUNCIONARIO);
        return integrante;
    }

    private IntegranteAdminForm criarFormulario() {
        IntegranteAdminForm form = new IntegranteAdminForm();
        form.setId(1L);
        form.setNome("Ana Souza");
        form.setNomeUsuario("ana.souza");
        form.setImgUrl("https://exemplo.com/ana.jpg");
        form.setPerfil(PerfilUsuario.FUNCIONARIO);
        return form;
    }
}
