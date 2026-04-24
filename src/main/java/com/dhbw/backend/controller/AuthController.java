package com.dhbw.backend.controller;

import com.dhbw.backend.dto.AuthResponseDTO;
import com.dhbw.backend.dto.LoginRequestDTO;
import com.dhbw.backend.dto.UserCreateDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.service.AuthService;
import com.dhbw.backend.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(summary = "Benutzer Login", description = "Prüft E-Mail/Passwort und gibt bei Erfolg einen JWT-Token zurück.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(summary = "Neuen Benutzer registrieren", description = "Legt einen neuen User an, hasht das Passwort und sendet eine Verifizierungs-E-Mail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User erfolgreich registriert"),
            @ApiResponse(responseCode = "400", description = "Ungültige Eingabe oder E-Mail/Username bereits vergeben")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@Valid @RequestBody UserCreateDTO dto) {
        Users user = new Users();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setBio(dto.getBio());
        user.setProfilePicUrl(dto.getProfilePicUrl());
        
        authService.registerUser(user);
        
        return ResponseEntity.ok(Map.of("message", "Registrierung erfolgreich. Bitte prüfe deine E-Mails, um deinen Account zu verifizieren."));
    }

    @Operation(summary = "E-Mail verifizieren", description = "Bestätigt die E-Mail-Adresse über das Token aus der Brevo E-Mail.")
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        boolean isVerified = authService.verifyEmail(token);
        
        if (isVerified) {
            return ResponseEntity.ok("E-Mail erfolgreich verifiziert! Du kannst dich nun einloggen.");
        } else {
            return ResponseEntity.badRequest().body("Der Verifizierungslink ist ungültig oder abgelaufen.");
        }
    }

    @Operation(summary = "Logout", description = "Invalidiert den aktuellen JWT-Token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreich ausgeloggt"),
            @ApiResponse(responseCode = "401", description = "Nicht authentifiziert")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklist(token);
        }
        return ResponseEntity.ok(Map.of("message", "Erfolgreich ausgeloggt."));
    }
}