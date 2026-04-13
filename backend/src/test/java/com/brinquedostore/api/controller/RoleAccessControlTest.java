package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AdminController.class, CategoriaAdminController.class, UsuarioAdminController.class, SearchController.class})
@Import(SecurityConfig.class)
class RoleAccessControlTest {

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
    @WithMockUser(roles = "CLIENTE")
    void clienteNaoPodeAcessarProdutosAdmin() throws Exception {
        mockMvc.perform(get("/administracao"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNaoPodeAcessarCatalogosAdmin() throws Exception {
        mockMvc.perform(get("/administracao/catalogos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNaoPodeAcessarUsuariosAdmin() throws Exception {
        mockMvc.perform(get("/administracao/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    void funcionarioPodeAcessarCatalogosAdmin() throws Exception {
        when(categoriaService.listarComFiltros("", null, 0, 8)).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/administracao/catalogos"))
                .andExpect(status().isOk());
    }
}
