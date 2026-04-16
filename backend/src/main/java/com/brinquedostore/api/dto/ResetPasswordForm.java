package com.brinquedostore.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ResetPasswordForm {

    @NotBlank(message = "Token de redefinição ausente.")
    private String token;

    @NotBlank(message = "Informe uma nova senha.")
    @Size(min = 8, max = 255, message = "A senha deve ter entre 8 e 255 caracteres.")
    private String senha;

    @NotBlank(message = "Confirme a nova senha.")
    private String confirmarSenha;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
