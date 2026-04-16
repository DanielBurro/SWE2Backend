package com.dhbw.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    // Ein geheimes Wort, mit dem dein Server die Stempel fälschungssicher macht.
    // In echten Projekten steht das in den application.properties!
    private final String SECRET_KEY = "mein_super_geheimes_eventplanner_passwort_das_niemand_kennen_darf";
    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    // 1. Token generieren (Gültig für 24 Stunden)
    public String generateToken(String email) {
        return JWT.create()
                .withSubject(email)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24h
                .sign(algorithm);
    }

    // 2. Token validieren und die E-Mail des Users auslesen
    public String validateTokenAndGetEmail(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build()
                    .verify(token);
            return decodedJWT.getSubject(); // Gibt die E-Mail zurück
        } catch (Exception e) {
            return null; // Token ist ungültig, abgelaufen oder gefälscht
        }
    }
}