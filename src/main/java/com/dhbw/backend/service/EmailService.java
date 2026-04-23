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
        String verificationUrl = frontendUrl + "/api/auth/verify?token=" + token;
    
        // Das hier ist dein Rettungsanker:
        System.out.println("\n************************************************");
        System.out.println("VERIFIZIERUNGS-LINK (Kopieren & im Browser öffnen):");
        System.out.println(verificationUrl);
        System.out.println("************************************************\n");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("d.burrows2310@gmail.com"); // Deine verifizierte Absender-Mail
            message.setTo(toEmail);
            message.setSubject("Bitte verifiziere dein Konto");
            message.setText("Klicke hier: " + verificationUrl);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("SMTP-Fehler (wird ignoriert): " + e.getMessage());
        }
    }

    public void sendInvitationEmail(String toEmail, String guestName, String eventName, String token) {
        // Personalisierter Link für die Einladung
        String invitationUrl = frontendUrl + "/invitation/view?token=" + token;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("d.burrows2310@gmail.com"); 
            message.setTo(toEmail);
            message.setSubject("Einladung zu " + eventName);
            message.setText("Hallo " + guestName + ",\n\ndu wurdest zum Event '" + eventName + "' eingeladen!\n\n" 
                    + "Klicke auf den folgenden Link, um die Details zu sehen und zu- oder abzusagen:\n" 
                    + invitationUrl);

            mailSender.send(message);
        } catch (Exception e) {
            // (20) Fehler blockiert Erstellung nicht
            System.err.println("E-Mail konnte nicht versendet werden: " + e.getMessage());
        }
    }
}