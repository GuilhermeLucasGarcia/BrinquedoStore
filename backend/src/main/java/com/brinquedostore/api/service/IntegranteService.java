package com.brinquedostore.api.service;

import com.brinquedostore.api.dto.IntegranteAdminForm;
import com.brinquedostore.api.dto.RegisterForm;
import com.brinquedostore.api.model.Integrante;
import com.brinquedostore.api.model.PerfilUsuario;
import com.brinquedostore.api.repository.IntegranteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class IntegranteService {
    private final IntegranteRepository integranteRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

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
            String raw = form.getSenha().trim();
            if (!senhaAtendePolitica(raw)) {
                throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres, com maiúscula, minúscula, número e caractere especial.");
            }
            if (senhaReutilizada(integrante, raw)) {
                throw new IllegalArgumentException("Você não pode reutilizar nenhuma das suas últimas 3 senhas.");
            }
            atualizarSenha(integrante, raw);
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

    public String criarTokenRedefinicaoSenha(String email) {
        String normalizado = normalizarEmail(email);
        if (normalizado == null) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }

        Integrante integrante = integranteRepository.findByEmailIgnoreCase(normalizado)
                .orElseThrow(() -> new IllegalArgumentException("E-mail não encontrado."));

        String token = gerarTokenSeguro();
        integrante.setResetTokenHash(sha256Base64Url(token));
        integrante.setResetTokenExpiraEm(LocalDateTime.now().plus(Duration.ofHours(1)));
        integranteRepository.save(integrante);
        return token;
    }

    public Integrante validarTokenRedefinicao(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token inválido.");
        }

        String hash = sha256Base64Url(token.trim());
        Integrante integrante = integranteRepository.findByResetTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido."));

        if (integrante.getResetTokenExpiraEm() == null || integrante.getResetTokenExpiraEm().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expirado.");
        }

        return integrante;
    }

    public void redefinirSenha(String token, String novaSenha, String confirmarSenha) {
        if (confirmarSenha == null || !confirmarSenha.equals(novaSenha)) {
            throw new IllegalArgumentException("A confirmação de senha não confere.");
        }
        if (!senhaAtendePolitica(novaSenha)) {
            throw new IllegalArgumentException("A senha deve ter no mínimo 8 caracteres, com maiúscula, minúscula, número e caractere especial.");
        }

        Integrante integrante = validarTokenRedefinicao(token);
        String raw = novaSenha.trim();

        if (senhaReutilizada(integrante, raw)) {
            throw new IllegalArgumentException("Você não pode reutilizar nenhuma das suas últimas 3 senhas.");
        }

        atualizarSenha(integrante, raw);
        integranteRepository.save(integrante);
    }

    private boolean senhaReutilizada(Integrante integrante, String raw) {
        if (integrante.getSenha() != null && passwordEncoder.matches(raw, integrante.getSenha())) {
            return true;
        }
        if (integrante.getSenhaAnterior1() != null && passwordEncoder.matches(raw, integrante.getSenhaAnterior1())) {
            return true;
        }
        if (integrante.getSenhaAnterior2() != null && passwordEncoder.matches(raw, integrante.getSenhaAnterior2())) {
            return true;
        }
        return integrante.getSenhaAnterior3() != null && passwordEncoder.matches(raw, integrante.getSenhaAnterior3());
    }

    private void atualizarSenha(Integrante integrante, String raw) {
        integrante.setSenhaAnterior3(integrante.getSenhaAnterior2());
        integrante.setSenhaAnterior2(integrante.getSenhaAnterior1());
        integrante.setSenhaAnterior1(integrante.getSenha());
        integrante.setSenha(passwordEncoder.encode(raw));
        integrante.setResetTokenHash(null);
        integrante.setResetTokenExpiraEm(null);
    }

    private String normalizarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    private String gerarTokenSeguro() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    String gerarHashToken(String valor) {
        return sha256Base64Url(valor);
    }

    private String sha256Base64Url(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha ao gerar hash.", ex);
        }
    }
}
