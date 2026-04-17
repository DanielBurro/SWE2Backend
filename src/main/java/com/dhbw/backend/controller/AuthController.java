package com.dhbw.backend.controller;

import com.dhbw.backend.dto.AuthResponseDTO;
import com.dhbw.backend.dto.LoginRequestDTO;
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
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