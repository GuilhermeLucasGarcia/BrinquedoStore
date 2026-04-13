package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CarrinhoService;
import com.brinquedostore.api.service.CategoriaService;
import com.brinquedostore.api.service.IntegranteService;
import com.brinquedostore.api.service.MarcaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@Import(SecurityConfig.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrinquedoService brinquedoService;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private MarcaService marcaService;

    @MockBean
    private IntegranteService integranteService;

    @MockBean
    private CarrinhoService carrinhoService;

    @Test
    void deveBuscarPublicamenteComTermoParcial() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Lego");
        categoria.setDescricao("Catalogo Lego");
        categoria.setAtivo(true);

        Brinquedo brinquedo = new Brinquedo();
        brinquedo.setId(2L);
        brinquedo.setNome("Lego City");
        brinquedo.setCategoria(categoria);

        when(categoriaService.buscarSugestoesAtivasPorNome("LE")).thenReturn(Collections.singletonList(categoria));
        when(brinquedoService.buscarSugestoesPorNome("LE")).thenReturn(Collections.singletonList(brinquedo));

        mockMvc.perform(get("/api/search/public").param("q", "LE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("catalogo"))
                .andExpect(jsonPath("$[0].label").value("Lego"))
                .andExpect(jsonPath("$[1].type").value("produto"))
                .andExpect(jsonPath("$[1].label").value("Lego City"));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    void deveBuscarNoAdminComTermoParcial() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setId(3L);
        categoria.setNome("Educativos");
        categoria.setDescricao("Catalogo");
        categoria.setAtivo(true);

        Marca marca = new Marca();
        marca.setId(4L);
        marca.setNome("Estrela");

        Integrante integrante = new Integrante();
        integrante.setId(6L);
        integrante.setNome("Estela Rocha");
        integrante.setNomeUsuario("estela.rocha");

        Brinquedo brinquedo = new Brinquedo();
        brinquedo.setId(5L);
        brinquedo.setNome("Quebra Cabeca");
        brinquedo.setCategoria(categoria);

        when(brinquedoService.buscarSugestoesPorNome("est")).thenReturn(Collections.singletonList(brinquedo));
        when(categoriaService.buscarSugestoesPorNome("est")).thenReturn(Collections.singletonList(categoria));
        when(marcaService.buscarSugestoesPorNome("est")).thenReturn(Collections.singletonList(marca));
        when(integranteService.buscarSugestoesPorNome("est")).thenReturn(Collections.singletonList(integrante));

        mockMvc.perform(get("/api/search/admin").param("q", "est"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].url").value("/administracao/editar/5"))
                .andExpect(jsonPath("$[1].url").value("/administracao/catalogos/editar/3"))
                .andExpect(jsonPath("$[2].url").value("/administracao/marcas?nome=Estrela"))
                .andExpect(jsonPath("$[3].url").value("/administracao/usuarios/editar/6"));
    }

    @Test
    void deveExigirAutenticacaoNaBuscaAdmin() throws Exception {
        mockMvc.perform(get("/api/search/admin").param("q", "lego"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteDeveReceberForbiddenNaBuscaAdmin() throws Exception {
        mockMvc.perform(get("/api/search/admin").param("q", "lego"))
                .andExpect(status().isForbidden());
    }
}
