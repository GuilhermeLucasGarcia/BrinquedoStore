package com.brinquedostore.api.service;

import com.brinquedostore.api.dto.IntegranteAdminForm;
import com.brinquedostore.api.dto.RegisterForm;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.PerfilUsuario;
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

    public List<Integrante> listarFuncionarios() {
        return integranteRepository.findAllByPerfilOrderByNomeAsc(PerfilUsuario.FUNCIONARIO);
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
        form.setEmail(integrante.getEmail());
        form.setPerfil(integrante.getPerfil());
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
        integrante.setEmail(normalizarEmail(form.getEmail()));
        integrante.setPerfil(form.getPerfil() != null ? form.getPerfil() : PerfilUsuario.CLIENTE);

        if (form.getSenha() != null && !form.getSenha().trim().isEmpty()) {
            integrante.setSenha(passwordEncoder.encode(form.getSenha().trim()));
        }

        return integranteRepository.save(integrante);
    }

    public Integrante registrarCliente(RegisterForm form) {
        validarCadastro(form);

        Integrante integrante = new Integrante();
        integrante.setNome(form.getNomeCompleto().trim());
        integrante.setEmail(normalizarEmail(form.getEmail()));
        integrante.setNomeUsuario(normalizarEmail(form.getEmail()));
        integrante.setSenha(passwordEncoder.encode(form.getSenha().trim()));
        integrante.setPerfil(PerfilUsuario.CLIENTE);

        return integranteRepository.save(integrante);
    }

    private void validarFormulario(IntegranteAdminForm form) {
        String nomeUsuario = form.getNomeUsuario() != null ? form.getNomeUsuario().trim() : "";
        String email = normalizarEmail(form.getEmail());
        if (form.getId() == null && (form.getSenha() == null || form.getSenha().trim().isEmpty())) {
            throw new IllegalArgumentException("Informe a senha para o novo usuário.");
        }
        if (form.getId() == null) {
            if (integranteRepository.existsByNomeUsuarioIgnoreCase(nomeUsuario)) {
                throw new IllegalArgumentException("Já existe um usuário com este nome de usuário.");
            }
            if (email != null && integranteRepository.existsByEmailIgnoreCase(email)) {
                throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
            }
            return;
        }
        if (integranteRepository.existsByNomeUsuarioIgnoreCaseAndIdNot(nomeUsuario, form.getId())) {
            throw new IllegalArgumentException("Já existe um usuário com este nome de usuário.");
        }
        if (email != null && integranteRepository.existsByEmailIgnoreCaseAndIdNot(email, form.getId())) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }
    }

    public boolean senhaAtendePolitica(String senha) {
        if (senha == null) {
            return false;
        }
        String valor = senha.trim();
        if (valor.length() < 8) {
            return false;
        }
        return valor.matches(".*[A-Z].*")
                && valor.matches(".*[a-z].*")
                && valor.matches(".*\\d.*")
                && valor.matches(".*[^A-Za-z0-9].*");
    }

    private void validarCadastro(RegisterForm form) {
        String email = normalizarEmail(form.getEmail());
        if (email == null) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (integranteRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Já existe uma conta cadastrada com este e-mail.");
        }
        if (!senhaAtendePolitica(form.getSenha())) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres, com maiúscula, minúscula, número e caractere especial.");
        }
        if (form.getConfirmarSenha() == null || !form.getConfirmarSenha().equals(form.getSenha())) {
            throw new IllegalArgumentException("A confirmação de senha não confere.");
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
