package com.brinquedostore.api.model;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "\"INTEGRANTE\"")
public class Integrante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"NOME\"", nullable = false)
    private String nome;

    @Column(name = "\"IMG_URL\"")
    private String imgUrl;

    @Column(name = "\"NOME_USUARIO\"")
    private String nomeUsuario;

    @Column(name = "\"EMAIL\"")
    private String email;

    @Column(name = "\"SENHA\"")
    private String senha;

    @Column(name = "\"SENHA_ANTERIOR_1\"")
    private String senhaAnterior1;

    @Column(name = "\"SENHA_ANTERIOR_2\"")
    private String senhaAnterior2;

    @Column(name = "\"SENHA_ANTERIOR_3\"")
    private String senhaAnterior3;

    @Column(name = "\"RESET_TOKEN_HASH\"")
    private String resetTokenHash;

    @Column(name = "\"RESET_TOKEN_EXPIRA_EM\"")
    private LocalDateTime resetTokenExpiraEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"PERFIL\"")
    private PerfilUsuario perfil = PerfilUsuario.CLIENTE;

    @UpdateTimestamp
    @Column(name = "\"DT_ALTERACAO\"")
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

    public String getSenhaAnterior1() {
        return senhaAnterior1;
    }

    public void setSenhaAnterior1(String senhaAnterior1) {
        this.senhaAnterior1 = senhaAnterior1;
    }

    public String getSenhaAnterior2() {
        return senhaAnterior2;
    }

    public void setSenhaAnterior2(String senhaAnterior2) {
        this.senhaAnterior2 = senhaAnterior2;
    }

    public String getSenhaAnterior3() {
        return senhaAnterior3;
    }

    public void setSenhaAnterior3(String senhaAnterior3) {
        this.senhaAnterior3 = senhaAnterior3;
    }

    public String getResetTokenHash() {
        return resetTokenHash;
    }

    public void setResetTokenHash(String resetTokenHash) {
        this.resetTokenHash = resetTokenHash;
    }

    public LocalDateTime getResetTokenExpiraEm() {
        return resetTokenExpiraEm;
    }

    public void setResetTokenExpiraEm(LocalDateTime resetTokenExpiraEm) {
        this.resetTokenExpiraEm = resetTokenExpiraEm;
    }

    public LocalDateTime getDtAlteracao() {
        return dtAlteracao;
    }

    public void setDtAlteracao(LocalDateTime dtAlteracao) {
        this.dtAlteracao = dtAlteracao;
    }

    public PerfilUsuario getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }
}
