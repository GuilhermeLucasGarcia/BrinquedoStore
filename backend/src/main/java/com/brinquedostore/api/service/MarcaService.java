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

    public Marca salvar(Marca marca) {
        return marcaRepository.save(marca);
    }

    public void deletar(Long id) {
        marcaRepository.deleteById(id);
    }
}
