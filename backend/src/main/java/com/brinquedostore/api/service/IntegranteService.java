package com.brinquedostore.api.service;

import com.brinquedostore.api.dto.IntegranteAdminForm;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.repository.IntegranteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IntegranteService {
    private final IntegranteRepository integranteRepository;
    private final PasswordEncoder passwordEncoder;

    public IntegranteService(IntegranteRepository integranteRepository, PasswordEncoder passwordEncoder) {
        this.integranteRepository = integranteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Integrante> listarTodos() {
        return integranteRepository.findAllByOrderByNomeAsc();
    }

    public List<Integrante> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return integranteRepository.findAllByOrderByNomeAsc();
        }
        String termo = nome.trim();
        return integranteRepository.findByNomeContainingIgnoreCaseOrNomeUsuarioContainingIgnoreCaseOrderByNomeAsc(termo, termo);
    }

    public List<Integrante> buscarSugestoesPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        String termo = nome.trim();
        return integranteRepository.findTop8ByNomeContainingIgnoreCaseOrNomeUsuarioContainingIgnoreCaseOrderByNomeAsc(termo, termo);
    }

    public Optional<Integrante> buscarPorId(Long id) {
        return integranteRepository.findById(id);
    }

    public IntegranteAdminForm criarFormulario() {
        return new IntegranteAdminForm();
    }

    public IntegranteAdminForm criarFormulario(Integrante integrante) {
        IntegranteAdminForm form = new IntegranteAdminForm();
        form.setId(integrante.getId());
        form.setNome(integrante.getNome());
        form.setImgUrl(integrante.getImgUrl());
        form.setNomeUsuario(integrante.getNomeUsuario());
        form.setDtAlteracao(integrante.getDtAlteracao());
        return form;
    }

    public Integrante salvar(IntegranteAdminForm form) {
        validarFormulario(form);

        Integrante integrante = form.getId() != null
                ? integranteRepository.findById(form.getId()).orElseThrow(() -> new IllegalArgumentException("Usuário inválido: " + form.getId()))
                : new Integrante();

        integrante.setNome(form.getNome().trim());
        integrante.setImgUrl(form.getImgUrl() != null && !form.getImgUrl().trim().isEmpty() ? form.getImgUrl().trim() : null);
        integrante.setNomeUsuario(form.getNomeUsuario().trim());

        if (form.getSenha() != null && !form.getSenha().trim().isEmpty()) {
            integrante.setSenha(passwordEncoder.encode(form.getSenha().trim()));
        }

        return integranteRepository.save(integrante);
    }

    private void validarFormulario(IntegranteAdminForm form) {
        String nomeUsuario = form.getNomeUsuario() != null ? form.getNomeUsuario().trim() : "";
        if (form.getId() == null && (form.getSenha() == null || form.getSenha().trim().isEmpty())) {
            throw new IllegalArgumentException("Informe a senha para o novo usuário.");
        }
        if (form.getId() == null) {
            if (integranteRepository.existsByNomeUsuarioIgnoreCase(nomeUsuario)) {
                throw new IllegalArgumentException("Já existe um usuário com este nome de usuário.");
            }
            return;
        }
        if (integranteRepository.existsByNomeUsuarioIgnoreCaseAndIdNot(nomeUsuario, form.getId())) {
            throw new IllegalArgumentException("Já existe um usuário com este nome de usuário.");
        }
    }
}
