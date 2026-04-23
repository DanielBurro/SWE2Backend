package com.dhbw.backend.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    public void sendVerificationEmail(String toEmail, String token) {
        // Die URL, auf die der Nutzer in der E-Mail klickt. 
        // Fürs lokale Testen nehmen wir localhost. Später ersetzt du das durch deine echte Domain.
        String verificationUrl = frontendUrl + "/api/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("d.burrows2310@gmail.com"); 
        message.setTo(toEmail);
        message.setSubject("Bitte bestätige deine E-Mail-Adresse für die Einladung");
        message.setText("Hallo,\n\nbitte klicke auf den folgenden Link, um deine Registrierung abzuschließen:\n" 
                + verificationUrl + "\n\nDer Link ist 24 Stunden gültig.");

        mailSender.send(message);
    }
}