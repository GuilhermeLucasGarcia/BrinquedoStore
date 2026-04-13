package com.brinquedostore.api.controller;

import com.brinquedostore.api.config.SecurityConfig;
import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CarrinhoService;
import com.brinquedostore.api.service.CategoriaService;
import com.brinquedostore.api.service.IntegranteService;
import com.brinquedostore.api.service.MarcaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PublicController.class, AdminController.class, CategoriaAdminController.class, UsuarioAdminController.class})
@Import(SecurityConfig.class)
class NavigationMenuIntegrationTest {

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

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Aventura");
        categoria.setDescricao("Categoria teste");
        categoria.setAtivo(true);

        Marca marca = new Marca();
        marca.setId(1L);
        marca.setNome("Marca Teste");

        Brinquedo brinquedo = new Brinquedo();
        brinquedo.setId(1L);
        brinquedo.setNome("Brinquedo Teste");
        brinquedo.setDescricao("Descricao");
        brinquedo.setCategoria(categoria);
        brinquedo.setMarca(marca);
        brinquedo.setImagemUrl("https://exemplo.com/brinquedo.jpg");
        brinquedo.setValor(99.9);

        when(brinquedoService.listarTodos()).thenReturn(Collections.singletonList(brinquedo));
        when(brinquedoService.buscarPorCategoria(anyString())).thenReturn(Collections.singletonList(brinquedo));
        when(categoriaService.listarAtivas()).thenReturn(Collections.singletonList(categoria));
        when(categoriaService.listarTodas()).thenReturn(Collections.singletonList(categoria));
        when(categoriaService.buscarPorNome(anyString())).thenReturn(Optional.of(categoria));
        when(marcaService.listarTodas()).thenReturn(Collections.singletonList(marca));
        when(integranteService.listarTodos()).thenReturn(Collections.emptyList());
    }

    @Test
    void naoDeveRenderizarMenuAdminParaVisitanteNaHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/login\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Entrar")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/administracao/catalogos"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Catálogos"))));
    }

    @Test
    void naoDeveRenderizarMenuAdminParaVisitanteNoCatalogoPublico() throws Exception {
        mockMvc.perform(get("/catalogo"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/administracao/catalogos"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Catálogos"))));
    }

    @Test
    void naoDeveRenderizarMenuAdminParaClienteNaPaginaSobre() throws Exception {
        mockMvc.perform(get("/sobre").with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("cliente").roles("CLIENTE")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/administracao/catalogos"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Catálogos"))));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    void deveRenderizarMenuAdminParaFuncionarioNaHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("href=\"/administracao/catalogos\" class=\"btn btn-light btn-sm rounded-pill text-primary shadow-sm\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Painel")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("href=\"/login\" class=\"btn btn-light btn-sm rounded-pill text-primary shadow-sm\""))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/catalogos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Catálogos")));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    void deveExibirTodosOsItensDoSubmenuNoPrimeiroAcessoAoAdminProdutos() throws Exception {
        mockMvc.perform(get("/administracao"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("aria-expanded=\"true\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"adminSubmenu\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/catalogos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/marcas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/usuarios")));
    }

    @Test
    @WithMockUser(roles = "FUNCIONARIO")
    void deveExibirTodosOsItensDoSubmenuNoPrimeiroAcessoAoAdminCatalogos() throws Exception {
        when(categoriaService.listarComFiltros("", null, 0, 8))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.singletonList(categoriaMock())));

        mockMvc.perform(get("/administracao/catalogos"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("aria-expanded=\"true\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"adminSubmenu\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/catalogos")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/marcas")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/administracao/usuarios")));
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNaoDeveVerMenuAdminNaHome() throws Exception {
        mockMvc.perform(get("/sobre"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("href=\"/login\" class=\"btn btn-light btn-sm rounded-pill text-primary shadow-sm\""))))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Sair")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("/administracao/catalogos"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Catálogos"))));
    }

    private Categoria categoriaMock() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Catalogo Teste");
        categoria.setDescricao("Descricao teste");
        categoria.setAtivo(true);
        return categoria;
    }
}
