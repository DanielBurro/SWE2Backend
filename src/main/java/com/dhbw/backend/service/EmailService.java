package com.dhbw.backend.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @SuppressWarnings("null")
    public void sendVerificationEmail(@NonNull String toEmail, String token) {
        String verificationUrl = frontendUrl + "/api/auth/verify?token=" + token;

        System.out.println("\n[MAIL-LOG] Verifizierungslink für " + toEmail + ": " + verificationUrl + "\n");

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("d.burrows2310@gmail.com"); // DEINE E-MAIL HIER
            helper.setTo(toEmail);
            helper.setSubject("Willkommen! Aktiviere deinen exklusiven Account");

            // WICHTIG: width="100%%" statt width="100%"
            String htmlContent = """
                <html>
                <body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #1a1a1a; color: #ffffff;">
                    <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #1a1a1a;">
                        <tr>
                            <td align="center" style="padding: 40px 0;">
                                <table width="600" border="0" cellspacing="0" cellpadding="0" style="background-color: #000000; border: 2px solid #d4af37; border-radius: 8px;">
                                    <tr>
                                        <td align="center" style="padding: 40px 40px 20px 40px;">
                                            <div style="border: 1px solid #d4af37; display: inline-block; padding: 10px 20px;">
                                                <span style="color: #d4af37; font-size: 20px; letter-spacing: 5px; text-transform: uppercase;">Member Access</span>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 20px 40px 40px 40px; text-align: center; line-height: 1.6;">
                                            <h1 style="color: #d4af37; font-size: 26px; margin-bottom: 20px;">Fast geschafft!</h1>
                                            <p style="font-size: 16px; color: #cccccc;">Vielen Dank für deine Registrierung. Um deinen Account zu aktivieren und Zugang zu unseren exklusiven Events zu erhalten, bestätige bitte kurz deine E-Mail-Adresse.</p>
                                            
                                            <div style="margin-top: 40px;">
                                                <a href="%s" style="background-color: #d4af37; color: #000000; padding: 18px 35px; text-decoration: none; font-weight: bold; border-radius: 2px; font-size: 14px; display: inline-block; text-transform: uppercase; letter-spacing: 1px;">Account aktivieren</a>
                                            </div>
                                            
                                            <p style="margin-top: 30px; font-size: 14px; color: #666666;">Falls der Button nicht funktioniert, kopiere diesen Link in deinen Browser:<br>
                                            <span style="color: #d4af37;">%s</span></p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 20px; text-align: center; background-color: #0a0a0a; color: #444444; font-size: 11px; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px;">
                                            &copy; 2026 Dein Event-Backend-Team. All rights reserved.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(verificationUrl, verificationUrl);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (Exception e) {
            System.err.println("Fehler beim Versand der Verifizierungs-Mail: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    public void sendInvitationEmail(String toEmail, String guestName, String eventTitle, String token) {
        String invitationUrl = frontendUrl + "/api/public/invitations/info?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("d.burrows2310@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Exklusive Einladung: " + eventTitle);

            String htmlContent = """
                <html>
                <body style="margin: 0; padding: 0; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif; background-color: #1a1a1a; color: #ffffff;">
                    <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #1a1a1a;">
                        <tr>
                            <td align="center" style="padding: 40px 0;">
                                <table width="600" border="0" cellspacing="0" cellpadding="0" style="background-color: #000000; border: 2px solid #d4af37; border-radius: 8px;">
                                    <tr>
                                        <td align="center" style="padding: 40px 40px 20px 40px;">
                                            <h1 style="color: #d4af37; font-size: 28px; margin: 0; text-transform: uppercase; letter-spacing: 2px;">Exklusive Einladung</h1>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 20px 40px 40px 40px; text-align: center; line-height: 1.6;">
                                            <p style="font-size: 18px; color: #ffffff;">Hallo <strong>%s</strong>,</p>
                                            <p style="font-size: 16px; color: #cccccc;">Es ist uns eine Freude, dich zum Event</p>
                                            <h2 style="color: #d4af37; font-size: 24px; margin: 10px 0;">%s</h2>
                                            <p style="font-size: 16px; color: #cccccc;">einzuladen. Wir würden uns sehr freuen, diesen besonderen Anlass mit dir zu feiern.</p>
                                            
                                            <div style="margin-top: 40px;">
                                                <a href="localhost:4200/profile" style="background-color: #d4af37; color: #000000; padding: 15px 30px; text-decoration: none; font-weight: bold; border-radius: 5px; font-size: 16px; display: inline-block;">Guck nach!!</a>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 20px; text-align: center; border-top: 1px solid #333; color: #666666; font-size: 12px;">
                                            Diese Einladung wurde persönlich für dich erstellt.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(guestName, eventTitle, invitationUrl);

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Fehler beim HTML-Mail-Versand: " + e.getMessage());
        }
    }
}