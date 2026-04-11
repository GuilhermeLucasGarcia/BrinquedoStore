package com.brinquedostore.api.service;

import com.brinquedostore.api.model.Marca;
import com.brinquedostore.api.repository.MarcaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarcaService {
    private final MarcaRepository marcaRepository;

    public MarcaService(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public List<Marca> listarTodas() {
        return marcaRepository.findAll();
    }

    public List<Marca> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return marcaRepository.findAll();
        }
        return marcaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());
    }

    public List<Marca> buscarSugestoesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return marcaRepository.findTop8ByNomeContainingIgnoreCaseOrderByNomeAsc(nome.trim());
    }

    public Marca salvar(Marca marca) {
        return marcaRepository.save(marca);
    }

    public void deletar(Long id) {
        marcaRepository.deleteById(id);
    }
}
