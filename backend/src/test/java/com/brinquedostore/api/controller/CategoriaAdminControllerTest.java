package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.service.CarrinhoService;
import com.brinquedostore.api.service.CategoriaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
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

@WebMvcTest(CategoriaAdminController.class)
@Import(SecurityConfig.class)
class CategoriaAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private CarrinhoService carrinhoService;

    @Test
    void deveRedirecionarParaLoginQuandoUsuarioNaoAutenticado() throws Exception {
        mockMvc.perform(get("/administracao/catalogos"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveListarCatalogosComFiltros() throws Exception {
        when(categoriaService.listarComFiltros("", null, 0, 8))
                .thenReturn(new PageImpl<>(Collections.singletonList(criarCategoria())));

        mockMvc.perform(get("/administracao/catalogos"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/catalogos"))
                .andExpect(model().attributeExists("catalogosPage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveSalvarCatalogoEExibirMensagem() throws Exception {
        when(categoriaService.salvar(any(Categoria.class))).thenReturn(criarCategoria());

        mockMvc.perform(post("/administracao/catalogos/salvar")
                        .with(csrf())
                        .param("nome", "Catálogo Verão")
                        .param("descricao", "Campanha de verão")
                        .param("imgUrl", "https://exemplo.com/catalogo.jpg")
                        .param("ativo", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administracao/catalogos"))
                .andExpect(flash().attributeExists("mensagem"));

        verify(categoriaService).salvar(any(Categoria.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveVoltarParaFormularioQuandoSalvarCatalogoInvalido() throws Exception {
        mockMvc.perform(post("/administracao/catalogos/salvar")
                        .with(csrf())
                        .param("nome", "")
                        .param("descricao", "")
                        .param("ativo", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/formulario-catalogo"))
                .andExpect(model().attributeHasFieldErrors("categoria", "nome", "descricao"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveAbrirTelaEdicao() throws Exception {
        when(categoriaService.buscarPorId(eq(1L))).thenReturn(Optional.of(criarCategoria()));

        mockMvc.perform(get("/administracao/catalogos/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/formulario-catalogo"))
                .andExpect(model().attributeExists("categoria"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveExibirErroAoExcluirCategoriaVinculadaAProdutos() throws Exception {
        org.mockito.Mockito.doThrow(new IllegalStateException("Não é possível excluir um catálogo vinculado a produtos."))
                .when(categoriaService).excluir(1L);

        mockMvc.perform(post("/administracao/catalogos/excluir/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administracao/catalogos"))
                .andExpect(flash().attributeExists("erro"));
    }

    private Categoria criarCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Catálogo Verão");
        categoria.setDescricao("Campanha de verão");
        categoria.setAtivo(Boolean.TRUE);
        categoria.setImgUrl("https://exemplo.com/catalogo.jpg");
        return categoria;
    }
}
