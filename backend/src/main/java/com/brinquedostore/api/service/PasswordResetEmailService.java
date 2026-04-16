package com.brinquedostore.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PasswordResetEmailService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetEmailService.class);

    private final JavaMailSender mailSender;
    private final String baseUrl;
    private final String from;
    private final String smtpHost;

    public PasswordResetEmailService(JavaMailSender mailSender,
                                    @Value("${app.base-url:http://localhost:8080}") String baseUrl,
                                    @Value("${app.mail.from:no-reply@behappy.local}") String from,
                                    @Value("${spring.mail.host:}") String smtpHost) {
        this.mailSender = mailSender;
        this.baseUrl = baseUrl != null ? baseUrl.replaceAll("/+$", "") : "http://localhost:8080";
        this.from = from;
        this.smtpHost = smtpHost;
    }

    public void enviarEmailRedefinicao(String email, String token) {
        String link = baseUrl + "/redefinir-senha?token=" + token;

        if (!StringUtils.hasText(smtpHost)) {
            logger.warn("SMTP nao configurado. Link de redefinicao disponivel apenas no log. email={}, link={}", email, link);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(from);
        message.setSubject("Redefinição de senha - BrinquedoStore");
        message.setText("Recebemos uma solicitação para redefinir sua senha.\n\n"
                + "Clique no link abaixo para criar uma nova senha (válido por 1 hora):\n"
                + link + "\n\n"
                + "Se você não solicitou esta alteração, ignore este e-mail.");

        mailSender.send(message);
        logger.info("Email de redefinicao enviado. email={}", email);
    }
}
