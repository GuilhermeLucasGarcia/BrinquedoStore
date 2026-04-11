package com.brinquedostore.api.controller;

import com.brinquedostore.api.dto.SearchSuggestionDto;
import com.brinquedostore.api.model.Brinquedo;
import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.service.BrinquedoService;
import com.brinquedostore.api.service.CategoriaService;
import com.brinquedostore.api.service.IntegranteService;
import com.brinquedostore.api.service.MarcaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final BrinquedoService brinquedoService;
    private final CategoriaService categoriaService;
    private final MarcaService marcaService;
    private final IntegranteService integranteService;

    public SearchController(BrinquedoService brinquedoService,
                            CategoriaService categoriaService,
                            MarcaService marcaService,
                            IntegranteService integranteService) {
        this.brinquedoService = brinquedoService;
        this.categoriaService = categoriaService;
        this.marcaService = marcaService;
        this.integranteService = integranteService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<SearchSuggestionDto>> buscarPublicamente(@RequestParam(defaultValue = "") String q) {
        String termo = normalizar(q);
        if (termo.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<SearchSuggestionDto> resultados = new ArrayList<>();
        for (Categoria categoria : categoriaService.buscarSugestoesAtivasPorNome(termo)) {
            resultados.add(new SearchSuggestionDto(
                    "catalogo",
                    categoria.getNome(),
                    "Catálogo",
                    "/catalogo/" + UriUtils.encodePathSegment(categoria.getNome(), StandardCharsets.UTF_8)
            ));
        }
        for (Brinquedo brinquedo : brinquedoService.buscarSugestoesPorNome(termo)) {
            String categoria = brinquedo.getCategoria() != null ? brinquedo.getCategoria().getNome() : "Produto";
            resultados.add(new SearchSuggestionDto(
                    "produto",
                    brinquedo.getNome(),
                    categoria,
                    "/detalhes/" + brinquedo.getId()
            ));
        }

        return ResponseEntity.ok(resultados);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<SearchSuggestionDto>> buscarNoAdmin(@RequestParam(defaultValue = "") String q) {
        String termo = normalizar(q);
        if (termo.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<SearchSuggestionDto> resultados = new ArrayList<>();
        for (Brinquedo brinquedo : brinquedoService.buscarSugestoesPorNome(termo)) {
            String categoria = brinquedo.getCategoria() != null ? brinquedo.getCategoria().getNome() : "Produto";
            resultados.add(new SearchSuggestionDto(
                    "produto",
                    brinquedo.getNome(),
                    "Produto • " + categoria,
                    "/administracao/editar/" + brinquedo.getId()
            ));
        }
        for (Categoria categoria : categoriaService.buscarSugestoesPorNome(termo)) {
            resultados.add(new SearchSuggestionDto(
                    "catalogo",
                    categoria.getNome(),
                    "Catálogo",
                    "/administracao/catalogos/editar/" + categoria.getId()
            ));
        }
        for (Marca marca : marcaService.buscarSugestoesPorNome(termo)) {
            resultados.add(new SearchSuggestionDto(
                    "marca",
                    marca.getNome(),
                    "Marca",
                    "/administracao/marcas?nome=" + UriUtils.encodeQueryParam(marca.getNome(), StandardCharsets.UTF_8)
            ));
        }
        for (Integrante integrante : integranteService.buscarSugestoesPorNome(termo)) {
            resultados.add(new SearchSuggestionDto(
                    "usuario",
                    integrante.getNome(),
                    "Usuário • " + (integrante.getNomeUsuario() != null ? integrante.getNomeUsuario() : "sem login"),
                    "/administracao/usuarios/editar/" + integrante.getId()
            ));
        }

        return ResponseEntity.ok(resultados);
    }

    private String normalizar(String termo) {
        return termo == null ? "" : termo.trim();
    }
}
