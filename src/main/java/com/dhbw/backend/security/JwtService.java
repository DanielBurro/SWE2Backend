package com.dhbw.backend.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final Algorithm algorithm;

    public JwtService(@Value("${application.security.jwt.secret-key}") String secretKey) {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }
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
