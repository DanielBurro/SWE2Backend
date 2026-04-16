package com.dhbw.backend.service;

import com.dhbw.backend.dto.AuthResponseDTO;
import com.dhbw.backend.dto.LoginRequestDTO;
import com.dhbw.backend.dto.UserDTO;
import com.dhbw.backend.model.Users;
import com.dhbw.backend.repository.UserRepository;
import com.dhbw.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        // 1. User suchen (hier nutze ich Users, wie in deiner Fehlermeldung gesehen)
        Users user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Ungültige E-Mail oder Passwort."));

        // 2. Passwort prüfen (Verschlüsseltes aus DB vs. Klartext vom Frontend)
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Ungültige E-Mail oder Passwort.");
        }

        // 3. Token generieren
        String token = jwtService.generateToken(user.getEmail());

        // 4. Response zusammenbauen
        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        
        // Hilfsmethode zum Umwandeln in UserDTO (kannst du aus dem UserController kopieren/nutzen)
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setBio(user.getBio());
        userDTO.setProfilePicUrl(user.getProfilePicUrl());
        
        response.setUser(userDTO);
        return response;
    }
}