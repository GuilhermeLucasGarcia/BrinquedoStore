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
