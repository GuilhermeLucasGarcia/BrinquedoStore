package com.brinquedostore.api.dto;

import com.brinquedostore.api.model.PerfilUsuario;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class IntegranteAdminForm {

    private Long id;

    @NotBlank(message = "Informe o nome do usuário.")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.")
    private String nome;

    @Size(max = 500, message = "A URL da imagem deve ter no máximo 500 caracteres.")
    private String imgUrl;

    @NotBlank(message = "Informe o nome de usuário.")
    @Size(max = 255, message = "O nome de usuário deve ter no máximo 255 caracteres.")
    private String nomeUsuario;

    @Email(message = "Informe um e-mail válido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    private String email;

    @Size(max = 255, message = "A senha deve ter no máximo 255 caracteres.")
    private String senha;

    private PerfilUsuario perfil = PerfilUsuario.CLIENTE;

    private LocalDateTime dtAlteracao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

    public LocalDateTime getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(LocalDateTime dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }
}
