package com.brinquedostore.api.service;

import com.brinquedostore.api.model.Categoria;
import com.brinquedostore.api.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;
    private final BrinquedoService brinquedoService;

    public CategoriaService(CategoriaRepository categoriaRepository, BrinquedoService brinquedoService) {
        this.categoriaRepository = categoriaRepository;
        this.brinquedoService = brinquedoService;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAllByOrderByNomeAsc();
    }

    public List<Categoria> listarAtivas() {
        return categoriaRepository.findByAtivoTrueOrderByNomeAsc();
    }

    public List<Categoria> buscarSugestoesAtivasPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return categoriaRepository.findTop8ByAtivoTrueAndNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());
    }

    public List<Categoria> buscarSugestoesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return categoriaRepository.findTop8ByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());
    }

    public Page<Categoria> listarComFiltros(String nome, Boolean ativo, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "dtAlteracao"));
        String nomeNormalizado = nome != null && !nome.trim().isEmpty() ? nome.trim() : null;
        return categoriaRepository.buscarComFiltros(nomeNormalizado, ativo, pageable);
    }

    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> buscarPorNome(String nome) {
        return categoriaRepository.findByNomeIgnoreCase(nome);
    }

    public Categoria salvar(Categoria categoria) {
        validarNomeDuplicado(categoria);
        if (categoria.getAtivo() == null) {
            categoria.setAtivo(Boolean.FALSE);
        }
        if (categoria.getDescricao() == null || categoria.getDescricao().trim().isEmpty()) {
            categoria.setDescricao("Catálogo " + categoria.getNome());
        }
        return categoriaRepository.save(categoria);
    }

    public void excluir(Long id) {
        if (brinquedoService.existeBrinquedoNaCategoria(id)) {
            throw new IllegalStateException("Não é possível excluir um catálogo vinculado a produtos.");
        }
        categoriaRepository.deleteById(id);
    }

    private void validarNomeDuplicado(Categoria categoria) {
        String nome = categoria.getNome() != null ? categoria.getNome().trim() : "";
        if (categoria.getId() == null) {
            if (categoriaRepository.existsByNomeIgnoreCase(nome)) {
                throw new IllegalArgumentException("Já existe um catálogo com este nome.");
            }
            return;
        }

        if (categoriaRepository.existsByNomeIgnoreCaseAndIdNot(nome, categoria.getId())) {
            throw new IllegalArgumentException("Já existe um catálogo com este nome.");
        }
    }
}
