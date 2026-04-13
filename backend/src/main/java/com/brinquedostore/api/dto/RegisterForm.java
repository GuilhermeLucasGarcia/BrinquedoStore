package com.brinquedostore.api.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "Informe seu nome completo.")
    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 255, message = "O e-mail deve ter no máximo 255 caracteres.")
    private String email;

    @NotBlank(message = "Informe uma senha.")
    @Size(min = 8, max = 255, message = "A senha deve ter entre 8 e 255 caracteres.")
    private String senha;

    @NotBlank(message = "Confirme sua senha.")
    private String confirmarSenha;

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }
}
