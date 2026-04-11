package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CarrinhoService;
import com.brinquedostore.api.service.CategoriaService;
import com.brinquedostore.api.service.MarcaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
class AdminControllerFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrinquedoService brinquedoService;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private MarcaService marcaService;

    @MockBean
    private CarrinhoService carrinhoService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveFiltrarProdutosPorNome() throws Exception {
        Brinquedo brinquedo = criarBrinquedo("Lego City");
        when(brinquedoService.buscarPorNome("lego")).thenReturn(Collections.singletonList(brinquedo));

        mockMvc.perform(get("/administracao").param("nome", "lego"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/administracao"))
                .andExpect(model().attribute("nomeFiltro", "lego"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Lego City")));

        verify(brinquedoService).buscarPorNome("lego");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveFiltrarMarcasPorNome() throws Exception {
        Marca marca = new Marca();
        marca.setId(1L);
        marca.setNome("Estrela");
        when(marcaService.buscarPorNome("estre")).thenReturn(Collections.singletonList(marca));

        mockMvc.perform(get("/administracao/marcas").param("nome", "estre"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/marcas"))
                .andExpect(model().attribute("nomeFiltro", "estre"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Estrela")));

        verify(marcaService).buscarPorNome("estre");
    }

    private Brinquedo criarBrinquedo(String nome) {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Blocos");
        categoria.setDescricao("Descricao");
        categoria.setAtivo(true);

        Brinquedo brinquedo = new Brinquedo();
        brinquedo.setId(1L);
        brinquedo.setNome(nome);
        brinquedo.setCategoria(categoria);
        brinquedo.setValor(149.90);
        brinquedo.setImagemUrl("https://exemplo.com/brinquedo.jpg");
        return brinquedo;
    }
}
